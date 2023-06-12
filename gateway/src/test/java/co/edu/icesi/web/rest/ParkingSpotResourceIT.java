package co.edu.icesi.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import co.edu.icesi.IntegrationTest;
import co.edu.icesi.domain.ParkingLot;
import co.edu.icesi.domain.ParkingSpot;
import co.edu.icesi.domain.enumeration.ParkingSpotStatus;
import co.edu.icesi.domain.enumeration.ParkingSpotType;
import co.edu.icesi.domain.enumeration.ParkingSpotVehicle;
import co.edu.icesi.repository.EntityManager;
import co.edu.icesi.repository.ParkingSpotRepository;
import co.edu.icesi.service.dto.ParkingSpotDTO;
import co.edu.icesi.service.mapper.ParkingSpotMapper;
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
 * Integration tests for the {@link ParkingSpotResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ParkingSpotResourceIT {

    private static final Integer DEFAULT_NUMBER = 1;
    private static final Integer UPDATED_NUMBER = 2;

    private static final ParkingSpotStatus DEFAULT_STATUS = ParkingSpotStatus.OCCUPIED;
    private static final ParkingSpotStatus UPDATED_STATUS = ParkingSpotStatus.AVAILABLE;

    private static final ParkingSpotType DEFAULT_SPOT_TYPE = ParkingSpotType.REGULAR;
    private static final ParkingSpotType UPDATED_SPOT_TYPE = ParkingSpotType.HANDICAPPED;

    private static final ParkingSpotVehicle DEFAULT_SPOT_VEHICLE = ParkingSpotVehicle.CARGO_LARGE;
    private static final ParkingSpotVehicle UPDATED_SPOT_VEHICLE = ParkingSpotVehicle.CARGO;

    private static final String ENTITY_API_URL = "/api/parking-spots";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ParkingSpotRepository parkingSpotRepository;

    @Autowired
    private ParkingSpotMapper parkingSpotMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private ParkingSpot parkingSpot;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ParkingSpot createEntity(EntityManager em) {
        ParkingSpot parkingSpot = new ParkingSpot()
            .number(DEFAULT_NUMBER)
            .status(DEFAULT_STATUS)
            .spotType(DEFAULT_SPOT_TYPE)
            .spotVehicle(DEFAULT_SPOT_VEHICLE);
        // Add required entity
        ParkingLot parkingLot;
        parkingLot = em.insert(ParkingLotResourceIT.createEntity(em)).block();
        parkingSpot.setParkingLotId(parkingLot);
        return parkingSpot;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ParkingSpot createUpdatedEntity(EntityManager em) {
        ParkingSpot parkingSpot = new ParkingSpot()
            .number(UPDATED_NUMBER)
            .status(UPDATED_STATUS)
            .spotType(UPDATED_SPOT_TYPE)
            .spotVehicle(UPDATED_SPOT_VEHICLE);
        // Add required entity
        ParkingLot parkingLot;
        parkingLot = em.insert(ParkingLotResourceIT.createUpdatedEntity(em)).block();
        parkingSpot.setParkingLotId(parkingLot);
        return parkingSpot;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(ParkingSpot.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        ParkingLotResourceIT.deleteEntities(em);
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        parkingSpot = createEntity(em);
    }

    @Test
    void createParkingSpot() throws Exception {
        int databaseSizeBeforeCreate = parkingSpotRepository.findAll().collectList().block().size();
        // Create the ParkingSpot
        ParkingSpotDTO parkingSpotDTO = parkingSpotMapper.toDto(parkingSpot);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(parkingSpotDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the ParkingSpot in the database
        List<ParkingSpot> parkingSpotList = parkingSpotRepository.findAll().collectList().block();
        assertThat(parkingSpotList).hasSize(databaseSizeBeforeCreate + 1);
        ParkingSpot testParkingSpot = parkingSpotList.get(parkingSpotList.size() - 1);
        assertThat(testParkingSpot.getNumber()).isEqualTo(DEFAULT_NUMBER);
        assertThat(testParkingSpot.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testParkingSpot.getSpotType()).isEqualTo(DEFAULT_SPOT_TYPE);
        assertThat(testParkingSpot.getSpotVehicle()).isEqualTo(DEFAULT_SPOT_VEHICLE);
    }

    @Test
    void createParkingSpotWithExistingId() throws Exception {
        // Create the ParkingSpot with an existing ID
        parkingSpot.setId(1L);
        ParkingSpotDTO parkingSpotDTO = parkingSpotMapper.toDto(parkingSpot);

        int databaseSizeBeforeCreate = parkingSpotRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(parkingSpotDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ParkingSpot in the database
        List<ParkingSpot> parkingSpotList = parkingSpotRepository.findAll().collectList().block();
        assertThat(parkingSpotList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkNumberIsRequired() throws Exception {
        int databaseSizeBeforeTest = parkingSpotRepository.findAll().collectList().block().size();
        // set the field null
        parkingSpot.setNumber(null);

        // Create the ParkingSpot, which fails.
        ParkingSpotDTO parkingSpotDTO = parkingSpotMapper.toDto(parkingSpot);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(parkingSpotDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ParkingSpot> parkingSpotList = parkingSpotRepository.findAll().collectList().block();
        assertThat(parkingSpotList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = parkingSpotRepository.findAll().collectList().block().size();
        // set the field null
        parkingSpot.setStatus(null);

        // Create the ParkingSpot, which fails.
        ParkingSpotDTO parkingSpotDTO = parkingSpotMapper.toDto(parkingSpot);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(parkingSpotDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ParkingSpot> parkingSpotList = parkingSpotRepository.findAll().collectList().block();
        assertThat(parkingSpotList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkSpotTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = parkingSpotRepository.findAll().collectList().block().size();
        // set the field null
        parkingSpot.setSpotType(null);

        // Create the ParkingSpot, which fails.
        ParkingSpotDTO parkingSpotDTO = parkingSpotMapper.toDto(parkingSpot);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(parkingSpotDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ParkingSpot> parkingSpotList = parkingSpotRepository.findAll().collectList().block();
        assertThat(parkingSpotList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkSpotVehicleIsRequired() throws Exception {
        int databaseSizeBeforeTest = parkingSpotRepository.findAll().collectList().block().size();
        // set the field null
        parkingSpot.setSpotVehicle(null);

        // Create the ParkingSpot, which fails.
        ParkingSpotDTO parkingSpotDTO = parkingSpotMapper.toDto(parkingSpot);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(parkingSpotDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ParkingSpot> parkingSpotList = parkingSpotRepository.findAll().collectList().block();
        assertThat(parkingSpotList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllParkingSpotsAsStream() {
        // Initialize the database
        parkingSpotRepository.save(parkingSpot).block();

        List<ParkingSpot> parkingSpotList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(ParkingSpotDTO.class)
            .getResponseBody()
            .map(parkingSpotMapper::toEntity)
            .filter(parkingSpot::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(parkingSpotList).isNotNull();
        assertThat(parkingSpotList).hasSize(1);
        ParkingSpot testParkingSpot = parkingSpotList.get(0);
        assertThat(testParkingSpot.getNumber()).isEqualTo(DEFAULT_NUMBER);
        assertThat(testParkingSpot.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testParkingSpot.getSpotType()).isEqualTo(DEFAULT_SPOT_TYPE);
        assertThat(testParkingSpot.getSpotVehicle()).isEqualTo(DEFAULT_SPOT_VEHICLE);
    }

    @Test
    void getAllParkingSpots() {
        // Initialize the database
        parkingSpotRepository.save(parkingSpot).block();

        // Get all the parkingSpotList
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
            .value(hasItem(parkingSpot.getId().intValue()))
            .jsonPath("$.[*].number")
            .value(hasItem(DEFAULT_NUMBER))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()))
            .jsonPath("$.[*].spotType")
            .value(hasItem(DEFAULT_SPOT_TYPE.toString()))
            .jsonPath("$.[*].spotVehicle")
            .value(hasItem(DEFAULT_SPOT_VEHICLE.toString()));
    }

    @Test
    void getParkingSpot() {
        // Initialize the database
        parkingSpotRepository.save(parkingSpot).block();

        // Get the parkingSpot
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, parkingSpot.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(parkingSpot.getId().intValue()))
            .jsonPath("$.number")
            .value(is(DEFAULT_NUMBER))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()))
            .jsonPath("$.spotType")
            .value(is(DEFAULT_SPOT_TYPE.toString()))
            .jsonPath("$.spotVehicle")
            .value(is(DEFAULT_SPOT_VEHICLE.toString()));
    }

    @Test
    void getNonExistingParkingSpot() {
        // Get the parkingSpot
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingParkingSpot() throws Exception {
        // Initialize the database
        parkingSpotRepository.save(parkingSpot).block();

        int databaseSizeBeforeUpdate = parkingSpotRepository.findAll().collectList().block().size();

        // Update the parkingSpot
        ParkingSpot updatedParkingSpot = parkingSpotRepository.findById(parkingSpot.getId()).block();
        updatedParkingSpot.number(UPDATED_NUMBER).status(UPDATED_STATUS).spotType(UPDATED_SPOT_TYPE).spotVehicle(UPDATED_SPOT_VEHICLE);
        ParkingSpotDTO parkingSpotDTO = parkingSpotMapper.toDto(updatedParkingSpot);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, parkingSpotDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(parkingSpotDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ParkingSpot in the database
        List<ParkingSpot> parkingSpotList = parkingSpotRepository.findAll().collectList().block();
        assertThat(parkingSpotList).hasSize(databaseSizeBeforeUpdate);
        ParkingSpot testParkingSpot = parkingSpotList.get(parkingSpotList.size() - 1);
        assertThat(testParkingSpot.getNumber()).isEqualTo(UPDATED_NUMBER);
        assertThat(testParkingSpot.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testParkingSpot.getSpotType()).isEqualTo(UPDATED_SPOT_TYPE);
        assertThat(testParkingSpot.getSpotVehicle()).isEqualTo(UPDATED_SPOT_VEHICLE);
    }

    @Test
    void putNonExistingParkingSpot() throws Exception {
        int databaseSizeBeforeUpdate = parkingSpotRepository.findAll().collectList().block().size();
        parkingSpot.setId(count.incrementAndGet());

        // Create the ParkingSpot
        ParkingSpotDTO parkingSpotDTO = parkingSpotMapper.toDto(parkingSpot);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, parkingSpotDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(parkingSpotDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ParkingSpot in the database
        List<ParkingSpot> parkingSpotList = parkingSpotRepository.findAll().collectList().block();
        assertThat(parkingSpotList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchParkingSpot() throws Exception {
        int databaseSizeBeforeUpdate = parkingSpotRepository.findAll().collectList().block().size();
        parkingSpot.setId(count.incrementAndGet());

        // Create the ParkingSpot
        ParkingSpotDTO parkingSpotDTO = parkingSpotMapper.toDto(parkingSpot);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(parkingSpotDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ParkingSpot in the database
        List<ParkingSpot> parkingSpotList = parkingSpotRepository.findAll().collectList().block();
        assertThat(parkingSpotList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamParkingSpot() throws Exception {
        int databaseSizeBeforeUpdate = parkingSpotRepository.findAll().collectList().block().size();
        parkingSpot.setId(count.incrementAndGet());

        // Create the ParkingSpot
        ParkingSpotDTO parkingSpotDTO = parkingSpotMapper.toDto(parkingSpot);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(parkingSpotDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ParkingSpot in the database
        List<ParkingSpot> parkingSpotList = parkingSpotRepository.findAll().collectList().block();
        assertThat(parkingSpotList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateParkingSpotWithPatch() throws Exception {
        // Initialize the database
        parkingSpotRepository.save(parkingSpot).block();

        int databaseSizeBeforeUpdate = parkingSpotRepository.findAll().collectList().block().size();

        // Update the parkingSpot using partial update
        ParkingSpot partialUpdatedParkingSpot = new ParkingSpot();
        partialUpdatedParkingSpot.setId(parkingSpot.getId());

        partialUpdatedParkingSpot.number(UPDATED_NUMBER).spotType(UPDATED_SPOT_TYPE).spotVehicle(UPDATED_SPOT_VEHICLE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedParkingSpot.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedParkingSpot))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ParkingSpot in the database
        List<ParkingSpot> parkingSpotList = parkingSpotRepository.findAll().collectList().block();
        assertThat(parkingSpotList).hasSize(databaseSizeBeforeUpdate);
        ParkingSpot testParkingSpot = parkingSpotList.get(parkingSpotList.size() - 1);
        assertThat(testParkingSpot.getNumber()).isEqualTo(UPDATED_NUMBER);
        assertThat(testParkingSpot.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testParkingSpot.getSpotType()).isEqualTo(UPDATED_SPOT_TYPE);
        assertThat(testParkingSpot.getSpotVehicle()).isEqualTo(UPDATED_SPOT_VEHICLE);
    }

    @Test
    void fullUpdateParkingSpotWithPatch() throws Exception {
        // Initialize the database
        parkingSpotRepository.save(parkingSpot).block();

        int databaseSizeBeforeUpdate = parkingSpotRepository.findAll().collectList().block().size();

        // Update the parkingSpot using partial update
        ParkingSpot partialUpdatedParkingSpot = new ParkingSpot();
        partialUpdatedParkingSpot.setId(parkingSpot.getId());

        partialUpdatedParkingSpot
            .number(UPDATED_NUMBER)
            .status(UPDATED_STATUS)
            .spotType(UPDATED_SPOT_TYPE)
            .spotVehicle(UPDATED_SPOT_VEHICLE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedParkingSpot.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedParkingSpot))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ParkingSpot in the database
        List<ParkingSpot> parkingSpotList = parkingSpotRepository.findAll().collectList().block();
        assertThat(parkingSpotList).hasSize(databaseSizeBeforeUpdate);
        ParkingSpot testParkingSpot = parkingSpotList.get(parkingSpotList.size() - 1);
        assertThat(testParkingSpot.getNumber()).isEqualTo(UPDATED_NUMBER);
        assertThat(testParkingSpot.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testParkingSpot.getSpotType()).isEqualTo(UPDATED_SPOT_TYPE);
        assertThat(testParkingSpot.getSpotVehicle()).isEqualTo(UPDATED_SPOT_VEHICLE);
    }

    @Test
    void patchNonExistingParkingSpot() throws Exception {
        int databaseSizeBeforeUpdate = parkingSpotRepository.findAll().collectList().block().size();
        parkingSpot.setId(count.incrementAndGet());

        // Create the ParkingSpot
        ParkingSpotDTO parkingSpotDTO = parkingSpotMapper.toDto(parkingSpot);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, parkingSpotDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(parkingSpotDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ParkingSpot in the database
        List<ParkingSpot> parkingSpotList = parkingSpotRepository.findAll().collectList().block();
        assertThat(parkingSpotList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchParkingSpot() throws Exception {
        int databaseSizeBeforeUpdate = parkingSpotRepository.findAll().collectList().block().size();
        parkingSpot.setId(count.incrementAndGet());

        // Create the ParkingSpot
        ParkingSpotDTO parkingSpotDTO = parkingSpotMapper.toDto(parkingSpot);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(parkingSpotDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ParkingSpot in the database
        List<ParkingSpot> parkingSpotList = parkingSpotRepository.findAll().collectList().block();
        assertThat(parkingSpotList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamParkingSpot() throws Exception {
        int databaseSizeBeforeUpdate = parkingSpotRepository.findAll().collectList().block().size();
        parkingSpot.setId(count.incrementAndGet());

        // Create the ParkingSpot
        ParkingSpotDTO parkingSpotDTO = parkingSpotMapper.toDto(parkingSpot);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(parkingSpotDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ParkingSpot in the database
        List<ParkingSpot> parkingSpotList = parkingSpotRepository.findAll().collectList().block();
        assertThat(parkingSpotList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteParkingSpot() {
        // Initialize the database
        parkingSpotRepository.save(parkingSpot).block();

        int databaseSizeBeforeDelete = parkingSpotRepository.findAll().collectList().block().size();

        // Delete the parkingSpot
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, parkingSpot.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<ParkingSpot> parkingSpotList = parkingSpotRepository.findAll().collectList().block();
        assertThat(parkingSpotList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
