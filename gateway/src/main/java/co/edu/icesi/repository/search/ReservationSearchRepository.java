package co.edu.icesi.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import co.edu.icesi.domain.Reservation;
import co.edu.icesi.repository.ReservationRepository;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link Reservation} entity.
 */
public interface ReservationSearchRepository
    extends ReactiveElasticsearchRepository<Reservation, Long>, ReservationSearchRepositoryInternal {}

interface ReservationSearchRepositoryInternal {
    Flux<Reservation> search(String query);

    Flux<Reservation> search(Query query);
}

class ReservationSearchRepositoryInternalImpl implements ReservationSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    ReservationSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<Reservation> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery);
    }

    @Override
    public Flux<Reservation> search(Query query) {
        return reactiveElasticsearchTemplate.search(query, Reservation.class).map(SearchHit::getContent);
    }
}
