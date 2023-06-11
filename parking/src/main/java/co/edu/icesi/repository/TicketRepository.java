package co.edu.icesi.repository;

import co.edu.icesi.domain.Ticket;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Ticket entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TicketRepository extends ReactiveCrudRepository<Ticket, Long>, TicketRepositoryInternal {
    @Query("SELECT * FROM ticket entity WHERE entity.parking_spot_id_id = :id")
    Flux<Ticket> findByParkingSpotId(Long id);

    @Query("SELECT * FROM ticket entity WHERE entity.parking_spot_id_id IS NULL")
    Flux<Ticket> findAllWhereParkingSpotIdIsNull();

    @Override
    <S extends Ticket> Mono<S> save(S entity);

    @Override
    Flux<Ticket> findAll();

    @Override
    Mono<Ticket> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface TicketRepositoryInternal {
    <S extends Ticket> Mono<S> save(S entity);

    Flux<Ticket> findAllBy(Pageable pageable);

    Flux<Ticket> findAll();

    Mono<Ticket> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Ticket> findAllBy(Pageable pageable, Criteria criteria);

}
