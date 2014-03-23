package chids.providers;

import static com.google.common.base.Strings.emptyToNull;
import static java.lang.System.getenv;
import io.orchestrate.client.Client;

import java.lang.reflect.Type;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import chids.service.BadRequestException;

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
        final String apiKey = assertNotNull(getenv(application), application + " not configured");
        return new Client(apiKey);
    }

    private static String assertNotNull(final String value, final String message) {
        if(emptyToNull(value) == null) {
            throw new BadRequestException(message);
        }
        return value;
    }
}
