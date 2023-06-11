package co.edu.icesi.repository.rowmapper;

import co.edu.icesi.domain.ParkingSpot;
import co.edu.icesi.domain.enumeration.ParkingSpotStatus;
import co.edu.icesi.domain.enumeration.ParkingSpotType;
import co.edu.icesi.domain.enumeration.ParkingSpotVehicle;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link ParkingSpot}, with proper type conversions.
 */
@Service
public class ParkingSpotRowMapper implements BiFunction<Row, String, ParkingSpot> {

    private final ColumnConverter converter;

    public ParkingSpotRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link ParkingSpot} stored in the database.
     */
    @Override
    public ParkingSpot apply(Row row, String prefix) {
        ParkingSpot entity = new ParkingSpot();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setNumber(converter.fromRow(row, prefix + "_number", Integer.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", ParkingSpotStatus.class));
        entity.setSpotType(converter.fromRow(row, prefix + "_spot_type", ParkingSpotType.class));
        entity.setSpotVehicle(converter.fromRow(row, prefix + "_spot_vehicle", ParkingSpotVehicle.class));
        entity.setParkingLotIdId(converter.fromRow(row, prefix + "_parking_lot_id_id", Long.class));
        return entity;
    }
}
