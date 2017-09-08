package be.gestatech.cargo.tracker.backend.domain.model.entity.location;

import be.gestatech.cargo.tracker.backend.domain.model.entity.generic.Identity;
import be.gestatech.cargo.tracker.backend.domain.model.vo.location.UnLocode;
import be.gestatech.cargo.tracker.backend.infrastructure.util.ObjectUtil;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@NamedQueries({
        @NamedQuery(name = "Location.findAll", query = "Select l from Location l"),
        @NamedQuery(name = "Location.findByUnLocode", query = "Select l from Location l where l.unLocode = :unLocode")
})
public class Location  extends Identity {

    private static final long serialVersionUID = -2518842567261884087L;

    @Embedded
    private UnLocode unLocode;

    @NotNull
    private String name;
    // Special Location object that marks an unknown location.
    public static final Location UNKNOWN = new Location(new UnLocode("XXXXX"), "Unknown location");

    public Location() {
        // Nothing to do.
    }

    public Location(UnLocode unLocode, String name) {
        ObjectUtil.nonNull(unLocode);
        ObjectUtil.nonNull(name);
        this.unLocode = unLocode;
        this.name = name;
    }

    public UnLocode getUnLocode() {
        return unLocode;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object other) {
        return ObjectUtil.equals(Location.class, this, other);
    }

    @Override
    public int hashCode() {
        return ObjectUtil.hash(this);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Location{");
        sb.append("unLocode=").append(unLocode);
        sb.append(", name='").append(name).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public boolean sameIdentityAs(Location other) {
        return this.unLocode.sameValueAs(other.unLocode);
    }

}
