package chids;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN_TYPE;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.JsonNode;

@Provider
@Consumes(TEXT_PLAIN)
public class TextPlainAsJson implements MessageBodyReader<JsonNode> {

    @Override
    public boolean isReadable(final Class<?> klass,
                              final Type type,
                              final Annotation[] annotations,
                              final MediaType mime) {
        return TEXT_PLAIN_TYPE.isCompatible(mime);
    }

    @Override
    public JsonNode readFrom(final Class<JsonNode> klass,
                             final Type type,
                             final Annotation[] annotations,
                             final MediaType mime,
                             final MultivaluedMap<String, String> headers,
                             final InputStream entity) throws IOException, WebApplicationException {
        return SnsOrchestrateProxy.JSON.readTree(entity);
    }
}
