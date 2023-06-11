package co.edu.icesi.domain;

import co.edu.icesi.domain.enumeration.BarrierStatus;
import co.edu.icesi.domain.enumeration.BarrierType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Barrier.
 */
@Entity
@Table(name = "barrier")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Barrier implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private BarrierType type;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BarrierStatus status;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "parkingSpots", "parkingSpots", "barriers" }, allowSetters = true)
    private ParkingLot parkingLot;

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
    }

    public Barrier parkingLot(ParkingLot parkingLot) {
        this.setParkingLot(parkingLot);
        return this;
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
