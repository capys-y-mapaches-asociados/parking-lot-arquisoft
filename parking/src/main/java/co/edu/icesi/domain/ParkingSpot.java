package co.edu.icesi.domain;

import co.edu.icesi.domain.enumeration.ParkingSpotStatus;
import co.edu.icesi.domain.enumeration.ParkingSpotType;
import co.edu.icesi.domain.enumeration.ParkingSpotVehicle;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ParkingSpot.
 */
@Entity
@Table(name = "parking_spot")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ParkingSpot implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Max(value = 13000)
    @Column(name = "number", nullable = false)
    private Integer number;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ParkingSpotStatus status;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "spot_type", nullable = false)
    private ParkingSpotType spotType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "spot_vehicle", nullable = false)
    private ParkingSpotVehicle spotVehicle;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "parkingSpots", "barriers" }, allowSetters = true)
    private ParkingLot parkingLotId;

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
    }

    public ParkingSpot parkingLotId(ParkingLot parkingLot) {
        this.setParkingLotId(parkingLot);
        return this;
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
