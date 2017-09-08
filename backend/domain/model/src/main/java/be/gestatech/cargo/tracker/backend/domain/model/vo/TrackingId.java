package be.gestatech.cargo.tracker.backend.domain.model.vo;

import be.gestatech.cargo.tracker.backend.infrastructure.util.ObjectUtil;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class TrackingId implements Serializable {

    private static final long serialVersionUID = 6949627824979276407L;

    @Column(name = "TRACKING_ID", unique = true, updatable = false)
    private String id;

    public TrackingId() {
        // Nothing to initialize.
    }

    public TrackingId(String id) {
        ObjectUtil.requireNonNull(id, "TrackingId.[id] should not be null");
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object other) {
        boolean response = (other instanceof TrackingId);
        if (response) {
            TrackingId trackingId = (TrackingId) other;
            response = sameValueAs(trackingId);
        }
        return response;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TrackingId{");
        sb.append("id='").append(id).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public boolean sameValueAs(TrackingId trackingId) {
        return Objects.equals(getId(), trackingId.getId());
    }
}
