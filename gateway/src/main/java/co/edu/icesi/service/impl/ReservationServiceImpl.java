package co.edu.icesi.service.impl;

import co.edu.icesi.domain.Reservation;
import co.edu.icesi.repository.ReservationRepository;
import co.edu.icesi.service.ReservationService;
import co.edu.icesi.service.dto.ReservationDTO;
import co.edu.icesi.service.mapper.ReservationMapper;
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
 * Service Implementation for managing {@link Reservation}.
 */
@Service
@Transactional
public class ReservationServiceImpl implements ReservationService {

    private final Logger log = LoggerFactory.getLogger(ReservationServiceImpl.class);

    private final ReservationRepository reservationRepository;

    private final ReservationMapper reservationMapper;

    public ReservationServiceImpl(ReservationRepository reservationRepository, ReservationMapper reservationMapper) {
        this.reservationRepository = reservationRepository;
        this.reservationMapper = reservationMapper;
    }

    @Override
    public Mono<ReservationDTO> save(ReservationDTO reservationDTO) {
        log.debug("Request to save Reservation : {}", reservationDTO);
        return reservationRepository.save(reservationMapper.toEntity(reservationDTO)).map(reservationMapper::toDto);
    }

    @Override
    public Mono<ReservationDTO> update(ReservationDTO reservationDTO) {
        log.debug("Request to update Reservation : {}", reservationDTO);
        return reservationRepository.save(reservationMapper.toEntity(reservationDTO)).map(reservationMapper::toDto);
    }

    @Override
    public Mono<ReservationDTO> partialUpdate(ReservationDTO reservationDTO) {
        log.debug("Request to partially update Reservation : {}", reservationDTO);

        return reservationRepository
            .findById(reservationDTO.getId())
            .map(existingReservation -> {
                reservationMapper.partialUpdate(existingReservation, reservationDTO);

                return existingReservation;
            })
            .flatMap(reservationRepository::save)
            .map(reservationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ReservationDTO> findAll() {
        log.debug("Request to get all Reservations");
        return reservationRepository.findAll().map(reservationMapper::toDto);
    }

    public Mono<Long> countAll() {
        return reservationRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<ReservationDTO> findOne(Long id) {
        log.debug("Request to get Reservation : {}", id);
        return reservationRepository.findById(id).map(reservationMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Reservation : {}", id);
        return reservationRepository.deleteById(id);
    }
}
