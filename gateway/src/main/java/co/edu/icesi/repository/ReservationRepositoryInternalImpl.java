package co.edu.icesi.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import co.edu.icesi.domain.Reservation;
import co.edu.icesi.domain.enumeration.ReservationStatus;
import co.edu.icesi.repository.rowmapper.CustomerRowMapper;
import co.edu.icesi.repository.rowmapper.NotificationRowMapper;
import co.edu.icesi.repository.rowmapper.ReservationRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the Reservation entity.
 */
@SuppressWarnings("unused")
class ReservationRepositoryInternalImpl extends SimpleR2dbcRepository<Reservation, Long> implements ReservationRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final CustomerRowMapper customerMapper;
    private final NotificationRowMapper notificationMapper;
    private final ReservationRowMapper reservationMapper;

    private static final Table entityTable = Table.aliased("reservation", EntityManager.ENTITY_ALIAS);
    private static final Table customerIdTable = Table.aliased("customer", "customerId");
    private static final Table notificationsTable = Table.aliased("notification", "notifications");

    public ReservationRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        CustomerRowMapper customerMapper,
        NotificationRowMapper notificationMapper,
        ReservationRowMapper reservationMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Reservation.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.customerMapper = customerMapper;
        this.notificationMapper = notificationMapper;
        this.reservationMapper = reservationMapper;
    }

    @Override
    public Flux<Reservation> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Reservation> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = ReservationSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(CustomerSqlHelper.getColumns(customerIdTable, "customerId"));
        columns.addAll(NotificationSqlHelper.getColumns(notificationsTable, "notifications"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(customerIdTable)
            .on(Column.create("customer_id_id", entityTable))
            .equals(Column.create("id", customerIdTable))
            .leftOuterJoin(notificationsTable)
            .on(Column.create("notifications_id", entityTable))
            .equals(Column.create("id", notificationsTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Reservation.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Reservation> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Reservation> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Reservation process(Row row, RowMetadata metadata) {
        Reservation entity = reservationMapper.apply(row, "e");
        entity.setCustomerId(customerMapper.apply(row, "customerId"));
        entity.setNotifications(notificationMapper.apply(row, "notifications"));
        return entity;
    }

    @Override
    public <S extends Reservation> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
