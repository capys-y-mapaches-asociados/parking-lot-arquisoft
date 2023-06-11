package co.edu.icesi.domain;

import co.edu.icesi.domain.enumeration.BarrierStatus;
import co.edu.icesi.domain.enumeration.BarrierType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Barrier.
 */
@Table("barrier")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "barrier")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Barrier implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("name")
    private String name;

    @NotNull(message = "must not be null")
    @Column("type")
    private BarrierType type;

    @NotNull(message = "must not be null")
    @Column("status")
    private BarrierStatus status;

    @Transient
    @JsonIgnoreProperties(value = { "parkingSpots", "parkingSpots", "barriers" }, allowSetters = true)
    private ParkingLot parkingLot;

    @Column("parking_lot_id")
    private Long parkingLotId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Barrier id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Barrier name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BarrierType getType() {
        return this.type;
    }

    public Barrier type(BarrierType type) {
        this.setType(type);
        return this;
    }

    public void setType(BarrierType type) {
        this.type = type;
    }

    public BarrierStatus getStatus() {
        return this.status;
    }

    public Barrier status(BarrierStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(BarrierStatus status) {
        this.status = status;
    }

    public ParkingLot getParkingLot() {
        return this.parkingLot;
    }

    public void setParkingLot(ParkingLot parkingLot) {
        this.parkingLot = parkingLot;
        this.parkingLotId = parkingLot != null ? parkingLot.getId() : null;
    }

    public Barrier parkingLot(ParkingLot parkingLot) {
        this.setParkingLot(parkingLot);
        return this;
    }

    public Long getParkingLotId() {
        return this.parkingLotId;
    }

    public void setParkingLotId(Long parkingLot) {
        this.parkingLotId = parkingLot;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Barrier)) {
            return false;
        }
        return id != null && id.equals(((Barrier) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Barrier{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", type='" + getType() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
