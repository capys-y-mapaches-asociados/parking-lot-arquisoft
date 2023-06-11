package co.edu.icesi.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import co.edu.icesi.domain.Ticket;
import co.edu.icesi.repository.TicketRepository;
import co.edu.icesi.repository.search.TicketSearchRepository;
import co.edu.icesi.service.TicketService;
import co.edu.icesi.service.dto.TicketDTO;
import co.edu.icesi.service.mapper.TicketMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Ticket}.
 */
@Service
@Transactional
public class TicketServiceImpl implements TicketService {

    private final Logger log = LoggerFactory.getLogger(TicketServiceImpl.class);

    private final TicketRepository ticketRepository;

    private final TicketMapper ticketMapper;

    private final TicketSearchRepository ticketSearchRepository;

    public TicketServiceImpl(TicketRepository ticketRepository, TicketMapper ticketMapper, TicketSearchRepository ticketSearchRepository) {
        this.ticketRepository = ticketRepository;
        this.ticketMapper = ticketMapper;
        this.ticketSearchRepository = ticketSearchRepository;
    }

    @Override
    public Mono<TicketDTO> save(TicketDTO ticketDTO) {
        log.debug("Request to save Ticket : {}", ticketDTO);
        return ticketRepository.save(ticketMapper.toEntity(ticketDTO)).flatMap(ticketSearchRepository::save).map(ticketMapper::toDto);
    }

    @Override
    public Mono<TicketDTO> update(TicketDTO ticketDTO) {
        log.debug("Request to update Ticket : {}", ticketDTO);
        return ticketRepository.save(ticketMapper.toEntity(ticketDTO)).flatMap(ticketSearchRepository::save).map(ticketMapper::toDto);
    }

    @Override
    public Mono<TicketDTO> partialUpdate(TicketDTO ticketDTO) {
        log.debug("Request to partially update Ticket : {}", ticketDTO);

        return ticketRepository
            .findById(ticketDTO.getId())
            .map(existingTicket -> {
                ticketMapper.partialUpdate(existingTicket, ticketDTO);

                return existingTicket;
            })
            .flatMap(ticketRepository::save)
            .flatMap(savedTicket -> {
                ticketSearchRepository.save(savedTicket);

                return Mono.just(savedTicket);
            })
            .map(ticketMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TicketDTO> findAll() {
        log.debug("Request to get all Tickets");
        return ticketRepository.findAll().map(ticketMapper::toDto);
    }

    public Mono<Long> countAll() {
        return ticketRepository.count();
    }

    public Mono<Long> searchCount() {
        return ticketSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<TicketDTO> findOne(Long id) {
        log.debug("Request to get Ticket : {}", id);
        return ticketRepository.findById(id).map(ticketMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Ticket : {}", id);
        return ticketRepository.deleteById(id).then(ticketSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TicketDTO> search(String query) {
        log.debug("Request to search Tickets for query {}", query);
        return ticketSearchRepository.search(query).map(ticketMapper::toDto);
    }
}
