package co.edu.icesi.repository.rowmapper;

import co.edu.icesi.domain.Barrier;
import co.edu.icesi.domain.enumeration.BarrierStatus;
import co.edu.icesi.domain.enumeration.BarrierType;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Barrier}, with proper type conversions.
 */
@Service
public class BarrierRowMapper implements BiFunction<Row, String, Barrier> {

    private final ColumnConverter converter;

    public BarrierRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Barrier} stored in the database.
     */
    @Override
    public Barrier apply(Row row, String prefix) {
        Barrier entity = new Barrier();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setType(converter.fromRow(row, prefix + "_type", BarrierType.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", BarrierStatus.class));
        entity.setParkingLotId(converter.fromRow(row, prefix + "_parking_lot_id", Long.class));
        return entity;
    }
}
