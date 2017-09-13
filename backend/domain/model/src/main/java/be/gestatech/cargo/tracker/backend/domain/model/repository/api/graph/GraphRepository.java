package be.gestatech.cargo.tracker.backend.domain.model.repository.api.graph;

import java.util.List;

public interface GraphRepository {

    List<String> listLocations();

    String getVoyageNumber(String from, String to);
}
