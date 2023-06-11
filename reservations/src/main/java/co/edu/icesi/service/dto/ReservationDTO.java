package co.edu.icesi.service.dto;

import co.edu.icesi.domain.enumeration.ReservationStatus;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link co.edu.icesi.domain.Reservation} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ReservationDTO implements Serializable {

    private Long id;

    @NotNull
    private UUID customerId;

    @NotNull
    private UUID parkingSpotId;

    @NotNull
    private Instant startTime;

    @NotNull
    private Instant endTime;

    @NotNull
    private ReservationStatus status;

    @NotNull
    @Pattern(regexp = "^([A-Z]{2})-([A-Fa-f0-9]{10, 14})$")
    private String reservationCode;

    private CustomerDTO customerId;

    private NotificationDTO notifications;

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

    public UUID getParkingSpotId() {
        return parkingSpotId;
    }

    public void setParkingSpotId(UUID parkingSpotId) {
        this.parkingSpotId = parkingSpotId;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public String getReservationCode() {
        return reservationCode;
    }

    public void setReservationCode(String reservationCode) {
        this.reservationCode = reservationCode;
    }

    public CustomerDTO getCustomerId() {
        return customerId;
    }

    public void setCustomerId(CustomerDTO customerId) {
        this.customerId = customerId;
    }

    public NotificationDTO getNotifications() {
        return notifications;
    }

    public void setNotifications(NotificationDTO notifications) {
        this.notifications = notifications;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReservationDTO)) {
            return false;
        }

        ReservationDTO reservationDTO = (ReservationDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, reservationDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ReservationDTO{" +
            "id=" + getId() +
            ", customerId='" + getCustomerId() + "'" +
            ", parkingSpotId='" + getParkingSpotId() + "'" +
            ", startTime='" + getStartTime() + "'" +
            ", endTime='" + getEndTime() + "'" +
            ", status='" + getStatus() + "'" +
            ", reservationCode='" + getReservationCode() + "'" +
            ", customerId=" + getCustomerId() +
            ", notifications=" + getNotifications() +
            "}";
    }
}
