package co.edu.icesi.service;

import co.edu.icesi.service.dto.BarrierDTO;
import java.util.List;
import java.util.Optional;

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
    BarrierDTO save(BarrierDTO barrierDTO);

    /**
     * Updates a barrier.
     *
     * @param barrierDTO the entity to update.
     * @return the persisted entity.
     */
    BarrierDTO update(BarrierDTO barrierDTO);

    /**
     * Partially updates a barrier.
     *
     * @param barrierDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<BarrierDTO> partialUpdate(BarrierDTO barrierDTO);

    /**
     * Get all the barriers.
     *
     * @return the list of entities.
     */
    List<BarrierDTO> findAll();

    /**
     * Get the "id" barrier.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<BarrierDTO> findOne(Long id);

    /**
     * Delete the "id" barrier.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
