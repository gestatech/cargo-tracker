package be.gestatech.cargo.tracker.backend.domain.model.shared;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.Objects;
import java.util.UUID;

@MappedSuperclass
public abstract class Identity {

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
        boolean response = (other instanceof be.gestatech.cargo.tracker.backend.domain.model.generic.Identity);
        if (response) {
            be.gestatech.cargo.tracker.backend.domain.model.generic.Identity identity = (be.gestatech.cargo.tracker.backend.domain.model.generic.Identity) other;
            response = Objects.nonNull(getId()) ? Objects.equals(getId(), identity.getId()) : Objects.isNull(identity.getId());
        }
        return response;
    }

    @Override
    public int hashCode() {
        return Objects.nonNull(getId()) ? getId().hashCode() : 0;
    }
}