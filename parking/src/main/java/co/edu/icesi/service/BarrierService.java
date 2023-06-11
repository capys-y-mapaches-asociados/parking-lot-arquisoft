package co.edu.icesi.service;

import co.edu.icesi.service.dto.BarrierDTO;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link co.edu.icesi.domain.Barrier}.
 */
public interface BarrierService {
    /**
     * Save a barrier.
     *
     * @param barrierDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<BarrierDTO> save(BarrierDTO barrierDTO);

    /**
     * Updates a barrier.
     *
     * @param barrierDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<BarrierDTO> update(BarrierDTO barrierDTO);

    /**
     * Partially updates a barrier.
     *
     * @param barrierDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<BarrierDTO> partialUpdate(BarrierDTO barrierDTO);

    /**
     * Get all the barriers.
     *
     * @return the list of entities.
     */
    Flux<BarrierDTO> findAll();

    /**
     * Returns the number of barriers available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" barrier.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<BarrierDTO> findOne(Long id);

    /**
     * Delete the "id" barrier.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
