package co.edu.icesi.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import co.edu.icesi.domain.ParkingSpot;
import co.edu.icesi.repository.ParkingSpotRepository;
import co.edu.icesi.repository.search.ParkingSpotSearchRepository;
import co.edu.icesi.service.ParkingSpotService;
import co.edu.icesi.service.dto.ParkingSpotDTO;
import co.edu.icesi.service.mapper.ParkingSpotMapper;
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
 * Service Implementation for managing {@link ParkingSpot}.
 */
@Service
@Transactional
public class ParkingSpotServiceImpl implements ParkingSpotService {

    private final Logger log = LoggerFactory.getLogger(ParkingSpotServiceImpl.class);

    private final ParkingSpotRepository parkingSpotRepository;

    private final ParkingSpotMapper parkingSpotMapper;

    private final ParkingSpotSearchRepository parkingSpotSearchRepository;

    public ParkingSpotServiceImpl(
        ParkingSpotRepository parkingSpotRepository,
        ParkingSpotMapper parkingSpotMapper,
        ParkingSpotSearchRepository parkingSpotSearchRepository
    ) {
        this.parkingSpotRepository = parkingSpotRepository;
        this.parkingSpotMapper = parkingSpotMapper;
        this.parkingSpotSearchRepository = parkingSpotSearchRepository;
    }

    @Override
    public Mono<ParkingSpotDTO> save(ParkingSpotDTO parkingSpotDTO) {
        log.debug("Request to save ParkingSpot : {}", parkingSpotDTO);
        return parkingSpotRepository
            .save(parkingSpotMapper.toEntity(parkingSpotDTO))
            .flatMap(parkingSpotSearchRepository::save)
            .map(parkingSpotMapper::toDto);
    }

    @Override
    public Mono<ParkingSpotDTO> update(ParkingSpotDTO parkingSpotDTO) {
        log.debug("Request to update ParkingSpot : {}", parkingSpotDTO);
        return parkingSpotRepository
            .save(parkingSpotMapper.toEntity(parkingSpotDTO))
            .flatMap(parkingSpotSearchRepository::save)
            .map(parkingSpotMapper::toDto);
    }

    @Override
    public Mono<ParkingSpotDTO> partialUpdate(ParkingSpotDTO parkingSpotDTO) {
        log.debug("Request to partially update ParkingSpot : {}", parkingSpotDTO);

        return parkingSpotRepository
            .findById(parkingSpotDTO.getId())
            .map(existingParkingSpot -> {
                parkingSpotMapper.partialUpdate(existingParkingSpot, parkingSpotDTO);

                return existingParkingSpot;
            })
            .flatMap(parkingSpotRepository::save)
            .flatMap(savedParkingSpot -> {
                parkingSpotSearchRepository.save(savedParkingSpot);

                return Mono.just(savedParkingSpot);
            })
            .map(parkingSpotMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ParkingSpotDTO> findAll() {
        log.debug("Request to get all ParkingSpots");
        return parkingSpotRepository.findAll().map(parkingSpotMapper::toDto);
    }

    public Mono<Long> countAll() {
        return parkingSpotRepository.count();
    }

    public Mono<Long> searchCount() {
        return parkingSpotSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<ParkingSpotDTO> findOne(Long id) {
        log.debug("Request to get ParkingSpot : {}", id);
        return parkingSpotRepository.findById(id).map(parkingSpotMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete ParkingSpot : {}", id);
        return parkingSpotRepository.deleteById(id).then(parkingSpotSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ParkingSpotDTO> search(String query) {
        log.debug("Request to search ParkingSpots for query {}", query);
        return parkingSpotSearchRepository.search(query).map(parkingSpotMapper::toDto);
    }
}
