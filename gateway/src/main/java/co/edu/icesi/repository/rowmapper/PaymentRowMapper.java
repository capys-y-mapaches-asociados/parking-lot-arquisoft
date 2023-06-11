package co.edu.icesi.repository.rowmapper;

import co.edu.icesi.domain.Payment;
import co.edu.icesi.domain.enumeration.PaymentMethod;
import co.edu.icesi.domain.enumeration.PaymentStatus;
import io.r2dbc.spi.Row;
import java.util.UUID;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Payment}, with proper type conversions.
 */
@Service
public class PaymentRowMapper implements BiFunction<Row, String, Payment> {

    private final ColumnConverter converter;

    public PaymentRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Payment} stored in the database.
     */
    @Override
    public Payment apply(Row row, String prefix) {
        Payment entity = new Payment();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setCustomerId(converter.fromRow(row, prefix + "_customer_id", UUID.class));
        entity.setReservationID(converter.fromRow(row, prefix + "_reservation_id", UUID.class));
        entity.setAmount(converter.fromRow(row, prefix + "_amount", Float.class));
        entity.setPaymentStatus(converter.fromRow(row, prefix + "_payment_status", PaymentStatus.class));
        entity.setPaymentMethod(converter.fromRow(row, prefix + "_payment_method", PaymentMethod.class));
        return entity;
    }
}
