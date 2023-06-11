package co.edu.icesi.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Notification.
 */
@Entity
@Table(name = "notification")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Notification implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "reservation_id", nullable = false)
    private UUID reservationId;

    @Size(max = 1000)
    @Column(name = "message", length = 1000)
    private String message;

    @NotNull
    @Column(name = "sent_at", nullable = false)
    private Instant sentAt;

    @NotNull
    @Column(name = "recipient_id", nullable = false)
    private UUID recipientId;

    @OneToMany(mappedBy = "notifications")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "customerId", "customerId", "notifications" }, allowSetters = true)
    private Set<Reservation> reservationIds = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Notification id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getReservationId() {
        return this.reservationId;
    }

    public Notification reservationId(UUID reservationId) {
        this.setReservationId(reservationId);
        return this;
    }

    public void setReservationId(UUID reservationId) {
        this.reservationId = reservationId;
    }

    public String getMessage() {
        return this.message;
    }

    public Notification message(String message) {
        this.setMessage(message);
        return this;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getSentAt() {
        return this.sentAt;
    }

    public Notification sentAt(Instant sentAt) {
        this.setSentAt(sentAt);
        return this;
    }

    public void setSentAt(Instant sentAt) {
        this.sentAt = sentAt;
    }

    public UUID getRecipientId() {
        return this.recipientId;
    }

    public Notification recipientId(UUID recipientId) {
        this.setRecipientId(recipientId);
        return this;
    }

    public void setRecipientId(UUID recipientId) {
        this.recipientId = recipientId;
    }

    public Set<Reservation> getReservationIds() {
        return this.reservationIds;
    }

    public void setReservationIds(Set<Reservation> reservations) {
        if (this.reservationIds != null) {
            this.reservationIds.forEach(i -> i.setNotifications(null));
        }
        if (reservations != null) {
            reservations.forEach(i -> i.setNotifications(this));
        }
        this.reservationIds = reservations;
    }

    public Notification reservationIds(Set<Reservation> reservations) {
        this.setReservationIds(reservations);
        return this;
    }

    public Notification addReservationId(Reservation reservation) {
        this.reservationIds.add(reservation);
        reservation.setNotifications(this);
        return this;
    }

    public Notification removeReservationId(Reservation reservation) {
        this.reservationIds.remove(reservation);
        reservation.setNotifications(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Notification)) {
            return false;
        }
        return id != null && id.equals(((Notification) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Notification{" +
            "id=" + getId() +
            ", reservationId='" + getReservationId() + "'" +
            ", message='" + getMessage() + "'" +
            ", sentAt='" + getSentAt() + "'" +
            ", recipientId='" + getRecipientId() + "'" +
            "}";
    }
}
