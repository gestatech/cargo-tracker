package be.gestatech.cargo.tracker.backend.domain.model.vo.cargo;

import be.gestatech.cargo.tracker.backend.domain.model.entity.cargo.Leg;
import be.gestatech.cargo.tracker.backend.domain.model.entity.handling.HandlingEvent;
import be.gestatech.cargo.tracker.backend.domain.model.vo.handling.HandlingHistory;
import be.gestatech.cargo.tracker.backend.domain.model.entity.location.Location;
import be.gestatech.cargo.tracker.backend.domain.model.entity.voyage.Voyage;
import be.gestatech.cargo.tracker.backend.domain.model.entity.cargo.constant.TransportStatus;
import be.gestatech.cargo.tracker.backend.domain.model.entity.cargo.constant.RoutingStatus;
import be.gestatech.cargo.tracker.backend.infrastructure.util.ObjectUtil;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;
import java.util.Objects;

import static be.gestatech.cargo.tracker.backend.domain.model.entity.cargo.constant.TransportStatus.*;
import static be.gestatech.cargo.tracker.backend.domain.model.entity.cargo.constant.RoutingStatus.*;

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

    public Delivery updateOnRouting(RouteSpecification routeSpecification, Itinerary itinerary) {
        ObjectUtil.requireNonNull(routeSpecification, "Route specification is required");
        return new Delivery(this.lastEvent, itinerary, routeSpecification);
    }

    public static Delivery derivedFrom(RouteSpecification routeSpecification, Itinerary itinerary, HandlingHistory handlingHistory) {
        ObjectUtil.requireNonNull(routeSpecification, "Route specification is required");
        ObjectUtil.requireNonNull(handlingHistory, "Delivery history is required");
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
        return ObjectUtil.nullSafe(lastKnownLocation, Location.UNKNOWN);
    }

    public void setLastKnownLocation(Location lastKnownLocation) {
        this.lastKnownLocation = lastKnownLocation;
    }

    public void setLastEvent(HandlingEvent lastEvent) {
        this.lastEvent = lastEvent;
    }

    public Voyage getCurrentVoyage() {
        return ObjectUtil.nullSafe(currentVoyage, Voyage.NONE);
    }

    public boolean isMisdirected() {
        return misdirected;
    }

    public void setMisdirected(boolean misdirected) {
        this.misdirected = misdirected;
    }

    public Date getEstimatedTimeOfArrival() {
        Date response = ETA_UNKOWN;
        if (eta != ETA_UNKOWN) {
            response = new Date(eta.getTime());
        }
        return response;
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
        if (ObjectUtil.isNull(lastEvent)) {
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
        Location response = null;
        if (ObjectUtil.nonNull(lastEvent)) {
            response = lastEvent.getLocation();
        }
        return response;
    }

    private Voyage calculateCurrentVoyage() {
        Voyage response = null;
        if (getTransportStatus().equals(ONBOARD_CARRIER) && ObjectUtil.nonNull(lastEvent)) {
            response = lastEvent.getVoyage();
        }
        return response;
    }

    private boolean calculateMisdirectionStatus(Itinerary itinerary) {
        boolean response = false;
        if (ObjectUtil.isNull(lastEvent)) {
            response = !itinerary.isExpected(lastEvent);
        }
        return response;
    }

    private Date calculateEta(Itinerary itinerary) {
        Date response = ETA_UNKOWN;
        if (onTrack()) {
            response = itinerary.getFinalArrivalDate();
        }
        return response;
    }

    private HandlingActivity calculateNextExpectedActivity(RouteSpecification routeSpecification, Itinerary itinerary) {
        if (!onTrack()) {
            return NO_ACTIVITY;
        }
        if (ObjectUtil.isNull(lastEvent)) {
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
        RoutingStatus response = MISROUTED;
        if (ObjectUtil.isNull(itinerary) || ObjectUtil.equals(itinerary, Itinerary.EMPTY_ITINERARY)) {
            response = NOT_ROUTED;
        } else if (routeSpecification.isSatisfiedBy(itinerary)) {
            response = ROUTED;
        }
        return response;
    }

    private boolean calculateUnloadedAtDestination(RouteSpecification routeSpecification) {
        return ObjectUtil.nonNull(lastEvent) && HandlingEvent.Type.UNLOAD.sameValueAs(lastEvent.getType()) && routeSpecification.getDestination().sameIdentityAs(lastEvent.getLocation());
    }

    private boolean onTrack() {
        return routingStatus.equals(ROUTED) && !misdirected;
    }

    @Override
    public boolean equals(Object other) {
        return ObjectUtil.equals(Delivery.class, this, other);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Delivery{");
        sb.append("transportStatus=").append(transportStatus);
        sb.append(", lastKnownLocation=").append(lastKnownLocation);
        sb.append(", currentVoyage=").append(currentVoyage);
        sb.append(", misdirected=").append(misdirected);
        sb.append(", eta=").append(eta);
        sb.append(", nextExpectedActivity=").append(nextExpectedActivity);
        sb.append(", isUnloadedAtDestination=").append(isUnloadedAtDestination);
        sb.append(", routingStatus=").append(routingStatus);
        sb.append(", calculatedAt=").append(calculatedAt);
        sb.append(", lastEvent=").append(lastEvent);
        sb.append('}');
        return sb.toString();
    }
}