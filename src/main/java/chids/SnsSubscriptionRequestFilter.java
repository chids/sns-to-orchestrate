package chids;

import static com.google.common.base.Preconditions.checkArgument;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN_TYPE;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.OK;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;

import javax.ws.rs.WebApplicationException;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

public class SnsSubscriptionRequestFilter implements ContainerRequestFilter {

    @Override
    public ContainerRequest filter(final ContainerRequest request) {
        if(isPlainTextPost(request)) {
            interceptSubscriptionConfirmation(request);
        }
        return request;
    }

    private static boolean isPlainTextPost(final ContainerRequest request) {
        return "POST".equals(request.getMethod()) && TEXT_PLAIN_TYPE.isCompatible(request.getMediaType());
    }

    private static void interceptSubscriptionConfirmation(final ContainerRequest request) {
        try {
            final JsonNode payload = SnsOrchestrateProxy.JSON.readTree(request.getEntityInputStream());
            if(isSubscriptionConfirmation(payload)) {
                final URI confirmation = URI.create(payload.get("SubscribeURL").asText());
                final HttpURLConnection connection = (HttpURLConnection)confirmation.toURL().openConnection();
                connection.setRequestMethod("GET");
                checkArgument(2 == (connection.getResponseCode() / 100));
                throw new WebApplicationException(OK);
            }
        }
        catch(final IOException e) {
            throw new WebApplicationException(BAD_REQUEST);
        }
    }

    private static boolean isSubscriptionConfirmation(final JsonNode payload) {
        return payload.has("Type")
                && "SubscriptionConfirmation".equals(payload.get("Type").asText())
                && payload.has("SubscribeURL");
    }
}
