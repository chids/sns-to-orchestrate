package plan3.sns2orchestrate.domain;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.emptyToNull;
import io.orchestrate.client.Client;
import io.orchestrate.client.EventStoreOperation;

import com.fasterxml.jackson.databind.JsonNode;

public class Event extends Persistable {

    private final String type;

    public Event(final String collection, final String id, final String type, final JsonNode payload) {
        super(collection, id, payload);
        this.type = checkNotNull(emptyToNull(type));
    }

    @Override
    public void persist(final Client client) {
        client.execute(new EventStoreOperation(this.collection, this.id, this.type, this.payload.toString()));
    }
}
