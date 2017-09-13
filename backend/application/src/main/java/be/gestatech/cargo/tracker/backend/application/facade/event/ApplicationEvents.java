package be.gestatech.cargo.tracker.backend.application.facade.event;

import be.gestatech.cargo.tracker.backend.domain.model.dto.HandlingEventRegistrationAttempt;
import be.gestatech.cargo.tracker.backend.domain.model.entity.cargo.Cargo;
import be.gestatech.cargo.tracker.backend.domain.model.entity.handling.HandlingEvent;

public interface ApplicationEvents {

    void cargoWasHandled(HandlingEvent event);

    void cargoWasMisdirected(Cargo cargo);

    void cargoHasArrived(Cargo cargo);

    void receivedHandlingEventRegistrationAttempt(HandlingEventRegistrationAttempt attempt);
}
