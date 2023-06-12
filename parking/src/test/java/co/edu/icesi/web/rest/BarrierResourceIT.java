package co.edu.icesi.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import co.edu.icesi.IntegrationTest;
import co.edu.icesi.domain.Barrier;
import co.edu.icesi.domain.ParkingLot;
import co.edu.icesi.domain.enumeration.BarrierStatus;
import co.edu.icesi.domain.enumeration.BarrierType;
import co.edu.icesi.repository.BarrierRepository;
import co.edu.icesi.service.dto.BarrierDTO;
import co.edu.icesi.service.mapper.BarrierMapper;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link BarrierResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
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

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private BarrierRepository barrierRepository;

    @Autowired
    private BarrierMapper barrierMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBarrierMockMvc;

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
        if (TestUtil.findAll(em, ParkingLot.class).isEmpty()) {
            parkingLot = ParkingLotResourceIT.createEntity(em);
            em.persist(parkingLot);
            em.flush();
        } else {
            parkingLot = TestUtil.findAll(em, ParkingLot.class).get(0);
        }
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
        if (TestUtil.findAll(em, ParkingLot.class).isEmpty()) {
            parkingLot = ParkingLotResourceIT.createUpdatedEntity(em);
            em.persist(parkingLot);
            em.flush();
        } else {
            parkingLot = TestUtil.findAll(em, ParkingLot.class).get(0);
        }
        barrier.setParkingLot(parkingLot);
        return barrier;
    }

    @BeforeEach
    public void initTest() {
        barrier = createEntity(em);
    }

    @Test
    @Transactional
    void createBarrier() throws Exception {
        int databaseSizeBeforeCreate = barrierRepository.findAll().size();
        // Create the Barrier
        BarrierDTO barrierDTO = barrierMapper.toDto(barrier);
        restBarrierMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(barrierDTO)))
            .andExpect(status().isCreated());

        // Validate the Barrier in the database
        List<Barrier> barrierList = barrierRepository.findAll();
        assertThat(barrierList).hasSize(databaseSizeBeforeCreate + 1);
        Barrier testBarrier = barrierList.get(barrierList.size() - 1);
        assertThat(testBarrier.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testBarrier.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testBarrier.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void createBarrierWithExistingId() throws Exception {
        // Create the Barrier with an existing ID
        barrier.setId(1L);
        BarrierDTO barrierDTO = barrierMapper.toDto(barrier);

        int databaseSizeBeforeCreate = barrierRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBarrierMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(barrierDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Barrier in the database
        List<Barrier> barrierList = barrierRepository.findAll();
        assertThat(barrierList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = barrierRepository.findAll().size();
        // set the field null
        barrier.setName(null);

        // Create the Barrier, which fails.
        BarrierDTO barrierDTO = barrierMapper.toDto(barrier);

        restBarrierMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(barrierDTO)))
            .andExpect(status().isBadRequest());

        List<Barrier> barrierList = barrierRepository.findAll();
        assertThat(barrierList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = barrierRepository.findAll().size();
        // set the field null
        barrier.setType(null);

        // Create the Barrier, which fails.
        BarrierDTO barrierDTO = barrierMapper.toDto(barrier);

        restBarrierMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(barrierDTO)))
            .andExpect(status().isBadRequest());

        List<Barrier> barrierList = barrierRepository.findAll();
        assertThat(barrierList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = barrierRepository.findAll().size();
        // set the field null
        barrier.setStatus(null);

        // Create the Barrier, which fails.
        BarrierDTO barrierDTO = barrierMapper.toDto(barrier);

        restBarrierMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(barrierDTO)))
            .andExpect(status().isBadRequest());

        List<Barrier> barrierList = barrierRepository.findAll();
        assertThat(barrierList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllBarriers() throws Exception {
        // Initialize the database
        barrierRepository.saveAndFlush(barrier);

        // Get all the barrierList
        restBarrierMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(barrier.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    void getBarrier() throws Exception {
        // Initialize the database
        barrierRepository.saveAndFlush(barrier);

        // Get the barrier
        restBarrierMockMvc
            .perform(get(ENTITY_API_URL_ID, barrier.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(barrier.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getNonExistingBarrier() throws Exception {
        // Get the barrier
        restBarrierMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingBarrier() throws Exception {
        // Initialize the database
        barrierRepository.saveAndFlush(barrier);

        int databaseSizeBeforeUpdate = barrierRepository.findAll().size();

        // Update the barrier
        Barrier updatedBarrier = barrierRepository.findById(barrier.getId()).get();
        // Disconnect from session so that the updates on updatedBarrier are not directly saved in db
        em.detach(updatedBarrier);
        updatedBarrier.name(UPDATED_NAME).type(UPDATED_TYPE).status(UPDATED_STATUS);
        BarrierDTO barrierDTO = barrierMapper.toDto(updatedBarrier);

        restBarrierMockMvc
            .perform(
                put(ENTITY_API_URL_ID, barrierDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(barrierDTO))
            )
            .andExpect(status().isOk());

        // Validate the Barrier in the database
        List<Barrier> barrierList = barrierRepository.findAll();
        assertThat(barrierList).hasSize(databaseSizeBeforeUpdate);
        Barrier testBarrier = barrierList.get(barrierList.size() - 1);
        assertThat(testBarrier.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testBarrier.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testBarrier.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void putNonExistingBarrier() throws Exception {
        int databaseSizeBeforeUpdate = barrierRepository.findAll().size();
        barrier.setId(count.incrementAndGet());

        // Create the Barrier
        BarrierDTO barrierDTO = barrierMapper.toDto(barrier);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBarrierMockMvc
            .perform(
                put(ENTITY_API_URL_ID, barrierDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(barrierDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Barrier in the database
        List<Barrier> barrierList = barrierRepository.findAll();
        assertThat(barrierList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBarrier() throws Exception {
        int databaseSizeBeforeUpdate = barrierRepository.findAll().size();
        barrier.setId(count.incrementAndGet());

        // Create the Barrier
        BarrierDTO barrierDTO = barrierMapper.toDto(barrier);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBarrierMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(barrierDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Barrier in the database
        List<Barrier> barrierList = barrierRepository.findAll();
        assertThat(barrierList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBarrier() throws Exception {
        int databaseSizeBeforeUpdate = barrierRepository.findAll().size();
        barrier.setId(count.incrementAndGet());

        // Create the Barrier
        BarrierDTO barrierDTO = barrierMapper.toDto(barrier);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBarrierMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(barrierDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Barrier in the database
        List<Barrier> barrierList = barrierRepository.findAll();
        assertThat(barrierList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBarrierWithPatch() throws Exception {
        // Initialize the database
        barrierRepository.saveAndFlush(barrier);

        int databaseSizeBeforeUpdate = barrierRepository.findAll().size();

        // Update the barrier using partial update
        Barrier partialUpdatedBarrier = new Barrier();
        partialUpdatedBarrier.setId(barrier.getId());

        partialUpdatedBarrier.type(UPDATED_TYPE).status(UPDATED_STATUS);

        restBarrierMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBarrier.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBarrier))
            )
            .andExpect(status().isOk());

        // Validate the Barrier in the database
        List<Barrier> barrierList = barrierRepository.findAll();
        assertThat(barrierList).hasSize(databaseSizeBeforeUpdate);
        Barrier testBarrier = barrierList.get(barrierList.size() - 1);
        assertThat(testBarrier.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testBarrier.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testBarrier.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void fullUpdateBarrierWithPatch() throws Exception {
        // Initialize the database
        barrierRepository.saveAndFlush(barrier);

        int databaseSizeBeforeUpdate = barrierRepository.findAll().size();

        // Update the barrier using partial update
        Barrier partialUpdatedBarrier = new Barrier();
        partialUpdatedBarrier.setId(barrier.getId());

        partialUpdatedBarrier.name(UPDATED_NAME).type(UPDATED_TYPE).status(UPDATED_STATUS);

        restBarrierMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBarrier.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBarrier))
            )
            .andExpect(status().isOk());

        // Validate the Barrier in the database
        List<Barrier> barrierList = barrierRepository.findAll();
        assertThat(barrierList).hasSize(databaseSizeBeforeUpdate);
        Barrier testBarrier = barrierList.get(barrierList.size() - 1);
        assertThat(testBarrier.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testBarrier.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testBarrier.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void patchNonExistingBarrier() throws Exception {
        int databaseSizeBeforeUpdate = barrierRepository.findAll().size();
        barrier.setId(count.incrementAndGet());

        // Create the Barrier
        BarrierDTO barrierDTO = barrierMapper.toDto(barrier);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBarrierMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, barrierDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(barrierDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Barrier in the database
        List<Barrier> barrierList = barrierRepository.findAll();
        assertThat(barrierList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBarrier() throws Exception {
        int databaseSizeBeforeUpdate = barrierRepository.findAll().size();
        barrier.setId(count.incrementAndGet());

        // Create the Barrier
        BarrierDTO barrierDTO = barrierMapper.toDto(barrier);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBarrierMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(barrierDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Barrier in the database
        List<Barrier> barrierList = barrierRepository.findAll();
        assertThat(barrierList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBarrier() throws Exception {
        int databaseSizeBeforeUpdate = barrierRepository.findAll().size();
        barrier.setId(count.incrementAndGet());

        // Create the Barrier
        BarrierDTO barrierDTO = barrierMapper.toDto(barrier);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBarrierMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(barrierDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Barrier in the database
        List<Barrier> barrierList = barrierRepository.findAll();
        assertThat(barrierList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBarrier() throws Exception {
        // Initialize the database
        barrierRepository.saveAndFlush(barrier);

        int databaseSizeBeforeDelete = barrierRepository.findAll().size();

        // Delete the barrier
        restBarrierMockMvc
            .perform(delete(ENTITY_API_URL_ID, barrier.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Barrier> barrierList = barrierRepository.findAll();
        assertThat(barrierList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
