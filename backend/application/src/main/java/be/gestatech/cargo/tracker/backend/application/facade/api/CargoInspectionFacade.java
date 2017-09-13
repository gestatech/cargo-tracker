package be.gestatech.cargo.tracker.backend.application.facade.api;

import be.gestatech.cargo.tracker.backend.domain.model.vo.cargo.TrackingId;

import javax.validation.constraints.NotNull;

public interface CargoInspectionFacade {

    /**
     * Inspect cargo and send relevant notifications to interested parties, for
     * example if a cargo has been misdirected, or unloaded at the final destination.
     */
    public void inspectCargo(@NotNull(message = "Tracking ID is required") TrackingId trackingId);
}