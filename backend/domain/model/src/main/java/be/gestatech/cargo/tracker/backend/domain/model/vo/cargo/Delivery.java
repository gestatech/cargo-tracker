package be.gestatech.cargo.tracker.backend.domain.model.vo.cargo;

import be.gestatech.cargo.tracker.backend.domain.model.entity.cargo.Leg;
import be.gestatech.cargo.tracker.backend.domain.model.entity.location.Location;
import be.gestatech.cargo.tracker.backend.domain.model.specification.cargo.RouteSpecification;
import be.gestatech.cargo.tracker.backend.infrastructure.util.ObjectsWrapper;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;

@Embeddable
public class Delivery implements Serializable {

    private static final long serialVersionUID = 1317870620046455603L;

    // Null object pattern.
    public static final Date ETA_UNKOWN = null;

    // Null object pattern
    public static final HandlingActivity NO_ACTIVITY = new HandlingActivity();

    @Enumerated(EnumType.STRING)
    @Column(name = "TRANSPORT_STATUS")
    @NotNull
    private TransportStatus transportStatus;

    @ManyToOne
    @JoinColumn(name = "LAST_KNOWN_LOCATION_ID")
    private Location lastKnownLocation;

    @ManyToOne
    @JoinColumn(name = "CURRENT_VOYAGE_ID")
    private Voyage currentVoyage;

    @NotNull
    private boolean misdirected;
    @Temporal(TemporalType.DATE)
    private Date eta;

    @Embedded
    private HandlingActivity nextExpectedActivity;

    @Column(name = "UNLOADED_AT_DESTINATION")
    @NotNull
    private boolean isUnloadedAtDestination;

    @Enumerated(EnumType.STRING)
    @Column(name = "ROUTING_STATUS")
    @NotNull
    private RoutingStatus routingStatus;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CALCULATED_AT")
    @NotNull
    private Date calculatedAt;

    @ManyToOne
    @JoinColumn(name = "LAST_EVENT_ID")
    private HandlingEvent lastEvent;

    public Delivery() {
        // Nothing to initialize
    }

    public Delivery(HandlingEvent lastEvent, Itinerary itinerary, RouteSpecification routeSpecification) {
        this.calculatedAt = new Date();
        this.lastEvent = lastEvent;
        this.misdirected = calculateMisdirectionStatus(itinerary);
        this.routingStatus = calculateRoutingStatus(itinerary, routeSpecification);
        this.transportStatus = calculateTransportStatus();
        this.lastKnownLocation = calculateLastKnownLocation();
        this.currentVoyage = calculateCurrentVoyage();
        this.eta = calculateEta(itinerary);
        this.nextExpectedActivity = calculateNextExpectedActivity(routeSpecification, itinerary);
        this.isUnloadedAtDestination = calculateUnloadedAtDestination(routeSpecification);
    }

    Delivery updateOnRouting(RouteSpecification routeSpecification, Itinerary itinerary) {
        ObjectsWrapper.requireNonNull(routeSpecification, "Route specification is required");
        return new Delivery(this.lastEvent, itinerary, routeSpecification);
    }

    static Delivery derivedFrom(RouteSpecification routeSpecification, Itinerary itinerary, HandlingHistory handlingHistory) {
        ObjectsWrapper.requireNonNull(routeSpecification, "Route specification is required");
        ObjectsWrapper.requireNonNull(handlingHistory, "Delivery history is required");
        HandlingEvent lastEvent = handlingHistory.getMostRecentlyCompletedEvent();
        return new Delivery(lastEvent, itinerary, routeSpecification);
    }

    public TransportStatus getTransportStatus() {
        return transportStatus;
    }

    public void setTransportStatus(TransportStatus transportStatus) {
        this.transportStatus = transportStatus;
    }

    public Location getLastKnownLocation() {
        return ObjectsWrapper.nullSafe(lastKnownLocation, Location.UNKNOWN);
    }

    public void setLastKnownLocation(Location lastKnownLocation) {
        this.lastKnownLocation = lastKnownLocation;
    }

    public void setLastEvent(HandlingEvent lastEvent) {
        this.lastEvent = lastEvent;
    }

    public Voyage getCurrentVoyage() {
        return ObjectsWrapper.nullSafe(currentVoyage, Voyage.NONE);
    }

    public boolean isMisdirected() {
        return misdirected;
    }

    public void setMisdirected(boolean misdirected) {
        this.misdirected = misdirected;
    }

    public Date getEstimatedTimeOfArrival() {
        if (eta != ETA_UNKOWN) {
            return new Date(eta.getTime());
        } else {
            return ETA_UNKOWN;
        }
    }

    public HandlingActivity getNextExpectedActivity() {
        return nextExpectedActivity;
    }

    public boolean isUnloadedAtDestination() {
        return isUnloadedAtDestination;
    }

    public void setUnloadedAtDestination(boolean isUnloadedAtDestination) {
        this.isUnloadedAtDestination = isUnloadedAtDestination;
    }

    public RoutingStatus getRoutingStatus() {
        return routingStatus;
    }

    public void setRoutingStatus(RoutingStatus routingStatus) {
        this.routingStatus = routingStatus;
    }

