package be.gestatech.cargo.tracker.backend.domain.model.specification.cargo;

import be.gestatech.cargo.tracker.backend.domain.model.entity.location.Location;
import be.gestatech.cargo.tracker.backend.domain.model.vo.cargo.Itinerary;
import be.gestatech.cargo.tracker.backend.infrastructure.specification.AbstractSpecification;
import be.gestatech.cargo.tracker.backend.infrastructure.util.ObjectUtil;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Embeddable
public class RouteSpecification extends AbstractSpecification<Itinerary> implements Serializable {

    private static final long serialVersionUID = 2221386138282968014L;
    @ManyToOne
    @JoinColumn(name = "spec_origin_id", updatable = false)

    private Location origin;
    @ManyToOne
    @JoinColumn(name = "spec_destination_id")
    private Location destination;
    @Temporal(TemporalType.DATE)
    @Column(name = "spec_arrival_deadline")
    @NotNull
    private Date arrivalDeadline;

    public RouteSpecification() {
    }

    public RouteSpecification(Location origin, Location destination, Date arrivalDeadline) {
        ObjectUtil.requireNonNull(origin, "Origin is required");
        ObjectUtil.requireNonNull(destination, "Destination is required");
        ObjectUtil.requireNonNull(arrivalDeadline, "Arrival deadline is required");
        ObjectUtil.requireTrue(!origin.sameIdentityAs(destination), "Origin and destination can't be the same: ", origin);
        this.origin = origin;
        this.destination = destination;
        this.arrivalDeadline = (Date) arrivalDeadline.clone();
    }

    public Location getOrigin() {
        return origin;
    }

    public Location getDestination() {
        return destination;
    }

    public Date getArrivalDeadline() {
        return new Date(arrivalDeadline.getTime());
    }

    @Override
    public boolean isSatisfiedBy(Itinerary itinerary) {
        return itinerary != null
                && getOrigin().sameIdentityAs(
                itinerary.getInitialDepartureLocation())
                && getDestination().sameIdentityAs(
                itinerary.getFinalArrivalLocation())
                && getArrivalDeadline().after(itinerary.getFinalArrivalDate());
    }

    @Override
    public boolean equals(Object other) {
       return ObjectUtil.equals(RouteSpecification.class, this, other);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RouteSpecification{");
        sb.append("origin=").append(origin);
        sb.append(", destination=").append(destination);
        sb.append(", arrivalDeadline=").append(arrivalDeadline);
        sb.append('}');
        return sb.toString();
    }
}
