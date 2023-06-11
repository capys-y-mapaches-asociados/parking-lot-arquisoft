package co.edu.icesi.domain;

import co.edu.icesi.domain.enumeration.ReservationStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Reservation.
 */
@Table("reservation")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "reservation")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Reservation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("customer_id")
    private UUID customerId;

    @NotNull(message = "must not be null")
    @Column("parking_spot_id")
    private UUID parkingSpotId;

    @NotNull(message = "must not be null")
    @Column("start_time")
    private Instant startTime;

    @NotNull(message = "must not be null")
    @Column("end_time")
    private Instant endTime;

    @NotNull(message = "must not be null")
    @Column("status")
    private ReservationStatus status;

    @NotNull(message = "must not be null")
    @Pattern(regexp = "^([A-Z]{2})-([A-Fa-f0-9]{10, 14})$")
    @Column("reservation_code")
    private String reservationCode;

    @Transient
    @Transient
    @JsonIgnoreProperties(value = { "reservations", "reservations" }, allowSetters = true)
    private Customer customerId;

    @Transient
    @Transient
    @JsonIgnoreProperties(value = { "reservations", "reservations" }, allowSetters = true)
    private Customer customerId;

    @Transient
    @JsonIgnoreProperties(value = { "reservationIds" }, allowSetters = true)
    private Notification notifications;

    @Column("customer_id_id")
    private Long customerIdId;

    @Column("customer_id_id")
    private Long customerIdId;

    @Column("notifications_id")
    private Long notificationsId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Reservation id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getCustomerId() {
        return this.customerId;
    }

    public Reservation customerId(UUID customerId) {
        this.setCustomerId(customerId);
        return this;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public UUID getParkingSpotId() {
        return this.parkingSpotId;
    }

    public Reservation parkingSpotId(UUID parkingSpotId) {
        this.setParkingSpotId(parkingSpotId);
        return this;
    }

    public void setParkingSpotId(UUID parkingSpotId) {
        this.parkingSpotId = parkingSpotId;
    }

    public Instant getStartTime() {
        return this.startTime;
    }

    public Reservation startTime(Instant startTime) {
        this.setStartTime(startTime);
        return this;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return this.endTime;
    }

    public Reservation endTime(Instant endTime) {
        this.setEndTime(endTime);
        return this;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public ReservationStatus getStatus() {
        return this.status;
    }

    public Reservation status(ReservationStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public String getReservationCode() {
        return this.reservationCode;
    }

    public Reservation reservationCode(String reservationCode) {
        this.setReservationCode(reservationCode);
        return this;
    }

    public void setReservationCode(String reservationCode) {
        this.reservationCode = reservationCode;
    }

    public Customer getCustomerId() {
        return this.customerId;
    }

    public void setCustomerId(Customer customer) {
        this.customerId = customer;
        this.customerIdId = customer != null ? customer.getId() : null;
    }

    public void setCustomerId(Customer customer) {
        this.customerId = customer;
        this.customerIdId = customer != null ? customer.getId() : null;
    }

    public Reservation customerId(Customer customer) {
        this.setCustomerId(customer);
        return this;
    }

    public Customer getCustomerId() {
        return this.customerId;
    }

    public void setCustomerId(Customer customer) {
        this.customerId = customer;
        this.customerIdId = customer != null ? customer.getId() : null;
    }

    public void setCustomerId(Customer customer) {
        this.customerId = customer;
        this.customerIdId = customer != null ? customer.getId() : null;
    }

    public Reservation customerId(Customer customer) {
        this.setCustomerId(customer);
        return this;
    }

    public Notification getNotifications() {
        return this.notifications;
    }

    public void setNotifications(Notification notification) {
        this.notifications = notification;
        this.notificationsId = notification != null ? notification.getId() : null;
    }

    public Reservation notifications(Notification notification) {
        this.setNotifications(notification);
        return this;
    }

    public Long getCustomerIdId() {
        return this.customerIdId;
    }

    public void setCustomerIdId(Long customer) {
        this.customerIdId = customer;
    }

    public Long getCustomerIdId() {
        return this.customerIdId;
    }

    public void setCustomerIdId(Long customer) {
        this.customerIdId = customer;
    }

    public Long getNotificationsId() {
        return this.notificationsId;
    }

    public void setNotificationsId(Long notification) {
        this.notificationsId = notification;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Reservation)) {
            return false;
        }
        return id != null && id.equals(((Reservation) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Reservation{" +
            "id=" + getId() +
            ", customerId='" + getCustomerId() + "'" +
            ", parkingSpotId='" + getParkingSpotId() + "'" +
            ", startTime='" + getStartTime() + "'" +
            ", endTime='" + getEndTime() + "'" +
            ", status='" + getStatus() + "'" +
            ", reservationCode='" + getReservationCode() + "'" +
            "}";
    }
}
