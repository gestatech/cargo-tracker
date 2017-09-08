package be.gestatech.cargo.tracker.backend.domain.model.entity.voyage;

import be.gestatech.cargo.tracker.backend.domain.model.entity.generic.Identity;
import be.gestatech.cargo.tracker.backend.domain.model.entity.location.Location;
import be.gestatech.cargo.tracker.backend.infrastructure.util.ArrayUtil;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "CARRIER_MOVEMENT")
public class CarrierMovement extends Identity {

    private static final long serialVersionUID = 4242984301490470819L;

    @ManyToOne
    @JoinColumn(name = "DEPARTURE_LOCATION_ID")
    @NotNull
    private Location departureLocation;

    @ManyToOne
    @JoinColumn(name = "ARRIVAL_LOCATION_ID")
    @NotNull
    private Location arrivalLocation;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DEPARTURE_TIME")
    @NotNull
    private Date departureTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ARRIVAL_TIME")
    @NotNull
    private Date arrivalTime;

    // Null object pattern
    public static final CarrierMovement NONE = new CarrierMovement(Location.UNKNOWN, Location.UNKNOWN, new Date(0), new Date(0));

    public CarrierMovement() {
        // Nothing to initialize.
    }

    public CarrierMovement(Location departureLocation, Location arrivalLocation, Date departureTime, Date arrivalTime) {
        ArrayUtil.noNullElements(new Object[]{departureLocation, arrivalLocation, departureTime, arrivalTime});
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.departureLocation = departureLocation;
        this.arrivalLocation = arrivalLocation;
    }

    public Location getDepartureLocation() {
        return departureLocation;
    }

    public Location getArrivalLocation() {
        return arrivalLocation;
    }

    public Date getDepartureTime() {
        return new Date(departureTime.getTime());
    }

    public Date getArrivalTime() {
        return new Date(arrivalTime.getTime());
    }

    @Override
    public boolean equals(Object other) {
        boolean response = false;
        if (other instanceof CarrierMovement) {
            CarrierMovement carrierMovement = (CarrierMovement) other;
            response = sameValueAs(carrierMovement);
        }
        return response;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getDepartureLocation(), getArrivalLocation(), getDepartureTime(), getArrivalTime());
    }

    private boolean sameValueAs(CarrierMovement other) {
        return Objects.deepEquals(this, other);
    }
}
