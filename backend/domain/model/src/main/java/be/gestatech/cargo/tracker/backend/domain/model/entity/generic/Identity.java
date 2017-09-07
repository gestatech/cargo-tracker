package be.gestatech.cargo.tracker.backend.domain.model.entity.generic;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@MappedSuperclass
public abstract class Identity implements Serializable {

    private static final long serialVersionUID = 3451744722128888885L;

    @Id
    private final String id;

    public Identity() {
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object other) {
        boolean response = (other instanceof Identity);
        if (response) {
            Identity identity = (Identity) other;
            response = Objects.equals(getId(), identity.getId());
        }
        return response;
    }

    @Override
    public int hashCode() {
        return Objects.nonNull(getId()) ? getId().hashCode() : 0;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Identity{");
        sb.append("id='").append(id).append('\'');
        sb.append('}');
        return sb.toString();
    }
}