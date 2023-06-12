package co.edu.icesi.web.rest;

import co.edu.icesi.repository.ParkingSpotRepository;
import co.edu.icesi.service.ParkingSpotService;
import co.edu.icesi.service.dto.ParkingSpotDTO;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link co.edu.icesi.domain.ParkingSpot}.
 */
@RestController
@RequestMapping("/api")
public class ParkingSpotResource {

    private final Logger log = LoggerFactory.getLogger(ParkingSpotResource.class);

    private static final String ENTITY_NAME = "parkingParkingSpot";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ParkingSpotService parkingSpotService;

    private final ParkingSpotRepository parkingSpotRepository;

    public ParkingSpotResource(ParkingSpotService parkingSpotService, ParkingSpotRepository parkingSpotRepository) {
        this.parkingSpotService = parkingSpotService;
        this.parkingSpotRepository = parkingSpotRepository;
    }

    /**
     * {@code POST  /parking-spots} : Create a new parkingSpot.
     *
     * @param parkingSpotDTO the parkingSpotDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new parkingSpotDTO, or with status {@code 400 (Bad Request)} if the parkingSpot has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/parking-spots")
    public ResponseEntity<ParkingSpotDTO> createParkingSpot(@Valid @RequestBody ParkingSpotDTO parkingSpotDTO) throws URISyntaxException {
        log.debug("REST request to save ParkingSpot : {}", parkingSpotDTO);
        if (parkingSpotDTO.getId() != null) {
            throw new BadRequestAlertException("A new parkingSpot cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ParkingSpotDTO result = parkingSpotService.save(parkingSpotDTO);
        return ResponseEntity
            .created(new URI("/api/parking-spots/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /parking-spots/:id} : Updates an existing parkingSpot.
     *
     * @param id the id of the parkingSpotDTO to save.
     * @param parkingSpotDTO the parkingSpotDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated parkingSpotDTO,
     * or with status {@code 400 (Bad Request)} if the parkingSpotDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the parkingSpotDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/parking-spots/{id}")
    public ResponseEntity<ParkingSpotDTO> updateParkingSpot(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ParkingSpotDTO parkingSpotDTO
    ) throws URISyntaxException {
        log.debug("REST request to update ParkingSpot : {}, {}", id, parkingSpotDTO);
        if (parkingSpotDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, parkingSpotDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!parkingSpotRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ParkingSpotDTO result = parkingSpotService.update(parkingSpotDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, parkingSpotDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /parking-spots/:id} : Partial updates given fields of an existing parkingSpot, field will ignore if it is null
     *
     * @param id the id of the parkingSpotDTO to save.
     * @param parkingSpotDTO the parkingSpotDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated parkingSpotDTO,
     * or with status {@code 400 (Bad Request)} if the parkingSpotDTO is not valid,
     * or with status {@code 404 (Not Found)} if the parkingSpotDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the parkingSpotDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/parking-spots/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ParkingSpotDTO> partialUpdateParkingSpot(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ParkingSpotDTO parkingSpotDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update ParkingSpot partially : {}, {}", id, parkingSpotDTO);
        if (parkingSpotDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, parkingSpotDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!parkingSpotRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ParkingSpotDTO> result = parkingSpotService.partialUpdate(parkingSpotDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, parkingSpotDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /parking-spots} : get all the parkingSpots.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of parkingSpots in body.
     */
    @GetMapping("/parking-spots")
    public List<ParkingSpotDTO> getAllParkingSpots() {
        log.debug("REST request to get all ParkingSpots");
        return parkingSpotService.findAll();
    }

    /**
     * {@code GET  /parking-spots/:id} : get the "id" parkingSpot.
     *
     * @param id the id of the parkingSpotDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the parkingSpotDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/parking-spots/{id}")
    public ResponseEntity<ParkingSpotDTO> getParkingSpot(@PathVariable Long id) {
        log.debug("REST request to get ParkingSpot : {}", id);
        Optional<ParkingSpotDTO> parkingSpotDTO = parkingSpotService.findOne(id);
        return ResponseUtil.wrapOrNotFound(parkingSpotDTO);
    }

    /**
     * {@code DELETE  /parking-spots/:id} : delete the "id" parkingSpot.
     *
     * @param id the id of the parkingSpotDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/parking-spots/{id}")
    public ResponseEntity<Void> deleteParkingSpot(@PathVariable Long id) {
        log.debug("REST request to delete ParkingSpot : {}", id);
        parkingSpotService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
