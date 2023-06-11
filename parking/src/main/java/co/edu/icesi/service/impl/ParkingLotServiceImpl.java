package co.edu.icesi.service.impl;

import co.edu.icesi.domain.ParkingLot;
import co.edu.icesi.repository.ParkingLotRepository;
import co.edu.icesi.service.ParkingLotService;
import co.edu.icesi.service.dto.ParkingLotDTO;
import co.edu.icesi.service.mapper.ParkingLotMapper;
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
 * Service Implementation for managing {@link ParkingLot}.
 */
@Service
@Transactional
public class ParkingLotServiceImpl implements ParkingLotService {

    private final Logger log = LoggerFactory.getLogger(ParkingLotServiceImpl.class);

    private final ParkingLotRepository parkingLotRepository;

    private final ParkingLotMapper parkingLotMapper;

    public ParkingLotServiceImpl(ParkingLotRepository parkingLotRepository, ParkingLotMapper parkingLotMapper) {
        this.parkingLotRepository = parkingLotRepository;
        this.parkingLotMapper = parkingLotMapper;
    }

    @Override
    public Mono<ParkingLotDTO> save(ParkingLotDTO parkingLotDTO) {
        log.debug("Request to save ParkingLot : {}", parkingLotDTO);
        return parkingLotRepository.save(parkingLotMapper.toEntity(parkingLotDTO)).map(parkingLotMapper::toDto);
    }

    @Override
    public Mono<ParkingLotDTO> update(ParkingLotDTO parkingLotDTO) {
        log.debug("Request to update ParkingLot : {}", parkingLotDTO);
        return parkingLotRepository.save(parkingLotMapper.toEntity(parkingLotDTO)).map(parkingLotMapper::toDto);
    }

    @Override
    public Mono<ParkingLotDTO> partialUpdate(ParkingLotDTO parkingLotDTO) {
        log.debug("Request to partially update ParkingLot : {}", parkingLotDTO);

        return parkingLotRepository
            .findById(parkingLotDTO.getId())
            .map(existingParkingLot -> {
                parkingLotMapper.partialUpdate(existingParkingLot, parkingLotDTO);

                return existingParkingLot;
            })
            .flatMap(parkingLotRepository::save)
            .map(parkingLotMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ParkingLotDTO> findAll() {
        log.debug("Request to get all ParkingLots");
        return parkingLotRepository.findAll().map(parkingLotMapper::toDto);
    }

    public Mono<Long> countAll() {
        return parkingLotRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<ParkingLotDTO> findOne(Long id) {
        log.debug("Request to get ParkingLot : {}", id);
        return parkingLotRepository.findById(id).map(parkingLotMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete ParkingLot : {}", id);
        return parkingLotRepository.deleteById(id);
    }
}
