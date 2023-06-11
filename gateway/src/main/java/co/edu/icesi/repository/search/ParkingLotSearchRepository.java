package co.edu.icesi.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import co.edu.icesi.domain.ParkingLot;
import co.edu.icesi.repository.ParkingLotRepository;
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
 * Spring Data Elasticsearch repository for the {@link ParkingLot} entity.
 */
public interface ParkingLotSearchRepository extends ReactiveElasticsearchRepository<ParkingLot, Long>, ParkingLotSearchRepositoryInternal {}

interface ParkingLotSearchRepositoryInternal {
    Flux<ParkingLot> search(String query);

    Flux<ParkingLot> search(Query query);
}

class ParkingLotSearchRepositoryInternalImpl implements ParkingLotSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    ParkingLotSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<ParkingLot> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery);
    }

    @Override
    public Flux<ParkingLot> search(Query query) {
        return reactiveElasticsearchTemplate.search(query, ParkingLot.class).map(SearchHit::getContent);
    }
}
