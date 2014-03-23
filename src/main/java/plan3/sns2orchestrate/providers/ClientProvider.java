package plan3.sns2orchestrate.providers;

import static com.codahale.metrics.health.HealthCheck.Result.healthy;
import static com.codahale.metrics.health.HealthCheck.Result.unhealthy;
import static com.google.common.base.Strings.emptyToNull;
import static java.lang.System.getenv;
import static java.util.concurrent.TimeUnit.SECONDS;
import io.orchestrate.client.Client;
import io.orchestrate.client.ClientBuilder;
import io.orchestrate.client.KvListOperation;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeoutException;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import plan3.sns2orchestrate.service.BadRequestException;


import com.codahale.metrics.health.HealthCheck.Result;
import com.fasterxml.jackson.databind.JsonNode;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;

@Provider
public class ClientProvider
        extends AbstractHttpContextInjectable<Client>
        implements InjectableProvider<Context, Type> {
    public final static String APPLICATION = "application";
    public final static String APPLICATIONp = '{' + APPLICATION + '}';
    private static final ConcurrentMap<String, Client> clients = new ConcurrentHashMap<>();

    @Override
    public ComponentScope getScope() {
        return ComponentScope.PerRequest;
    }

    @Override
    public Injectable<Client> getInjectable(final ComponentContext ic, final Context a, final Type c) {
        return c.equals(Client.class) ? this : null;
    }

    @Override
    public Client getValue(final HttpContext c) {
        final MultivaluedMap<String, String> parameters = c.getUriInfo().getPathParameters(true);
        final String application = assertNotNull(parameters.getFirst(APPLICATION), "No value for " + APPLICATION);
        return clients.containsKey(application) ? clients.get(application) : newClient(application);
    }

    private static Client newClient(final String application) {
        final String apiKey = assertNotNull(getenv(application), application + " not configured");
        final Client client = new ClientBuilder(apiKey).useSSL(true).build();
        clients.put(application, client);
        return client;
    }

    private static String assertNotNull(final String value, final String message) {
        if(emptyToNull(value) == null) {
            throw new BadRequestException(message);
        }
        return value;
    }

    public static Result ping() {
        final List<Client> clientele = new ArrayList<>(clients.values());
        if(clientele.isEmpty()) {
            return unhealthy("No requests made, nothing to verify ¯\\_(ツ)_/¯");
        }
        final Client client = clientele.get(ThreadLocalRandom.current().nextInt(0, clientele.size()));
        try {
            final KvListOperation<JsonNode> operation = new KvListOperation<>("whatever", 0, JsonNode.class);
            final int result = client.execute(operation).get(2, SECONDS).getCount();
            return (result == 0) ? healthy() : unhealthy("Expected 0 results for 0 paging");
        }
        catch(InterruptedException | ExecutionException | TimeoutException e) {
            return unhealthy(e);
        }
    }
}
