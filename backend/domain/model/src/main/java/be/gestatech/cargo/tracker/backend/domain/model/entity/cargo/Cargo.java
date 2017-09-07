package be.gestatech.cargo.tracker.backend.domain.model.entity.cargo;

import be.gestatech.cargo.tracker.backend.domain.model.entity.location.Location;
import be.gestatech.cargo.tracker.backend.domain.model.entity.generic.Identity;
import be.gestatech.cargo.tracker.backend.domain.model.specification.cargo.RouteSpecification;
import be.gestatech.cargo.tracker.backend.domain.model.vo.cargo.Delivery;
import be.gestatech.cargo.tracker.backend.domain.model.vo.cargo.Itinerary;
import be.gestatech.cargo.tracker.backend.domain.model.vo.TrackingId;
import be.gestatech.cargo.tracker.backend.infrastructure.util.ObjectsWrapper;

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
    private TrackingId trackingId;

    @ManyToOne
    @JoinColumn(name = "ORIGIN_ID", updatable = false)
    private Location origin;

    @Embedded
    private RouteSpecification routeSpecification;

    // This should be nullable: https://java.net/jira/browse/JPA_SPEC-42
    @Embedded
    private Itinerary itinerary;

    @Embedded
    private Delivery delivery;

    public Cargo() {
        // Nothing to initialize.
    }

    public Cargo(TrackingId trackingId, RouteSpecification routeSpecification) {
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


    public Delivery getDelivery() {
        return delivery;
    }

    public Itinerary getItinerary() {
        return ObjectsWrapper.nullSafe(this.itinerary, Itinerary.EMPTY_ITINERARY);
    }

    public void specifyNewRoute(RouteSpecification routeSpecification) {
        Objects.requireNonNull(routeSpecification, "Route specification is required");
        this.routeSpecification = routeSpecification;
        this.delivery = delivery.updateOnRouting(this.routeSpecification, this.itinerary);
    }

    public void assignToRoute(Itinerary itinerary) {
        Objects.requireNonNull(itinerary, "Itinerary is required for assignment");
        this.itinerary = itinerary;
        this.delivery = delivery.updateOnRouting(this.routeSpecification, this.itinerary);
    }

    public void deriveDeliveryProgress(HandlingHistory handlingHistory) {
        this.delivery = Delivery.derivedFrom(getRouteSpecification(), getItinerary(), handlingHistory);
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

    private boolean sameIdentityAs(Cargo other) {
        return trackingId.sameValueAs(other.trackingId);
    }
}
