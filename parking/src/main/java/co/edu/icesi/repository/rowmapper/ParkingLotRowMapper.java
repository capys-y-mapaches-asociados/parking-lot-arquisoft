package co.edu.icesi.repository.rowmapper;

import co.edu.icesi.domain.ParkingLot;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link ParkingLot}, with proper type conversions.
 */
@Service
public class ParkingLotRowMapper implements BiFunction<Row, String, ParkingLot> {

    private final ColumnConverter converter;

    public ParkingLotRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link ParkingLot} stored in the database.
     */
    @Override
    public ParkingLot apply(Row row, String prefix) {
        ParkingLot entity = new ParkingLot();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setLocation(converter.fromRow(row, prefix + "_location", String.class));
        entity.setCapacity(converter.fromRow(row, prefix + "_capacity", Integer.class));
        return entity;
    }
}
