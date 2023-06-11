package co.edu.icesi.domain;

import co.edu.icesi.domain.enumeration.ReservationStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Reservation.
 */
@Entity
@Table(name = "reservation")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Reservation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @NotNull
    @Column(name = "parking_spot_id", nullable = false)
    private UUID parkingSpotId;

    @NotNull
    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @NotNull
    @Column(name = "end_time", nullable = false)
    private Instant endTime;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReservationStatus status;

    @NotNull
    @Pattern(regexp = "^([A-Z]{2})-([A-Fa-f0-9]{10, 14})$")
    @Column(name = "reservation_code", nullable = false, unique = true)
    private String reservationCode;

    @ManyToOne(optional = false)
    @NotNull
    @ManyToOne
    @JsonIgnoreProperties(value = { "reservations", "reservations" }, allowSetters = true)
    private Customer customerId;

    @ManyToOne(optional = false)
    @NotNull
    @ManyToOne
    @JsonIgnoreProperties(value = { "reservations", "reservations" }, allowSetters = true)
    private Customer customerId;

    @ManyToOne
    @JsonIgnoreProperties(value = { "reservationIds" }, allowSetters = true)
    private Notification notifications;

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
    }

    public Reservation notifications(Notification notification) {
        this.setNotifications(notification);
        return this;
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
