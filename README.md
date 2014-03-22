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
   * Both endpoints requires a JSON request body
   * The path parameter `{key-field}` specifies which field whose value will be the key in `{collection}`
* **MIME** content type
   * The service accepts JSON payloads sent with HTTP content-type `text/plain` because that's how AWS SNS does.
   * `application/json` is also accepted


### Endpoints

All endpoints require three parameters:

* The value of `{application}` must map to a environment variable whose value is the Orchestrate API that grants write access to `{collection}`
*  `{collection}` is the Orchestrate.io collection
*  `{key-field}` is the field within the payload whose value will be used as key in `{collection}`

#### Saving and updating documents
`POST /{application}/{collection}/{key-field}`

* Creates, or updates, the key specified by `{key-field}` in `{collection}`.

#### Appending events to documents
`POST /{application}/{collection}/{key-field}/{event-type}`

* Creates an event of the given `{event-type}` on the key specified by `{key-field}` in `{collection}` with the current time as timestamp.

### Running / Deploying
Made to run on Heroku or locally with [foreman](http://ddollar.github.io/foreman/). Requires at least one environment variable for `{application}` that contains a valid API key for Orchestrate.io.

### SNS subscription configuration
* HTTP or HTTPS subscription
* Raw message delivery
