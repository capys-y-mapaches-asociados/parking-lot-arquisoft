package co.edu.icesi.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import co.edu.icesi.IntegrationTest;
import co.edu.icesi.domain.Barrier;
import co.edu.icesi.domain.ParkingLot;
import co.edu.icesi.domain.enumeration.BarrierStatus;
import co.edu.icesi.domain.enumeration.BarrierType;
import co.edu.icesi.repository.BarrierRepository;
import co.edu.icesi.repository.EntityManager;
import co.edu.icesi.repository.search.BarrierSearchRepository;
import co.edu.icesi.service.dto.BarrierDTO;
import co.edu.icesi.service.mapper.BarrierMapper;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.collections4.IterableUtils;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Integration tests for the {@link BarrierResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class BarrierResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final BarrierType DEFAULT_TYPE = BarrierType.ENTRY;
    private static final BarrierType UPDATED_TYPE = BarrierType.EXIT;

    private static final BarrierStatus DEFAULT_STATUS = BarrierStatus.OPEN;
    private static final BarrierStatus UPDATED_STATUS = BarrierStatus.CLOSED;

    private static final String ENTITY_API_URL = "/api/barriers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/barriers";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private BarrierRepository barrierRepository;

    @Autowired
    private BarrierMapper barrierMapper;

    @Autowired
    private BarrierSearchRepository barrierSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Barrier barrier;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Barrier createEntity(EntityManager em) {
        Barrier barrier = new Barrier().name(DEFAULT_NAME).type(DEFAULT_TYPE).status(DEFAULT_STATUS);
        // Add required entity
        ParkingLot parkingLot;
        parkingLot = em.insert(ParkingLotResourceIT.createEntity(em)).block();
        barrier.setParkingLot(parkingLot);
        return barrier;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Barrier createUpdatedEntity(EntityManager em) {
        Barrier barrier = new Barrier().name(UPDATED_NAME).type(UPDATED_TYPE).status(UPDATED_STATUS);
        // Add required entity
        ParkingLot parkingLot;
        parkingLot = em.insert(ParkingLotResourceIT.createUpdatedEntity(em)).block();
        barrier.setParkingLot(parkingLot);
        return barrier;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Barrier.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        ParkingLotResourceIT.deleteEntities(em);
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        barrierSearchRepository.deleteAll().block();
        assertThat(barrierSearchRepository.count().block()).isEqualTo(0);
    }

    @BeforeEach
    public void setupCsrf() {
        webTestClient = webTestClient.mutateWith(csrf());
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        barrier = createEntity(em);
    }

    @Test
    void createBarrier() throws Exception {
        int databaseSizeBeforeCreate = barrierRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(barrierSearchRepository.findAll().collectList().block());
        // Create the Barrier
        BarrierDTO barrierDTO = barrierMapper.toDto(barrier);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(barrierDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Barrier in the database
        List<Barrier> barrierList = barrierRepository.findAll().collectList().block();
        assertThat(barrierList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(barrierSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Barrier testBarrier = barrierList.get(barrierList.size() - 1);
        assertThat(testBarrier.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testBarrier.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testBarrier.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    void createBarrierWithExistingId() throws Exception {
        // Create the Barrier with an existing ID
        barrier.setId(1L);
        BarrierDTO barrierDTO = barrierMapper.toDto(barrier);

        int databaseSizeBeforeCreate = barrierRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(barrierSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(barrierDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Barrier in the database
        List<Barrier> barrierList = barrierRepository.findAll().collectList().block();
        assertThat(barrierList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(barrierSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = barrierRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(barrierSearchRepository.findAll().collectList().block());
        // set the field null
        barrier.setName(null);

        // Create the Barrier, which fails.
        BarrierDTO barrierDTO = barrierMapper.toDto(barrier);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(barrierDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Barrier> barrierList = barrierRepository.findAll().collectList().block();
        assertThat(barrierList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(barrierSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = barrierRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(barrierSearchRepository.findAll().collectList().block());
        // set the field null
        barrier.setType(null);

        // Create the Barrier, which fails.
        BarrierDTO barrierDTO = barrierMapper.toDto(barrier);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(barrierDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Barrier> barrierList = barrierRepository.findAll().collectList().block();
        assertThat(barrierList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(barrierSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = barrierRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(barrierSearchRepository.findAll().collectList().block());
        // set the field null
        barrier.setStatus(null);

        // Create the Barrier, which fails.
        BarrierDTO barrierDTO = barrierMapper.toDto(barrier);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(barrierDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Barrier> barrierList = barrierRepository.findAll().collectList().block();
        assertThat(barrierList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(barrierSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllBarriersAsStream() {
        // Initialize the database
        barrierRepository.save(barrier).block();

        List<Barrier> barrierList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(BarrierDTO.class)
            .getResponseBody()
            .map(barrierMapper::toEntity)
            .filter(barrier::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(barrierList).isNotNull();
        assertThat(barrierList).hasSize(1);
        Barrier testBarrier = barrierList.get(0);
        assertThat(testBarrier.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testBarrier.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testBarrier.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    void getAllBarriers() {
        // Initialize the database
        barrierRepository.save(barrier).block();

        // Get all the barrierList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(barrier.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].type")
            .value(hasItem(DEFAULT_TYPE.toString()))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()));
    }

    @Test
    void getBarrier() {
        // Initialize the database
        barrierRepository.save(barrier).block();

        // Get the barrier
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, barrier.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(barrier.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.type")
            .value(is(DEFAULT_TYPE.toString()))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()));
    }

    @Test
    void getNonExistingBarrier() {
        // Get the barrier
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingBarrier() throws Exception {
        // Initialize the database
        barrierRepository.save(barrier).block();

        int databaseSizeBeforeUpdate = barrierRepository.findAll().collectList().block().size();
        barrierSearchRepository.save(barrier).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(barrierSearchRepository.findAll().collectList().block());

        // Update the barrier
        Barrier updatedBarrier = barrierRepository.findById(barrier.getId()).block();
        updatedBarrier.name(UPDATED_NAME).type(UPDATED_TYPE).status(UPDATED_STATUS);
        BarrierDTO barrierDTO = barrierMapper.toDto(updatedBarrier);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, barrierDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(barrierDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Barrier in the database
        List<Barrier> barrierList = barrierRepository.findAll().collectList().block();
        assertThat(barrierList).hasSize(databaseSizeBeforeUpdate);
        Barrier testBarrier = barrierList.get(barrierList.size() - 1);
        assertThat(testBarrier.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testBarrier.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testBarrier.getStatus()).isEqualTo(UPDATED_STATUS);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(barrierSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Barrier> barrierSearchList = IterableUtils.toList(barrierSearchRepository.findAll().collectList().block());
                Barrier testBarrierSearch = barrierSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testBarrierSearch.getName()).isEqualTo(UPDATED_NAME);
                assertThat(testBarrierSearch.getType()).isEqualTo(UPDATED_TYPE);
                assertThat(testBarrierSearch.getStatus()).isEqualTo(UPDATED_STATUS);
            });
    }

    @Test
    void putNonExistingBarrier() throws Exception {
        int databaseSizeBeforeUpdate = barrierRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(barrierSearchRepository.findAll().collectList().block());
        barrier.setId(count.incrementAndGet());

        // Create the Barrier
        BarrierDTO barrierDTO = barrierMapper.toDto(barrier);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, barrierDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(barrierDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Barrier in the database
        List<Barrier> barrierList = barrierRepository.findAll().collectList().block();
        assertThat(barrierList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(barrierSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchBarrier() throws Exception {
        int databaseSizeBeforeUpdate = barrierRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(barrierSearchRepository.findAll().collectList().block());
        barrier.setId(count.incrementAndGet());

        // Create the Barrier
        BarrierDTO barrierDTO = barrierMapper.toDto(barrier);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(barrierDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Barrier in the database
        List<Barrier> barrierList = barrierRepository.findAll().collectList().block();
        assertThat(barrierList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(barrierSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamBarrier() throws Exception {
        int databaseSizeBeforeUpdate = barrierRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(barrierSearchRepository.findAll().collectList().block());
        barrier.setId(count.incrementAndGet());

        // Create the Barrier
        BarrierDTO barrierDTO = barrierMapper.toDto(barrier);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(barrierDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Barrier in the database
        List<Barrier> barrierList = barrierRepository.findAll().collectList().block();
        assertThat(barrierList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(barrierSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateBarrierWithPatch() throws Exception {
        // Initialize the database
        barrierRepository.save(barrier).block();

        int databaseSizeBeforeUpdate = barrierRepository.findAll().collectList().block().size();

        // Update the barrier using partial update
        Barrier partialUpdatedBarrier = new Barrier();
        partialUpdatedBarrier.setId(barrier.getId());

        partialUpdatedBarrier.type(UPDATED_TYPE).status(UPDATED_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedBarrier.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedBarrier))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Barrier in the database
        List<Barrier> barrierList = barrierRepository.findAll().collectList().block();
        assertThat(barrierList).hasSize(databaseSizeBeforeUpdate);
        Barrier testBarrier = barrierList.get(barrierList.size() - 1);
        assertThat(testBarrier.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testBarrier.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testBarrier.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    void fullUpdateBarrierWithPatch() throws Exception {
        // Initialize the database
        barrierRepository.save(barrier).block();

        int databaseSizeBeforeUpdate = barrierRepository.findAll().collectList().block().size();

        // Update the barrier using partial update
        Barrier partialUpdatedBarrier = new Barrier();
        partialUpdatedBarrier.setId(barrier.getId());

        partialUpdatedBarrier.name(UPDATED_NAME).type(UPDATED_TYPE).status(UPDATED_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedBarrier.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedBarrier))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Barrier in the database
        List<Barrier> barrierList = barrierRepository.findAll().collectList().block();
        assertThat(barrierList).hasSize(databaseSizeBeforeUpdate);
        Barrier testBarrier = barrierList.get(barrierList.size() - 1);
        assertThat(testBarrier.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testBarrier.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testBarrier.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    void patchNonExistingBarrier() throws Exception {
        int databaseSizeBeforeUpdate = barrierRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(barrierSearchRepository.findAll().collectList().block());
        barrier.setId(count.incrementAndGet());

        // Create the Barrier
        BarrierDTO barrierDTO = barrierMapper.toDto(barrier);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, barrierDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(barrierDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Barrier in the database
        List<Barrier> barrierList = barrierRepository.findAll().collectList().block();
        assertThat(barrierList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(barrierSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchBarrier() throws Exception {
        int databaseSizeBeforeUpdate = barrierRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(barrierSearchRepository.findAll().collectList().block());
        barrier.setId(count.incrementAndGet());

        // Create the Barrier
        BarrierDTO barrierDTO = barrierMapper.toDto(barrier);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(barrierDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Barrier in the database
        List<Barrier> barrierList = barrierRepository.findAll().collectList().block();
        assertThat(barrierList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(barrierSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamBarrier() throws Exception {
        int databaseSizeBeforeUpdate = barrierRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(barrierSearchRepository.findAll().collectList().block());
        barrier.setId(count.incrementAndGet());

        // Create the Barrier
        BarrierDTO barrierDTO = barrierMapper.toDto(barrier);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(barrierDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Barrier in the database
        List<Barrier> barrierList = barrierRepository.findAll().collectList().block();
        assertThat(barrierList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(barrierSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteBarrier() {
        // Initialize the database
        barrierRepository.save(barrier).block();
        barrierRepository.save(barrier).block();
        barrierSearchRepository.save(barrier).block();

        int databaseSizeBeforeDelete = barrierRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(barrierSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the barrier
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, barrier.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Barrier> barrierList = barrierRepository.findAll().collectList().block();
        assertThat(barrierList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(barrierSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchBarrier() {
        // Initialize the database
        barrier = barrierRepository.save(barrier).block();
        barrierSearchRepository.save(barrier).block();

        // Search the barrier
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + barrier.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(barrier.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].type")
            .value(hasItem(DEFAULT_TYPE.toString()))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()));
    }
}
