package chids.providers;

import static com.google.common.base.Preconditions.checkArgument;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN_TYPE;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.OK;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;

import javax.ws.rs.WebApplicationException;

import chids.service.SnsOrchestrateProxy;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.io.ByteStreams;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

public class SnsSubscriptionRequestFilter implements ContainerRequestFilter {

    @Override
    public ContainerRequest filter(final ContainerRequest request) {
        if(isSubscriptionRequest(request)) {
            interceptSubscriptionConfirmation(request);
        }
        return request;
    }

    private static boolean isSubscriptionRequest(final ContainerRequest request) {
        return "POST".equals(request.getMethod())
                && TEXT_PLAIN_TYPE.isCompatible(request.getMediaType())
                && "SubscriptionConfirmation".equals(request.getHeaderValue("X-Amz-Sns-Message-Type"));
    }

    private static void interceptSubscriptionConfirmation(final ContainerRequest request) {
        try {
            final byte[] entity = readEntity(request);
            final JsonNode payload = SnsOrchestrateProxy.JSON.readTree(entity);
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

    private static byte[] readEntity(final ContainerRequest request) throws IOException {
        final byte[] entity = ByteStreams.toByteArray(request.getEntityInputStream());
        request.setEntityInputStream(new ByteArrayInputStream(entity));
        return entity;
    }

    private static boolean isSubscriptionConfirmation(final JsonNode payload) {
        return payload.has("Type")
                && "SubscriptionConfirmation".equals(payload.get("Type").asText())
                && payload.has("SubscribeURL");
    }
}
