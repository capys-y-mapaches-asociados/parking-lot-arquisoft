package co.edu.icesi.domain;

import co.edu.icesi.domain.enumeration.PaymentMethod;
import co.edu.icesi.domain.enumeration.PaymentStatus;
import java.io.Serializable;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Payment.
 */
@Table("payment")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Payment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("customer_id")
    private Integer customerId;

    @Column("reservation_id")
    private Integer reservationID;

    @NotNull(message = "must not be null")
    @DecimalMax(value = "1000.00")
    @Column("amount")
    private Float amount;

    @NotNull(message = "must not be null")
    @Column("payment_status")
    private PaymentStatus paymentStatus;

    @NotNull(message = "must not be null")
    @Column("payment_method")
    private PaymentMethod paymentMethod;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Payment id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCustomerId() {
        return this.customerId;
    }

    public Payment customerId(Integer customerId) {
        this.setCustomerId(customerId);
        return this;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Integer getReservationID() {
        return this.reservationID;
    }

    public Payment reservationID(Integer reservationID) {
        this.setReservationID(reservationID);
        return this;
    }

    public void setReservationID(Integer reservationID) {
        this.reservationID = reservationID;
    }

    public Float getAmount() {
        return this.amount;
    }

    public Payment amount(Float amount) {
        this.setAmount(amount);
        return this;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public PaymentStatus getPaymentStatus() {
        return this.paymentStatus;
    }

    public Payment paymentStatus(PaymentStatus paymentStatus) {
        this.setPaymentStatus(paymentStatus);
        return this;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public PaymentMethod getPaymentMethod() {
        return this.paymentMethod;
    }

    public Payment paymentMethod(PaymentMethod paymentMethod) {
        this.setPaymentMethod(paymentMethod);
        return this;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Payment)) {
            return false;
        }
        return id != null && id.equals(((Payment) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Payment{" +
            "id=" + getId() +
            ", customerId=" + getCustomerId() +
            ", reservationID=" + getReservationID() +
            ", amount=" + getAmount() +
            ", paymentStatus='" + getPaymentStatus() + "'" +
            ", paymentMethod='" + getPaymentMethod() + "'" +
            "}";
    }
}
