package be.gestatech.cargo.tracker.backend.domain.model.repository.api.voyage;

import be.gestatech.cargo.tracker.backend.domain.model.entity.voyage.Voyage;
import be.gestatech.cargo.tracker.backend.domain.model.vo.voyage.VoyageNumber;

import java.util.List;

public interface VoyageRepository {

    Voyage find(VoyageNumber voyageNumber);

    List<Voyage> findAll();
}
