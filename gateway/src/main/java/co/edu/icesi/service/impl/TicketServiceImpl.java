package co.edu.icesi.service.impl;

import co.edu.icesi.domain.Ticket;
import co.edu.icesi.repository.TicketRepository;
import co.edu.icesi.service.TicketService;
import co.edu.icesi.service.dto.TicketDTO;
import co.edu.icesi.service.mapper.TicketMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
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

    public TicketServiceImpl(TicketRepository ticketRepository, TicketMapper ticketMapper) {
        this.ticketRepository = ticketRepository;
        this.ticketMapper = ticketMapper;
    }

    @Override
    public Mono<TicketDTO> save(TicketDTO ticketDTO) {
        log.debug("Request to save Ticket : {}", ticketDTO);
        return ticketRepository.save(ticketMapper.toEntity(ticketDTO)).map(ticketMapper::toDto);
    }

    @Override
    public Mono<TicketDTO> update(TicketDTO ticketDTO) {
        log.debug("Request to update Ticket : {}", ticketDTO);
        return ticketRepository.save(ticketMapper.toEntity(ticketDTO)).map(ticketMapper::toDto);
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

    @Override
    @Transactional(readOnly = true)
    public Mono<TicketDTO> findOne(Long id) {
        log.debug("Request to get Ticket : {}", id);
        return ticketRepository.findById(id).map(ticketMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Ticket : {}", id);
        return ticketRepository.deleteById(id);
    }
}
