package co.edu.icesi.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import co.edu.icesi.IntegrationTest;
import co.edu.icesi.domain.Payment;
import co.edu.icesi.domain.enumeration.PaymentMethod;
import co.edu.icesi.domain.enumeration.PaymentStatus;
import co.edu.icesi.repository.EntityManager;
import co.edu.icesi.repository.PaymentRepository;
import co.edu.icesi.service.dto.PaymentDTO;
import co.edu.icesi.service.mapper.PaymentMapper;
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
 * Integration tests for the {@link PaymentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class PaymentResourceIT {

    private static final Integer DEFAULT_CUSTOMER_ID = 1;
    private static final Integer UPDATED_CUSTOMER_ID = 2;

    private static final Integer DEFAULT_RESERVATION_ID = 1;
    private static final Integer UPDATED_RESERVATION_ID = 2;

    private static final Float DEFAULT_AMOUNT = 1000.00F;
    private static final Float UPDATED_AMOUNT = 999F;

    private static final PaymentStatus DEFAULT_PAYMENT_STATUS = PaymentStatus.PENDING;
    private static final PaymentStatus UPDATED_PAYMENT_STATUS = PaymentStatus.PLACED;

    private static final PaymentMethod DEFAULT_PAYMENT_METHOD = PaymentMethod.CARD;
    private static final PaymentMethod UPDATED_PAYMENT_METHOD = PaymentMethod.CASH;

    private static final String ENTITY_API_URL = "/api/payments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentMapper paymentMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Payment payment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Payment createEntity(EntityManager em) {
        Payment payment = new Payment()
            .customerId(DEFAULT_CUSTOMER_ID)
            .reservationID(DEFAULT_RESERVATION_ID)
            .amount(DEFAULT_AMOUNT)
            .paymentStatus(DEFAULT_PAYMENT_STATUS)
            .paymentMethod(DEFAULT_PAYMENT_METHOD);
        return payment;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Payment createUpdatedEntity(EntityManager em) {
        Payment payment = new Payment()
            .customerId(UPDATED_CUSTOMER_ID)
            .reservationID(UPDATED_RESERVATION_ID)
            .amount(UPDATED_AMOUNT)
            .paymentStatus(UPDATED_PAYMENT_STATUS)
            .paymentMethod(UPDATED_PAYMENT_METHOD);
        return payment;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Payment.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        payment = createEntity(em);
    }

    @Test
    void createPayment() throws Exception {
        int databaseSizeBeforeCreate = paymentRepository.findAll().collectList().block().size();
        // Create the Payment
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(paymentDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll().collectList().block();
        assertThat(paymentList).hasSize(databaseSizeBeforeCreate + 1);
        Payment testPayment = paymentList.get(paymentList.size() - 1);
        assertThat(testPayment.getCustomerId()).isEqualTo(DEFAULT_CUSTOMER_ID);
        assertThat(testPayment.getReservationID()).isEqualTo(DEFAULT_RESERVATION_ID);
        assertThat(testPayment.getAmount()).isEqualTo(DEFAULT_AMOUNT);
        assertThat(testPayment.getPaymentStatus()).isEqualTo(DEFAULT_PAYMENT_STATUS);
        assertThat(testPayment.getPaymentMethod()).isEqualTo(DEFAULT_PAYMENT_METHOD);
    }

    @Test
    void createPaymentWithExistingId() throws Exception {
        // Create the Payment with an existing ID
        payment.setId(1L);
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        int databaseSizeBeforeCreate = paymentRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(paymentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll().collectList().block();
        assertThat(paymentList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkCustomerIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = paymentRepository.findAll().collectList().block().size();
        // set the field null
        payment.setCustomerId(null);

        // Create the Payment, which fails.
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(paymentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Payment> paymentList = paymentRepository.findAll().collectList().block();
        assertThat(paymentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkAmountIsRequired() throws Exception {
        int databaseSizeBeforeTest = paymentRepository.findAll().collectList().block().size();
        // set the field null
        payment.setAmount(null);

        // Create the Payment, which fails.
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(paymentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Payment> paymentList = paymentRepository.findAll().collectList().block();
        assertThat(paymentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkPaymentStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = paymentRepository.findAll().collectList().block().size();
        // set the field null
        payment.setPaymentStatus(null);

        // Create the Payment, which fails.
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(paymentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Payment> paymentList = paymentRepository.findAll().collectList().block();
        assertThat(paymentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkPaymentMethodIsRequired() throws Exception {
        int databaseSizeBeforeTest = paymentRepository.findAll().collectList().block().size();
        // set the field null
        payment.setPaymentMethod(null);

        // Create the Payment, which fails.
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(paymentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Payment> paymentList = paymentRepository.findAll().collectList().block();
        assertThat(paymentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllPaymentsAsStream() {
        // Initialize the database
        paymentRepository.save(payment).block();

        List<Payment> paymentList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(PaymentDTO.class)
            .getResponseBody()
            .map(paymentMapper::toEntity)
            .filter(payment::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(paymentList).isNotNull();
        assertThat(paymentList).hasSize(1);
        Payment testPayment = paymentList.get(0);
        assertThat(testPayment.getCustomerId()).isEqualTo(DEFAULT_CUSTOMER_ID);
        assertThat(testPayment.getReservationID()).isEqualTo(DEFAULT_RESERVATION_ID);
        assertThat(testPayment.getAmount()).isEqualTo(DEFAULT_AMOUNT);
        assertThat(testPayment.getPaymentStatus()).isEqualTo(DEFAULT_PAYMENT_STATUS);
        assertThat(testPayment.getPaymentMethod()).isEqualTo(DEFAULT_PAYMENT_METHOD);
    }

    @Test
    void getAllPayments() {
        // Initialize the database
        paymentRepository.save(payment).block();

        // Get all the paymentList
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
            .value(hasItem(payment.getId().intValue()))
            .jsonPath("$.[*].customerId")
            .value(hasItem(DEFAULT_CUSTOMER_ID))
            .jsonPath("$.[*].reservationID")
            .value(hasItem(DEFAULT_RESERVATION_ID))
            .jsonPath("$.[*].amount")
            .value(hasItem(DEFAULT_AMOUNT.doubleValue()))
            .jsonPath("$.[*].paymentStatus")
            .value(hasItem(DEFAULT_PAYMENT_STATUS.toString()))
            .jsonPath("$.[*].paymentMethod")
            .value(hasItem(DEFAULT_PAYMENT_METHOD.toString()));
    }

    @Test
    void getPayment() {
        // Initialize the database
        paymentRepository.save(payment).block();

        // Get the payment
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, payment.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(payment.getId().intValue()))
            .jsonPath("$.customerId")
            .value(is(DEFAULT_CUSTOMER_ID))
            .jsonPath("$.reservationID")
            .value(is(DEFAULT_RESERVATION_ID))
            .jsonPath("$.amount")
            .value(is(DEFAULT_AMOUNT.doubleValue()))
            .jsonPath("$.paymentStatus")
            .value(is(DEFAULT_PAYMENT_STATUS.toString()))
            .jsonPath("$.paymentMethod")
            .value(is(DEFAULT_PAYMENT_METHOD.toString()));
    }

    @Test
    void getNonExistingPayment() {
        // Get the payment
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingPayment() throws Exception {
        // Initialize the database
        paymentRepository.save(payment).block();

        int databaseSizeBeforeUpdate = paymentRepository.findAll().collectList().block().size();

        // Update the payment
        Payment updatedPayment = paymentRepository.findById(payment.getId()).block();
        updatedPayment
            .customerId(UPDATED_CUSTOMER_ID)
            .reservationID(UPDATED_RESERVATION_ID)
            .amount(UPDATED_AMOUNT)
            .paymentStatus(UPDATED_PAYMENT_STATUS)
            .paymentMethod(UPDATED_PAYMENT_METHOD);
        PaymentDTO paymentDTO = paymentMapper.toDto(updatedPayment);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, paymentDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(paymentDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll().collectList().block();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
        Payment testPayment = paymentList.get(paymentList.size() - 1);
        assertThat(testPayment.getCustomerId()).isEqualTo(UPDATED_CUSTOMER_ID);
        assertThat(testPayment.getReservationID()).isEqualTo(UPDATED_RESERVATION_ID);
        assertThat(testPayment.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testPayment.getPaymentStatus()).isEqualTo(UPDATED_PAYMENT_STATUS);
        assertThat(testPayment.getPaymentMethod()).isEqualTo(UPDATED_PAYMENT_METHOD);
    }

    @Test
    void putNonExistingPayment() throws Exception {
        int databaseSizeBeforeUpdate = paymentRepository.findAll().collectList().block().size();
        payment.setId(count.incrementAndGet());

        // Create the Payment
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, paymentDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(paymentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll().collectList().block();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchPayment() throws Exception {
        int databaseSizeBeforeUpdate = paymentRepository.findAll().collectList().block().size();
        payment.setId(count.incrementAndGet());

        // Create the Payment
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(paymentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll().collectList().block();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamPayment() throws Exception {
        int databaseSizeBeforeUpdate = paymentRepository.findAll().collectList().block().size();
        payment.setId(count.incrementAndGet());

        // Create the Payment
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(paymentDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll().collectList().block();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdatePaymentWithPatch() throws Exception {
        // Initialize the database
        paymentRepository.save(payment).block();

        int databaseSizeBeforeUpdate = paymentRepository.findAll().collectList().block().size();

        // Update the payment using partial update
        Payment partialUpdatedPayment = new Payment();
        partialUpdatedPayment.setId(payment.getId());

        partialUpdatedPayment.reservationID(UPDATED_RESERVATION_ID).paymentMethod(UPDATED_PAYMENT_METHOD);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPayment.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPayment))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll().collectList().block();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
        Payment testPayment = paymentList.get(paymentList.size() - 1);
        assertThat(testPayment.getCustomerId()).isEqualTo(DEFAULT_CUSTOMER_ID);
        assertThat(testPayment.getReservationID()).isEqualTo(UPDATED_RESERVATION_ID);
        assertThat(testPayment.getAmount()).isEqualTo(DEFAULT_AMOUNT);
        assertThat(testPayment.getPaymentStatus()).isEqualTo(DEFAULT_PAYMENT_STATUS);
        assertThat(testPayment.getPaymentMethod()).isEqualTo(UPDATED_PAYMENT_METHOD);
    }

    @Test
    void fullUpdatePaymentWithPatch() throws Exception {
        // Initialize the database
        paymentRepository.save(payment).block();

        int databaseSizeBeforeUpdate = paymentRepository.findAll().collectList().block().size();

        // Update the payment using partial update
        Payment partialUpdatedPayment = new Payment();
        partialUpdatedPayment.setId(payment.getId());

        partialUpdatedPayment
            .customerId(UPDATED_CUSTOMER_ID)
            .reservationID(UPDATED_RESERVATION_ID)
            .amount(UPDATED_AMOUNT)
            .paymentStatus(UPDATED_PAYMENT_STATUS)
            .paymentMethod(UPDATED_PAYMENT_METHOD);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPayment.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPayment))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll().collectList().block();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
        Payment testPayment = paymentList.get(paymentList.size() - 1);
        assertThat(testPayment.getCustomerId()).isEqualTo(UPDATED_CUSTOMER_ID);
        assertThat(testPayment.getReservationID()).isEqualTo(UPDATED_RESERVATION_ID);
        assertThat(testPayment.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testPayment.getPaymentStatus()).isEqualTo(UPDATED_PAYMENT_STATUS);
        assertThat(testPayment.getPaymentMethod()).isEqualTo(UPDATED_PAYMENT_METHOD);
    }

    @Test
    void patchNonExistingPayment() throws Exception {
        int databaseSizeBeforeUpdate = paymentRepository.findAll().collectList().block().size();
        payment.setId(count.incrementAndGet());

        // Create the Payment
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, paymentDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(paymentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll().collectList().block();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchPayment() throws Exception {
        int databaseSizeBeforeUpdate = paymentRepository.findAll().collectList().block().size();
        payment.setId(count.incrementAndGet());

        // Create the Payment
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(paymentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll().collectList().block();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamPayment() throws Exception {
        int databaseSizeBeforeUpdate = paymentRepository.findAll().collectList().block().size();
        payment.setId(count.incrementAndGet());

        // Create the Payment
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(paymentDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll().collectList().block();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deletePayment() {
        // Initialize the database
        paymentRepository.save(payment).block();

        int databaseSizeBeforeDelete = paymentRepository.findAll().collectList().block().size();

        // Delete the payment
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, payment.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Payment> paymentList = paymentRepository.findAll().collectList().block();
        assertThat(paymentList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
