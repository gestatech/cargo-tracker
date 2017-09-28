package be.gestatech.cargo.tracker.backend.interfaces.rest.config;

import be.gestatech.cargo.tracker.backend.application.facade.moxy.JsonMoxyConfigurationContextResolver;
import be.gestatech.cargo.tracker.backend.interfaces.rest.endpoint.CargoMonitoringResource;
import be.gestatech.cargo.tracker.backend.interfaces.rest.endpoint.GraphTraversalResource;
import be.gestatech.cargo.tracker.backend.interfaces.rest.endpoint.HandlingReportResource;
import org.glassfish.jersey.moxy.json.MoxyJsonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("api")
public class JaxrsConfiguration extends ResourceConfig {

    public JaxrsConfiguration() {
        // Resources
        packages(new String[]{ HandlingReportResource.class.getPackage().getName(), GraphTraversalResource.class.getPackage().getName(), CargoMonitoringResource.class.getPackage().getName()});
        // Enable Bean Validation error messages.
        property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);
        // Providers - JSON.
        register(new MoxyJsonFeature());
        register(new JsonMoxyConfigurationContextResolver()); // TODO See if this can be removed.
    }
}
