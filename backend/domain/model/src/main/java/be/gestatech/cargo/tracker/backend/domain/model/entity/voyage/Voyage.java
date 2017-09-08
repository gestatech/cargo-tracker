package be.gestatech.cargo.tracker.backend.domain.model.entity.voyage;

import be.gestatech.cargo.tracker.backend.domain.model.entity.generic.Identity;
import be.gestatech.cargo.tracker.backend.domain.model.entity.location.Location;
import be.gestatech.cargo.tracker.backend.domain.model.vo.voyage.Schedule;
import be.gestatech.cargo.tracker.backend.domain.model.vo.voyage.VoyageNumber;
import be.gestatech.cargo.tracker.backend.infrastructure.util.ObjectUtil;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@NamedQueries({
        @NamedQuery(name = "Voyage.findByVoyageNumber", query = "Select v from Voyage v where v.voyageNumber = :voyageNumber"),
        @NamedQuery(name = "Voyage.findAll", query = "Select v from Voyage v order by v.voyageNumber")
})
public class Voyage extends Identity {

    private static final long serialVersionUID = -1402525320004172074L;

    @Embedded
    @NotNull
    private VoyageNumber voyageNumber;

    @Embedded
    @NotNull
    private Schedule schedule;

    // Null object pattern
    public static final Voyage NONE = new Voyage(new VoyageNumber(""), Schedule.EMPTY);

    public Voyage() {
        // Nothing to initialize
    }

    public Voyage(VoyageNumber voyageNumber, Schedule schedule) {
        ObjectUtil.requireNonNull(voyageNumber, "Voyage number is required");
        ObjectUtil.requireNonNull(schedule, "Schedule is required");
        this.voyageNumber = voyageNumber;
        this.schedule = schedule;
    }

    public VoyageNumber getVoyageNumber() {
        return voyageNumber;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    @Override
    public boolean equals(Object other) {
        return ObjectUtil.equals(Voyage.class, this, other);
    }

    @Override
    public int hashCode() {
        return ObjectUtil.hash(this);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Voyage{");
        sb.append("voyageNumber=").append(voyageNumber);
        sb.append(", schedule=").append(schedule);
        sb.append('}');
        return sb.toString();
    }

    public boolean sameIdentityAs(Voyage other) {
        return this.getVoyageNumber().sameValueAs(other.getVoyageNumber());
    }

    /**
     * Builder pattern is used for incremental construction of a Voyage
     * aggregate. This serves as an aggregate factory.
     */
    public static class Builder {

        private List<CarrierMovement> carrierMovements = new ArrayList<>();
        private VoyageNumber voyageNumber;
        private Location departureLocation;

        public Builder(VoyageNumber voyageNumber, Location departureLocation) {
            ObjectUtil.requireNonNull(voyageNumber, "Voyage number is required");
            ObjectUtil.requireNonNull(departureLocation, "Departure location is required");
            this.voyageNumber = voyageNumber;
            this.departureLocation = departureLocation;
        }

        public Builder addMovement(Location arrivalLocation, Date departureTime, Date arrivalTime) {
            carrierMovements.add(new CarrierMovement(departureLocation, arrivalLocation, departureTime, arrivalTime));
            // Next departure location is the same as this arrival location
            this.departureLocation = arrivalLocation;
            return this;
        }

        public Voyage build() {
            return new Voyage(voyageNumber, new Schedule(carrierMovements));
        }
    }
}
