package co.edu.icesi.service.dto;

import co.edu.icesi.domain.enumeration.BarrierStatus;
import co.edu.icesi.domain.enumeration.BarrierType;
import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link co.edu.icesi.domain.Barrier} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BarrierDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private String name;

    @NotNull(message = "must not be null")
    private BarrierType type;

    @NotNull(message = "must not be null")
    private BarrierStatus status;

    private ParkingLotDTO parkingLot;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BarrierType getType() {
        return type;
    }

    public void setType(BarrierType type) {
        this.type = type;
    }

    public BarrierStatus getStatus() {
        return status;
    }

    public void setStatus(BarrierStatus status) {
        this.status = status;
    }

    public ParkingLotDTO getParkingLot() {
        return parkingLot;
    }

    public void setParkingLot(ParkingLotDTO parkingLot) {
        this.parkingLot = parkingLot;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BarrierDTO)) {
            return false;
        }

        BarrierDTO barrierDTO = (BarrierDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, barrierDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BarrierDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", type='" + getType() + "'" +
            ", status='" + getStatus() + "'" +
            ", parkingLot=" + getParkingLot() +
            "}";
    }
}
