package be.gestatech.cargo.tracker.backend.application.facade.api;

import be.gestatech.cargo.tracker.backend.domain.model.entity.handling.HandlingEvent;
import be.gestatech.cargo.tracker.backend.domain.model.exception.CannotCreateHandlingEventException;
import be.gestatech.cargo.tracker.backend.domain.model.vo.cargo.TrackingId;
import be.gestatech.cargo.tracker.backend.domain.model.vo.location.UnLocode;
import be.gestatech.cargo.tracker.backend.domain.model.vo.voyage.VoyageNumber;

import java.util.Date;

public interface HandlingEventFacade {
    /**
     * Registers a handling event in the system, and notifies interested parties
     * that a cargo has been handled.
     */
    void registerHandlingEvent(Date completionTime, TrackingId trackingId, VoyageNumber voyageNumber, UnLocode unLocode, HandlingEvent.Type type) throws CannotCreateHandlingEventException;
}
