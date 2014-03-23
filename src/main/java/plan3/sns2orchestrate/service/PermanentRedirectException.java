package plan3.sns2orchestrate.service;

import static java.util.Collections.singletonMap;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.MOVED_PERMANENTLY;

import java.net.URI;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class PermanentRedirectException extends WebApplicationException {

    private static final long serialVersionUID = 216499421508464065L;

    public PermanentRedirectException(final URI location) {
        super(Response
                .status(MOVED_PERMANENTLY)
                .location(location)
                .type(APPLICATION_JSON)
                .entity(singletonMap("location", location.toString()))
                .build());
    }
}
