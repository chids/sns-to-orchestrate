# Wat?

This app proxies POST request to [Orchestrate.io](http://orchestrate.io) in a way that makes it somewhat feasible to set up [AWS SNS HTTP(S) subscriptions](http://docs.aws.amazon.com/sns/latest/dg/SendMessageToHttp.html) to this service and have all messages published to a topic...

* ...[saved as objects](https://orchestrate.io/docs/api/#key/value/put-(create/update\)) into a collection
* ...[appended as events](https://orchestrate.io/docs/api/#events/put)

## Endpoints

### Create/update message in collection
`POST /{collection}`

* If the JSON request body contains a field named "id" on the root level; remove it from the JSON structure and save the remaining data beneath that key.
* If the JSON request body **does not** contain a field named "id"; save the JSON under a randomly generated UUID and return:
```json
{"id": "the generated uuid used as key"}
```

## Running / Deploying
Made to run on Heroku or locally with [foreman](http://ddollar.github.io/foreman/). Requires the environment variable `ORCHESTRATE_KEY` to contain a valid API key for Orchestrate.io.

## SNS configuration
* HTTP or HTTPS subscription
* Raw message delivery