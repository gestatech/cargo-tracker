package be.gestatech.cargo.tracker.backend.domain.model.dto;

import be.gestatech.cargo.tracker.backend.domain.model.entity.handling.HandlingEvent;
import be.gestatech.cargo.tracker.backend.domain.model.vo.cargo.TrackingId;
import be.gestatech.cargo.tracker.backend.domain.model.vo.location.UnLocode;
import be.gestatech.cargo.tracker.backend.domain.model.vo.voyage.VoyageNumber;
import be.gestatech.cargo.tracker.backend.infrastructure.util.ObjectUtil;

import java.io.Serializable;
import java.util.Date;

public class HandlingEventRegistrationAttempt implements Serializable {

    private final Date registrationTime;
    private final Date completionTime;
    private final TrackingId trackingId;
    private final VoyageNumber voyageNumber;
    private final HandlingEvent.Type type;
    private final UnLocode unLocode;

    public HandlingEventRegistrationAttempt(Date registrationDate, Date completionDate, TrackingId trackingId, VoyageNumber voyageNumber, HandlingEvent.Type type, UnLocode unLocode) {
        this.registrationTime = registrationDate;
        this.completionTime = completionDate;
        this.trackingId = trackingId;
        this.voyageNumber = voyageNumber;
        this.type = type;
        this.unLocode = unLocode;
    }

    public Date getCompletionTime() {
        return new Date(completionTime.getTime());
    }

    public TrackingId getTrackingId() {
        return trackingId;
    }

    public VoyageNumber getVoyageNumber() {
        return voyageNumber;
    }

    public HandlingEvent.Type getType() {
        return type;
    }

    public UnLocode getUnLocode() {
        return unLocode;
    }

    public Date getRegistrationTime() {
        return registrationTime;
    }

    @Override
    public boolean equals(Object other) {
        boolean response = false;
        if (other instanceof HandlingEventRegistrationAttempt) {
            HandlingEventRegistrationAttempt handlingEventRegistrationAttempt = (HandlingEventRegistrationAttempt) other;
            response = ObjectUtil.deepEquals(this, handlingEventRegistrationAttempt);
        }
        return response;
    }

    @Override
    public int hashCode() {
        return ObjectUtil.hash(this);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("HandlingEventRegistrationAttempt{");
        sb.append("registrationTime=").append(registrationTime);
        sb.append(", completionTime=").append(completionTime);
        sb.append(", trackingId=").append(trackingId);
        sb.append(", voyageNumber=").append(voyageNumber);
        sb.append(", type=").append(type);
        sb.append(", unLocode=").append(unLocode);
        sb.append('}');
        return sb.toString();
    }
}
