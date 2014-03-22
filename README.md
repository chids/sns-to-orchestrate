# Wat?
This service delegates POST requests to [Orchestrate.io](http://orchestrate.io). The service endpoints are designed to make it somewhat feasible to set up [AWS SNS HTTP(S) subscriptions](http://docs.aws.amazon.com/sns/latest/dg/SendMessageToHttp.html) to this service.

That way you can have all messages published to a topic...

* ...[added or updated](https://orchestrate.io/docs/api/#key/value/put-\(create/update\)) in a collection
* ...[appended as events](https://orchestrate.io/docs/api/#events/put) to a key in a collection

## Security?
Considered non-existent, don't trust this.

## Service

### General
* **JSON** payload
   * Both endpoints requires the JSON request body to contain a field named `id` on the root level. The `id` field will be removed from the JSON structure and used as the key in the `{collection}`.
* **MIME** content type
   * Requests are required to use HTTP content-type `text/plain` because that's what AWS SNS uses.

### Endpoints

All endpoints require the `{application}` path parameter to map to a environment variable whose value is the Orchestrate API that grants write access to `{collection}`.

* `POST /{application}/{collection}`
   * Creates, or updates, the key in the given collection.
* `POST /{application}/{collection}/{event-type}`
   * Creates an event of the given `{event-type}` in the `{collection}` with the current time as timestamp.

### Running / Deploying
Made to run on Heroku or locally with [foreman](http://ddollar.github.io/foreman/). Requires at least one environment variable for `{application}` that contains a valid API key for Orchestrate.io.

### SNS subscription configuration
* HTTP or HTTPS subscription
* Raw message delivery
