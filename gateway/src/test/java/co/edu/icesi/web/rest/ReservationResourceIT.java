package co.edu.icesi.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import co.edu.icesi.IntegrationTest;
import co.edu.icesi.domain.Reservation;
import co.edu.icesi.domain.enumeration.ReservationStatus;
import co.edu.icesi.repository.EntityManager;
import co.edu.icesi.repository.ReservationRepository;
import co.edu.icesi.repository.search.ReservationSearchRepository;
import co.edu.icesi.service.dto.ReservationDTO;
import co.edu.icesi.service.mapper.ReservationMapper;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.UUID;
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
 * Integration tests for the {@link ReservationResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ReservationResourceIT {

    private static final UUID DEFAULT_PARKING_SPOT_ID = UUID.randomUUID();
    private static final UUID UPDATED_PARKING_SPOT_ID = UUID.randomUUID();

    private static final Instant DEFAULT_START_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_END_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_END_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final ReservationStatus DEFAULT_STATUS = ReservationStatus.PLACED;
    private static final ReservationStatus UPDATED_STATUS = ReservationStatus.ACTIVE;

    private static final String DEFAULT_RESERVATION_CODE = "AY-a{10, 14}";
    private static final String UPDATED_RESERVATION_CODE = "PP-8{10, 14}";

    private static final String ENTITY_API_URL = "/api/reservations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/reservations";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationMapper reservationMapper;

    @Autowired
    private ReservationSearchRepository reservationSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Reservation reservation;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Reservation createEntity(EntityManager em) {
        Reservation reservation = new Reservation()
            .parkingSpotId(DEFAULT_PARKING_SPOT_ID)
            .startTime(DEFAULT_START_TIME)
            .endTime(DEFAULT_END_TIME)
            .status(DEFAULT_STATUS)
            .reservationCode(DEFAULT_RESERVATION_CODE);
        return reservation;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Reservation createUpdatedEntity(EntityManager em) {
        Reservation reservation = new Reservation()
            .parkingSpotId(UPDATED_PARKING_SPOT_ID)
            .startTime(UPDATED_START_TIME)
            .endTime(UPDATED_END_TIME)
            .status(UPDATED_STATUS)
            .reservationCode(UPDATED_RESERVATION_CODE);
        return reservation;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Reservation.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        reservationSearchRepository.deleteAll().block();
        assertThat(reservationSearchRepository.count().block()).isEqualTo(0);
    }

    @BeforeEach
    public void setupCsrf() {
        webTestClient = webTestClient.mutateWith(csrf());
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        reservation = createEntity(em);
    }

    @Test
    void createReservation() throws Exception {
        int databaseSizeBeforeCreate = reservationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reservationSearchRepository.findAll().collectList().block());
        // Create the Reservation
        ReservationDTO reservationDTO = reservationMapper.toDto(reservation);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(reservationDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Reservation in the database
        List<Reservation> reservationList = reservationRepository.findAll().collectList().block();
        assertThat(reservationList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(reservationSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Reservation testReservation = reservationList.get(reservationList.size() - 1);
        assertThat(testReservation.getParkingSpotId()).isEqualTo(DEFAULT_PARKING_SPOT_ID);
        assertThat(testReservation.getStartTime()).isEqualTo(DEFAULT_START_TIME);
        assertThat(testReservation.getEndTime()).isEqualTo(DEFAULT_END_TIME);
        assertThat(testReservation.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testReservation.getReservationCode()).isEqualTo(DEFAULT_RESERVATION_CODE);
    }

    @Test
    void createReservationWithExistingId() throws Exception {
        // Create the Reservation with an existing ID
        reservation.setId(1L);
        ReservationDTO reservationDTO = reservationMapper.toDto(reservation);

        int databaseSizeBeforeCreate = reservationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reservationSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(reservationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Reservation in the database
        List<Reservation> reservationList = reservationRepository.findAll().collectList().block();
        assertThat(reservationList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reservationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkParkingSpotIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = reservationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reservationSearchRepository.findAll().collectList().block());
        // set the field null
        reservation.setParkingSpotId(null);

        // Create the Reservation, which fails.
        ReservationDTO reservationDTO = reservationMapper.toDto(reservation);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(reservationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Reservation> reservationList = reservationRepository.findAll().collectList().block();
        assertThat(reservationList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reservationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkStartTimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = reservationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reservationSearchRepository.findAll().collectList().block());
        // set the field null
        reservation.setStartTime(null);

        // Create the Reservation, which fails.
        ReservationDTO reservationDTO = reservationMapper.toDto(reservation);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(reservationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Reservation> reservationList = reservationRepository.findAll().collectList().block();
        assertThat(reservationList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reservationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkEndTimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = reservationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reservationSearchRepository.findAll().collectList().block());
        // set the field null
        reservation.setEndTime(null);

        // Create the Reservation, which fails.
        ReservationDTO reservationDTO = reservationMapper.toDto(reservation);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(reservationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Reservation> reservationList = reservationRepository.findAll().collectList().block();
        assertThat(reservationList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reservationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = reservationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reservationSearchRepository.findAll().collectList().block());
        // set the field null
        reservation.setStatus(null);

        // Create the Reservation, which fails.
        ReservationDTO reservationDTO = reservationMapper.toDto(reservation);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(reservationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Reservation> reservationList = reservationRepository.findAll().collectList().block();
        assertThat(reservationList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reservationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkReservationCodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = reservationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reservationSearchRepository.findAll().collectList().block());
        // set the field null
        reservation.setReservationCode(null);

        // Create the Reservation, which fails.
        ReservationDTO reservationDTO = reservationMapper.toDto(reservation);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(reservationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Reservation> reservationList = reservationRepository.findAll().collectList().block();
        assertThat(reservationList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reservationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllReservationsAsStream() {
        // Initialize the database
        reservationRepository.save(reservation).block();

        List<Reservation> reservationList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(ReservationDTO.class)
            .getResponseBody()
            .map(reservationMapper::toEntity)
            .filter(reservation::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(reservationList).isNotNull();
        assertThat(reservationList).hasSize(1);
        Reservation testReservation = reservationList.get(0);
        assertThat(testReservation.getParkingSpotId()).isEqualTo(DEFAULT_PARKING_SPOT_ID);
        assertThat(testReservation.getStartTime()).isEqualTo(DEFAULT_START_TIME);
        assertThat(testReservation.getEndTime()).isEqualTo(DEFAULT_END_TIME);
        assertThat(testReservation.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testReservation.getReservationCode()).isEqualTo(DEFAULT_RESERVATION_CODE);
    }

    @Test
    void getAllReservations() {
        // Initialize the database
        reservationRepository.save(reservation).block();

        // Get all the reservationList
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
            .value(hasItem(reservation.getId().intValue()))
            .jsonPath("$.[*].parkingSpotId")
            .value(hasItem(DEFAULT_PARKING_SPOT_ID.toString()))
            .jsonPath("$.[*].startTime")
            .value(hasItem(DEFAULT_START_TIME.toString()))
            .jsonPath("$.[*].endTime")
            .value(hasItem(DEFAULT_END_TIME.toString()))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()))
            .jsonPath("$.[*].reservationCode")
            .value(hasItem(DEFAULT_RESERVATION_CODE));
    }

    @Test
    void getReservation() {
        // Initialize the database
        reservationRepository.save(reservation).block();

        // Get the reservation
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, reservation.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(reservation.getId().intValue()))
            .jsonPath("$.parkingSpotId")
            .value(is(DEFAULT_PARKING_SPOT_ID.toString()))
            .jsonPath("$.startTime")
            .value(is(DEFAULT_START_TIME.toString()))
            .jsonPath("$.endTime")
            .value(is(DEFAULT_END_TIME.toString()))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()))
            .jsonPath("$.reservationCode")
            .value(is(DEFAULT_RESERVATION_CODE));
    }

    @Test
    void getNonExistingReservation() {
        // Get the reservation
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingReservation() throws Exception {
        // Initialize the database
        reservationRepository.save(reservation).block();

        int databaseSizeBeforeUpdate = reservationRepository.findAll().collectList().block().size();
        reservationSearchRepository.save(reservation).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reservationSearchRepository.findAll().collectList().block());

        // Update the reservation
        Reservation updatedReservation = reservationRepository.findById(reservation.getId()).block();
        updatedReservation
            .parkingSpotId(UPDATED_PARKING_SPOT_ID)
            .startTime(UPDATED_START_TIME)
            .endTime(UPDATED_END_TIME)
            .status(UPDATED_STATUS)
            .reservationCode(UPDATED_RESERVATION_CODE);
        ReservationDTO reservationDTO = reservationMapper.toDto(updatedReservation);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, reservationDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(reservationDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Reservation in the database
        List<Reservation> reservationList = reservationRepository.findAll().collectList().block();
        assertThat(reservationList).hasSize(databaseSizeBeforeUpdate);
        Reservation testReservation = reservationList.get(reservationList.size() - 1);
        assertThat(testReservation.getParkingSpotId()).isEqualTo(UPDATED_PARKING_SPOT_ID);
        assertThat(testReservation.getStartTime()).isEqualTo(UPDATED_START_TIME);
        assertThat(testReservation.getEndTime()).isEqualTo(UPDATED_END_TIME);
        assertThat(testReservation.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testReservation.getReservationCode()).isEqualTo(UPDATED_RESERVATION_CODE);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(reservationSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Reservation> reservationSearchList = IterableUtils.toList(reservationSearchRepository.findAll().collectList().block());
                Reservation testReservationSearch = reservationSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testReservationSearch.getParkingSpotId()).isEqualTo(UPDATED_PARKING_SPOT_ID);
                assertThat(testReservationSearch.getStartTime()).isEqualTo(UPDATED_START_TIME);
                assertThat(testReservationSearch.getEndTime()).isEqualTo(UPDATED_END_TIME);
                assertThat(testReservationSearch.getStatus()).isEqualTo(UPDATED_STATUS);
                assertThat(testReservationSearch.getReservationCode()).isEqualTo(UPDATED_RESERVATION_CODE);
            });
    }

    @Test
    void putNonExistingReservation() throws Exception {
        int databaseSizeBeforeUpdate = reservationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reservationSearchRepository.findAll().collectList().block());
        reservation.setId(count.incrementAndGet());

        // Create the Reservation
        ReservationDTO reservationDTO = reservationMapper.toDto(reservation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, reservationDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(reservationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Reservation in the database
        List<Reservation> reservationList = reservationRepository.findAll().collectList().block();
        assertThat(reservationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reservationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchReservation() throws Exception {
        int databaseSizeBeforeUpdate = reservationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reservationSearchRepository.findAll().collectList().block());
        reservation.setId(count.incrementAndGet());

        // Create the Reservation
        ReservationDTO reservationDTO = reservationMapper.toDto(reservation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(reservationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Reservation in the database
        List<Reservation> reservationList = reservationRepository.findAll().collectList().block();
        assertThat(reservationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reservationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamReservation() throws Exception {
        int databaseSizeBeforeUpdate = reservationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reservationSearchRepository.findAll().collectList().block());
        reservation.setId(count.incrementAndGet());

        // Create the Reservation
        ReservationDTO reservationDTO = reservationMapper.toDto(reservation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(reservationDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Reservation in the database
        List<Reservation> reservationList = reservationRepository.findAll().collectList().block();
        assertThat(reservationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reservationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateReservationWithPatch() throws Exception {
        // Initialize the database
        reservationRepository.save(reservation).block();

        int databaseSizeBeforeUpdate = reservationRepository.findAll().collectList().block().size();

        // Update the reservation using partial update
        Reservation partialUpdatedReservation = new Reservation();
        partialUpdatedReservation.setId(reservation.getId());

        partialUpdatedReservation.parkingSpotId(UPDATED_PARKING_SPOT_ID).endTime(UPDATED_END_TIME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedReservation.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedReservation))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Reservation in the database
        List<Reservation> reservationList = reservationRepository.findAll().collectList().block();
        assertThat(reservationList).hasSize(databaseSizeBeforeUpdate);
        Reservation testReservation = reservationList.get(reservationList.size() - 1);
        assertThat(testReservation.getParkingSpotId()).isEqualTo(UPDATED_PARKING_SPOT_ID);
        assertThat(testReservation.getStartTime()).isEqualTo(DEFAULT_START_TIME);
        assertThat(testReservation.getEndTime()).isEqualTo(UPDATED_END_TIME);
        assertThat(testReservation.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testReservation.getReservationCode()).isEqualTo(DEFAULT_RESERVATION_CODE);
    }

    @Test
    void fullUpdateReservationWithPatch() throws Exception {
        // Initialize the database
        reservationRepository.save(reservation).block();

        int databaseSizeBeforeUpdate = reservationRepository.findAll().collectList().block().size();

        // Update the reservation using partial update
        Reservation partialUpdatedReservation = new Reservation();
        partialUpdatedReservation.setId(reservation.getId());

        partialUpdatedReservation
            .parkingSpotId(UPDATED_PARKING_SPOT_ID)
            .startTime(UPDATED_START_TIME)
            .endTime(UPDATED_END_TIME)
            .status(UPDATED_STATUS)
            .reservationCode(UPDATED_RESERVATION_CODE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedReservation.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedReservation))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Reservation in the database
        List<Reservation> reservationList = reservationRepository.findAll().collectList().block();
        assertThat(reservationList).hasSize(databaseSizeBeforeUpdate);
        Reservation testReservation = reservationList.get(reservationList.size() - 1);
        assertThat(testReservation.getParkingSpotId()).isEqualTo(UPDATED_PARKING_SPOT_ID);
        assertThat(testReservation.getStartTime()).isEqualTo(UPDATED_START_TIME);
        assertThat(testReservation.getEndTime()).isEqualTo(UPDATED_END_TIME);
        assertThat(testReservation.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testReservation.getReservationCode()).isEqualTo(UPDATED_RESERVATION_CODE);
    }

    @Test
    void patchNonExistingReservation() throws Exception {
        int databaseSizeBeforeUpdate = reservationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reservationSearchRepository.findAll().collectList().block());
        reservation.setId(count.incrementAndGet());

        // Create the Reservation
        ReservationDTO reservationDTO = reservationMapper.toDto(reservation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, reservationDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(reservationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Reservation in the database
        List<Reservation> reservationList = reservationRepository.findAll().collectList().block();
        assertThat(reservationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reservationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchReservation() throws Exception {
        int databaseSizeBeforeUpdate = reservationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reservationSearchRepository.findAll().collectList().block());
        reservation.setId(count.incrementAndGet());

        // Create the Reservation
        ReservationDTO reservationDTO = reservationMapper.toDto(reservation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(reservationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Reservation in the database
        List<Reservation> reservationList = reservationRepository.findAll().collectList().block();
        assertThat(reservationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reservationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamReservation() throws Exception {
        int databaseSizeBeforeUpdate = reservationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reservationSearchRepository.findAll().collectList().block());
        reservation.setId(count.incrementAndGet());

        // Create the Reservation
        ReservationDTO reservationDTO = reservationMapper.toDto(reservation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(reservationDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Reservation in the database
        List<Reservation> reservationList = reservationRepository.findAll().collectList().block();
        assertThat(reservationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reservationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteReservation() {
        // Initialize the database
        reservationRepository.save(reservation).block();
        reservationRepository.save(reservation).block();
        reservationSearchRepository.save(reservation).block();

        int databaseSizeBeforeDelete = reservationRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reservationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the reservation
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, reservation.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Reservation> reservationList = reservationRepository.findAll().collectList().block();
        assertThat(reservationList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reservationSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchReservation() {
        // Initialize the database
        reservation = reservationRepository.save(reservation).block();
        reservationSearchRepository.save(reservation).block();

        // Search the reservation
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + reservation.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(reservation.getId().intValue()))
            .jsonPath("$.[*].parkingSpotId")
            .value(hasItem(DEFAULT_PARKING_SPOT_ID.toString()))
            .jsonPath("$.[*].startTime")
            .value(hasItem(DEFAULT_START_TIME.toString()))
            .jsonPath("$.[*].endTime")
            .value(hasItem(DEFAULT_END_TIME.toString()))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()))
            .jsonPath("$.[*].reservationCode")
            .value(hasItem(DEFAULT_RESERVATION_CODE));
    }
}
