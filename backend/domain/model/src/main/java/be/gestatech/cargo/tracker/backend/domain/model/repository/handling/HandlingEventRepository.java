package be.gestatech.cargo.tracker.backend.domain.model.repository.handling;

import be.gestatech.cargo.tracker.backend.domain.model.entity.handling.HandlingEvent;
import be.gestatech.cargo.tracker.backend.domain.model.vo.cargo.TrackingId;
import be.gestatech.cargo.tracker.backend.domain.model.vo.handling.HandlingHistory;

public interface HandlingEventRepository {

    void store(HandlingEvent event);

    HandlingHistory lookupHandlingHistoryOfCargo(TrackingId trackingId);
}