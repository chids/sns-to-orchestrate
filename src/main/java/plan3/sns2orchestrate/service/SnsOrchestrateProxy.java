package plan3.sns2orchestrate.service;

import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import plan3.sns2orchestrate.Proxy;
import plan3.sns2orchestrate.providers.ClientProvider;
import plan3.sns2orchestrate.providers.ForceHttpsFilter;
import plan3.sns2orchestrate.providers.PersistableProvider;
import plan3.sns2orchestrate.providers.SnsSubscriptionRequestFilter;
import plan3.sns2orchestrate.providers.TextPlainAsJson;

import com.codahale.metrics.health.HealthCheck;
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

public class SnsOrchestrateProxy extends Application<Configuration> {

    @Override
    public void initialize(final Bootstrap<Configuration> bootstrap) {}

    @Override
    public void run(final Configuration configuration, final Environment environment) throws Exception {
        new SnsSubscriptionRequestFilter().addTo(environment.jersey().getResourceConfig());
        new ForceHttpsFilter().addTo(environment.jersey().getResourceConfig());
        environment.jersey().register(ClientProvider.class);
        environment.jersey().register(TextPlainAsJson.class);
        environment.jersey().register(PersistableProvider.class);
        environment.jersey().register(Proxy.class);
        environment.healthChecks().register("orchestrate.io", new HealthCheck() {
            @Override
            protected Result check() throws Exception {
                return ClientProvider.ping();
            }
        });
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
