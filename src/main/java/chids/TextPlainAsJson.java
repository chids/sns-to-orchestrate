package chids;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN_TYPE;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.joda.JodaModule;

@Provider
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
        return MAPPER.readTree(entity);
    }

    private static final ObjectMapper MAPPER = configure(new ObjectMapper(new JsonFactory()));

    public static ObjectMapper configure(final ObjectMapper mapper) {
        return mapper
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .configure(JsonParser.Feature.ALLOW_COMMENTS, false)
                .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
                .setVisibility(PropertyAccessor.CREATOR, JsonAutoDetect.Visibility.ANY)
                .setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE)
                .setVisibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE)
                .setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.NONE)
                .setAnnotationIntrospector(new JacksonAnnotationIntrospector())
                .registerModule(new JodaModule())
                .registerModule(new GuavaModule())
                .setDateFormat(new ISO8601DateFormat());
    }
}
