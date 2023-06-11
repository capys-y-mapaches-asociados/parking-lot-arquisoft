package co.edu.icesi.service.dto;

import co.edu.icesi.domain.enumeration.TicketStatus;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link co.edu.icesi.domain.Ticket} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TicketDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    @Pattern(regexp = "^[A-F0-9]{6, 10}$")
    private String ticketCode;

    @NotNull(message = "must not be null")
    private Instant issuedAt;

    @NotNull(message = "must not be null")
    private Instant entryTime;

    @NotNull(message = "must not be null")
    private Instant exitTime;

    @NotNull(message = "must not be null")
    private TicketStatus status;

    private ParkingSpotDTO parkingSpotId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTicketCode() {
        return ticketCode;
    }

    public void setTicketCode(String ticketCode) {
        this.ticketCode = ticketCode;
    }

    public Instant getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(Instant issuedAt) {
        this.issuedAt = issuedAt;
    }

    public Instant getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(Instant entryTime) {
        this.entryTime = entryTime;
    }

    public Instant getExitTime() {
        return exitTime;
    }

    public void setExitTime(Instant exitTime) {
        this.exitTime = exitTime;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public ParkingSpotDTO getParkingSpotId() {
        return parkingSpotId;
    }

    public void setParkingSpotId(ParkingSpotDTO parkingSpotId) {
        this.parkingSpotId = parkingSpotId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TicketDTO)) {
            return false;
        }

        TicketDTO ticketDTO = (TicketDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, ticketDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TicketDTO{" +
            "id=" + getId() +
            ", ticketCode='" + getTicketCode() + "'" +
            ", issuedAt='" + getIssuedAt() + "'" +
            ", entryTime='" + getEntryTime() + "'" +
            ", exitTime='" + getExitTime() + "'" +
            ", status='" + getStatus() + "'" +
            ", parkingSpotId=" + getParkingSpotId() +
            "}";
    }
}
