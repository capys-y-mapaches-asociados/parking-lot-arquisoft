package co.edu.icesi.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import co.edu.icesi.domain.Ticket;
import co.edu.icesi.domain.enumeration.TicketStatus;
import co.edu.icesi.repository.rowmapper.ParkingSpotRowMapper;
import co.edu.icesi.repository.rowmapper.TicketRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the Ticket entity.
 */
@SuppressWarnings("unused")
class TicketRepositoryInternalImpl extends SimpleR2dbcRepository<Ticket, Long> implements TicketRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final ParkingSpotRowMapper parkingspotMapper;
    private final TicketRowMapper ticketMapper;

    private static final Table entityTable = Table.aliased("ticket", EntityManager.ENTITY_ALIAS);
    private static final Table parkingSpotIdTable = Table.aliased("parking_spot", "parkingSpotId");

    public TicketRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        ParkingSpotRowMapper parkingspotMapper,
        TicketRowMapper ticketMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Ticket.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.parkingspotMapper = parkingspotMapper;
        this.ticketMapper = ticketMapper;
    }

    @Override
    public Flux<Ticket> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Ticket> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = TicketSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(ParkingSpotSqlHelper.getColumns(parkingSpotIdTable, "parkingSpotId"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(parkingSpotIdTable)
            .on(Column.create("parking_spot_id_id", entityTable))
            .equals(Column.create("id", parkingSpotIdTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Ticket.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Ticket> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Ticket> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Ticket process(Row row, RowMetadata metadata) {
        Ticket entity = ticketMapper.apply(row, "e");
        entity.setParkingSpotId(parkingspotMapper.apply(row, "parkingSpotId"));
        return entity;
    }

    @Override
    public <S extends Ticket> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
