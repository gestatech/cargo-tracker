package be.gestatech.cargo.tracker.backend.application.facade.bean;

import be.gestatech.cargo.tracker.backend.application.facade.api.HandlingEventFacade;
import be.gestatech.cargo.tracker.backend.application.facade.event.ApplicationEvents;
import be.gestatech.cargo.tracker.backend.domain.model.entity.handling.HandlingEvent;
import be.gestatech.cargo.tracker.backend.domain.model.exception.CannotCreateHandlingEventException;
import be.gestatech.cargo.tracker.backend.domain.model.factory.HandlingEventFactory;
import be.gestatech.cargo.tracker.backend.domain.model.repository.api.handling.HandlingEventRepository;
import be.gestatech.cargo.tracker.backend.domain.model.vo.cargo.TrackingId;
import be.gestatech.cargo.tracker.backend.domain.model.vo.location.UnLocode;
import be.gestatech.cargo.tracker.backend.domain.model.vo.voyage.VoyageNumber;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Date;
import java.util.logging.Logger;

@Stateless
public class DefaultHandlingEventFacadeBean implements HandlingEventFacade {

    @Inject
    private ApplicationEvents applicationEvents;

    @Inject
    private HandlingEventRepository handlingEventRepository;

    @Inject
    private HandlingEventFactory handlingEventFactory;

    private static final Logger logger = Logger.getLogger(DefaultHandlingEventFacadeBean.class.getName());

    @Override
    public void registerHandlingEvent(Date completionTime, TrackingId trackingId, VoyageNumber voyageNumber, UnLocode unLocode, HandlingEvent.Type type) throws CannotCreateHandlingEventException {
        Date registrationTime = new Date();
        /* Using a factory to create a HandlingEvent (aggregate). This is where
         it is determined wether the incoming data, the attempt, actually is capable
         of representing a real handling event. */
        HandlingEvent event = handlingEventFactory.createHandlingEvent(registrationTime, completionTime, trackingId, voyageNumber, unLocode, type);

        /* Store the new handling event, which updates the persistent
         state of the handling event aggregate (but not the cargo aggregate -
         that happens asynchronously!)
         */
        handlingEventRepository.store(event);

        /* Publish an event stating that a cargo has been handled. */
        applicationEvents.cargoWasHandled(event);

        logger.info("Registered handling event");
    }
}
