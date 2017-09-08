package be.gestatech.cargo.tracker.backend.domain.model.factory;

import be.gestatech.cargo.tracker.backend.domain.model.entity.cargo.Cargo;
import be.gestatech.cargo.tracker.backend.domain.model.entity.handling.HandlingEvent;
import be.gestatech.cargo.tracker.backend.domain.model.entity.location.Location;
import be.gestatech.cargo.tracker.backend.domain.model.entity.voyage.Voyage;
import be.gestatech.cargo.tracker.backend.domain.model.exception.CannotCreateHandlingEventException;
import be.gestatech.cargo.tracker.backend.domain.model.exception.UnknownCargoException;
import be.gestatech.cargo.tracker.backend.domain.model.exception.UnknownLocationException;
import be.gestatech.cargo.tracker.backend.domain.model.exception.UnknownVoyageException;
import be.gestatech.cargo.tracker.backend.domain.model.repository.cargo.CargoRepository;
import be.gestatech.cargo.tracker.backend.domain.model.repository.location.LocationRepository;
import be.gestatech.cargo.tracker.backend.domain.model.repository.voyage.VoyageRepository;
import be.gestatech.cargo.tracker.backend.domain.model.vo.cargo.TrackingId;
import be.gestatech.cargo.tracker.backend.domain.model.vo.location.UnLocode;
import be.gestatech.cargo.tracker.backend.domain.model.vo.voyage.VoyageNumber;
import be.gestatech.cargo.tracker.backend.infrastructure.util.ObjectUtil;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.Date;

@ApplicationScoped
public class HandlingEventFactory implements Serializable {

    private static final long serialVersionUID = 9066072863836583233L;

    @Inject
    private CargoRepository cargoRepository;

    @Inject
    private VoyageRepository voyageRepository;

    @Inject
    private LocationRepository locationRepository;

    // TODO Look at the exception handling more seriously.
    public HandlingEvent createHandlingEvent(Date registrationTime, Date completionTime, TrackingId trackingId, VoyageNumber voyageNumber, UnLocode unlocode, HandlingEvent.Type type) throws CannotCreateHandlingEventException {
        Cargo cargo = findCargo(trackingId);
        Voyage voyage = findVoyage(voyageNumber);
        Location location = findLocation(unlocode);

        try {
            if (ObjectUtil.isNull(voyage)) {
                return new HandlingEvent(cargo, completionTime, registrationTime, type, location);
            } else {
                return new HandlingEvent(cargo, completionTime, registrationTime, type, location, voyage);
            }
        } catch (Exception e) {
            throw new CannotCreateHandlingEventException(e);
        }
    }

    private Cargo findCargo(TrackingId trackingId) throws UnknownCargoException {
        Cargo cargo = cargoRepository.find(trackingId);
        if (ObjectUtil.isNull(cargo)) {
            throw new UnknownCargoException(trackingId);
        }
        return cargo;
    }

    private Voyage findVoyage(VoyageNumber voyageNumber) throws UnknownVoyageException {
        if (ObjectUtil.isNull(voyageNumber)) {
            return null;
        }
        Voyage voyage = voyageRepository.find(voyageNumber);
        if (ObjectUtil.isNull(voyage)) {
            throw new UnknownVoyageException(voyageNumber);
        }
        return voyage;
    }

    private Location findLocation(UnLocode unlocode) throws UnknownLocationException {
        Location location = locationRepository.find(unlocode);
        if (ObjectUtil.isNull(location)) {
            throw new UnknownLocationException(unlocode);
        }
        return location;
    }
}