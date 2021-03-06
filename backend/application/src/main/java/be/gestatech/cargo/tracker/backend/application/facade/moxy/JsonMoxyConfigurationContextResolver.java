package be.gestatech.cargo.tracker.backend.application.facade.moxy;

import org.glassfish.jersey.moxy.json.MoxyJsonConfig;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

@Provider
// TODO See if this can be removed.
public class JsonMoxyConfigurationContextResolver implements ContextResolver<MoxyJsonConfig> {

    @Override
    public MoxyJsonConfig getContext(Class<?> objectType) {

        MoxyJsonConfig configuration = new MoxyJsonConfig();

        Map<String, String> namespacePrefixMapper = new HashMap<>(1);
        namespacePrefixMapper.put("http://www.w3.org/2001/XMLSchema-instance", "xsi");
        configuration.setNamespacePrefixMapper(namespacePrefixMapper);
        configuration.setNamespaceSeparator(':');

        return configuration;
    }
}