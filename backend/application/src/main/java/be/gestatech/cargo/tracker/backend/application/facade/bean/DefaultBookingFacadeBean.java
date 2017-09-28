package be.gestatech.cargo.tracker.backend.application.facade.bean;

import be.gestatech.cargo.tracker.backend.application.facade.api.BookingFacade;
import be.gestatech.cargo.tracker.backend.domain.model.entity.cargo.Cargo;
import be.gestatech.cargo.tracker.backend.domain.model.entity.location.Location;
import be.gestatech.cargo.tracker.backend.domain.model.repository.api.cargo.CargoRepository;
import be.gestatech.cargo.tracker.backend.domain.model.repository.api.location.LocationRepository;
import be.gestatech.cargo.tracker.backend.domain.model.vo.cargo.Itinerary;
import be.gestatech.cargo.tracker.backend.domain.model.vo.cargo.RouteSpecification;
import be.gestatech.cargo.tracker.backend.domain.model.vo.cargo.TrackingId;
import be.gestatech.cargo.tracker.backend.domain.model.vo.location.UnLocode;
import be.gestatech.cargo.tracker.backend.domain.service.RoutingService;
import be.gestatech.cargo.tracker.backend.infrastructure.util.ObjectUtil;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class DefaultBookingFacadeBean implements BookingFacade {

    @Inject
    private CargoRepository cargoRepository;

    @Inject
    private LocationRepository locationRepository;

    @Inject
    private RoutingService routingService;

    // TODO See if the logger can be injected.
    private static final Logger logger = Logger.getLogger(DefaultBookingFacadeBean.class.getName());

    @Override
    public TrackingId bookNewCargo(UnLocode originUnLocode, UnLocode destinationUnLocode, Date arrivalDeadline) {
        TrackingId trackingId = cargoRepository.nextTrackingId();
        Location origin = locationRepository.find(originUnLocode);
        Location destination = locationRepository.find(destinationUnLocode);
        RouteSpecification routeSpecification = new RouteSpecification(origin, destination, arrivalDeadline);
        Cargo cargo = new Cargo(trackingId, routeSpecification);
        cargoRepository.store(cargo);
        logger.log(Level.INFO, "Booked new cargo with tracking id {0}", cargo.getTrackingId().getId());
        return cargo.getTrackingId();
    }

    @Override
    public List<Itinerary> requestPossibleRoutesForCargo(TrackingId trackingId) {
        Cargo cargo = cargoRepository.find(trackingId);
        if (ObjectUtil.isNull(cargo)) {
            return Collections.emptyList();
        }
        return routingService.fetchRoutesForSpecification(cargo.getRouteSpecification());
    }

    @Override
    public void assignCargoToRoute(Itinerary itinerary, TrackingId trackingId) {
        Cargo cargo = cargoRepository.find(trackingId);
        cargo.assignToRoute(itinerary);
        cargoRepository.store(cargo);
        logger.log(Level.INFO, "Assigned cargo {0} to new route", trackingId);
    }

    @Override
    public void changeDestination(TrackingId trackingId, UnLocode unLocode) {
        Cargo cargo = cargoRepository.find(trackingId);
        Location newDestination = locationRepository.find(unLocode);
        RouteSpecification routeSpecification = new RouteSpecification(cargo.getOrigin(), newDestination, cargo.getRouteSpecification().getArrivalDeadline());
        cargo.specifyNewRoute(routeSpecification);
        cargoRepository.store(cargo);
        logger.log(Level.INFO, "Changed destination for cargo {0} to {1}", new Object[]{trackingId, routeSpecification.getDestination()});
    }
}
