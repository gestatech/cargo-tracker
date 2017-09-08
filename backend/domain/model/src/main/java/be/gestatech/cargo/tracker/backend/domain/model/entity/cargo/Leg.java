package be.gestatech.cargo.tracker.backend.domain.model.entity.cargo;

import be.gestatech.cargo.tracker.backend.domain.model.entity.generic.Identity;
import be.gestatech.cargo.tracker.backend.domain.model.entity.location.Location;
import be.gestatech.cargo.tracker.backend.domain.model.entity.voyage.Voyage;
import be.gestatech.cargo.tracker.backend.infrastructure.util.ArrayUtil;
import be.gestatech.cargo.tracker.backend.infrastructure.util.ObjectUtil;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Objects;

@Entity
public class Leg extends Identity {

    private static final long serialVersionUID = -5279692843391375118L;

    @ManyToOne
    @JoinColumn(name = "VOYAGE_ID")
    @NotNull
    private Voyage voyage;

    @ManyToOne
    @JoinColumn(name = "LOAD_LOCATION_ID")
    @NotNull
    private Location loadLocation;

    @ManyToOne
    @JoinColumn(name = "UNLOAD_LOCATION_ID")
    @NotNull
    private Location unloadLocation;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LOAD_TIME")
    @NotNull
    private Date loadTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UNLOAD_TIME")
    @NotNull
    private Date unloadTime;

    public Leg() {
        // Nothing to initialize.
    }

    public Leg(Voyage voyage, Location loadLocation, Location unloadLocation, Date loadTime, Date unloadTime) {
        ArrayUtil.noNullElements(new Object[]{voyage, loadLocation, unloadLocation, loadTime, unloadTime});
        this.voyage = voyage;
        this.loadLocation = loadLocation;
        this.unloadLocation = unloadLocation;
        this.loadTime = loadTime;
        this.unloadTime = unloadTime;
    }

    public Voyage getVoyage() {
        return voyage;
    }

    public Location getLoadLocation() {
        return loadLocation;
    }

    public Location getUnloadLocation() {
        return unloadLocation;
    }

    public Date getLoadTime() {
        return new Date(loadTime.getTime());
    }

    public Date getUnloadTime() {
        return new Date(unloadTime.getTime());
    }

    private boolean sameValueAs(Leg other) {
        return ObjectUtil.equals(this, other);
    }

    @Override
    public boolean equals(Object other) {
        boolean response = false;
        if (other instanceof Leg) {
            Leg leg = (Leg) other;
            response = sameValueAs(leg);
        }
        return response;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getVoyage(), getLoadLocation(), getUnloadLocation(), getLoadTime(), getUnloadTime());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Leg{");
        sb.append("voyage=").append(voyage);
        sb.append(", loadLocation=").append(loadLocation);
        sb.append(", unloadLocation=").append(unloadLocation);
        sb.append(", loadTime=").append(loadTime);
        sb.append(", unloadTime=").append(unloadTime);
        sb.append('}');
        return sb.toString();
    }
}
