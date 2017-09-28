package be.gestatech.cargo.tracker.backend.application.facade.bean;

import be.gestatech.cargo.tracker.backend.application.facade.event.ApplicationEvents;
import be.gestatech.cargo.tracker.backend.application.facade.api.CargoInspectionFacade;
import be.gestatech.cargo.tracker.backend.domain.model.entity.cargo.Cargo;
import be.gestatech.cargo.tracker.backend.domain.model.repository.api.cargo.CargoRepository;
import be.gestatech.cargo.tracker.backend.domain.model.repository.api.handling.HandlingEventRepository;
import be.gestatech.cargo.tracker.backend.domain.model.vo.cargo.TrackingId;
import be.gestatech.cargo.tracker.backend.domain.model.vo.handling.HandlingHistory;
import be.gestatech.cargo.tracker.backend.infrastructure.events.CargoInspected;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class DefaultCargoInspectionFacadeBean implements CargoInspectionFacade {

    @Inject
    private ApplicationEvents applicationEvents;

    @Inject
    private CargoRepository cargoRepository;

    @Inject
    private HandlingEventRepository handlingEventRepository;

    @Inject
    @CargoInspected
    private Event<Cargo> cargoInspected;

    private static final Logger logger = Logger.getLogger(DefaultCargoInspectionFacadeBean.class.getName());

    @Override
    public void inspectCargo(TrackingId trackingId) {
        Cargo cargo = cargoRepository.find(trackingId);

        if (cargo == null) {
            logger.log(Level.WARNING, "Can't inspect non-existing cargo {0}", trackingId);
            return;
        }

        HandlingHistory handlingHistory = handlingEventRepository.lookupHandlingHistoryOfCargo(trackingId);

        cargo.deriveDeliveryProgress(handlingHistory);

        if (cargo.getDelivery().isMisdirected()) {
            applicationEvents.cargoWasMisdirected(cargo);
        }

        if (cargo.getDelivery().isUnloadedAtDestination()) {
            applicationEvents.cargoHasArrived(cargo);
        }

        cargoRepository.store(cargo);

        cargoInspected.fire(cargo);
    }
}