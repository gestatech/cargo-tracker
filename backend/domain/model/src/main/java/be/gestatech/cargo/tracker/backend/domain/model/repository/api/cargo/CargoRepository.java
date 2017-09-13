package be.gestatech.cargo.tracker.backend.domain.model.repository.api.cargo;

import be.gestatech.cargo.tracker.backend.domain.model.entity.cargo.Cargo;
import be.gestatech.cargo.tracker.backend.domain.model.vo.cargo.TrackingId;

import java.util.List;

public interface CargoRepository {

    Cargo find(TrackingId trackingId);

    List<Cargo> findAll();

    void store(Cargo cargo);

    TrackingId nextTrackingId();

    List<TrackingId> getAllTrackingIds();

}
