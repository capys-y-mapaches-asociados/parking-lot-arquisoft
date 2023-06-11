package co.edu.icesi.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import co.edu.icesi.IntegrationTest;
import co.edu.icesi.domain.ParkingLot;
import co.edu.icesi.domain.ParkingSpot;
import co.edu.icesi.domain.enumeration.ParkingSpotStatus;
import co.edu.icesi.domain.enumeration.ParkingSpotType;
import co.edu.icesi.domain.enumeration.ParkingSpotVehicle;
import co.edu.icesi.repository.ParkingSpotRepository;
import co.edu.icesi.service.dto.ParkingSpotDTO;
import co.edu.icesi.service.mapper.ParkingSpotMapper;
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
 * Integration tests for the {@link ParkingSpotResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ParkingSpotResourceIT {

    private static final Integer DEFAULT_NUMBER = 13000;
    private static final Integer UPDATED_NUMBER = 12999;

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
    private MockMvc restParkingSpotMockMvc;

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
        if (TestUtil.findAll(em, ParkingLot.class).isEmpty()) {
            parkingLot = ParkingLotResourceIT.createEntity(em);
            em.persist(parkingLot);
            em.flush();
        } else {
            parkingLot = TestUtil.findAll(em, ParkingLot.class).get(0);
        }
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
        if (TestUtil.findAll(em, ParkingLot.class).isEmpty()) {
            parkingLot = ParkingLotResourceIT.createUpdatedEntity(em);
            em.persist(parkingLot);
            em.flush();
        } else {
            parkingLot = TestUtil.findAll(em, ParkingLot.class).get(0);
        }
        parkingSpot.setParkingLotId(parkingLot);
        return parkingSpot;
    }

    @BeforeEach
    public void initTest() {
        parkingSpot = createEntity(em);
    }

    @Test
    @Transactional
    void createParkingSpot() throws Exception {
        int databaseSizeBeforeCreate = parkingSpotRepository.findAll().size();
        // Create the ParkingSpot
        ParkingSpotDTO parkingSpotDTO = parkingSpotMapper.toDto(parkingSpot);
        restParkingSpotMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(parkingSpotDTO))
            )
            .andExpect(status().isCreated());

        // Validate the ParkingSpot in the database
        List<ParkingSpot> parkingSpotList = parkingSpotRepository.findAll();
        assertThat(parkingSpotList).hasSize(databaseSizeBeforeCreate + 1);
        ParkingSpot testParkingSpot = parkingSpotList.get(parkingSpotList.size() - 1);
        assertThat(testParkingSpot.getNumber()).isEqualTo(DEFAULT_NUMBER);
        assertThat(testParkingSpot.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testParkingSpot.getSpotType()).isEqualTo(DEFAULT_SPOT_TYPE);
        assertThat(testParkingSpot.getSpotVehicle()).isEqualTo(DEFAULT_SPOT_VEHICLE);
    }

    @Test
    @Transactional
    void createParkingSpotWithExistingId() throws Exception {
        // Create the ParkingSpot with an existing ID
        parkingSpot.setId(1L);
        ParkingSpotDTO parkingSpotDTO = parkingSpotMapper.toDto(parkingSpot);

        int databaseSizeBeforeCreate = parkingSpotRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restParkingSpotMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(parkingSpotDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ParkingSpot in the database
        List<ParkingSpot> parkingSpotList = parkingSpotRepository.findAll();
        assertThat(parkingSpotList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNumberIsRequired() throws Exception {
        int databaseSizeBeforeTest = parkingSpotRepository.findAll().size();
        // set the field null
        parkingSpot.setNumber(null);

        // Create the ParkingSpot, which fails.
        ParkingSpotDTO parkingSpotDTO = parkingSpotMapper.toDto(parkingSpot);

        restParkingSpotMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(parkingSpotDTO))
            )
            .andExpect(status().isBadRequest());

        List<ParkingSpot> parkingSpotList = parkingSpotRepository.findAll();
        assertThat(parkingSpotList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = parkingSpotRepository.findAll().size();
        // set the field null
        parkingSpot.setStatus(null);

        // Create the ParkingSpot, which fails.
        ParkingSpotDTO parkingSpotDTO = parkingSpotMapper.toDto(parkingSpot);

        restParkingSpotMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(parkingSpotDTO))
            )
            .andExpect(status().isBadRequest());

        List<ParkingSpot> parkingSpotList = parkingSpotRepository.findAll();
        assertThat(parkingSpotList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSpotTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = parkingSpotRepository.findAll().size();
        // set the field null
        parkingSpot.setSpotType(null);

        // Create the ParkingSpot, which fails.
        ParkingSpotDTO parkingSpotDTO = parkingSpotMapper.toDto(parkingSpot);

        restParkingSpotMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(parkingSpotDTO))
            )
            .andExpect(status().isBadRequest());

        List<ParkingSpot> parkingSpotList = parkingSpotRepository.findAll();
        assertThat(parkingSpotList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSpotVehicleIsRequired() throws Exception {
        int databaseSizeBeforeTest = parkingSpotRepository.findAll().size();
        // set the field null
        parkingSpot.setSpotVehicle(null);

        // Create the ParkingSpot, which fails.
        ParkingSpotDTO parkingSpotDTO = parkingSpotMapper.toDto(parkingSpot);

        restParkingSpotMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(parkingSpotDTO))
            )
            .andExpect(status().isBadRequest());

        List<ParkingSpot> parkingSpotList = parkingSpotRepository.findAll();
        assertThat(parkingSpotList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllParkingSpots() throws Exception {
        // Initialize the database
        parkingSpotRepository.saveAndFlush(parkingSpot);

        // Get all the parkingSpotList
        restParkingSpotMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(parkingSpot.getId().intValue())))
            .andExpect(jsonPath("$.[*].number").value(hasItem(DEFAULT_NUMBER)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].spotType").value(hasItem(DEFAULT_SPOT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].spotVehicle").value(hasItem(DEFAULT_SPOT_VEHICLE.toString())));
    }

    @Test
    @Transactional
    void getParkingSpot() throws Exception {
        // Initialize the database
        parkingSpotRepository.saveAndFlush(parkingSpot);

        // Get the parkingSpot
        restParkingSpotMockMvc
            .perform(get(ENTITY_API_URL_ID, parkingSpot.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(parkingSpot.getId().intValue()))
            .andExpect(jsonPath("$.number").value(DEFAULT_NUMBER))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.spotType").value(DEFAULT_SPOT_TYPE.toString()))
            .andExpect(jsonPath("$.spotVehicle").value(DEFAULT_SPOT_VEHICLE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingParkingSpot() throws Exception {
        // Get the parkingSpot
        restParkingSpotMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingParkingSpot() throws Exception {
        // Initialize the database
        parkingSpotRepository.saveAndFlush(parkingSpot);

        int databaseSizeBeforeUpdate = parkingSpotRepository.findAll().size();

        // Update the parkingSpot
        ParkingSpot updatedParkingSpot = parkingSpotRepository.findById(parkingSpot.getId()).get();
        // Disconnect from session so that the updates on updatedParkingSpot are not directly saved in db
        em.detach(updatedParkingSpot);
        updatedParkingSpot.number(UPDATED_NUMBER).status(UPDATED_STATUS).spotType(UPDATED_SPOT_TYPE).spotVehicle(UPDATED_SPOT_VEHICLE);
        ParkingSpotDTO parkingSpotDTO = parkingSpotMapper.toDto(updatedParkingSpot);

        restParkingSpotMockMvc
            .perform(
                put(ENTITY_API_URL_ID, parkingSpotDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(parkingSpotDTO))
            )
            .andExpect(status().isOk());

        // Validate the ParkingSpot in the database
        List<ParkingSpot> parkingSpotList = parkingSpotRepository.findAll();
        assertThat(parkingSpotList).hasSize(databaseSizeBeforeUpdate);
        ParkingSpot testParkingSpot = parkingSpotList.get(parkingSpotList.size() - 1);
        assertThat(testParkingSpot.getNumber()).isEqualTo(UPDATED_NUMBER);
        assertThat(testParkingSpot.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testParkingSpot.getSpotType()).isEqualTo(UPDATED_SPOT_TYPE);
        assertThat(testParkingSpot.getSpotVehicle()).isEqualTo(UPDATED_SPOT_VEHICLE);
    }

    @Test
    @Transactional
    void putNonExistingParkingSpot() throws Exception {
        int databaseSizeBeforeUpdate = parkingSpotRepository.findAll().size();
        parkingSpot.setId(count.incrementAndGet());

        // Create the ParkingSpot
        ParkingSpotDTO parkingSpotDTO = parkingSpotMapper.toDto(parkingSpot);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restParkingSpotMockMvc
            .perform(
                put(ENTITY_API_URL_ID, parkingSpotDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(parkingSpotDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ParkingSpot in the database
        List<ParkingSpot> parkingSpotList = parkingSpotRepository.findAll();
        assertThat(parkingSpotList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchParkingSpot() throws Exception {
        int databaseSizeBeforeUpdate = parkingSpotRepository.findAll().size();
        parkingSpot.setId(count.incrementAndGet());

        // Create the ParkingSpot
        ParkingSpotDTO parkingSpotDTO = parkingSpotMapper.toDto(parkingSpot);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParkingSpotMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(parkingSpotDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ParkingSpot in the database
        List<ParkingSpot> parkingSpotList = parkingSpotRepository.findAll();
        assertThat(parkingSpotList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamParkingSpot() throws Exception {
        int databaseSizeBeforeUpdate = parkingSpotRepository.findAll().size();
        parkingSpot.setId(count.incrementAndGet());

        // Create the ParkingSpot
        ParkingSpotDTO parkingSpotDTO = parkingSpotMapper.toDto(parkingSpot);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParkingSpotMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(parkingSpotDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ParkingSpot in the database
        List<ParkingSpot> parkingSpotList = parkingSpotRepository.findAll();
        assertThat(parkingSpotList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateParkingSpotWithPatch() throws Exception {
        // Initialize the database
        parkingSpotRepository.saveAndFlush(parkingSpot);

        int databaseSizeBeforeUpdate = parkingSpotRepository.findAll().size();

        // Update the parkingSpot using partial update
        ParkingSpot partialUpdatedParkingSpot = new ParkingSpot();
        partialUpdatedParkingSpot.setId(parkingSpot.getId());

        partialUpdatedParkingSpot.number(UPDATED_NUMBER).spotType(UPDATED_SPOT_TYPE).spotVehicle(UPDATED_SPOT_VEHICLE);

        restParkingSpotMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedParkingSpot.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedParkingSpot))
            )
            .andExpect(status().isOk());

        // Validate the ParkingSpot in the database
        List<ParkingSpot> parkingSpotList = parkingSpotRepository.findAll();
        assertThat(parkingSpotList).hasSize(databaseSizeBeforeUpdate);
        ParkingSpot testParkingSpot = parkingSpotList.get(parkingSpotList.size() - 1);
        assertThat(testParkingSpot.getNumber()).isEqualTo(UPDATED_NUMBER);
        assertThat(testParkingSpot.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testParkingSpot.getSpotType()).isEqualTo(UPDATED_SPOT_TYPE);
        assertThat(testParkingSpot.getSpotVehicle()).isEqualTo(UPDATED_SPOT_VEHICLE);
    }

    @Test
    @Transactional
    void fullUpdateParkingSpotWithPatch() throws Exception {
        // Initialize the database
        parkingSpotRepository.saveAndFlush(parkingSpot);

        int databaseSizeBeforeUpdate = parkingSpotRepository.findAll().size();

        // Update the parkingSpot using partial update
        ParkingSpot partialUpdatedParkingSpot = new ParkingSpot();
        partialUpdatedParkingSpot.setId(parkingSpot.getId());

        partialUpdatedParkingSpot
            .number(UPDATED_NUMBER)
            .status(UPDATED_STATUS)
            .spotType(UPDATED_SPOT_TYPE)
            .spotVehicle(UPDATED_SPOT_VEHICLE);

        restParkingSpotMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedParkingSpot.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedParkingSpot))
            )
            .andExpect(status().isOk());

        // Validate the ParkingSpot in the database
        List<ParkingSpot> parkingSpotList = parkingSpotRepository.findAll();
        assertThat(parkingSpotList).hasSize(databaseSizeBeforeUpdate);
        ParkingSpot testParkingSpot = parkingSpotList.get(parkingSpotList.size() - 1);
        assertThat(testParkingSpot.getNumber()).isEqualTo(UPDATED_NUMBER);
        assertThat(testParkingSpot.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testParkingSpot.getSpotType()).isEqualTo(UPDATED_SPOT_TYPE);
        assertThat(testParkingSpot.getSpotVehicle()).isEqualTo(UPDATED_SPOT_VEHICLE);
    }

    @Test
    @Transactional
    void patchNonExistingParkingSpot() throws Exception {
        int databaseSizeBeforeUpdate = parkingSpotRepository.findAll().size();
        parkingSpot.setId(count.incrementAndGet());

        // Create the ParkingSpot
        ParkingSpotDTO parkingSpotDTO = parkingSpotMapper.toDto(parkingSpot);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restParkingSpotMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, parkingSpotDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(parkingSpotDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ParkingSpot in the database
        List<ParkingSpot> parkingSpotList = parkingSpotRepository.findAll();
        assertThat(parkingSpotList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchParkingSpot() throws Exception {
        int databaseSizeBeforeUpdate = parkingSpotRepository.findAll().size();
        parkingSpot.setId(count.incrementAndGet());

        // Create the ParkingSpot
        ParkingSpotDTO parkingSpotDTO = parkingSpotMapper.toDto(parkingSpot);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParkingSpotMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(parkingSpotDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ParkingSpot in the database
        List<ParkingSpot> parkingSpotList = parkingSpotRepository.findAll();
        assertThat(parkingSpotList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamParkingSpot() throws Exception {
        int databaseSizeBeforeUpdate = parkingSpotRepository.findAll().size();
        parkingSpot.setId(count.incrementAndGet());

        // Create the ParkingSpot
        ParkingSpotDTO parkingSpotDTO = parkingSpotMapper.toDto(parkingSpot);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParkingSpotMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(parkingSpotDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ParkingSpot in the database
        List<ParkingSpot> parkingSpotList = parkingSpotRepository.findAll();
        assertThat(parkingSpotList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteParkingSpot() throws Exception {
        // Initialize the database
        parkingSpotRepository.saveAndFlush(parkingSpot);

        int databaseSizeBeforeDelete = parkingSpotRepository.findAll().size();

        // Delete the parkingSpot
        restParkingSpotMockMvc
            .perform(delete(ENTITY_API_URL_ID, parkingSpot.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ParkingSpot> parkingSpotList = parkingSpotRepository.findAll();
        assertThat(parkingSpotList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
