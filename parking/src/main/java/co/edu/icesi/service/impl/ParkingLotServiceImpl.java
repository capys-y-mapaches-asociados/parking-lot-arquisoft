package co.edu.icesi.service.impl;

import co.edu.icesi.domain.ParkingLot;
import co.edu.icesi.repository.ParkingLotRepository;
import co.edu.icesi.service.ParkingLotService;
import co.edu.icesi.service.dto.ParkingLotDTO;
import co.edu.icesi.service.mapper.ParkingLotMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public ParkingLotDTO save(ParkingLotDTO parkingLotDTO) {
        log.debug("Request to save ParkingLot : {}", parkingLotDTO);
        ParkingLot parkingLot = parkingLotMapper.toEntity(parkingLotDTO);
        parkingLot = parkingLotRepository.save(parkingLot);
        return parkingLotMapper.toDto(parkingLot);
    }

    @Override
    public ParkingLotDTO update(ParkingLotDTO parkingLotDTO) {
        log.debug("Request to update ParkingLot : {}", parkingLotDTO);
        ParkingLot parkingLot = parkingLotMapper.toEntity(parkingLotDTO);
        parkingLot = parkingLotRepository.save(parkingLot);
        return parkingLotMapper.toDto(parkingLot);
    }

    @Override
    public Optional<ParkingLotDTO> partialUpdate(ParkingLotDTO parkingLotDTO) {
        log.debug("Request to partially update ParkingLot : {}", parkingLotDTO);

        return parkingLotRepository
            .findById(parkingLotDTO.getId())
            .map(existingParkingLot -> {
                parkingLotMapper.partialUpdate(existingParkingLot, parkingLotDTO);

                return existingParkingLot;
            })
            .map(parkingLotRepository::save)
            .map(parkingLotMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParkingLotDTO> findAll() {
        log.debug("Request to get all ParkingLots");
        return parkingLotRepository.findAll().stream().map(parkingLotMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ParkingLotDTO> findOne(Long id) {
        log.debug("Request to get ParkingLot : {}", id);
        return parkingLotRepository.findById(id).map(parkingLotMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete ParkingLot : {}", id);
        parkingLotRepository.deleteById(id);
    }
}
