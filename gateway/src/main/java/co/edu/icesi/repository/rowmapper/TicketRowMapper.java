package co.edu.icesi.repository.rowmapper;

import co.edu.icesi.domain.Ticket;
import co.edu.icesi.domain.enumeration.TicketStatus;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Ticket}, with proper type conversions.
 */
@Service
public class TicketRowMapper implements BiFunction<Row, String, Ticket> {

    private final ColumnConverter converter;

    public TicketRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Ticket} stored in the database.
     */
    @Override
    public Ticket apply(Row row, String prefix) {
        Ticket entity = new Ticket();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setTicketCode(converter.fromRow(row, prefix + "_ticket_code", String.class));
        entity.setIssuedAt(converter.fromRow(row, prefix + "_issued_at", Instant.class));
        entity.setEntryTime(converter.fromRow(row, prefix + "_entry_time", Instant.class));
        entity.setExitTime(converter.fromRow(row, prefix + "_exit_time", Instant.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", TicketStatus.class));
        entity.setParkingSpotIdId(converter.fromRow(row, prefix + "_parking_spot_id_id", Long.class));
        return entity;
    }
}
