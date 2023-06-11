package co.edu.icesi.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import co.edu.icesi.domain.Barrier;
import co.edu.icesi.domain.enumeration.BarrierStatus;
import co.edu.icesi.domain.enumeration.BarrierType;
import co.edu.icesi.repository.rowmapper.BarrierRowMapper;
import co.edu.icesi.repository.rowmapper.ParkingLotRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
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
 * Spring Data R2DBC custom repository implementation for the Barrier entity.
 */
@SuppressWarnings("unused")
class BarrierRepositoryInternalImpl extends SimpleR2dbcRepository<Barrier, Long> implements BarrierRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final ParkingLotRowMapper parkinglotMapper;
    private final BarrierRowMapper barrierMapper;

    private static final Table entityTable = Table.aliased("barrier", EntityManager.ENTITY_ALIAS);
    private static final Table parkingLotTable = Table.aliased("parking_lot", "parkingLot");

    public BarrierRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        ParkingLotRowMapper parkinglotMapper,
        BarrierRowMapper barrierMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Barrier.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.parkinglotMapper = parkinglotMapper;
        this.barrierMapper = barrierMapper;
    }

    @Override
    public Flux<Barrier> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Barrier> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = BarrierSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(ParkingLotSqlHelper.getColumns(parkingLotTable, "parkingLot"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(parkingLotTable)
            .on(Column.create("parking_lot_id", entityTable))
            .equals(Column.create("id", parkingLotTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Barrier.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Barrier> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Barrier> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Barrier process(Row row, RowMetadata metadata) {
        Barrier entity = barrierMapper.apply(row, "e");
        entity.setParkingLot(parkinglotMapper.apply(row, "parkingLot"));
        return entity;
    }

    @Override
    public <S extends Barrier> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
