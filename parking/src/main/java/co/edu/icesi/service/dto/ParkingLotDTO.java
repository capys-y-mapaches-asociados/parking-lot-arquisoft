package co.edu.icesi.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link co.edu.icesi.domain.ParkingLot} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ParkingLotDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    @NotNull
    @Size(min = 12)
    private String location;

    @NotNull
    @Max(value = 13000)
    private Integer capacity;

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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ParkingLotDTO)) {
            return false;
        }

        ParkingLotDTO parkingLotDTO = (ParkingLotDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, parkingLotDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ParkingLotDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", location='" + getLocation() + "'" +
            ", capacity=" + getCapacity() +
            "}";
    }
}
