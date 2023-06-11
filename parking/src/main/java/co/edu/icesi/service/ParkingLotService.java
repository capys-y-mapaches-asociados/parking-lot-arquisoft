package co.edu.icesi.service;

import co.edu.icesi.service.dto.ParkingLotDTO;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link co.edu.icesi.domain.ParkingLot}.
 */
public interface ParkingLotService {
    /**
     * Save a parkingLot.
     *
     * @param parkingLotDTO the entity to save.
     * @return the persisted entity.
     */
    ParkingLotDTO save(ParkingLotDTO parkingLotDTO);

    /**
     * Updates a parkingLot.
     *
     * @param parkingLotDTO the entity to update.
     * @return the persisted entity.
     */
    ParkingLotDTO update(ParkingLotDTO parkingLotDTO);

    /**
     * Partially updates a parkingLot.
     *
     * @param parkingLotDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ParkingLotDTO> partialUpdate(ParkingLotDTO parkingLotDTO);

    /**
     * Get all the parkingLots.
     *
     * @return the list of entities.
     */
    List<ParkingLotDTO> findAll();

    /**
     * Get the "id" parkingLot.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ParkingLotDTO> findOne(Long id);

    /**
     * Delete the "id" parkingLot.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
