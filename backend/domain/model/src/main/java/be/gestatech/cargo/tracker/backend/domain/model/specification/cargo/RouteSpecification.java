package be.gestatech.cargo.tracker.backend.domain.model.specification.cargo;

import be.gestatech.cargo.tracker.backend.domain.model.entity.location.Location;
import be.gestatech.cargo.tracker.backend.domain.model.vo.cargo.Itinerary;
import be.gestatech.cargo.tracker.backend.infrastructure.specification.AbstractSpecification;
import be.gestatech.cargo.tracker.backend.infrastructure.util.ObjectsWrapper;

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
        ObjectsWrapper.requireNonNull(origin, "Origin is required");
        ObjectsWrapper.requireNonNull(destination, "Destination is required");
        ObjectsWrapper.requireNonNull(arrivalDeadline, "Arrival deadline is required");
        ObjectsWrapper.isTrue(!origin.sameIdentityAs(destination), "Origin and destination can't be the same: ", origin);

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

    private boolean sameValueAs(RouteSpecification other) {
        return other != null
                && new EqualsBuilder().append(this.origin, other.origin)
                .append(this.destination, other.destination)
                .append(this.arrivalDeadline, other.arrivalDeadline)
                .isEquals();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RouteSpecification that = (RouteSpecification) o;

        return sameValueAs(that);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.origin)
                .append(this.destination).append(this.arrivalDeadline)
                .toHashCode();
    }
}
