package co.edu.icesi.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import co.edu.icesi.domain.ParkingSpot;
import co.edu.icesi.repository.ParkingSpotRepository;
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
 * Spring Data Elasticsearch repository for the {@link ParkingSpot} entity.
 */
public interface ParkingSpotSearchRepository
    extends ReactiveElasticsearchRepository<ParkingSpot, Long>, ParkingSpotSearchRepositoryInternal {}

interface ParkingSpotSearchRepositoryInternal {
    Flux<ParkingSpot> search(String query);

    Flux<ParkingSpot> search(Query query);
}

class ParkingSpotSearchRepositoryInternalImpl implements ParkingSpotSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    ParkingSpotSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<ParkingSpot> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery);
    }

    @Override
    public Flux<ParkingSpot> search(Query query) {
        return reactiveElasticsearchTemplate.search(query, ParkingSpot.class).map(SearchHit::getContent);
    }
}
