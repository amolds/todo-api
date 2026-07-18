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


## Database setup

```sql
CREATE DATABASE todo
ON PRIMARY (
    NAME = todo_data,
    FILENAME = '/var/opt/mssql/data/todo_data.mdf',
    SIZE = 100MB,
    FILEGROWTH = 25MB
)
LOG ON (
    NAME = todo_log,
    FILENAME = '/var/opt/mssql/data/todo_log.ldf',
    SIZE = 50MB,
    FILEGROWTH = 10MB
);
GO

IF OBJECT_ID(N'dbo.Tasks', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.Tasks (
        id NVARCHAR(100) NOT NULL PRIMARY KEY,
        username NVARCHAR(255) NOT NULL,
        title NVARCHAR(255) NOT NULL,
        description NVARCHAR(MAX) NOT NULL,
        priority NVARCHAR(20) NOT NULL,
        completed BIT NOT NULL CONSTRAINT DF_Tasks_Completed DEFAULT (0),

        CONSTRAINT CK_Tasks_Priority
            CHECK (priority IN ('Low', 'Medium', 'High', 'Vital'))
    );

    CREATE INDEX IX_Tasks_Username ON dbo.Tasks(username);
    CREATE INDEX IX_Tasks_Username_Priority ON dbo.Tasks(username, priority);
END
GO
```

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
