package chids;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.emptyToNull;
import static java.lang.System.currentTimeMillis;
import static java.lang.System.getenv;
import static java.util.Collections.singletonMap;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import io.orchestrate.client.Client;
import io.orchestrate.client.EventStoreOperation;
import io.orchestrate.client.KvStoreOperation;

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.fasterxml.jackson.databind.JsonNode;

@Path(Proxy.APPLICATIONp + '/' + Proxy.COLLECTIONp + '/' + Proxy.IDp)
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
public class Proxy {

    final static String ID = "id";
    final static String IDp = '{' + ID + '}';
    final static String APPLICATION = "application";
    final static String APPLICATIONp = '{' + APPLICATION + '}';
    final static String COLLECTION = "collection";
    final static String COLLECTIONp = '{' + COLLECTION + '}';
    private final static String TYPE = "type";
    private final static String TYPEp = '{' + TYPE + '}';

    @POST
    public Map<String, String> addToCollection(@PathParam(APPLICATION) final String application,
                                               @PathParam(COLLECTION) final String collection,
                                               @PathParam(ID) final String field,
                                               final JsonNode payload) {
        final String id = assertId(payload, field);
        client(application)
                .execute(new KvStoreOperation(checkNotNull(emptyToNull(collection)), id, payload.toString()));
        return singletonMap("status", "ok");
    }

    @POST
    @Path(TYPEp)
    public Map<String, String> appendToKey(@PathParam(APPLICATION) final String application,
                                           @PathParam(COLLECTION) final String collection,
                                           @PathParam(ID) final String field,
                                           @PathParam(TYPE) final String type,
                                           final JsonNode payload) {
        final String id = assertId(payload, field);
        checkNotNull(emptyToNull(type), "No type");
        client(application).execute(
                new EventStoreOperation(collection, id, type, payload.toString(), currentTimeMillis()));
        return singletonMap("status", "ok");
    }

    private static String assertId(final JsonNode payload, final String field) {
        checkNotNull(emptyToNull(field), "No id field specified");
        checkArgument(payload.isObject(), "Invalid JSON payload, not an object");
        final JsonNode idField = payload.findPath(field);
        checkArgument(idField.isValueNode(), "Unable to find " + field);
        return checkNotNull(emptyToNull(idField.asText()), "No value for " + field);
    }

    Client client(final String application) {
        return new Client(checkNotNull(emptyToNull(getenv(application))));
    }
}
