package be.gestatech.cargo.tracker.backend.domain.model.vo.cargo;

import be.gestatech.cargo.tracker.backend.domain.model.entity.handling.HandlingEvent;
import be.gestatech.cargo.tracker.backend.domain.model.entity.location.Location;
import be.gestatech.cargo.tracker.backend.domain.model.entity.voyage.Voyage;
import be.gestatech.cargo.tracker.backend.infrastructure.util.ObjectUtil;

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
        ObjectUtil.requireNonNull(type, "Handling event type is required");
        ObjectUtil.requireNonNull(location, "Location is required");
        this.type = type;
        this.location = location;
    }

    public HandlingActivity(HandlingEvent.Type type, Location location, Voyage voyage) {
        ObjectUtil.requireNonNull(type, "Handling event type is required");
        ObjectUtil.requireNonNull(location, "Location is required");
        ObjectUtil.requireNonNull(voyage, "Voyage is required");
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
        return ObjectUtil.equals(HandlingActivity.class, this, other);
    }

    @Override
    public int hashCode() {
        return ObjectUtil.hash(this);
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
        return Objects.equals(this, other);
    }
}