    public Date getCalculatedAt() {
        return new Date(calculatedAt.getTime());
    }

    public void setCalculatedAt(Date calculatedAt) {
        this.calculatedAt = calculatedAt;
    }

    private TransportStatus calculateTransportStatus() {
        if (ObjectsWrapper.isNull(lastEvent)) {
            return NOT_RECEIVED;
        }
        switch (lastEvent.getType()) {
            case LOAD:
                return ONBOARD_CARRIER;
            case UNLOAD:
            case RECEIVE:
            case CUSTOMS:
                return IN_PORT;
            case CLAIM:
                return CLAIMED;
            default:
                return UNKNOWN;
        }
    }

    private Location calculateLastKnownLocation() {
        if (ObjectsWrapper.nonNull(lastEvent)) {
            return lastEvent.getLocation();
        } else {
            return null;
        }
    }

    private Voyage calculateCurrentVoyage() {
        if (getTransportStatus().equals(ONBOARD_CARRIER) && ObjectsWrapper.nonNull(lastEvent)) {
            return lastEvent.getVoyage();
        } else {
            return null;
        }
    }

    private boolean calculateMisdirectionStatus(Itinerary itinerary) {
        if (ObjectsWrapper.nonNull(lastEvent)) {
            return false;
        } else {
            return !itinerary.isExpected(lastEvent);
        }
    }

    private Date calculateEta(Itinerary itinerary) {
        if (onTrack()) {
            return itinerary.getFinalArrivalDate();
        } else {
            return ETA_UNKOWN;
        }
    }

    private HandlingActivity calculateNextExpectedActivity(RouteSpecification routeSpecification, Itinerary itinerary) {
        if (!onTrack()) {
            return NO_ACTIVITY;
        }
        if (ObjectsWrapper.isNull(lastEvent)) {
            return new HandlingActivity(HandlingEvent.Type.RECEIVE, routeSpecification.getOrigin());
        }
        switch (lastEvent.getType()) {
            case LOAD:
                for (Leg leg : itinerary.getLegs()) {
                    if (leg.getLoadLocation().sameIdentityAs(lastEvent.getLocation())) {
                        return new HandlingActivity(HandlingEvent.Type.UNLOAD, leg.getUnloadLocation(), leg.getVoyage());
                    }
                }
                return NO_ACTIVITY;
            case UNLOAD:
                for (Iterator<Leg> iterator = itinerary.getLegs().iterator(); iterator.hasNext(); ) {
                    Leg leg = iterator.next();
                    if (leg.getUnloadLocation().sameIdentityAs(lastEvent.getLocation())) {
                        if (iterator.hasNext()) {
                            Leg nextLeg = iterator.next();
                            return new HandlingActivity(HandlingEvent.Type.LOAD, nextLeg.getLoadLocation(), nextLeg.getVoyage());
                        } else {
                            return new HandlingActivity(HandlingEvent.Type.CLAIM, leg.getUnloadLocation());
                        }
                    }
                }
                return NO_ACTIVITY;
            case RECEIVE:
                Leg firstLeg = itinerary.getLegs().iterator().next();
                return new HandlingActivity(HandlingEvent.Type.LOAD, firstLeg.getLoadLocation(), firstLeg.getVoyage());
            case CLAIM:
            default:
                return NO_ACTIVITY;
        }
    }

    private RoutingStatus calculateRoutingStatus(Itinerary itinerary, RouteSpecification routeSpecification) {
        if (itinerary == null || itinerary == Itinerary.EMPTY_ITINERARY) {
            return NOT_ROUTED;
        } else {
            if (routeSpecification.isSatisfiedBy(itinerary)) {
                return ROUTED;
            } else {
                return MISROUTED;
            }
        }
    }

    private boolean calculateUnloadedAtDestination(RouteSpecification routeSpecification) {
        return lastEvent != null && HandlingEvent.Type.UNLOAD.sameValueAs(lastEvent.getType()) && routeSpecification.getDestination().sameIdentityAs(lastEvent.getLocation());
    }

    private boolean onTrack() {
        return routingStatus.equals(ROUTED) && !misdirected;
    }

    private boolean sameValueAs(Delivery other) {
        return other != null && new EqualsBuilder()
                .append(this.transportStatus, other.transportStatus)
                .append(this.lastKnownLocation, other.lastKnownLocation)
                .append(this.currentVoyage, other.currentVoyage)
                .append(this.misdirected, other.misdirected)
                .append(this.eta, other.eta)
                .append(this.nextExpectedActivity, other.nextExpectedActivity)
                .append(this.isUnloadedAtDestination, other.isUnloadedAtDestination)
                .append(this.routingStatus, other.routingStatus)
                .append(this.calculatedAt, other.calculatedAt)
                .append(this.lastEvent, other.lastEvent).isEquals();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Delivery other = (Delivery) o;
        return sameValueAs(other);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(transportStatus)
                .append(lastKnownLocation).append(currentVoyage)
                .append(misdirected).append(eta).append(nextExpectedActivity)
                .append(isUnloadedAtDestination).append(routingStatus)
                .append(calculatedAt).append(lastEvent).toHashCode();
    }
}