package co.edu.icesi.service;

import co.edu.icesi.service.dto.TicketDTO;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link co.edu.icesi.domain.Ticket}.
 */
public interface TicketService {
    /**
     * Save a ticket.
     *
     * @param ticketDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<TicketDTO> save(TicketDTO ticketDTO);

    /**
     * Updates a ticket.
     *
     * @param ticketDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<TicketDTO> update(TicketDTO ticketDTO);

    /**
     * Partially updates a ticket.
     *
     * @param ticketDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<TicketDTO> partialUpdate(TicketDTO ticketDTO);

    /**
     * Get all the tickets.
     *
     * @return the list of entities.
     */
    Flux<TicketDTO> findAll();

    /**
     * Returns the number of tickets available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of tickets available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" ticket.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<TicketDTO> findOne(Long id);

    /**
     * Delete the "id" ticket.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the ticket corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    Flux<TicketDTO> search(String query);
}
