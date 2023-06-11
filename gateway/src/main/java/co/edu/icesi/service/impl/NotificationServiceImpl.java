package co.edu.icesi.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import co.edu.icesi.domain.Notification;
import co.edu.icesi.repository.NotificationRepository;
import co.edu.icesi.repository.search.NotificationSearchRepository;
import co.edu.icesi.service.NotificationService;
import co.edu.icesi.service.dto.NotificationDTO;
import co.edu.icesi.service.mapper.NotificationMapper;
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
 * Service Implementation for managing {@link Notification}.
 */
@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final NotificationRepository notificationRepository;

    private final NotificationMapper notificationMapper;

    private final NotificationSearchRepository notificationSearchRepository;

    public NotificationServiceImpl(
        NotificationRepository notificationRepository,
        NotificationMapper notificationMapper,
        NotificationSearchRepository notificationSearchRepository
    ) {
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
        this.notificationSearchRepository = notificationSearchRepository;
    }

    @Override
    public Mono<NotificationDTO> save(NotificationDTO notificationDTO) {
        log.debug("Request to save Notification : {}", notificationDTO);
        return notificationRepository
            .save(notificationMapper.toEntity(notificationDTO))
            .flatMap(notificationSearchRepository::save)
            .map(notificationMapper::toDto);
    }

    @Override
    public Mono<NotificationDTO> update(NotificationDTO notificationDTO) {
        log.debug("Request to update Notification : {}", notificationDTO);
        return notificationRepository
            .save(notificationMapper.toEntity(notificationDTO))
            .flatMap(notificationSearchRepository::save)
            .map(notificationMapper::toDto);
    }

    @Override
    public Mono<NotificationDTO> partialUpdate(NotificationDTO notificationDTO) {
        log.debug("Request to partially update Notification : {}", notificationDTO);

        return notificationRepository
            .findById(notificationDTO.getId())
            .map(existingNotification -> {
                notificationMapper.partialUpdate(existingNotification, notificationDTO);

                return existingNotification;
            })
            .flatMap(notificationRepository::save)
            .flatMap(savedNotification -> {
                notificationSearchRepository.save(savedNotification);

                return Mono.just(savedNotification);
            })
            .map(notificationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<NotificationDTO> findAll() {
        log.debug("Request to get all Notifications");
        return notificationRepository.findAll().map(notificationMapper::toDto);
    }

    public Mono<Long> countAll() {
        return notificationRepository.count();
    }

    public Mono<Long> searchCount() {
        return notificationSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<NotificationDTO> findOne(Long id) {
        log.debug("Request to get Notification : {}", id);
        return notificationRepository.findById(id).map(notificationMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Notification : {}", id);
        return notificationRepository.deleteById(id).then(notificationSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<NotificationDTO> search(String query) {
        log.debug("Request to search Notifications for query {}", query);
        return notificationSearchRepository.search(query).map(notificationMapper::toDto);
    }
}
