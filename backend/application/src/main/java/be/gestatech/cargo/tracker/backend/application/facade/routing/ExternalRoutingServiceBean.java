package be.gestatech.cargo.tracker.backend.application.facade.routing;

import be.gestatech.cargo.tracker.backend.application.facade.moxy.JsonMoxyConfigurationContextResolver;
import be.gestatech.cargo.tracker.backend.domain.model.dto.TransitEdge;
import be.gestatech.cargo.tracker.backend.domain.model.dto.TransitPath;
import be.gestatech.cargo.tracker.backend.domain.model.entity.cargo.Leg;
import be.gestatech.cargo.tracker.backend.domain.model.repository.api.location.LocationRepository;
import be.gestatech.cargo.tracker.backend.domain.model.repository.api.voyage.VoyageRepository;
import be.gestatech.cargo.tracker.backend.domain.model.vo.cargo.Itinerary;
import be.gestatech.cargo.tracker.backend.domain.model.vo.cargo.RouteSpecification;
import be.gestatech.cargo.tracker.backend.domain.model.vo.location.UnLocode;
import be.gestatech.cargo.tracker.backend.domain.model.vo.voyage.VoyageNumber;
import be.gestatech.cargo.tracker.backend.domain.service.RoutingService;
import org.glassfish.jersey.moxy.json.MoxyJsonFeature;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class ExternalRoutingServiceBean implements RoutingService {

    // TODO Use injection instead?
    private static final Logger log = Logger.getLogger(ExternalRoutingServiceBean.class.getName());

    // TODO Can I use injection?
    private final Client jaxrsClient = ClientBuilder.newClient();

    @Resource(lookup = "java:app/configuration/GraphTraversalUrl")
    private String graphTraversalUrl;

    private WebTarget graphTraversalResource;

    @Inject
    private LocationRepository locationRepository;

    @Inject
    private VoyageRepository voyageRepository;

    @PostConstruct
    public void init() {
        graphTraversalResource = jaxrsClient.target(graphTraversalUrl);
        graphTraversalResource.register(new MoxyJsonFeature()).register(new JsonMoxyConfigurationContextResolver());
    }

    @Override
    public List<Itinerary> fetchRoutesForSpecification(RouteSpecification routeSpecification) {
        // The RouteSpecification is picked apart and adapted to the external API.
        String origin = routeSpecification.getOrigin().getUnLocode().getId();
        String destination = routeSpecification.getDestination().getUnLocode().getId();

        List<TransitPath> transitPaths = graphTraversalResource
                .queryParam("origin", origin)
                .queryParam("destination", destination)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get(new GenericType<List<TransitPath>>() {});

        // The returned result is then translated back into our domain model.
        List<Itinerary> itineraries = new ArrayList<>();

        for (TransitPath transitPath : transitPaths) {
            Itinerary itinerary = toItinerary(transitPath);
            // Use the specification to safe-guard against invalid itineraries
            if (routeSpecification.isSatisfiedBy(itinerary)) {
                itineraries.add(itinerary);
            } else {
                log.log(Level.FINE, "Received itinerary that did not satisfy the route specification");
            }
        }
        return itineraries;
    }

    private Itinerary toItinerary(TransitPath transitPath) {
        List<Leg> legs = new ArrayList<>(transitPath.getTransitEdges().size());
        for (TransitEdge edge : transitPath.getTransitEdges()) {
            legs.add(toLeg(edge));
        }
        return new Itinerary(legs);
    }

    private Leg toLeg(TransitEdge edge) {
        return new Leg(voyageRepository.find(new VoyageNumber(edge.getVoyageNumber())), locationRepository.find(new UnLocode(edge.getFromUnLocode())), locationRepository.find(new UnLocode(edge.getToUnLocode())), edge.getFromDate(), edge.getToDate());
    }
}