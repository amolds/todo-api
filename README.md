# todo-api

This project was created using the [Ktor Project Generator](https://start.ktor.io).

Here are some useful links to get you started:
 * [Ktor Documentation](https://ktor.io/docs/home.html)
 * [Ktor GitHub page](https://github.com/ktorio/ktor)
 * [Ktor Slack chat](https://app.slack.com/client/T09229ZC6/C0A974TJ9). [Request an invite](https://surveys.jetbrains.com/s3/kotlin-slack-sign-up).


## Features
Here's a list of features included in this project:

| Name | Description |
|------|-------------|
| [Content Negotiation](https://start.ktor.io/p/io.ktor/server-content-negotiation) | Provides automatic content conversion according to Content-Type and Accept headers |
| [kotlinx.serialization](https://start.ktor.io/p/io.ktor/server-kotlinx-serialization) | Handles JSON serialization using kotlinx.serialization library |
| [Koin](https://insert-koin.io/docs/reference/koin-ktor/ktor/) | Provides dependency injection for application services |


## Building & Running
To build or run the project, use one of the following tasks:


| Task | Description |
|------|-------------|
| `./gradlew test`    | Run the tests     |
| `./gradlew build`   | Build the project |
| `./gradlew run`     | Run the server    |

To use SQL Server-backed todos, set:

* `SQLSERVER_JDBC_URL`
* `SQLSERVER_USERNAME`
* `SQLSERVER_PASSWORD`

To run with HTTPS, set:

* `HTTP_PORT` (for example `8080`)
* `HTTPS_PORT` (for example `8443`)
* `SSL_KEYSTORE_PATH`
* `SSL_KEY_ALIAS`
* `SSL_KEYSTORE_PASSWORD`
* `SSL_PRIVATE_KEY_PASSWORD`

Example local keystore:

`keytool -genkeypair -alias todo-api -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore ./todo-api.p12 -validity 3650`

If the server starts successfully, you'll see the following output:
```
2024-12-04 14:32:45.584 [main] INFO  Application - Application started in 0.303 seconds.
2024-12-04 14:32:45.682 [main] INFO  Application - Responding at http://0.0.0.0:8080
```
