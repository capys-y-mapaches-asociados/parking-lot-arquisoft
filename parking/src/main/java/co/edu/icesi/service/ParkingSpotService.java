package co.edu.icesi.service;

import co.edu.icesi.service.dto.ParkingSpotDTO;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link co.edu.icesi.domain.ParkingSpot}.
 */
public interface ParkingSpotService {
    /**
     * Save a parkingSpot.
     *
     * @param parkingSpotDTO the entity to save.
     * @return the persisted entity.
     */
    ParkingSpotDTO save(ParkingSpotDTO parkingSpotDTO);

    /**
     * Updates a parkingSpot.
     *
     * @param parkingSpotDTO the entity to update.
     * @return the persisted entity.
     */
    ParkingSpotDTO update(ParkingSpotDTO parkingSpotDTO);

    /**
     * Partially updates a parkingSpot.
     *
     * @param parkingSpotDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ParkingSpotDTO> partialUpdate(ParkingSpotDTO parkingSpotDTO);

    /**
     * Get all the parkingSpots.
     *
     * @return the list of entities.
     */
    List<ParkingSpotDTO> findAll();

    /**
     * Get the "id" parkingSpot.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ParkingSpotDTO> findOne(Long id);

    /**
     * Delete the "id" parkingSpot.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
