package plan3.sns2orchestrate;

import static java.util.Collections.singletonMap;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static plan3.sns2orchestrate.providers.ClientProvider.APPLICATIONp;
import static plan3.sns2orchestrate.providers.PersistableProvider.COLLECTIONp;
import static plan3.sns2orchestrate.providers.PersistableProvider.IDp;
import static plan3.sns2orchestrate.providers.PersistableProvider.TYPEp;
import io.orchestrate.client.Client;

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import plan3.sns2orchestrate.domain.Persistable;

import com.codahale.metrics.annotation.Timed;


@Path(APPLICATIONp + '/' + COLLECTIONp + '/' + IDp)
@Produces(APPLICATION_JSON)
@Consumes({ TEXT_PLAIN, APPLICATION_JSON })
public class Proxy {

    @POST
    @Timed
    public Map<String, String> addToCollection(@Context final Client client, @Context final Persistable document) {
        document.persist(client);
        return singletonMap("status", "ok");
    }

    @POST
    @Timed
    @Path(TYPEp)
    public Map<String, String> appendToKey(@Context final Client client, @Context final Persistable event) {
        event.persist(client);
        return singletonMap("status", "ok");
    }
}
