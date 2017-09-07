package be.gestatech.cargo.tracker.backend.domain.model.entity.cargo;

import be.gestatech.cargo.tracker.backend.domain.model.entity.location.Location;
import be.gestatech.cargo.tracker.backend.domain.model.entity.generic.Identity;
import be.gestatech.cargo.tracker.backend.domain.specification.RouteSpecification;
import be.gestatech.cargo.tracker.backend.domain.vo.cargo.Delivery;
import be.gestatech.cargo.tracker.backend.domain.vo.cargo.Itinerary;
import be.gestatech.cargo.tracker.backend.domain.vo.TrackingId;

import javax.persistence.*;
import java.util.Objects;

@Entity
@NamedQueries({
        @NamedQuery(name = "Cargo.findAll", query = "Select c from Cargo c"),
        @NamedQuery(name = "Cargo.findByTrackingId", query = "Select c from Cargo c where c.trackingId = :trackingId"),
        @NamedQuery(name = "Cargo.getAllTrackingIds", query = "Select c.trackingId from Cargo c")
})
public class Cargo extends Identity {

    private static final long serialVersionUID = 197440919219066342L;

    @Embedded
    private be.gestatech.cargo.tracker.backend.domain.vo.TrackingId trackingId;

    @ManyToOne
    @JoinColumn(name = "ORIGIN_ID", updatable = false)
    private Location origin;

    @Embedded
    private RouteSpecification routeSpecification;

    @Embedded // This should be nullable: https://java.net/jira/browse/JPA_SPEC-42
    private Itinerary itinerary;

    @Embedded
    private Delivery delivery;

    public Cargo() {
        // Nothing to initialize.
    }

    public Cargo(be.gestatech.cargo.tracker.backend.domain.vo.TrackingId trackingId, RouteSpecification routeSpecification) {
        Objects.requireNonNull(trackingId, "Tracking ID is required");
        Objects.requireNonNull(routeSpecification, "Route specification is required");

        this.trackingId = trackingId;
        this.origin = routeSpecification.getOrigin();
        this.routeSpecification = routeSpecification;

        this.delivery = Delivery.derivedFrom(this.routeSpecification, this.itinerary, HandlingHistory.EMPTY);
        this.itinerary = Itinerary.EMPTY_ITINERARY;
    }

    public TrackingId getTrackingId() {
        return trackingId;
    }

    public void setOrigin(Location origin) {
        this.origin = origin;
    }

    public Location getOrigin() {
        return origin;
    }

    public RouteSpecification getRouteSpecification() {
        return routeSpecification;
    }

    /**
     * @return The delivery. Never null.
     */
    public Delivery getDelivery() {
        return delivery;
    }

    /**
     * @return The itinerary. Never null.
     */
    public Itinerary getItinerary() {
        return DomainObjectUtils.nullSafe(this.itinerary,
                Itinerary.EMPTY_ITINERARY);
    }

    /**
     * Specifies a new route for this cargo.
     */
    public void specifyNewRoute(RouteSpecification routeSpecification) {
        Objects.requireNonNull(routeSpecification, "Route specification is required");

        this.routeSpecification = routeSpecification;
        // Handling consistency within the Cargo aggregate synchronously
        this.delivery = delivery.updateOnRouting(this.routeSpecification,
                this.itinerary);
    }

    public void assignToRoute(Itinerary itinerary) {
        Objects.requireNonNull(itinerary, "Itinerary is required for assignment");

        this.itinerary = itinerary;
        // Handling consistency within the Cargo aggregate synchronously
        this.delivery = delivery.updateOnRouting(this.routeSpecification,
                this.itinerary);
    }

    /**
     * Updates all aspects of the cargo aggregate status based on the current
     * route specification, itinerary and handling of the cargo.
     * <p/>
     * When either of those three changes, i.e. when a new route is specified
     * for the cargo, the cargo is assigned to a route or when the cargo is
     * handled, the status must be re-calculated.
     * <p/>
     * {@link RouteSpecification} and {@link Itinerary} are both inside the
     * Cargo aggregate, so changes to them cause the status to be updated
     * <b>synchronously</b>, but changes to the delivery history (when a cargo
     * is handled) cause the status update to happen <b>asynchronously</b> since
     * {@link HandlingEvent} is in a different aggregate.
     *
     * @param handlingHistory handling history
     */
    public void deriveDeliveryProgress(HandlingHistory handlingHistory) {
        this.delivery = Delivery.derivedFrom(getRouteSpecification(), getItinerary(),
                handlingHistory);
    }


    @Override
    public boolean equals(Object other) {
        boolean response = (other instanceof Identity);
        if (response) {
            be.gestatech.cargo.tracker.backend.domain.vo.Cargo cargo = (be.gestatech.cargo.tracker.backend.domain.vo.Cargo) other;
            response = sameIdentityAs(cargo);
        }
        return response;
    }

    @Override
    public int hashCode() {
        return trackingId.hashCode();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Cargo{");
        sb.append("trackingId=").append(trackingId);
        sb.append(", origin=").append(origin);
        sb.append(", routeSpecification=").append(routeSpecification);
        sb.append(", itinerary=").append(itinerary);
        sb.append(", delivery=").append(delivery);
        sb.append('}');
        return sb.toString();
    }

    private boolean sameIdentityAs(be.gestatech.cargo.tracker.backend.domain.vo.Cargo other) {
        return trackingId.sameValueAs(other.trackingId);
    }
}
