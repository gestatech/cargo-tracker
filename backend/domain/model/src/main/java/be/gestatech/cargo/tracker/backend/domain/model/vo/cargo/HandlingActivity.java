package be.gestatech.cargo.tracker.backend.domain.model.vo.cargo;

import be.gestatech.cargo.tracker.backend.domain.model.entity.location.Location;
import be.gestatech.cargo.tracker.backend.infrastructure.util.ObjectsWrapper;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class HandlingActivity implements Serializable {

    private static final long serialVersionUID = -5145874212041410921L;

    @Enumerated(EnumType.STRING)
    @Column(name = "NEXT_EXPECTED_HANDLING_EVENT_TYPE")
    private HandlingEvent.Type type;
    @ManyToOne
    @JoinColumn(name = "NEXT_EXPECTED_LOCATION_ID")
    private Location location;
    @ManyToOne
    @JoinColumn(name = "NEXT_EXPECTED_VOYAGE_ID")
    private Voyage voyage;

    public HandlingActivity() {
    }

    public HandlingActivity(HandlingEvent.Type type, Location location) {
        ObjectsWrapper.requireNonNull(type, "Handling event type is required");
        ObjectsWrapper.requireNonNull(location, "Location is required");
        this.type = type;
        this.location = location;
    }

    public HandlingActivity(HandlingEvent.Type type, Location location, Voyage voyage) {
        ObjectsWrapper.requireNonNull(type, "Handling event type is required");
        ObjectsWrapper.requireNonNull(location, "Location is required");
        ObjectsWrapper.requireNonNull(voyage, "Voyage is required");
        this.type = type;
        this.location = location;
        this.voyage = voyage;
    }

    public HandlingEvent.Type getType() {
        return type;
    }

    public Location getLocation() {
        return location;
    }

    public Voyage getVoyage() {
        return voyage;
    }

    public boolean isEmpty() {
        if (type != null) {
            return false;
        }
        if (location != null) {
            return false;
        }
        return voyage == null;
    }

    @Override
    public boolean equals(Object other) {
        boolean response = false;
        if (other instanceof HandlingActivity) {
            HandlingActivity handlingActivity = (HandlingActivity) other;
            response = sameValueAs(handlingActivity);
        }
        return response;
    }

    @Override
    public int hashCode() {
        return ObjectsWrapper.hash(getType(), getLocation(), getVoyage());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("HandlingActivity{");
        sb.append("type=").append(type);
        sb.append(", location=").append(location);
        sb.append(", voyage=").append(voyage);
        sb.append('}');
        return sb.toString();
    }

    private boolean sameValueAs(HandlingActivity other) {
        return ObjectsWrapper.nonNull(other) && Objects.equals(this, other);
    }
}
