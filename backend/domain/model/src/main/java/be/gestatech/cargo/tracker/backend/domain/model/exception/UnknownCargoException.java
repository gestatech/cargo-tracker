package be.gestatech.cargo.tracker.backend.domain.model.exception;

import be.gestatech.cargo.tracker.backend.domain.model.vo.cargo.TrackingId;

public class UnknownCargoException extends CannotCreateHandlingEventException {

    private static final long serialVersionUID = -1692281362780025429L;

    private final TrackingId trackingId;

    public UnknownCargoException(TrackingId trackingId) {
        this.trackingId = trackingId;
    }

    @Override
    public String getMessage() {
        return String.format("No cargo with tracking id [%s] exists in the system", trackingId.getId());
    }
}