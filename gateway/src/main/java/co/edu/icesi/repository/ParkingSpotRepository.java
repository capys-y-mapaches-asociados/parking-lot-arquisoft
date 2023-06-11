package co.edu.icesi.repository;

import co.edu.icesi.domain.ParkingSpot;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the ParkingSpot entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ParkingSpotRepository extends ReactiveCrudRepository<ParkingSpot, Long>, ParkingSpotRepositoryInternal {
    @Query("SELECT * FROM parking_spot entity WHERE entity.parking_lot_id_id = :id")
    Flux<ParkingSpot> findByParkingLotId(Long id);

    @Query("SELECT * FROM parking_spot entity WHERE entity.parking_lot_id_id IS NULL")
    Flux<ParkingSpot> findAllWhereParkingLotIdIsNull();

    @Query("SELECT * FROM parking_spot entity WHERE entity.parking_lot_id_id = :id")
    Flux<ParkingSpot> findByParkingLotId(Long id);

    @Query("SELECT * FROM parking_spot entity WHERE entity.parking_lot_id_id IS NULL")
    Flux<ParkingSpot> findAllWhereParkingLotIdIsNull();

    @Override
    <S extends ParkingSpot> Mono<S> save(S entity);

    @Override
    Flux<ParkingSpot> findAll();

    @Override
    Mono<ParkingSpot> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ParkingSpotRepositoryInternal {
    <S extends ParkingSpot> Mono<S> save(S entity);

    Flux<ParkingSpot> findAllBy(Pageable pageable);

    Flux<ParkingSpot> findAll();

    Mono<ParkingSpot> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<ParkingSpot> findAllBy(Pageable pageable, Criteria criteria);

}
