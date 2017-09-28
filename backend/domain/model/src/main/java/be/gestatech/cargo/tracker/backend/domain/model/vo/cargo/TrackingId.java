package be.gestatech.cargo.tracker.backend.domain.model.vo.cargo;

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
        return ObjectUtil.equals(TrackingId.class, this, other);
    }

    @Override
    public int hashCode() {
        return ObjectUtil.hash(this);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TrackingId{");
        sb.append("id='").append(id).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public boolean sameValueAs(TrackingId trackingId) {
        return ObjectUtil.deepEquals(getId(), trackingId.getId());
    }
}
