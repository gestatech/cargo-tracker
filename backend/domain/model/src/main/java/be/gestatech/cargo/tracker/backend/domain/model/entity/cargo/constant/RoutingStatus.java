package be.gestatech.cargo.tracker.backend.domain.model.entity.cargo.constant;

public enum RoutingStatus {

    NOT_ROUTED, ROUTED, MISROUTED;

    public boolean sameValueAs(RoutingStatus other) {
        return this.equals(other);
    }
}
