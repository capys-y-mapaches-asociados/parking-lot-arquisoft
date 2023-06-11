package co.edu.icesi.repository;

import co.edu.icesi.domain.Barrier;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Barrier entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BarrierRepository extends ReactiveCrudRepository<Barrier, Long>, BarrierRepositoryInternal {
    @Query("SELECT * FROM barrier entity WHERE entity.parking_lot_id = :id")
    Flux<Barrier> findByParkingLot(Long id);

    @Query("SELECT * FROM barrier entity WHERE entity.parking_lot_id IS NULL")
    Flux<Barrier> findAllWhereParkingLotIsNull();

    @Override
    <S extends Barrier> Mono<S> save(S entity);

    @Override
    Flux<Barrier> findAll();

    @Override
    Mono<Barrier> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface BarrierRepositoryInternal {
    <S extends Barrier> Mono<S> save(S entity);

    Flux<Barrier> findAllBy(Pageable pageable);

    Flux<Barrier> findAll();

    Mono<Barrier> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Barrier> findAllBy(Pageable pageable, Criteria criteria);

}
