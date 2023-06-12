package co.edu.icesi.repository;

import co.edu.icesi.domain.ParkingLot;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the ParkingLot entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ParkingLotRepository extends ReactiveCrudRepository<ParkingLot, Long>, ParkingLotRepositoryInternal {
    @Override
    <S extends ParkingLot> Mono<S> save(S entity);

    @Override
    Flux<ParkingLot> findAll();

    @Override
    Mono<ParkingLot> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ParkingLotRepositoryInternal {
    <S extends ParkingLot> Mono<S> save(S entity);

    Flux<ParkingLot> findAllBy(Pageable pageable);

    Flux<ParkingLot> findAll();

    Mono<ParkingLot> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<ParkingLot> findAllBy(Pageable pageable, Criteria criteria);

}
