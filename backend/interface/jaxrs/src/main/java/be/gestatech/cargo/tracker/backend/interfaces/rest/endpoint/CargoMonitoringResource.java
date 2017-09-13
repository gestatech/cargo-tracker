package be.gestatech.cargo.tracker.backend.interfaces.rest.endpoint;

import be.gestatech.cargo.tracker.backend.domain.model.entity.cargo.Cargo;
import be.gestatech.cargo.tracker.backend.domain.model.repository.api.cargo.CargoRepository;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Stateless
@Path("/cargo")
public class CargoMonitoringResource {

    public static final String ISO_8601_FORMAT = "yyyy-MM-dd HH:mm";

    @Inject
    private CargoRepository cargoRepository;

    public CargoMonitoringResource() {}

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonArray getAllCargo() {
        List<Cargo> cargos = cargoRepository.findAll();
        JsonArrayBuilder builder = Json.createArrayBuilder();
        for (Cargo cargo : cargos) {
            builder.add(Json.createObjectBuilder()
                    .add("trackingId", cargo.getTrackingId().getId())
                    .add("routingStatus", cargo.getDelivery().getRoutingStatus().toString())
                    .add("misdirected", cargo.getDelivery().isMisdirected())
                    .add("transportStatus", cargo.getDelivery().getTransportStatus().toString())
                    .add("atDestination", cargo.getDelivery().isUnloadedAtDestination())
                    .add("origin", cargo.getOrigin().getUnLocode().getId())
                    .add("lastKnownLocation", cargo.getDelivery().getLastKnownLocation().getUnLocode().getId().equals("XXXXX") ? "Unknown" : cargo.getDelivery().getLastKnownLocation().getUnLocode().getId())
            );
        }
        return builder.build();
    }
}
