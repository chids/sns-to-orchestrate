package chids.providers;

import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.base.Strings.nullToEmpty;

import java.lang.reflect.Type;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import chids.domain.Document;
import chids.domain.Event;
import chids.domain.Persistable;
import chids.service.BadRequestException;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;

@Provider
public class PersistableProvider
        extends AbstractHttpContextInjectable<Persistable>
        implements InjectableProvider<Context, Type> {
    public final static String ID = "id";
    public final static String IDp = '{' + ID + '}';
    public final static String COLLECTION = "collection";
    public final static String COLLECTIONp = '{' + COLLECTION + '}';
    public final static String TYPE = "type";
    public final static String TYPEp = '{' + TYPE + '}';

    @Override
    public ComponentScope getScope() {
        return ComponentScope.PerRequest;
    }

    @Override
    public Injectable<Persistable> getInjectable(final ComponentContext ic, final Context a, final Type c) {
        return c.equals(Persistable.class) ? this : null;
    }

    @Override
    public Persistable getValue(final HttpContext c) {
        final MultivaluedMap<String, String> params = c.getUriInfo().getPathParameters(true);
        final JsonNode payload = c.getRequest().getEntity(JsonNode.class);
        final String collection = readParameter(params, COLLECTION);
        final String id = readId(payload, readParameter(params, ID));
        if(params.containsKey(TYPE))
        {
            return new Event(collection, id, readParameter(params, TYPE), payload);
        }
        return new Document(collection, id, payload);
    }

    private static String readId(final JsonNode payload, final String field) {
        assertNotNull(field, "No id field specified");
        assertTrue(payload.isObject(), "Invalid JSON payload, not an object");
        final JsonNode idField = payload.findPath(field);
        assertTrue(idField.isValueNode(), "Unable to find id field: " + field);
        return assertNotNull(idField.asText(), "No value for id field: " + field);
    }

    private static String assertNotNull(final String value, final String message) {
        if(null == emptyToNull(value)) {
            throw new BadRequestException(message);
        }
        return value;
    }

    private static void assertTrue(final boolean condition, final String message) {
        if(!condition) {
            throw new BadRequestException(message);
        }
    }

    private static String readParameter(final MultivaluedMap<String, String> params, final String parameter) {
        if(params.containsKey(parameter)) {
            final String value = params.getFirst(parameter);
            if(nullToEmpty(value).isEmpty()) {
                throw new BadRequestException("No value for " + parameter);
            }
            return value;
        }
        throw new BadRequestException("Unable to find " + parameter);
    }
}
