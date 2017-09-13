package be.gestatech.cargo.tracker.backend.domain.model.repository.api.location;

import be.gestatech.cargo.tracker.backend.domain.model.entity.location.Location;
import be.gestatech.cargo.tracker.backend.domain.model.vo.location.UnLocode;

import java.util.List;

public interface LocationRepository {

    Location find(UnLocode unLocode);

    List<Location> findAll();
}
