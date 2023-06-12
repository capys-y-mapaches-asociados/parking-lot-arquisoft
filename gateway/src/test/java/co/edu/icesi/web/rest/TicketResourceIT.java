package co.edu.icesi.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import co.edu.icesi.IntegrationTest;
import co.edu.icesi.domain.ParkingSpot;
import co.edu.icesi.domain.Ticket;
import co.edu.icesi.domain.enumeration.TicketStatus;
import co.edu.icesi.repository.EntityManager;
import co.edu.icesi.repository.TicketRepository;
import co.edu.icesi.service.dto.TicketDTO;
import co.edu.icesi.service.mapper.TicketMapper;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link TicketResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class TicketResourceIT {

    private static final String DEFAULT_TICKET_CODE = "1{6, 10}";
    private static final String UPDATED_TICKET_CODE = "1{6, 10}B";

    private static final Instant DEFAULT_ISSUED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ISSUED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_ENTRY_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ENTRY_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_EXIT_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_EXIT_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final TicketStatus DEFAULT_STATUS = TicketStatus.ACTIVE;
    private static final TicketStatus UPDATED_STATUS = TicketStatus.EXPIRED;

    private static final String ENTITY_API_URL = "/api/tickets";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TicketMapper ticketMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Ticket ticket;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Ticket createEntity(EntityManager em) {
        Ticket ticket = new Ticket()
            .ticketCode(DEFAULT_TICKET_CODE)
            .issuedAt(DEFAULT_ISSUED_AT)
            .entryTime(DEFAULT_ENTRY_TIME)
            .exitTime(DEFAULT_EXIT_TIME)
            .status(DEFAULT_STATUS);
        // Add required entity
        ParkingSpot parkingSpot;
        parkingSpot = em.insert(ParkingSpotResourceIT.createEntity(em)).block();
        ticket.setParkingSpotId(parkingSpot);
        return ticket;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Ticket createUpdatedEntity(EntityManager em) {
        Ticket ticket = new Ticket()
            .ticketCode(UPDATED_TICKET_CODE)
            .issuedAt(UPDATED_ISSUED_AT)
            .entryTime(UPDATED_ENTRY_TIME)
            .exitTime(UPDATED_EXIT_TIME)
            .status(UPDATED_STATUS);
        // Add required entity
        ParkingSpot parkingSpot;
        parkingSpot = em.insert(ParkingSpotResourceIT.createUpdatedEntity(em)).block();
        ticket.setParkingSpotId(parkingSpot);
        return ticket;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Ticket.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        ParkingSpotResourceIT.deleteEntities(em);
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        ticket = createEntity(em);
    }

    @Test
    void createTicket() throws Exception {
        int databaseSizeBeforeCreate = ticketRepository.findAll().collectList().block().size();
        // Create the Ticket
        TicketDTO ticketDTO = ticketMapper.toDto(ticket);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ticketDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Ticket in the database
        List<Ticket> ticketList = ticketRepository.findAll().collectList().block();
        assertThat(ticketList).hasSize(databaseSizeBeforeCreate + 1);
        Ticket testTicket = ticketList.get(ticketList.size() - 1);
        assertThat(testTicket.getTicketCode()).isEqualTo(DEFAULT_TICKET_CODE);
        assertThat(testTicket.getIssuedAt()).isEqualTo(DEFAULT_ISSUED_AT);
        assertThat(testTicket.getEntryTime()).isEqualTo(DEFAULT_ENTRY_TIME);
        assertThat(testTicket.getExitTime()).isEqualTo(DEFAULT_EXIT_TIME);
        assertThat(testTicket.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    void createTicketWithExistingId() throws Exception {
        // Create the Ticket with an existing ID
        ticket.setId(1L);
        TicketDTO ticketDTO = ticketMapper.toDto(ticket);

        int databaseSizeBeforeCreate = ticketRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ticketDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Ticket in the database
        List<Ticket> ticketList = ticketRepository.findAll().collectList().block();
        assertThat(ticketList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkTicketCodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = ticketRepository.findAll().collectList().block().size();
        // set the field null
        ticket.setTicketCode(null);

        // Create the Ticket, which fails.
        TicketDTO ticketDTO = ticketMapper.toDto(ticket);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ticketDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Ticket> ticketList = ticketRepository.findAll().collectList().block();
        assertThat(ticketList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkIssuedAtIsRequired() throws Exception {
        int databaseSizeBeforeTest = ticketRepository.findAll().collectList().block().size();
        // set the field null
        ticket.setIssuedAt(null);

        // Create the Ticket, which fails.
        TicketDTO ticketDTO = ticketMapper.toDto(ticket);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ticketDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Ticket> ticketList = ticketRepository.findAll().collectList().block();
        assertThat(ticketList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkEntryTimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = ticketRepository.findAll().collectList().block().size();
        // set the field null
        ticket.setEntryTime(null);

        // Create the Ticket, which fails.
        TicketDTO ticketDTO = ticketMapper.toDto(ticket);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ticketDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Ticket> ticketList = ticketRepository.findAll().collectList().block();
        assertThat(ticketList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkExitTimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = ticketRepository.findAll().collectList().block().size();
        // set the field null
        ticket.setExitTime(null);

        // Create the Ticket, which fails.
        TicketDTO ticketDTO = ticketMapper.toDto(ticket);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ticketDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Ticket> ticketList = ticketRepository.findAll().collectList().block();
        assertThat(ticketList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = ticketRepository.findAll().collectList().block().size();
        // set the field null
        ticket.setStatus(null);

        // Create the Ticket, which fails.
        TicketDTO ticketDTO = ticketMapper.toDto(ticket);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ticketDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Ticket> ticketList = ticketRepository.findAll().collectList().block();
        assertThat(ticketList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllTicketsAsStream() {
        // Initialize the database
        ticketRepository.save(ticket).block();

        List<Ticket> ticketList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(TicketDTO.class)
            .getResponseBody()
            .map(ticketMapper::toEntity)
            .filter(ticket::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(ticketList).isNotNull();
        assertThat(ticketList).hasSize(1);
        Ticket testTicket = ticketList.get(0);
        assertThat(testTicket.getTicketCode()).isEqualTo(DEFAULT_TICKET_CODE);
        assertThat(testTicket.getIssuedAt()).isEqualTo(DEFAULT_ISSUED_AT);
        assertThat(testTicket.getEntryTime()).isEqualTo(DEFAULT_ENTRY_TIME);
        assertThat(testTicket.getExitTime()).isEqualTo(DEFAULT_EXIT_TIME);
        assertThat(testTicket.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    void getAllTickets() {
        // Initialize the database
        ticketRepository.save(ticket).block();

        // Get all the ticketList
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
            .value(hasItem(ticket.getId().intValue()))
            .jsonPath("$.[*].ticketCode")
            .value(hasItem(DEFAULT_TICKET_CODE))
            .jsonPath("$.[*].issuedAt")
            .value(hasItem(DEFAULT_ISSUED_AT.toString()))
            .jsonPath("$.[*].entryTime")
            .value(hasItem(DEFAULT_ENTRY_TIME.toString()))
            .jsonPath("$.[*].exitTime")
            .value(hasItem(DEFAULT_EXIT_TIME.toString()))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()));
    }

    @Test
    void getTicket() {
        // Initialize the database
        ticketRepository.save(ticket).block();

        // Get the ticket
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, ticket.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(ticket.getId().intValue()))
            .jsonPath("$.ticketCode")
            .value(is(DEFAULT_TICKET_CODE))
            .jsonPath("$.issuedAt")
            .value(is(DEFAULT_ISSUED_AT.toString()))
            .jsonPath("$.entryTime")
            .value(is(DEFAULT_ENTRY_TIME.toString()))
            .jsonPath("$.exitTime")
            .value(is(DEFAULT_EXIT_TIME.toString()))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()));
    }

    @Test
    void getNonExistingTicket() {
        // Get the ticket
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingTicket() throws Exception {
        // Initialize the database
        ticketRepository.save(ticket).block();

        int databaseSizeBeforeUpdate = ticketRepository.findAll().collectList().block().size();

        // Update the ticket
        Ticket updatedTicket = ticketRepository.findById(ticket.getId()).block();
        updatedTicket
            .ticketCode(UPDATED_TICKET_CODE)
            .issuedAt(UPDATED_ISSUED_AT)
            .entryTime(UPDATED_ENTRY_TIME)
            .exitTime(UPDATED_EXIT_TIME)
            .status(UPDATED_STATUS);
        TicketDTO ticketDTO = ticketMapper.toDto(updatedTicket);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, ticketDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ticketDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Ticket in the database
        List<Ticket> ticketList = ticketRepository.findAll().collectList().block();
        assertThat(ticketList).hasSize(databaseSizeBeforeUpdate);
        Ticket testTicket = ticketList.get(ticketList.size() - 1);
        assertThat(testTicket.getTicketCode()).isEqualTo(UPDATED_TICKET_CODE);
        assertThat(testTicket.getIssuedAt()).isEqualTo(UPDATED_ISSUED_AT);
        assertThat(testTicket.getEntryTime()).isEqualTo(UPDATED_ENTRY_TIME);
        assertThat(testTicket.getExitTime()).isEqualTo(UPDATED_EXIT_TIME);
        assertThat(testTicket.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    void putNonExistingTicket() throws Exception {
        int databaseSizeBeforeUpdate = ticketRepository.findAll().collectList().block().size();
        ticket.setId(count.incrementAndGet());

        // Create the Ticket
        TicketDTO ticketDTO = ticketMapper.toDto(ticket);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, ticketDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ticketDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Ticket in the database
        List<Ticket> ticketList = ticketRepository.findAll().collectList().block();
        assertThat(ticketList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchTicket() throws Exception {
        int databaseSizeBeforeUpdate = ticketRepository.findAll().collectList().block().size();
        ticket.setId(count.incrementAndGet());

        // Create the Ticket
        TicketDTO ticketDTO = ticketMapper.toDto(ticket);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ticketDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Ticket in the database
        List<Ticket> ticketList = ticketRepository.findAll().collectList().block();
        assertThat(ticketList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamTicket() throws Exception {
        int databaseSizeBeforeUpdate = ticketRepository.findAll().collectList().block().size();
        ticket.setId(count.incrementAndGet());

        // Create the Ticket
        TicketDTO ticketDTO = ticketMapper.toDto(ticket);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ticketDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Ticket in the database
        List<Ticket> ticketList = ticketRepository.findAll().collectList().block();
        assertThat(ticketList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateTicketWithPatch() throws Exception {
        // Initialize the database
        ticketRepository.save(ticket).block();

        int databaseSizeBeforeUpdate = ticketRepository.findAll().collectList().block().size();

        // Update the ticket using partial update
        Ticket partialUpdatedTicket = new Ticket();
        partialUpdatedTicket.setId(ticket.getId());

        partialUpdatedTicket.ticketCode(UPDATED_TICKET_CODE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTicket.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedTicket))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Ticket in the database
        List<Ticket> ticketList = ticketRepository.findAll().collectList().block();
        assertThat(ticketList).hasSize(databaseSizeBeforeUpdate);
        Ticket testTicket = ticketList.get(ticketList.size() - 1);
        assertThat(testTicket.getTicketCode()).isEqualTo(UPDATED_TICKET_CODE);
        assertThat(testTicket.getIssuedAt()).isEqualTo(DEFAULT_ISSUED_AT);
        assertThat(testTicket.getEntryTime()).isEqualTo(DEFAULT_ENTRY_TIME);
        assertThat(testTicket.getExitTime()).isEqualTo(DEFAULT_EXIT_TIME);
        assertThat(testTicket.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    void fullUpdateTicketWithPatch() throws Exception {
        // Initialize the database
        ticketRepository.save(ticket).block();

        int databaseSizeBeforeUpdate = ticketRepository.findAll().collectList().block().size();

        // Update the ticket using partial update
        Ticket partialUpdatedTicket = new Ticket();
        partialUpdatedTicket.setId(ticket.getId());

        partialUpdatedTicket
            .ticketCode(UPDATED_TICKET_CODE)
            .issuedAt(UPDATED_ISSUED_AT)
            .entryTime(UPDATED_ENTRY_TIME)
            .exitTime(UPDATED_EXIT_TIME)
            .status(UPDATED_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTicket.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedTicket))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Ticket in the database
        List<Ticket> ticketList = ticketRepository.findAll().collectList().block();
        assertThat(ticketList).hasSize(databaseSizeBeforeUpdate);
        Ticket testTicket = ticketList.get(ticketList.size() - 1);
        assertThat(testTicket.getTicketCode()).isEqualTo(UPDATED_TICKET_CODE);
        assertThat(testTicket.getIssuedAt()).isEqualTo(UPDATED_ISSUED_AT);
        assertThat(testTicket.getEntryTime()).isEqualTo(UPDATED_ENTRY_TIME);
        assertThat(testTicket.getExitTime()).isEqualTo(UPDATED_EXIT_TIME);
        assertThat(testTicket.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    void patchNonExistingTicket() throws Exception {
        int databaseSizeBeforeUpdate = ticketRepository.findAll().collectList().block().size();
        ticket.setId(count.incrementAndGet());

        // Create the Ticket
        TicketDTO ticketDTO = ticketMapper.toDto(ticket);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, ticketDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(ticketDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Ticket in the database
        List<Ticket> ticketList = ticketRepository.findAll().collectList().block();
        assertThat(ticketList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchTicket() throws Exception {
        int databaseSizeBeforeUpdate = ticketRepository.findAll().collectList().block().size();
        ticket.setId(count.incrementAndGet());

        // Create the Ticket
        TicketDTO ticketDTO = ticketMapper.toDto(ticket);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(ticketDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Ticket in the database
        List<Ticket> ticketList = ticketRepository.findAll().collectList().block();
        assertThat(ticketList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamTicket() throws Exception {
        int databaseSizeBeforeUpdate = ticketRepository.findAll().collectList().block().size();
        ticket.setId(count.incrementAndGet());

        // Create the Ticket
        TicketDTO ticketDTO = ticketMapper.toDto(ticket);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(ticketDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Ticket in the database
        List<Ticket> ticketList = ticketRepository.findAll().collectList().block();
        assertThat(ticketList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteTicket() {
        // Initialize the database
        ticketRepository.save(ticket).block();

        int databaseSizeBeforeDelete = ticketRepository.findAll().collectList().block().size();

        // Delete the ticket
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, ticket.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Ticket> ticketList = ticketRepository.findAll().collectList().block();
        assertThat(ticketList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
