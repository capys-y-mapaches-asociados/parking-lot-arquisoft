package co.edu.icesi.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import co.edu.icesi.domain.ParkingSpot;
import co.edu.icesi.domain.enumeration.ParkingSpotStatus;
import co.edu.icesi.domain.enumeration.ParkingSpotType;
import co.edu.icesi.domain.enumeration.ParkingSpotVehicle;
import co.edu.icesi.repository.rowmapper.ParkingLotRowMapper;
import co.edu.icesi.repository.rowmapper.ParkingSpotRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the ParkingSpot entity.
 */
@SuppressWarnings("unused")
class ParkingSpotRepositoryInternalImpl extends SimpleR2dbcRepository<ParkingSpot, Long> implements ParkingSpotRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final ParkingLotRowMapper parkinglotMapper;
    private final ParkingSpotRowMapper parkingspotMapper;

    private static final Table entityTable = Table.aliased("parking_spot", EntityManager.ENTITY_ALIAS);
    private static final Table parkingLotIdTable = Table.aliased("parking_lot", "parkingLotId");

    public ParkingSpotRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        ParkingLotRowMapper parkinglotMapper,
        ParkingSpotRowMapper parkingspotMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(ParkingSpot.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.parkinglotMapper = parkinglotMapper;
        this.parkingspotMapper = parkingspotMapper;
    }

    @Override
    public Flux<ParkingSpot> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<ParkingSpot> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = ParkingSpotSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(ParkingLotSqlHelper.getColumns(parkingLotIdTable, "parkingLotId"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(parkingLotIdTable)
            .on(Column.create("parking_lot_id_id", entityTable))
            .equals(Column.create("id", parkingLotIdTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, ParkingSpot.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<ParkingSpot> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<ParkingSpot> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private ParkingSpot process(Row row, RowMetadata metadata) {
        ParkingSpot entity = parkingspotMapper.apply(row, "e");
        entity.setParkingLotId(parkinglotMapper.apply(row, "parkingLotId"));
        return entity;
    }

    @Override
    public <S extends ParkingSpot> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
