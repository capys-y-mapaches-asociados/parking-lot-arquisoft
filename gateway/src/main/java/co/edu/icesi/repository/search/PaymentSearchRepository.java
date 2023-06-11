package co.edu.icesi.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import co.edu.icesi.domain.Payment;
import co.edu.icesi.repository.PaymentRepository;
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
 * Spring Data Elasticsearch repository for the {@link Payment} entity.
 */
public interface PaymentSearchRepository extends ReactiveElasticsearchRepository<Payment, Long>, PaymentSearchRepositoryInternal {}

interface PaymentSearchRepositoryInternal {
    Flux<Payment> search(String query);

    Flux<Payment> search(Query query);
}

class PaymentSearchRepositoryInternalImpl implements PaymentSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    PaymentSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<Payment> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery);
    }

    @Override
    public Flux<Payment> search(Query query) {
        return reactiveElasticsearchTemplate.search(query, Payment.class).map(SearchHit::getContent);
    }
}
