package co.edu.icesi.service;

import co.edu.icesi.service.dto.TicketDTO;
import java.util.List;
import java.util.Optional;

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
    TicketDTO save(TicketDTO ticketDTO);

    /**
     * Updates a ticket.
     *
     * @param ticketDTO the entity to update.
     * @return the persisted entity.
     */
    TicketDTO update(TicketDTO ticketDTO);

    /**
     * Partially updates a ticket.
     *
     * @param ticketDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<TicketDTO> partialUpdate(TicketDTO ticketDTO);

    /**
     * Get all the tickets.
     *
     * @return the list of entities.
     */
    List<TicketDTO> findAll();

    /**
     * Get the "id" ticket.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<TicketDTO> findOne(Long id);

    /**
     * Delete the "id" ticket.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
