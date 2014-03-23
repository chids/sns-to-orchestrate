package plan3.sns2orchestrate.domain;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.emptyToNull;
import io.orchestrate.client.Client;

import com.fasterxml.jackson.databind.JsonNode;

public abstract class Persistable {

    protected final String collection;
    protected final String id;
    protected final JsonNode payload;

    protected Persistable(final String collection, final String id, final JsonNode payload) {
        checkArgument(payload.isObject(), "Payload not object node");
        this.collection = checkNotNull(emptyToNull(collection));
        this.id = checkNotNull(emptyToNull(id));
        this.payload = checkNotNull(payload);
    }

    public abstract void persist(final Client client);
}
