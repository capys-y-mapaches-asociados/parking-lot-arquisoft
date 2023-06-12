package co.edu.icesi.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import co.edu.icesi.IntegrationTest;
import co.edu.icesi.domain.Barrier;
import co.edu.icesi.domain.ParkingLot;
import co.edu.icesi.domain.ParkingSpot;
import co.edu.icesi.repository.ParkingLotRepository;
import co.edu.icesi.service.dto.ParkingLotDTO;
import co.edu.icesi.service.mapper.ParkingLotMapper;
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
 * Integration tests for the {@link ParkingLotResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
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
    private MockMvc restParkingLotMockMvc;

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
        if (TestUtil.findAll(em, ParkingSpot.class).isEmpty()) {
            parkingSpot = ParkingSpotResourceIT.createEntity(em);
            em.persist(parkingSpot);
            em.flush();
        } else {
            parkingSpot = TestUtil.findAll(em, ParkingSpot.class).get(0);
        }
        parkingLot.getParkingSpots().add(parkingSpot);
        // Add required entity
        Barrier barrier;
        if (TestUtil.findAll(em, Barrier.class).isEmpty()) {
            barrier = BarrierResourceIT.createEntity(em);
            em.persist(barrier);
            em.flush();
        } else {
            barrier = TestUtil.findAll(em, Barrier.class).get(0);
        }
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
        if (TestUtil.findAll(em, ParkingSpot.class).isEmpty()) {
            parkingSpot = ParkingSpotResourceIT.createUpdatedEntity(em);
            em.persist(parkingSpot);
            em.flush();
        } else {
            parkingSpot = TestUtil.findAll(em, ParkingSpot.class).get(0);
        }
        parkingLot.getParkingSpots().add(parkingSpot);
        // Add required entity
        Barrier barrier;
        if (TestUtil.findAll(em, Barrier.class).isEmpty()) {
            barrier = BarrierResourceIT.createUpdatedEntity(em);
            em.persist(barrier);
            em.flush();
        } else {
            barrier = TestUtil.findAll(em, Barrier.class).get(0);
        }
        parkingLot.getBarriers().add(barrier);
        return parkingLot;
    }

    @BeforeEach
    public void initTest() {
        parkingLot = createEntity(em);
    }

    @Test
    @Transactional
    void createParkingLot() throws Exception {
        int databaseSizeBeforeCreate = parkingLotRepository.findAll().size();
        // Create the ParkingLot
        ParkingLotDTO parkingLotDTO = parkingLotMapper.toDto(parkingLot);
        restParkingLotMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(parkingLotDTO)))
            .andExpect(status().isCreated());

        // Validate the ParkingLot in the database
        List<ParkingLot> parkingLotList = parkingLotRepository.findAll();
        assertThat(parkingLotList).hasSize(databaseSizeBeforeCreate + 1);
        ParkingLot testParkingLot = parkingLotList.get(parkingLotList.size() - 1);
        assertThat(testParkingLot.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testParkingLot.getLocation()).isEqualTo(DEFAULT_LOCATION);
        assertThat(testParkingLot.getCapacity()).isEqualTo(DEFAULT_CAPACITY);
    }

    @Test
    @Transactional
    void createParkingLotWithExistingId() throws Exception {
        // Create the ParkingLot with an existing ID
        parkingLot.setId(1L);
        ParkingLotDTO parkingLotDTO = parkingLotMapper.toDto(parkingLot);

        int databaseSizeBeforeCreate = parkingLotRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restParkingLotMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(parkingLotDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ParkingLot in the database
        List<ParkingLot> parkingLotList = parkingLotRepository.findAll();
        assertThat(parkingLotList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = parkingLotRepository.findAll().size();
        // set the field null
        parkingLot.setName(null);

        // Create the ParkingLot, which fails.
        ParkingLotDTO parkingLotDTO = parkingLotMapper.toDto(parkingLot);

        restParkingLotMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(parkingLotDTO)))
            .andExpect(status().isBadRequest());

        List<ParkingLot> parkingLotList = parkingLotRepository.findAll();
        assertThat(parkingLotList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLocationIsRequired() throws Exception {
        int databaseSizeBeforeTest = parkingLotRepository.findAll().size();
        // set the field null
        parkingLot.setLocation(null);

        // Create the ParkingLot, which fails.
        ParkingLotDTO parkingLotDTO = parkingLotMapper.toDto(parkingLot);

        restParkingLotMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(parkingLotDTO)))
            .andExpect(status().isBadRequest());

        List<ParkingLot> parkingLotList = parkingLotRepository.findAll();
        assertThat(parkingLotList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCapacityIsRequired() throws Exception {
        int databaseSizeBeforeTest = parkingLotRepository.findAll().size();
        // set the field null
        parkingLot.setCapacity(null);

        // Create the ParkingLot, which fails.
        ParkingLotDTO parkingLotDTO = parkingLotMapper.toDto(parkingLot);

        restParkingLotMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(parkingLotDTO)))
            .andExpect(status().isBadRequest());

        List<ParkingLot> parkingLotList = parkingLotRepository.findAll();
        assertThat(parkingLotList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllParkingLots() throws Exception {
        // Initialize the database
        parkingLotRepository.saveAndFlush(parkingLot);

        // Get all the parkingLotList
        restParkingLotMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(parkingLot.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].location").value(hasItem(DEFAULT_LOCATION)))
            .andExpect(jsonPath("$.[*].capacity").value(hasItem(DEFAULT_CAPACITY)));
    }

    @Test
    @Transactional
    void getParkingLot() throws Exception {
        // Initialize the database
        parkingLotRepository.saveAndFlush(parkingLot);

        // Get the parkingLot
        restParkingLotMockMvc
            .perform(get(ENTITY_API_URL_ID, parkingLot.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(parkingLot.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.location").value(DEFAULT_LOCATION))
            .andExpect(jsonPath("$.capacity").value(DEFAULT_CAPACITY));
    }

    @Test
    @Transactional
    void getNonExistingParkingLot() throws Exception {
        // Get the parkingLot
        restParkingLotMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingParkingLot() throws Exception {
        // Initialize the database
        parkingLotRepository.saveAndFlush(parkingLot);

        int databaseSizeBeforeUpdate = parkingLotRepository.findAll().size();

        // Update the parkingLot
        ParkingLot updatedParkingLot = parkingLotRepository.findById(parkingLot.getId()).get();
        // Disconnect from session so that the updates on updatedParkingLot are not directly saved in db
        em.detach(updatedParkingLot);
        updatedParkingLot.name(UPDATED_NAME).location(UPDATED_LOCATION).capacity(UPDATED_CAPACITY);
        ParkingLotDTO parkingLotDTO = parkingLotMapper.toDto(updatedParkingLot);

        restParkingLotMockMvc
            .perform(
                put(ENTITY_API_URL_ID, parkingLotDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(parkingLotDTO))
            )
            .andExpect(status().isOk());

        // Validate the ParkingLot in the database
        List<ParkingLot> parkingLotList = parkingLotRepository.findAll();
        assertThat(parkingLotList).hasSize(databaseSizeBeforeUpdate);
        ParkingLot testParkingLot = parkingLotList.get(parkingLotList.size() - 1);
        assertThat(testParkingLot.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testParkingLot.getLocation()).isEqualTo(UPDATED_LOCATION);
        assertThat(testParkingLot.getCapacity()).isEqualTo(UPDATED_CAPACITY);
    }

    @Test
    @Transactional
    void putNonExistingParkingLot() throws Exception {
        int databaseSizeBeforeUpdate = parkingLotRepository.findAll().size();
        parkingLot.setId(count.incrementAndGet());

        // Create the ParkingLot
        ParkingLotDTO parkingLotDTO = parkingLotMapper.toDto(parkingLot);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restParkingLotMockMvc
            .perform(
                put(ENTITY_API_URL_ID, parkingLotDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(parkingLotDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ParkingLot in the database
        List<ParkingLot> parkingLotList = parkingLotRepository.findAll();
        assertThat(parkingLotList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchParkingLot() throws Exception {
        int databaseSizeBeforeUpdate = parkingLotRepository.findAll().size();
        parkingLot.setId(count.incrementAndGet());

        // Create the ParkingLot
        ParkingLotDTO parkingLotDTO = parkingLotMapper.toDto(parkingLot);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParkingLotMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(parkingLotDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ParkingLot in the database
        List<ParkingLot> parkingLotList = parkingLotRepository.findAll();
        assertThat(parkingLotList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamParkingLot() throws Exception {
        int databaseSizeBeforeUpdate = parkingLotRepository.findAll().size();
        parkingLot.setId(count.incrementAndGet());

        // Create the ParkingLot
        ParkingLotDTO parkingLotDTO = parkingLotMapper.toDto(parkingLot);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParkingLotMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(parkingLotDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ParkingLot in the database
        List<ParkingLot> parkingLotList = parkingLotRepository.findAll();
        assertThat(parkingLotList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateParkingLotWithPatch() throws Exception {
        // Initialize the database
        parkingLotRepository.saveAndFlush(parkingLot);

        int databaseSizeBeforeUpdate = parkingLotRepository.findAll().size();

        // Update the parkingLot using partial update
        ParkingLot partialUpdatedParkingLot = new ParkingLot();
        partialUpdatedParkingLot.setId(parkingLot.getId());

        partialUpdatedParkingLot.name(UPDATED_NAME).location(UPDATED_LOCATION);

        restParkingLotMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedParkingLot.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedParkingLot))
            )
            .andExpect(status().isOk());

        // Validate the ParkingLot in the database
        List<ParkingLot> parkingLotList = parkingLotRepository.findAll();
        assertThat(parkingLotList).hasSize(databaseSizeBeforeUpdate);
        ParkingLot testParkingLot = parkingLotList.get(parkingLotList.size() - 1);
        assertThat(testParkingLot.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testParkingLot.getLocation()).isEqualTo(UPDATED_LOCATION);
        assertThat(testParkingLot.getCapacity()).isEqualTo(DEFAULT_CAPACITY);
    }

    @Test
    @Transactional
    void fullUpdateParkingLotWithPatch() throws Exception {
        // Initialize the database
        parkingLotRepository.saveAndFlush(parkingLot);

        int databaseSizeBeforeUpdate = parkingLotRepository.findAll().size();

        // Update the parkingLot using partial update
        ParkingLot partialUpdatedParkingLot = new ParkingLot();
        partialUpdatedParkingLot.setId(parkingLot.getId());

        partialUpdatedParkingLot.name(UPDATED_NAME).location(UPDATED_LOCATION).capacity(UPDATED_CAPACITY);

        restParkingLotMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedParkingLot.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedParkingLot))
            )
            .andExpect(status().isOk());

        // Validate the ParkingLot in the database
        List<ParkingLot> parkingLotList = parkingLotRepository.findAll();
        assertThat(parkingLotList).hasSize(databaseSizeBeforeUpdate);
        ParkingLot testParkingLot = parkingLotList.get(parkingLotList.size() - 1);
        assertThat(testParkingLot.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testParkingLot.getLocation()).isEqualTo(UPDATED_LOCATION);
        assertThat(testParkingLot.getCapacity()).isEqualTo(UPDATED_CAPACITY);
    }

    @Test
    @Transactional
    void patchNonExistingParkingLot() throws Exception {
        int databaseSizeBeforeUpdate = parkingLotRepository.findAll().size();
        parkingLot.setId(count.incrementAndGet());

        // Create the ParkingLot
        ParkingLotDTO parkingLotDTO = parkingLotMapper.toDto(parkingLot);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restParkingLotMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, parkingLotDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(parkingLotDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ParkingLot in the database
        List<ParkingLot> parkingLotList = parkingLotRepository.findAll();
        assertThat(parkingLotList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchParkingLot() throws Exception {
        int databaseSizeBeforeUpdate = parkingLotRepository.findAll().size();
        parkingLot.setId(count.incrementAndGet());

        // Create the ParkingLot
        ParkingLotDTO parkingLotDTO = parkingLotMapper.toDto(parkingLot);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParkingLotMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(parkingLotDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ParkingLot in the database
        List<ParkingLot> parkingLotList = parkingLotRepository.findAll();
        assertThat(parkingLotList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamParkingLot() throws Exception {
        int databaseSizeBeforeUpdate = parkingLotRepository.findAll().size();
        parkingLot.setId(count.incrementAndGet());

        // Create the ParkingLot
        ParkingLotDTO parkingLotDTO = parkingLotMapper.toDto(parkingLot);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParkingLotMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(parkingLotDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ParkingLot in the database
        List<ParkingLot> parkingLotList = parkingLotRepository.findAll();
        assertThat(parkingLotList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteParkingLot() throws Exception {
        // Initialize the database
        parkingLotRepository.saveAndFlush(parkingLot);

        int databaseSizeBeforeDelete = parkingLotRepository.findAll().size();

        // Delete the parkingLot
        restParkingLotMockMvc
            .perform(delete(ENTITY_API_URL_ID, parkingLot.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ParkingLot> parkingLotList = parkingLotRepository.findAll();
        assertThat(parkingLotList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
