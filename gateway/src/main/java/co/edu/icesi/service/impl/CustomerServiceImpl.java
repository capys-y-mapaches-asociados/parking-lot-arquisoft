package co.edu.icesi.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import co.edu.icesi.domain.Customer;
import co.edu.icesi.repository.CustomerRepository;
import co.edu.icesi.repository.search.CustomerSearchRepository;
import co.edu.icesi.service.CustomerService;
import co.edu.icesi.service.dto.CustomerDTO;
import co.edu.icesi.service.mapper.CustomerMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Customer}.
 */
@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);

    private final CustomerRepository customerRepository;

    private final CustomerMapper customerMapper;

    private final CustomerSearchRepository customerSearchRepository;

    public CustomerServiceImpl(
        CustomerRepository customerRepository,
        CustomerMapper customerMapper,
        CustomerSearchRepository customerSearchRepository
    ) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
        this.customerSearchRepository = customerSearchRepository;
    }

    @Override
    public Mono<CustomerDTO> save(CustomerDTO customerDTO) {
        log.debug("Request to save Customer : {}", customerDTO);
        return customerRepository
            .save(customerMapper.toEntity(customerDTO))
            .flatMap(customerSearchRepository::save)
            .map(customerMapper::toDto);
    }

    @Override
    public Mono<CustomerDTO> update(CustomerDTO customerDTO) {
        log.debug("Request to update Customer : {}", customerDTO);
        return customerRepository
            .save(customerMapper.toEntity(customerDTO))
            .flatMap(customerSearchRepository::save)
            .map(customerMapper::toDto);
    }

    @Override
    public Mono<CustomerDTO> partialUpdate(CustomerDTO customerDTO) {
        log.debug("Request to partially update Customer : {}", customerDTO);

        return customerRepository
            .findById(customerDTO.getId())
            .map(existingCustomer -> {
                customerMapper.partialUpdate(existingCustomer, customerDTO);

                return existingCustomer;
            })
            .flatMap(customerRepository::save)
            .flatMap(savedCustomer -> {
                customerSearchRepository.save(savedCustomer);

                return Mono.just(savedCustomer);
            })
            .map(customerMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<CustomerDTO> findAll() {
        log.debug("Request to get all Customers");
        return customerRepository.findAll().map(customerMapper::toDto);
    }

    public Mono<Long> countAll() {
        return customerRepository.count();
    }

    public Mono<Long> searchCount() {
        return customerSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<CustomerDTO> findOne(Long id) {
        log.debug("Request to get Customer : {}", id);
        return customerRepository.findById(id).map(customerMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Customer : {}", id);
        return customerRepository.deleteById(id).then(customerSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<CustomerDTO> search(String query) {
        log.debug("Request to search Customers for query {}", query);
        return customerSearchRepository.search(query).map(customerMapper::toDto);
    }
}
