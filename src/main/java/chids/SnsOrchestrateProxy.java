package chids;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Configuration;
import com.yammer.dropwizard.config.Environment;

public class SnsOrchestrateProxy extends Service<Configuration> {

    @Override
    public void initialize(final Bootstrap<Configuration> bootstrap) {}

    @Override
    @SuppressWarnings("unchecked")
    public void run(final Configuration configuration, final Environment environment) throws Exception {
        environment.getJerseyResourceConfig().getContainerRequestFilters().add(new SnsSubscriptionRequestFilter());
        environment.addProvider(ClientProvider.class);
        environment.addProvider(TextPlainAsJson.class);
        environment.addResource(Proxy.class);
    }

    public static void main(final String[] args) throws Exception {
        new SnsOrchestrateProxy().run(args);
    }

    public static final ObjectMapper JSON = configure(new ObjectMapper(new JsonFactory()));

    public static ObjectMapper configure(final ObjectMapper mapper) {
        return mapper
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .configure(JsonParser.Feature.ALLOW_COMMENTS, false)
                .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
                .setVisibility(PropertyAccessor.CREATOR, JsonAutoDetect.Visibility.ANY)
                .setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE)
                .setVisibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE)
                .setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.NONE)
                .setAnnotationIntrospector(new JacksonAnnotationIntrospector())
                .registerModule(new JodaModule())
                .registerModule(new GuavaModule())
                .setDateFormat(new ISO8601DateFormat());
    }
}
