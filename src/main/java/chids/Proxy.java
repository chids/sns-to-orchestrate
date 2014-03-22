package chids;

import static chids.ClientProvider.APPLICATIONp;
import static chids.PersistableProvider.COLLECTIONp;
import static chids.PersistableProvider.IDp;
import static chids.PersistableProvider.TYPEp;
import static java.util.Collections.singletonMap;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import io.orchestrate.client.Client;

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

@Path(APPLICATIONp + '/' + COLLECTIONp + '/' + IDp)
@Produces(APPLICATION_JSON)
@Consumes({ TEXT_PLAIN, APPLICATION_JSON })
public class Proxy {

    @POST
    public Map<String, String> addToCollection(@Context final Client client, @Context final Persistable document) {
        document.persist(client);
        return singletonMap("status", "ok");
    }

    @POST
    @Path(TYPEp)
    public Map<String, String> appendToKey(@Context final Client client, @Context final Persistable event) {
        event.persist(client);
        return singletonMap("status", "ok");
    }
}
