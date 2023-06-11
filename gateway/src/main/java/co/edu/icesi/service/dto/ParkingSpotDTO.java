package co.edu.icesi.service.dto;

import co.edu.icesi.domain.enumeration.ParkingSpotStatus;
import co.edu.icesi.domain.enumeration.ParkingSpotType;
import co.edu.icesi.domain.enumeration.ParkingSpotVehicle;
import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link co.edu.icesi.domain.ParkingSpot} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ParkingSpotDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    @Max(value = 13000)
    private Integer number;

    @NotNull(message = "must not be null")
    private ParkingSpotStatus status;

    @NotNull(message = "must not be null")
    private ParkingSpotType spotType;

    @NotNull(message = "must not be null")
    private ParkingSpotVehicle spotVehicle;

    private ParkingLotDTO parkingLotId;

    private ParkingLotDTO parkingLotId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public ParkingSpotStatus getStatus() {
        return status;
    }

    public void setStatus(ParkingSpotStatus status) {
        this.status = status;
    }

    public ParkingSpotType getSpotType() {
        return spotType;
    }

    public void setSpotType(ParkingSpotType spotType) {
        this.spotType = spotType;
    }

    public ParkingSpotVehicle getSpotVehicle() {
        return spotVehicle;
    }

    public void setSpotVehicle(ParkingSpotVehicle spotVehicle) {
        this.spotVehicle = spotVehicle;
    }

    public ParkingLotDTO getParkingLotId() {
        return parkingLotId;
    }

    public void setParkingLotId(ParkingLotDTO parkingLotId) {
        this.parkingLotId = parkingLotId;
    }

    public ParkingLotDTO getParkingLotId() {
        return parkingLotId;
    }

    public void setParkingLotId(ParkingLotDTO parkingLotId) {
        this.parkingLotId = parkingLotId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ParkingSpotDTO)) {
            return false;
        }

        ParkingSpotDTO parkingSpotDTO = (ParkingSpotDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, parkingSpotDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ParkingSpotDTO{" +
            "id=" + getId() +
            ", number=" + getNumber() +
            ", status='" + getStatus() + "'" +
            ", spotType='" + getSpotType() + "'" +
            ", spotVehicle='" + getSpotVehicle() + "'" +
            ", parkingLotId=" + getParkingLotId() +
            ", parkingLotId=" + getParkingLotId() +
            "}";
    }
}
