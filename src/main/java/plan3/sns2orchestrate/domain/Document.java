package plan3.sns2orchestrate.domain;

import io.orchestrate.client.Client;
import io.orchestrate.client.KvStoreOperation;

import com.fasterxml.jackson.databind.JsonNode;

public class Document extends Persistable {

    public Document(final String collection, final String id, final JsonNode payload) {
        super(collection, id, payload);
    }

    @Override
    public void persist(final Client client) {
        client.execute(new KvStoreOperation(this.collection, this.id, this.payload.toString()));
    }
}
