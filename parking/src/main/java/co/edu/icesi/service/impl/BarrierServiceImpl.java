package co.edu.icesi.service.impl;

import co.edu.icesi.domain.Barrier;
import co.edu.icesi.repository.BarrierRepository;
import co.edu.icesi.service.BarrierService;
import co.edu.icesi.service.dto.BarrierDTO;
import co.edu.icesi.service.mapper.BarrierMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Barrier}.
 */
@Service
@Transactional
public class BarrierServiceImpl implements BarrierService {

    private final Logger log = LoggerFactory.getLogger(BarrierServiceImpl.class);

    private final BarrierRepository barrierRepository;

    private final BarrierMapper barrierMapper;

    public BarrierServiceImpl(BarrierRepository barrierRepository, BarrierMapper barrierMapper) {
        this.barrierRepository = barrierRepository;
        this.barrierMapper = barrierMapper;
    }

    @Override
    public BarrierDTO save(BarrierDTO barrierDTO) {
        log.debug("Request to save Barrier : {}", barrierDTO);
        Barrier barrier = barrierMapper.toEntity(barrierDTO);
        barrier = barrierRepository.save(barrier);
        return barrierMapper.toDto(barrier);
    }

    @Override
    public BarrierDTO update(BarrierDTO barrierDTO) {
        log.debug("Request to update Barrier : {}", barrierDTO);
        Barrier barrier = barrierMapper.toEntity(barrierDTO);
        barrier = barrierRepository.save(barrier);
        return barrierMapper.toDto(barrier);
    }

    @Override
    public Optional<BarrierDTO> partialUpdate(BarrierDTO barrierDTO) {
        log.debug("Request to partially update Barrier : {}", barrierDTO);

        return barrierRepository
            .findById(barrierDTO.getId())
            .map(existingBarrier -> {
                barrierMapper.partialUpdate(existingBarrier, barrierDTO);

                return existingBarrier;
            })
            .map(barrierRepository::save)
            .map(barrierMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BarrierDTO> findAll() {
        log.debug("Request to get all Barriers");
        return barrierRepository.findAll().stream().map(barrierMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BarrierDTO> findOne(Long id) {
        log.debug("Request to get Barrier : {}", id);
        return barrierRepository.findById(id).map(barrierMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Barrier : {}", id);
        barrierRepository.deleteById(id);
    }
}
