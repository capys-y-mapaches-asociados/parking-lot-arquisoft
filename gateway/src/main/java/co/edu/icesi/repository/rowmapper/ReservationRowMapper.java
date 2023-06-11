package co.edu.icesi.repository.rowmapper;

import co.edu.icesi.domain.Reservation;
import co.edu.icesi.domain.enumeration.ReservationStatus;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.UUID;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Reservation}, with proper type conversions.
 */
@Service
public class ReservationRowMapper implements BiFunction<Row, String, Reservation> {

    private final ColumnConverter converter;

    public ReservationRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Reservation} stored in the database.
     */
    @Override
    public Reservation apply(Row row, String prefix) {
        Reservation entity = new Reservation();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setCustomerId(converter.fromRow(row, prefix + "_customer_id", UUID.class));
        entity.setParkingSpotId(converter.fromRow(row, prefix + "_parking_spot_id", UUID.class));
        entity.setStartTime(converter.fromRow(row, prefix + "_start_time", Instant.class));
        entity.setEndTime(converter.fromRow(row, prefix + "_end_time", Instant.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", ReservationStatus.class));
        entity.setReservationCode(converter.fromRow(row, prefix + "_reservation_code", String.class));
        entity.setCustomerIdId(converter.fromRow(row, prefix + "_customer_id_id", Long.class));
        entity.setNotificationsId(converter.fromRow(row, prefix + "_notifications_id", Long.class));
        return entity;
    }
}
