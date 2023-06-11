package co.edu.icesi.service;

import co.edu.icesi.service.dto.ParkingSpotDTO;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
    Mono<ParkingSpotDTO> save(ParkingSpotDTO parkingSpotDTO);

    /**
     * Updates a parkingSpot.
     *
     * @param parkingSpotDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<ParkingSpotDTO> update(ParkingSpotDTO parkingSpotDTO);

    /**
     * Partially updates a parkingSpot.
     *
     * @param parkingSpotDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<ParkingSpotDTO> partialUpdate(ParkingSpotDTO parkingSpotDTO);

    /**
     * Get all the parkingSpots.
     *
     * @return the list of entities.
     */
    Flux<ParkingSpotDTO> findAll();

    /**
     * Returns the number of parkingSpots available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" parkingSpot.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<ParkingSpotDTO> findOne(Long id);

    /**
     * Delete the "id" parkingSpot.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
