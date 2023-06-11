package co.edu.icesi.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import co.edu.icesi.domain.Barrier;
import co.edu.icesi.repository.BarrierRepository;
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
 * Spring Data Elasticsearch repository for the {@link Barrier} entity.
 */
public interface BarrierSearchRepository extends ReactiveElasticsearchRepository<Barrier, Long>, BarrierSearchRepositoryInternal {}

interface BarrierSearchRepositoryInternal {
    Flux<Barrier> search(String query);

    Flux<Barrier> search(Query query);
}

class BarrierSearchRepositoryInternalImpl implements BarrierSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    BarrierSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<Barrier> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery);
    }

    @Override
    public Flux<Barrier> search(Query query) {
        return reactiveElasticsearchTemplate.search(query, Barrier.class).map(SearchHit::getContent);
    }
}
