package chids;

import static java.util.Collections.singletonMap;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class BadRequestException extends WebApplicationException {

    private static final long serialVersionUID = 6528378027377796110L;

    public BadRequestException(final String message) {
        super(Response
                .status(BAD_REQUEST)
                .type(APPLICATION_JSON)
                .entity(singletonMap("error", message))
                .build());
    }
}
