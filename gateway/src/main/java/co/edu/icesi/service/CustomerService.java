package co.edu.icesi.service;

import co.edu.icesi.service.dto.CustomerDTO;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link co.edu.icesi.domain.Customer}.
 */
public interface CustomerService {
    /**
     * Save a customer.
     *
     * @param customerDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<CustomerDTO> save(CustomerDTO customerDTO);

    /**
     * Updates a customer.
     *
     * @param customerDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<CustomerDTO> update(CustomerDTO customerDTO);

    /**
     * Partially updates a customer.
     *
     * @param customerDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<CustomerDTO> partialUpdate(CustomerDTO customerDTO);

    /**
     * Get all the customers.
     *
     * @return the list of entities.
     */
    Flux<CustomerDTO> findAll();

    /**
     * Returns the number of customers available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of customers available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" customer.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<CustomerDTO> findOne(Long id);

    /**
     * Delete the "id" customer.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the customer corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    Flux<CustomerDTO> search(String query);
}
