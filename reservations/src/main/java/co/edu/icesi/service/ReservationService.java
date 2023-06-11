package co.edu.icesi.service;

import co.edu.icesi.service.dto.ReservationDTO;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link co.edu.icesi.domain.Reservation}.
 */
public interface ReservationService {
    /**
     * Save a reservation.
     *
     * @param reservationDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<ReservationDTO> save(ReservationDTO reservationDTO);

    /**
     * Updates a reservation.
     *
     * @param reservationDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<ReservationDTO> update(ReservationDTO reservationDTO);

    /**
     * Partially updates a reservation.
     *
     * @param reservationDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<ReservationDTO> partialUpdate(ReservationDTO reservationDTO);

    /**
     * Get all the reservations.
     *
     * @return the list of entities.
     */
    Flux<ReservationDTO> findAll();

    /**
     * Returns the number of reservations available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" reservation.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<ReservationDTO> findOne(Long id);

    /**
     * Delete the "id" reservation.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
