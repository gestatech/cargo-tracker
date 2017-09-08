package be.gestatech.cargo.tracker.backend.domain.model.vo.voyage;

import be.gestatech.cargo.tracker.backend.domain.model.entity.voyage.CarrierMovement;
import be.gestatech.cargo.tracker.backend.infrastructure.util.CollectionUtil;
import be.gestatech.cargo.tracker.backend.infrastructure.util.ObjectUtil;
import org.eclipse.persistence.annotations.PrivateOwned;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Embeddable
public class Schedule implements Serializable {

    private static final long serialVersionUID = -377957895150575427L;
    // Null object pattern.
    public static final Schedule EMPTY = new Schedule();
    // TODO Look into why cascade delete doesn't work.
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "VOYAGE_ID")
    // TODO Index as cm_index
    @PrivateOwned
    @NotNull
    @Size(min = 1)
    private List<CarrierMovement> carrierMovements = Collections.emptyList();

    public Schedule() {
        // Nothing to initialize.
    }

    public Schedule(List<CarrierMovement> carrierMovements) {
        ObjectUtil.requireNonNull(carrierMovements);
        CollectionUtil.noNullElements(carrierMovements);
        CollectionUtil.notEmpty(carrierMovements);
        this.carrierMovements = carrierMovements;
    }

    public List<CarrierMovement> getCarrierMovements() {
        return Collections.unmodifiableList(carrierMovements);
    }

    @Override
    public boolean equals(Object other) {
        return ObjectUtil.equals(Schedule.class, this, other);
    }

    @Override
    public int hashCode() {
        return ObjectUtil.hash(this);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Schedule{");
        sb.append("carrierMovements=").append(carrierMovements);
        sb.append('}');
        return sb.toString();
    }

    private boolean sameValueAs(Schedule other) {
        return ObjectUtil.deepEquals(getCarrierMovements(), other.getCarrierMovements());
    }
}
