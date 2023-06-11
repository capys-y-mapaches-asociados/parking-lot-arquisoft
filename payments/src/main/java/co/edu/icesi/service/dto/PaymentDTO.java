package co.edu.icesi.service.dto;

import co.edu.icesi.domain.enumeration.PaymentMethod;
import co.edu.icesi.domain.enumeration.PaymentStatus;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link co.edu.icesi.domain.Payment} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PaymentDTO implements Serializable {

    private Long id;

    @NotNull
    private UUID customerId;

    private UUID reservationID;

    @NotNull
    @DecimalMax(value = "1000.00")
    private Float amount;

    @NotNull
    private PaymentStatus paymentStatus;

    @NotNull
    private PaymentMethod paymentMethod;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public UUID getReservationID() {
        return reservationID;
    }

    public void setReservationID(UUID reservationID) {
        this.reservationID = reservationID;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PaymentDTO)) {
            return false;
        }

        PaymentDTO paymentDTO = (PaymentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, paymentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PaymentDTO{" +
            "id=" + getId() +
            ", customerId='" + getCustomerId() + "'" +
            ", reservationID='" + getReservationID() + "'" +
            ", amount=" + getAmount() +
            ", paymentStatus='" + getPaymentStatus() + "'" +
            ", paymentMethod='" + getPaymentMethod() + "'" +
            "}";
    }
}
