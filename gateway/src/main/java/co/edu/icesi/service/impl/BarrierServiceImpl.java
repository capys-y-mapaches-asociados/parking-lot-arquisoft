package co.edu.icesi.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import co.edu.icesi.domain.Barrier;
import co.edu.icesi.repository.BarrierRepository;
import co.edu.icesi.repository.search.BarrierSearchRepository;
import co.edu.icesi.service.BarrierService;
import co.edu.icesi.service.dto.BarrierDTO;
import co.edu.icesi.service.mapper.BarrierMapper;
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
 * Service Implementation for managing {@link Barrier}.
 */
@Service
@Transactional
public class BarrierServiceImpl implements BarrierService {

    private final Logger log = LoggerFactory.getLogger(BarrierServiceImpl.class);

    private final BarrierRepository barrierRepository;

    private final BarrierMapper barrierMapper;

    private final BarrierSearchRepository barrierSearchRepository;

    public BarrierServiceImpl(
        BarrierRepository barrierRepository,
        BarrierMapper barrierMapper,
        BarrierSearchRepository barrierSearchRepository
    ) {
        this.barrierRepository = barrierRepository;
        this.barrierMapper = barrierMapper;
        this.barrierSearchRepository = barrierSearchRepository;
    }

    @Override
    public Mono<BarrierDTO> save(BarrierDTO barrierDTO) {
        log.debug("Request to save Barrier : {}", barrierDTO);
        return barrierRepository.save(barrierMapper.toEntity(barrierDTO)).flatMap(barrierSearchRepository::save).map(barrierMapper::toDto);
    }

    @Override
    public Mono<BarrierDTO> update(BarrierDTO barrierDTO) {
        log.debug("Request to update Barrier : {}", barrierDTO);
        return barrierRepository.save(barrierMapper.toEntity(barrierDTO)).flatMap(barrierSearchRepository::save).map(barrierMapper::toDto);
    }

    @Override
    public Mono<BarrierDTO> partialUpdate(BarrierDTO barrierDTO) {
        log.debug("Request to partially update Barrier : {}", barrierDTO);

        return barrierRepository
            .findById(barrierDTO.getId())
            .map(existingBarrier -> {
                barrierMapper.partialUpdate(existingBarrier, barrierDTO);

                return existingBarrier;
            })
            .flatMap(barrierRepository::save)
            .flatMap(savedBarrier -> {
                barrierSearchRepository.save(savedBarrier);

                return Mono.just(savedBarrier);
            })
            .map(barrierMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<BarrierDTO> findAll() {
        log.debug("Request to get all Barriers");
        return barrierRepository.findAll().map(barrierMapper::toDto);
    }

    public Mono<Long> countAll() {
        return barrierRepository.count();
    }

    public Mono<Long> searchCount() {
        return barrierSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<BarrierDTO> findOne(Long id) {
        log.debug("Request to get Barrier : {}", id);
        return barrierRepository.findById(id).map(barrierMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Barrier : {}", id);
        return barrierRepository.deleteById(id).then(barrierSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<BarrierDTO> search(String query) {
        log.debug("Request to search Barriers for query {}", query);
        return barrierSearchRepository.search(query).map(barrierMapper::toDto);
    }
}
