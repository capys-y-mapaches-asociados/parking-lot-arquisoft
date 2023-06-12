package co.edu.icesi.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import co.edu.icesi.IntegrationTest;
import co.edu.icesi.domain.Barrier;
import co.edu.icesi.domain.ParkingLot;
import co.edu.icesi.domain.ParkingSpot;
import co.edu.icesi.repository.EntityManager;
import co.edu.icesi.repository.ParkingLotRepository;
import co.edu.icesi.service.dto.ParkingLotDTO;
import co.edu.icesi.service.mapper.ParkingLotMapper;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link ParkingLotResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ParkingLotResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LOCATION = "AAAAAAAAAAAA";
    private static final String UPDATED_LOCATION = "BBBBBBBBBBBB";

    private static final Integer DEFAULT_CAPACITY = 13000;
    private static final Integer UPDATED_CAPACITY = 12999;

    private static final String ENTITY_API_URL = "/api/parking-lots";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ParkingLotRepository parkingLotRepository;

    @Autowired
    private ParkingLotMapper parkingLotMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private ParkingLot parkingLot;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ParkingLot createEntity(EntityManager em) {
        ParkingLot parkingLot = new ParkingLot().name(DEFAULT_NAME).location(DEFAULT_LOCATION).capacity(DEFAULT_CAPACITY);
        // Add required entity
        ParkingSpot parkingSpot;
        parkingSpot = em.insert(ParkingSpotResourceIT.createEntity(em)).block();
        parkingLot.getParkingSpots().add(parkingSpot);
        // Add required entity
        Barrier barrier;
        barrier = em.insert(BarrierResourceIT.createEntity(em)).block();
        parkingLot.getBarriers().add(barrier);
        return parkingLot;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ParkingLot createUpdatedEntity(EntityManager em) {
        ParkingLot parkingLot = new ParkingLot().name(UPDATED_NAME).location(UPDATED_LOCATION).capacity(UPDATED_CAPACITY);
        // Add required entity
        ParkingSpot parkingSpot;
        parkingSpot = em.insert(ParkingSpotResourceIT.createUpdatedEntity(em)).block();
        parkingLot.getParkingSpots().add(parkingSpot);
        // Add required entity
        Barrier barrier;
        barrier = em.insert(BarrierResourceIT.createUpdatedEntity(em)).block();
        parkingLot.getBarriers().add(barrier);
        return parkingLot;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(ParkingLot.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        ParkingSpotResourceIT.deleteEntities(em);
        BarrierResourceIT.deleteEntities(em);
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        parkingLot = createEntity(em);
    }

    @Test
    void createParkingLot() throws Exception {
        int databaseSizeBeforeCreate = parkingLotRepository.findAll().collectList().block().size();
        // Create the ParkingLot
        ParkingLotDTO parkingLotDTO = parkingLotMapper.toDto(parkingLot);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(parkingLotDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the ParkingLot in the database
        List<ParkingLot> parkingLotList = parkingLotRepository.findAll().collectList().block();
        assertThat(parkingLotList).hasSize(databaseSizeBeforeCreate + 1);
        ParkingLot testParkingLot = parkingLotList.get(parkingLotList.size() - 1);
        assertThat(testParkingLot.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testParkingLot.getLocation()).isEqualTo(DEFAULT_LOCATION);
        assertThat(testParkingLot.getCapacity()).isEqualTo(DEFAULT_CAPACITY);
    }

    @Test
    void createParkingLotWithExistingId() throws Exception {
        // Create the ParkingLot with an existing ID
        parkingLot.setId(1L);
        ParkingLotDTO parkingLotDTO = parkingLotMapper.toDto(parkingLot);

        int databaseSizeBeforeCreate = parkingLotRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(parkingLotDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ParkingLot in the database
        List<ParkingLot> parkingLotList = parkingLotRepository.findAll().collectList().block();
        assertThat(parkingLotList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = parkingLotRepository.findAll().collectList().block().size();
        // set the field null
        parkingLot.setName(null);

        // Create the ParkingLot, which fails.
        ParkingLotDTO parkingLotDTO = parkingLotMapper.toDto(parkingLot);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(parkingLotDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ParkingLot> parkingLotList = parkingLotRepository.findAll().collectList().block();
        assertThat(parkingLotList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkLocationIsRequired() throws Exception {
        int databaseSizeBeforeTest = parkingLotRepository.findAll().collectList().block().size();
        // set the field null
        parkingLot.setLocation(null);

        // Create the ParkingLot, which fails.
        ParkingLotDTO parkingLotDTO = parkingLotMapper.toDto(parkingLot);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(parkingLotDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ParkingLot> parkingLotList = parkingLotRepository.findAll().collectList().block();
        assertThat(parkingLotList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkCapacityIsRequired() throws Exception {
        int databaseSizeBeforeTest = parkingLotRepository.findAll().collectList().block().size();
        // set the field null
        parkingLot.setCapacity(null);

        // Create the ParkingLot, which fails.
        ParkingLotDTO parkingLotDTO = parkingLotMapper.toDto(parkingLot);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(parkingLotDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ParkingLot> parkingLotList = parkingLotRepository.findAll().collectList().block();
        assertThat(parkingLotList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllParkingLotsAsStream() {
        // Initialize the database
        parkingLotRepository.save(parkingLot).block();

        List<ParkingLot> parkingLotList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(ParkingLotDTO.class)
            .getResponseBody()
            .map(parkingLotMapper::toEntity)
            .filter(parkingLot::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(parkingLotList).isNotNull();
        assertThat(parkingLotList).hasSize(1);
        ParkingLot testParkingLot = parkingLotList.get(0);
        assertThat(testParkingLot.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testParkingLot.getLocation()).isEqualTo(DEFAULT_LOCATION);
        assertThat(testParkingLot.getCapacity()).isEqualTo(DEFAULT_CAPACITY);
    }

    @Test
    void getAllParkingLots() {
        // Initialize the database
        parkingLotRepository.save(parkingLot).block();

        // Get all the parkingLotList
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
            .value(hasItem(parkingLot.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].location")
            .value(hasItem(DEFAULT_LOCATION))
            .jsonPath("$.[*].capacity")
            .value(hasItem(DEFAULT_CAPACITY));
    }

    @Test
    void getParkingLot() {
        // Initialize the database
        parkingLotRepository.save(parkingLot).block();

        // Get the parkingLot
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, parkingLot.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(parkingLot.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.location")
            .value(is(DEFAULT_LOCATION))
            .jsonPath("$.capacity")
            .value(is(DEFAULT_CAPACITY));
    }

    @Test
    void getNonExistingParkingLot() {
        // Get the parkingLot
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingParkingLot() throws Exception {
        // Initialize the database
        parkingLotRepository.save(parkingLot).block();

        int databaseSizeBeforeUpdate = parkingLotRepository.findAll().collectList().block().size();

        // Update the parkingLot
        ParkingLot updatedParkingLot = parkingLotRepository.findById(parkingLot.getId()).block();
        updatedParkingLot.name(UPDATED_NAME).location(UPDATED_LOCATION).capacity(UPDATED_CAPACITY);
        ParkingLotDTO parkingLotDTO = parkingLotMapper.toDto(updatedParkingLot);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, parkingLotDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(parkingLotDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ParkingLot in the database
        List<ParkingLot> parkingLotList = parkingLotRepository.findAll().collectList().block();
        assertThat(parkingLotList).hasSize(databaseSizeBeforeUpdate);
        ParkingLot testParkingLot = parkingLotList.get(parkingLotList.size() - 1);
        assertThat(testParkingLot.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testParkingLot.getLocation()).isEqualTo(UPDATED_LOCATION);
        assertThat(testParkingLot.getCapacity()).isEqualTo(UPDATED_CAPACITY);
    }

    @Test
    void putNonExistingParkingLot() throws Exception {
        int databaseSizeBeforeUpdate = parkingLotRepository.findAll().collectList().block().size();
        parkingLot.setId(count.incrementAndGet());

        // Create the ParkingLot
        ParkingLotDTO parkingLotDTO = parkingLotMapper.toDto(parkingLot);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, parkingLotDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(parkingLotDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ParkingLot in the database
        List<ParkingLot> parkingLotList = parkingLotRepository.findAll().collectList().block();
        assertThat(parkingLotList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchParkingLot() throws Exception {
        int databaseSizeBeforeUpdate = parkingLotRepository.findAll().collectList().block().size();
        parkingLot.setId(count.incrementAndGet());

        // Create the ParkingLot
        ParkingLotDTO parkingLotDTO = parkingLotMapper.toDto(parkingLot);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(parkingLotDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ParkingLot in the database
        List<ParkingLot> parkingLotList = parkingLotRepository.findAll().collectList().block();
        assertThat(parkingLotList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamParkingLot() throws Exception {
        int databaseSizeBeforeUpdate = parkingLotRepository.findAll().collectList().block().size();
        parkingLot.setId(count.incrementAndGet());

        // Create the ParkingLot
        ParkingLotDTO parkingLotDTO = parkingLotMapper.toDto(parkingLot);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(parkingLotDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ParkingLot in the database
        List<ParkingLot> parkingLotList = parkingLotRepository.findAll().collectList().block();
        assertThat(parkingLotList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateParkingLotWithPatch() throws Exception {
        // Initialize the database
        parkingLotRepository.save(parkingLot).block();

        int databaseSizeBeforeUpdate = parkingLotRepository.findAll().collectList().block().size();

        // Update the parkingLot using partial update
        ParkingLot partialUpdatedParkingLot = new ParkingLot();
        partialUpdatedParkingLot.setId(parkingLot.getId());

        partialUpdatedParkingLot.name(UPDATED_NAME).location(UPDATED_LOCATION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedParkingLot.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedParkingLot))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ParkingLot in the database
        List<ParkingLot> parkingLotList = parkingLotRepository.findAll().collectList().block();
        assertThat(parkingLotList).hasSize(databaseSizeBeforeUpdate);
        ParkingLot testParkingLot = parkingLotList.get(parkingLotList.size() - 1);
        assertThat(testParkingLot.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testParkingLot.getLocation()).isEqualTo(UPDATED_LOCATION);
        assertThat(testParkingLot.getCapacity()).isEqualTo(DEFAULT_CAPACITY);
    }

    @Test
    void fullUpdateParkingLotWithPatch() throws Exception {
        // Initialize the database
        parkingLotRepository.save(parkingLot).block();

        int databaseSizeBeforeUpdate = parkingLotRepository.findAll().collectList().block().size();

        // Update the parkingLot using partial update
        ParkingLot partialUpdatedParkingLot = new ParkingLot();
        partialUpdatedParkingLot.setId(parkingLot.getId());

        partialUpdatedParkingLot.name(UPDATED_NAME).location(UPDATED_LOCATION).capacity(UPDATED_CAPACITY);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedParkingLot.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedParkingLot))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ParkingLot in the database
        List<ParkingLot> parkingLotList = parkingLotRepository.findAll().collectList().block();
        assertThat(parkingLotList).hasSize(databaseSizeBeforeUpdate);
        ParkingLot testParkingLot = parkingLotList.get(parkingLotList.size() - 1);
        assertThat(testParkingLot.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testParkingLot.getLocation()).isEqualTo(UPDATED_LOCATION);
        assertThat(testParkingLot.getCapacity()).isEqualTo(UPDATED_CAPACITY);
    }

    @Test
    void patchNonExistingParkingLot() throws Exception {
        int databaseSizeBeforeUpdate = parkingLotRepository.findAll().collectList().block().size();
        parkingLot.setId(count.incrementAndGet());

        // Create the ParkingLot
        ParkingLotDTO parkingLotDTO = parkingLotMapper.toDto(parkingLot);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, parkingLotDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(parkingLotDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ParkingLot in the database
        List<ParkingLot> parkingLotList = parkingLotRepository.findAll().collectList().block();
        assertThat(parkingLotList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchParkingLot() throws Exception {
        int databaseSizeBeforeUpdate = parkingLotRepository.findAll().collectList().block().size();
        parkingLot.setId(count.incrementAndGet());

        // Create the ParkingLot
        ParkingLotDTO parkingLotDTO = parkingLotMapper.toDto(parkingLot);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(parkingLotDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ParkingLot in the database
        List<ParkingLot> parkingLotList = parkingLotRepository.findAll().collectList().block();
        assertThat(parkingLotList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamParkingLot() throws Exception {
        int databaseSizeBeforeUpdate = parkingLotRepository.findAll().collectList().block().size();
        parkingLot.setId(count.incrementAndGet());

        // Create the ParkingLot
        ParkingLotDTO parkingLotDTO = parkingLotMapper.toDto(parkingLot);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(parkingLotDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ParkingLot in the database
        List<ParkingLot> parkingLotList = parkingLotRepository.findAll().collectList().block();
        assertThat(parkingLotList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteParkingLot() {
        // Initialize the database
        parkingLotRepository.save(parkingLot).block();

        int databaseSizeBeforeDelete = parkingLotRepository.findAll().collectList().block().size();

        // Delete the parkingLot
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, parkingLot.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<ParkingLot> parkingLotList = parkingLotRepository.findAll().collectList().block();
        assertThat(parkingLotList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
