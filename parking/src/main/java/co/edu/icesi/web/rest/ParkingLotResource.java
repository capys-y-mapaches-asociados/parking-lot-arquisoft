package co.edu.icesi.web.rest;

import co.edu.icesi.repository.ParkingLotRepository;
import co.edu.icesi.service.ParkingLotService;
import co.edu.icesi.service.dto.ParkingLotDTO;
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
 * REST controller for managing {@link co.edu.icesi.domain.ParkingLot}.
 */
@RestController
@RequestMapping("/api")
public class ParkingLotResource {

    private final Logger log = LoggerFactory.getLogger(ParkingLotResource.class);

    private static final String ENTITY_NAME = "parkingParkingLot";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ParkingLotService parkingLotService;

    private final ParkingLotRepository parkingLotRepository;

    public ParkingLotResource(ParkingLotService parkingLotService, ParkingLotRepository parkingLotRepository) {
        this.parkingLotService = parkingLotService;
        this.parkingLotRepository = parkingLotRepository;
    }

    /**
     * {@code POST  /parking-lots} : Create a new parkingLot.
     *
     * @param parkingLotDTO the parkingLotDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new parkingLotDTO, or with status {@code 400 (Bad Request)} if the parkingLot has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/parking-lots")
    public Mono<ResponseEntity<ParkingLotDTO>> createParkingLot(@Valid @RequestBody ParkingLotDTO parkingLotDTO) throws URISyntaxException {
        log.debug("REST request to save ParkingLot : {}", parkingLotDTO);
        if (parkingLotDTO.getId() != null) {
            throw new BadRequestAlertException("A new parkingLot cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return parkingLotService
            .save(parkingLotDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/parking-lots/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /parking-lots/:id} : Updates an existing parkingLot.
     *
     * @param id the id of the parkingLotDTO to save.
     * @param parkingLotDTO the parkingLotDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated parkingLotDTO,
     * or with status {@code 400 (Bad Request)} if the parkingLotDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the parkingLotDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/parking-lots/{id}")
    public Mono<ResponseEntity<ParkingLotDTO>> updateParkingLot(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ParkingLotDTO parkingLotDTO
    ) throws URISyntaxException {
        log.debug("REST request to update ParkingLot : {}, {}", id, parkingLotDTO);
        if (parkingLotDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, parkingLotDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return parkingLotRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return parkingLotService
                    .update(parkingLotDTO)
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
     * {@code PATCH  /parking-lots/:id} : Partial updates given fields of an existing parkingLot, field will ignore if it is null
     *
     * @param id the id of the parkingLotDTO to save.
     * @param parkingLotDTO the parkingLotDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated parkingLotDTO,
     * or with status {@code 400 (Bad Request)} if the parkingLotDTO is not valid,
     * or with status {@code 404 (Not Found)} if the parkingLotDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the parkingLotDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/parking-lots/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<ParkingLotDTO>> partialUpdateParkingLot(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ParkingLotDTO parkingLotDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update ParkingLot partially : {}, {}", id, parkingLotDTO);
        if (parkingLotDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, parkingLotDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return parkingLotRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<ParkingLotDTO> result = parkingLotService.partialUpdate(parkingLotDTO);

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
     * {@code GET  /parking-lots} : get all the parkingLots.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of parkingLots in body.
     */
    @GetMapping("/parking-lots")
    public Mono<List<ParkingLotDTO>> getAllParkingLots() {
        log.debug("REST request to get all ParkingLots");
        return parkingLotService.findAll().collectList();
    }

    /**
     * {@code GET  /parking-lots} : get all the parkingLots as a stream.
     * @return the {@link Flux} of parkingLots.
     */
    @GetMapping(value = "/parking-lots", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<ParkingLotDTO> getAllParkingLotsAsStream() {
        log.debug("REST request to get all ParkingLots as a stream");
        return parkingLotService.findAll();
    }

    /**
     * {@code GET  /parking-lots/:id} : get the "id" parkingLot.
     *
     * @param id the id of the parkingLotDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the parkingLotDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/parking-lots/{id}")
    public Mono<ResponseEntity<ParkingLotDTO>> getParkingLot(@PathVariable Long id) {
        log.debug("REST request to get ParkingLot : {}", id);
        Mono<ParkingLotDTO> parkingLotDTO = parkingLotService.findOne(id);
        return ResponseUtil.wrapOrNotFound(parkingLotDTO);
    }

    /**
     * {@code DELETE  /parking-lots/:id} : delete the "id" parkingLot.
     *
     * @param id the id of the parkingLotDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/parking-lots/{id}")
    public Mono<ResponseEntity<Void>> deleteParkingLot(@PathVariable Long id) {
        log.debug("REST request to delete ParkingLot : {}", id);
        return parkingLotService
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
