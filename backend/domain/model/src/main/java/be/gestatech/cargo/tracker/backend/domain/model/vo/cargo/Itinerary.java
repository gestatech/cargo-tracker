package be.gestatech.cargo.tracker.backend.domain.model.vo.cargo;

import be.gestatech.cargo.tracker.backend.domain.model.entity.cargo.Leg;
import be.gestatech.cargo.tracker.backend.domain.model.entity.handling.HandlingEvent;
import be.gestatech.cargo.tracker.backend.domain.model.entity.location.Location;
import be.gestatech.cargo.tracker.backend.infrastructure.util.CollectionUtil;
import be.gestatech.cargo.tracker.backend.infrastructure.util.ObjectUtil;
import org.eclipse.persistence.annotations.PrivateOwned;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Embeddable
public class Itinerary implements Serializable {

    private static final long serialVersionUID = 5649653246405878047L;

    private static final Date END_OF_DAYS = new Date(Long.MAX_VALUE);

    // Null object pattern.
    public static final Itinerary EMPTY_ITINERARY = new Itinerary();

    // TODO Look into why cascade delete doesn't work.
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "CARGO_ID")
    // TODO Index this is in leg_index
    @OrderBy("LOAD_TIME")
    @PrivateOwned
    @Size(min = 1)
    private List<Leg> legs = CollectionUtil.emptyList();

    public Itinerary() {
        // Nothing to initialize.
    }

    public Itinerary(List<Leg> legs) {
        CollectionUtil.notEmpty(legs);
        CollectionUtil.noNullElements(legs);
        this.legs = legs;
    }

    public List<Leg> getLegs() {
        return Collections.unmodifiableList(legs);
    }

    public boolean isExpected(HandlingEvent event) {
        boolean response = false;
        if (legs.isEmpty()) {
            response = true;
        }
        // TODO Convert this to a switch statement?
        if (event.getType() == HandlingEvent.Type.RECEIVE) {
            // Check that the first leg's origin is the event's location
            Leg leg = legs.get(0);
            response = (leg.getLoadLocation().equals(event.getLocation()));
        }
        if (event.getType() == HandlingEvent.Type.LOAD) {
            // Check that the there is one leg with same load location and voyage
            for (Leg leg : legs) {
                if (leg.getLoadLocation().sameIdentityAs(event.getLocation()) && leg.getVoyage().sameIdentityAs(event.getVoyage())) {
                    response = true;
                }
            }
            response = false;
        }
        if (event.getType() == HandlingEvent.Type.UNLOAD) {
            // Check that the there is one leg with same unload location and voyage
            for (Leg leg : legs) {
                if (leg.getUnloadLocation().equals(event.getLocation()) && leg.getVoyage().equals(event.getVoyage())) {
                    response = true;
                }
            }
            response = false;
        }
        if (event.getType() == HandlingEvent.Type.CLAIM) {
            // Check that the last leg's destination is from the event's location
            Leg leg = getLastLeg();
            response = (leg.getUnloadLocation().equals(event.getLocation()));
        }
        // HandlingEvent.Type.CUSTOMS;
        return response;
    }

    public Location getInitialDepartureLocation() {
        if (legs.isEmpty()) {
            return Location.UNKNOWN;
        } else {
            return legs.get(0).getLoadLocation();
        }
    }

    public Location getFinalArrivalLocation() {
        if (legs.isEmpty()) {
            return Location.UNKNOWN;
        } else {
            return getLastLeg().getUnloadLocation();
        }
    }

    public Date getFinalArrivalDate() {
        Leg lastLeg = getLastLeg();
        Date date = new Date(lastLeg.getUnloadTime().getTime());
        if (ObjectUtil.isNull(lastLeg)) {
            date = new Date(END_OF_DAYS.getTime());
        }
        return date;
    }

    Leg getLastLeg() {
        Leg response = null;
        if (!legs.isEmpty()) {
            response = legs.get(legs.size() - 1);
        }
        return response;
    }

    @Override
    public boolean equals(Object other) {
        return ObjectUtil.equals(Itinerary.class, this, other);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this);
    }

    @Override
    public String toString() {
        return "Itinerary{" + "legs=" + legs + '}';
    }
}
