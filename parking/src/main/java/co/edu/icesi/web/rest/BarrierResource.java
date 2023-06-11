package co.edu.icesi.web.rest;

import co.edu.icesi.repository.BarrierRepository;
import co.edu.icesi.service.BarrierService;
import co.edu.icesi.service.dto.BarrierDTO;
import co.edu.icesi.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link co.edu.icesi.domain.Barrier}.
 */
@RestController
@RequestMapping("/api")
public class BarrierResource {

    private final Logger log = LoggerFactory.getLogger(BarrierResource.class);

    private static final String ENTITY_NAME = "parkingBarrier";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BarrierService barrierService;

    private final BarrierRepository barrierRepository;

    public BarrierResource(BarrierService barrierService, BarrierRepository barrierRepository) {
        this.barrierService = barrierService;
        this.barrierRepository = barrierRepository;
    }

    /**
     * {@code POST  /barriers} : Create a new barrier.
     *
     * @param barrierDTO the barrierDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new barrierDTO, or with status {@code 400 (Bad Request)} if the barrier has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/barriers")
    public Mono<ResponseEntity<BarrierDTO>> createBarrier(@Valid @RequestBody BarrierDTO barrierDTO) throws URISyntaxException {
        log.debug("REST request to save Barrier : {}", barrierDTO);
        if (barrierDTO.getId() != null) {
            throw new BadRequestAlertException("A new barrier cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return barrierService
            .save(barrierDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/barriers/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /barriers/:id} : Updates an existing barrier.
     *
     * @param id the id of the barrierDTO to save.
     * @param barrierDTO the barrierDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated barrierDTO,
     * or with status {@code 400 (Bad Request)} if the barrierDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the barrierDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/barriers/{id}")
    public Mono<ResponseEntity<BarrierDTO>> updateBarrier(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody BarrierDTO barrierDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Barrier : {}, {}", id, barrierDTO);
        if (barrierDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, barrierDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return barrierRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return barrierService
                    .update(barrierDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /barriers/:id} : Partial updates given fields of an existing barrier, field will ignore if it is null
     *
     * @param id the id of the barrierDTO to save.
     * @param barrierDTO the barrierDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated barrierDTO,
     * or with status {@code 400 (Bad Request)} if the barrierDTO is not valid,
     * or with status {@code 404 (Not Found)} if the barrierDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the barrierDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/barriers/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<BarrierDTO>> partialUpdateBarrier(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody BarrierDTO barrierDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Barrier partially : {}, {}", id, barrierDTO);
        if (barrierDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, barrierDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return barrierRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<BarrierDTO> result = barrierService.partialUpdate(barrierDTO);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /barriers} : get all the barriers.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of barriers in body.
     */
    @GetMapping("/barriers")
    public Mono<List<BarrierDTO>> getAllBarriers() {
        log.debug("REST request to get all Barriers");
        return barrierService.findAll().collectList();
    }

    /**
     * {@code GET  /barriers} : get all the barriers as a stream.
     * @return the {@link Flux} of barriers.
     */
    @GetMapping(value = "/barriers", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<BarrierDTO> getAllBarriersAsStream() {
        log.debug("REST request to get all Barriers as a stream");
        return barrierService.findAll();
    }

    /**
     * {@code GET  /barriers/:id} : get the "id" barrier.
     *
     * @param id the id of the barrierDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the barrierDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/barriers/{id}")
    public Mono<ResponseEntity<BarrierDTO>> getBarrier(@PathVariable Long id) {
        log.debug("REST request to get Barrier : {}", id);
        Mono<BarrierDTO> barrierDTO = barrierService.findOne(id);
        return ResponseUtil.wrapOrNotFound(barrierDTO);
    }

    /**
     * {@code DELETE  /barriers/:id} : delete the "id" barrier.
     *
     * @param id the id of the barrierDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/barriers/{id}")
    public Mono<ResponseEntity<Void>> deleteBarrier(@PathVariable Long id) {
        log.debug("REST request to delete Barrier : {}", id);
        return barrierService
            .delete(id)
            .then(
                Mono.just(
                    ResponseEntity
                        .noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }
}
