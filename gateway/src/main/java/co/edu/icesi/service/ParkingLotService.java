package co.edu.icesi.service;

import co.edu.icesi.service.dto.ParkingLotDTO;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
    Mono<ParkingLotDTO> save(ParkingLotDTO parkingLotDTO);

    /**
     * Updates a parkingLot.
     *
     * @param parkingLotDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<ParkingLotDTO> update(ParkingLotDTO parkingLotDTO);

    /**
     * Partially updates a parkingLot.
     *
     * @param parkingLotDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<ParkingLotDTO> partialUpdate(ParkingLotDTO parkingLotDTO);

    /**
     * Get all the parkingLots.
     *
     * @return the list of entities.
     */
    Flux<ParkingLotDTO> findAll();

    /**
     * Returns the number of parkingLots available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of parkingLots available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" parkingLot.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<ParkingLotDTO> findOne(Long id);

    /**
     * Delete the "id" parkingLot.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the parkingLot corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    Flux<ParkingLotDTO> search(String query);
}
