package be.gestatech.cargo.tracker.backend.domain.service;

import be.gestatech.cargo.tracker.backend.domain.model.vo.cargo.Itinerary;
import be.gestatech.cargo.tracker.backend.domain.model.vo.cargo.RouteSpecification;

import java.util.List;

public interface RoutingService {
    List<Itinerary> fetchRoutesForSpecification(RouteSpecification routeSpecification);
}
