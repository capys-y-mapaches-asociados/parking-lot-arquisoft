package co.edu.icesi.repository;

import co.edu.icesi.domain.Reservation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Reservation entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ReservationRepository extends ReactiveCrudRepository<Reservation, Long>, ReservationRepositoryInternal {
    @Query("SELECT * FROM reservation entity WHERE entity.customer_id_id = :id")
    Flux<Reservation> findByCustomerId(Long id);

    @Query("SELECT * FROM reservation entity WHERE entity.customer_id_id IS NULL")
    Flux<Reservation> findAllWhereCustomerIdIsNull();

    @Query("SELECT * FROM reservation entity WHERE entity.notifications_id = :id")
    Flux<Reservation> findByNotifications(Long id);

    @Query("SELECT * FROM reservation entity WHERE entity.notifications_id IS NULL")
    Flux<Reservation> findAllWhereNotificationsIsNull();

    @Override
    <S extends Reservation> Mono<S> save(S entity);

    @Override
    Flux<Reservation> findAll();

    @Override
    Mono<Reservation> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ReservationRepositoryInternal {
    <S extends Reservation> Mono<S> save(S entity);

    Flux<Reservation> findAllBy(Pageable pageable);

    Flux<Reservation> findAll();

    Mono<Reservation> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Reservation> findAllBy(Pageable pageable, Criteria criteria);

}
