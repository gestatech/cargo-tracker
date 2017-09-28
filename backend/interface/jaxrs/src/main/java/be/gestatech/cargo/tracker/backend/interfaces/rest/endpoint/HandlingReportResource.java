package be.gestatech.cargo.tracker.backend.interfaces.rest.endpoint;

import be.gestatech.cargo.tracker.backend.application.facade.event.ApplicationEvents;
import be.gestatech.cargo.tracker.backend.domain.model.dto.HandlingEventRegistrationAttempt;
import be.gestatech.cargo.tracker.backend.domain.model.dto.HandlingReport;
import be.gestatech.cargo.tracker.backend.domain.model.entity.handling.HandlingEvent;
import be.gestatech.cargo.tracker.backend.domain.model.vo.cargo.TrackingId;
import be.gestatech.cargo.tracker.backend.domain.model.vo.location.UnLocode;
import be.gestatech.cargo.tracker.backend.domain.model.vo.voyage.VoyageNumber;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Stateless // TODO Make this a stateless bean for better scalability.
@Path("/handling")
public class HandlingReportResource {

    public static final String ISO_8601_FORMAT = "yyyy-MM-dd HH:mm";

    @Inject
    private ApplicationEvents applicationEvents;

    public HandlingReportResource() {}

    @POST
    @Path("/reports")
    @Consumes(MediaType.APPLICATION_JSON)
    // TODO Better exception handling.
    public void submitReport(@NotNull @Valid HandlingReport handlingReport) {
        try {
            VoyageNumber voyageNumber = null;
            Date completionTime = new SimpleDateFormat(ISO_8601_FORMAT).parse(handlingReport.getCompletionTime());
            if (handlingReport.getVoyageNumber() != null) {
                voyageNumber = new VoyageNumber(handlingReport.getVoyageNumber());
            }
            HandlingEvent.Type type = HandlingEvent.Type.valueOf(handlingReport.getEventType());
            UnLocode unLocode = new UnLocode(handlingReport.getUnLocode());
            TrackingId trackingId = new TrackingId(handlingReport.getTrackingId());
            Date registrationTime = new Date();
            HandlingEventRegistrationAttempt attempt = new HandlingEventRegistrationAttempt(registrationTime, completionTime, trackingId, voyageNumber, type, unLocode);
            applicationEvents.receivedHandlingEventRegistrationAttempt(attempt);
        } catch (ParseException ex) {
            throw new RuntimeException("Error parsing completion time", ex);
        }
    }

}
