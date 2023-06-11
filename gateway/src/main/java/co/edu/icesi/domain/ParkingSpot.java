package co.edu.icesi.domain;

import co.edu.icesi.domain.enumeration.ParkingSpotStatus;
import co.edu.icesi.domain.enumeration.ParkingSpotType;
import co.edu.icesi.domain.enumeration.ParkingSpotVehicle;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A ParkingSpot.
 */
@Table("parking_spot")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "parkingspot")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ParkingSpot implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Max(value = 13000)
    @Column("number")
    private Integer number;

    @NotNull(message = "must not be null")
    @Column("status")
    private ParkingSpotStatus status;

    @NotNull(message = "must not be null")
    @Column("spot_type")
    private ParkingSpotType spotType;

    @NotNull(message = "must not be null")
    @Column("spot_vehicle")
    private ParkingSpotVehicle spotVehicle;

    @Transient
    @Transient
    @JsonIgnoreProperties(value = { "parkingSpots", "parkingSpots", "barriers" }, allowSetters = true)
    private ParkingLot parkingLotId;

    @Transient
    @Transient
    @JsonIgnoreProperties(value = { "parkingSpots", "parkingSpots", "barriers" }, allowSetters = true)
    private ParkingLot parkingLotId;

    @Column("parking_lot_id_id")
    private Long parkingLotIdId;

    @Column("parking_lot_id_id")
    private Long parkingLotIdId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ParkingSpot id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumber() {
        return this.number;
    }

    public ParkingSpot number(Integer number) {
        this.setNumber(number);
        return this;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public ParkingSpotStatus getStatus() {
        return this.status;
    }

    public ParkingSpot status(ParkingSpotStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(ParkingSpotStatus status) {
        this.status = status;
    }

    public ParkingSpotType getSpotType() {
        return this.spotType;
    }

    public ParkingSpot spotType(ParkingSpotType spotType) {
        this.setSpotType(spotType);
        return this;
    }

    public void setSpotType(ParkingSpotType spotType) {
        this.spotType = spotType;
    }

    public ParkingSpotVehicle getSpotVehicle() {
        return this.spotVehicle;
    }

    public ParkingSpot spotVehicle(ParkingSpotVehicle spotVehicle) {
        this.setSpotVehicle(spotVehicle);
        return this;
    }

    public void setSpotVehicle(ParkingSpotVehicle spotVehicle) {
        this.spotVehicle = spotVehicle;
    }

    public ParkingLot getParkingLotId() {
        return this.parkingLotId;
    }

    public void setParkingLotId(ParkingLot parkingLot) {
        this.parkingLotId = parkingLot;
        this.parkingLotIdId = parkingLot != null ? parkingLot.getId() : null;
    }

    public void setParkingLotId(ParkingLot parkingLot) {
        this.parkingLotId = parkingLot;
        this.parkingLotIdId = parkingLot != null ? parkingLot.getId() : null;
    }

    public ParkingSpot parkingLotId(ParkingLot parkingLot) {
        this.setParkingLotId(parkingLot);
        return this;
    }

    public ParkingLot getParkingLotId() {
        return this.parkingLotId;
    }

    public void setParkingLotId(ParkingLot parkingLot) {
        this.parkingLotId = parkingLot;
        this.parkingLotIdId = parkingLot != null ? parkingLot.getId() : null;
    }

    public void setParkingLotId(ParkingLot parkingLot) {
        this.parkingLotId = parkingLot;
        this.parkingLotIdId = parkingLot != null ? parkingLot.getId() : null;
    }

    public ParkingSpot parkingLotId(ParkingLot parkingLot) {
        this.setParkingLotId(parkingLot);
        return this;
    }

    public Long getParkingLotIdId() {
        return this.parkingLotIdId;
    }

    public void setParkingLotIdId(Long parkingLot) {
        this.parkingLotIdId = parkingLot;
    }

    public Long getParkingLotIdId() {
        return this.parkingLotIdId;
    }

    public void setParkingLotIdId(Long parkingLot) {
        this.parkingLotIdId = parkingLot;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ParkingSpot)) {
            return false;
        }
        return id != null && id.equals(((ParkingSpot) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ParkingSpot{" +
            "id=" + getId() +
            ", number=" + getNumber() +
            ", status='" + getStatus() + "'" +
            ", spotType='" + getSpotType() + "'" +
            ", spotVehicle='" + getSpotVehicle() + "'" +
            "}";
    }
}
