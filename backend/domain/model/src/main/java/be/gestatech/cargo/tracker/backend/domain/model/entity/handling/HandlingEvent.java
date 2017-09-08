package be.gestatech.cargo.tracker.backend.domain.model.entity.handling;

import be.gestatech.cargo.tracker.backend.domain.model.entity.cargo.Cargo;
import be.gestatech.cargo.tracker.backend.domain.model.entity.generic.Identity;
import be.gestatech.cargo.tracker.backend.domain.model.entity.location.Location;
import be.gestatech.cargo.tracker.backend.domain.model.entity.voyage.Voyage;
import be.gestatech.cargo.tracker.backend.infrastructure.util.ObjectUtil;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Objects;

@Entity
@NamedQuery(name = "HandlingEvent.findByTrackingId", query = "Select e from HandlingEvent e where e.cargo.trackingId = :trackingId")
public class HandlingEvent extends Identity {

    private static final long serialVersionUID = -8439257145614353700L;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Type type;

    @ManyToOne
    @JoinColumn(name = "VOYAGE_ID")
    private Voyage voyage;

    @ManyToOne
    @JoinColumn(name = "LOCATION_ID")
    @NotNull
    private Location location;

    @Temporal(TemporalType.DATE)
    @NotNull
    @Column(name = "COMPLETION_TIME")
    private Date completionTime;

    @Temporal(TemporalType.DATE)
    @NotNull
    @Column(name = "REGISTRATION")
    private Date registrationTime;

    @ManyToOne
    @JoinColumn(name = "CARGO_ID")
    @NotNull
    private Cargo cargo;

    @Transient
    private String summary;

    public enum Type {

        // Loaded onto voyage from port location.
        LOAD(true),
        // Unloaded from voyage to port location
        UNLOAD(true),
        // Received by carrier
        RECEIVE(false),
        // Cargo claimed by recepient
        CLAIM(false),
        // Cargo went through customs
        CUSTOMS(false);

        private final boolean voyageRequired;

        Type(boolean voyageRequired) {
            this.voyageRequired = voyageRequired;
        }

        public boolean requiresVoyage() {
            return voyageRequired;
        }

        public boolean prohibitsVoyage() {
            return !requiresVoyage();
        }

        public boolean sameValueAs(Type other) {
            return other != null && this.equals(other);
        }
    }

    public HandlingEvent() {
        // Nothing to initialize.
    }


    public HandlingEvent(Cargo cargo, Date completionTime, Date registrationTime, Type type, Location location, Voyage voyage) {
        ObjectUtil.requireNonNull(cargo, "Cargo is required");
        ObjectUtil.requireNonNull(completionTime, "Completion time is required");
        ObjectUtil.requireNonNull(registrationTime, "Registration time is required");
        ObjectUtil.requireNonNull(type, "Handling event type is required");
        ObjectUtil.requireNonNull(location, "Location is required");
        ObjectUtil.requireNonNull(voyage, "Voyage is required");

        if (type.prohibitsVoyage()) {
            throw new IllegalArgumentException("Voyage is not allowed with event type " + type);
        }

        this.voyage = voyage;
        this.completionTime = (Date) completionTime.clone();
        this.registrationTime = (Date) registrationTime.clone();
        this.type = type;
        this.location = location;
        this.cargo = cargo;
    }

    public HandlingEvent(Cargo cargo, Date completionTime, Date registrationTime, Type type, Location location) {
        ObjectUtil.requireNonNull(cargo, "Cargo is required");
        ObjectUtil.requireNonNull(completionTime, "Completion time is required");
        ObjectUtil.requireNonNull(registrationTime, "Registration time is required");
        ObjectUtil.requireNonNull(type, "Handling event type is required");
        ObjectUtil.requireNonNull(location, "Location is required");

        if (type.requiresVoyage()) {
            throw new IllegalArgumentException("Voyage is required for event type " + type);
        }

        this.completionTime = (Date) completionTime.clone();
        this.registrationTime = (Date) registrationTime.clone();
        this.type = type;
        this.location = location;
        this.cargo = cargo;
        this.voyage = null;
    }

    public Type getType() {
        return this.type;
    }

    public Voyage getVoyage() {
        return ObjectUtil.nullSafe(this.voyage, Voyage.NONE);
    }

    public Date getCompletionTime() {
        return new Date(this.completionTime.getTime());
    }

    public Date getRegistrationTime() {
        return new Date(this.registrationTime.getTime());
    }

    public Location getLocation() {
        return this.location;
    }

    public Cargo getCargo() {
        return this.cargo;
    }

    public String getSummary() {
        StringBuilder builder = new StringBuilder(location.getName()).append("\n")
                .append(completionTime).append("\n")
                .append("Type: ").append(type).append("\n")
                .append("Reg.: ").append(registrationTime)
                .append("\n");
        if (voyage != null) {
            builder.append("Voyage: ").append(voyage.getVoyageNumber());
        }
        return builder.toString();
    }

    @Override
    public boolean equals(Object other) {
        boolean response = false;

        if (other instanceof HandlingEvent) {
            HandlingEvent handlingEvent = (HandlingEvent) other;
            response = sameEventAs(handlingEvent);
        }
        return response;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getType(), getVoyage(), getLocation(), getCompletionTime(), getRegistrationTime(), getCargo(), getSummary());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("HandlingEvent{");
        sb.append("type=").append(type);
        sb.append(", voyage=").append(voyage);
        sb.append(", location=").append(location);
        sb.append(", completionTime=").append(completionTime);
        sb.append(", registrationTime=").append(registrationTime);
        sb.append(", cargo=").append(cargo);
        sb.append(", summary='").append(summary).append('\'');
        sb.append('}');
        return sb.toString();
    }

    private boolean sameEventAs(HandlingEvent other) {
        return ObjectUtil.equals(this, other);
    }
}
