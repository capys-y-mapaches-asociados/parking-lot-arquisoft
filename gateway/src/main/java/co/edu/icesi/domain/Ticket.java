package co.edu.icesi.domain;

import co.edu.icesi.domain.enumeration.TicketStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Ticket.
 */
@Table("ticket")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Ticket implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Pattern(regexp = "^[A-F0-9]{6, 10}$")
    @Column("ticket_code")
    private String ticketCode;

    @NotNull(message = "must not be null")
    @Column("issued_at")
    private Instant issuedAt;

    @NotNull(message = "must not be null")
    @Column("entry_time")
    private Instant entryTime;

    @NotNull(message = "must not be null")
    @Column("exit_time")
    private Instant exitTime;

    @NotNull(message = "must not be null")
    @Column("status")
    private TicketStatus status;

    @Transient
    @JsonIgnoreProperties(value = { "parkingLotId" }, allowSetters = true)
    private ParkingSpot parkingSpotId;

    @Column("parking_spot_id_id")
    private Long parkingSpotIdId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Ticket id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTicketCode() {
        return this.ticketCode;
    }

    public Ticket ticketCode(String ticketCode) {
        this.setTicketCode(ticketCode);
        return this;
    }

    public void setTicketCode(String ticketCode) {
        this.ticketCode = ticketCode;
    }

    public Instant getIssuedAt() {
        return this.issuedAt;
    }

    public Ticket issuedAt(Instant issuedAt) {
        this.setIssuedAt(issuedAt);
        return this;
    }

    public void setIssuedAt(Instant issuedAt) {
        this.issuedAt = issuedAt;
    }

    public Instant getEntryTime() {
        return this.entryTime;
    }

    public Ticket entryTime(Instant entryTime) {
        this.setEntryTime(entryTime);
        return this;
    }

    public void setEntryTime(Instant entryTime) {
        this.entryTime = entryTime;
    }

    public Instant getExitTime() {
        return this.exitTime;
    }

    public Ticket exitTime(Instant exitTime) {
        this.setExitTime(exitTime);
        return this;
    }

    public void setExitTime(Instant exitTime) {
        this.exitTime = exitTime;
    }

    public TicketStatus getStatus() {
        return this.status;
    }

    public Ticket status(TicketStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public ParkingSpot getParkingSpotId() {
        return this.parkingSpotId;
    }

    public void setParkingSpotId(ParkingSpot parkingSpot) {
        this.parkingSpotId = parkingSpot;
        this.parkingSpotIdId = parkingSpot != null ? parkingSpot.getId() : null;
    }

    public Ticket parkingSpotId(ParkingSpot parkingSpot) {
        this.setParkingSpotId(parkingSpot);
        return this;
    }

    public Long getParkingSpotIdId() {
        return this.parkingSpotIdId;
    }

    public void setParkingSpotIdId(Long parkingSpot) {
        this.parkingSpotIdId = parkingSpot;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Ticket)) {
            return false;
        }
        return id != null && id.equals(((Ticket) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Ticket{" +
            "id=" + getId() +
            ", ticketCode='" + getTicketCode() + "'" +
            ", issuedAt='" + getIssuedAt() + "'" +
            ", entryTime='" + getEntryTime() + "'" +
            ", exitTime='" + getExitTime() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
