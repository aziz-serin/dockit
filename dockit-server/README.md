# Dockit Server

## Background

This application, along with ```dockit-agent``` is developed as a solution for unified auditing and monitoring for virtual
machines and docker engines which run in a cloud or in an on-premise setting. If configured correctly, it processes upcoming
agent requests, records audit data sent from the agent and analyzes and alerts the admin users if there is any abnormality or issue
in the audited virtual machine.

## Configuration

There are two important files for configuration, the first being the 
[application properties file](src/main/resources/application.properties). This file contains many config parameters about database,
server port, etc., but the most important one being the three:

```properties
dockit.server.config.file.name=#config file name
dockit.server.keystore.name=#keystore name
dockit.server.config.directory.path=# path to config directory
```

The first two properties are straightforward, just the name of the specified config files. ```directory.path``` should be the location
for your existing config directory from another deployment, or manually constructed config, or, the directory you wish to generate the
config location for. To get a better understanding, if deploying for the first time set it to a directory of your choice where you wish to
generate the config for, and see the generated config files. The generated config file will contain additional configuration about some operations
performed by the server, and improper configuration of these variables can result in crashes, so consult the documentation before changing them.  
  
For emailing functionality to work, update ```spring.mail``` properties of the [application properties file](src/main/resources/application.properties).
At the moment, configuration parameters are set to use gmail smtp host, but desired it can be updated. For more information about configuring the
gmail smtp settings, consult [here](https://support.google.com/mail/answer/7104828?hl=en&rd=3&visit_id=638459342455166969-3019610846).  
  
There are two required environment variables which need to be set for the server to be able to start up, which are ```USERNAME``` and ```PASSWORD```.
These values represent the username and password for the default admin to be created in the server when it is deployed. It is advised the admin created with these
values after the deployment should be updated to ensure security of the created admin user. You can set these variables before running the server locally by placing
them before the run command as following,

```bash
USERNAME=YOUR_USERNAME PASSWORD=YOUR_PASSWORD ./mvnw spring-boot:run
```

This method sets them only for the current command, ```./mvnw``` script in this case and does not modify your current environment. Alternatively, they can also be 
exported to be used later in the same session as follows:

```bash
export USERNAME=YOUR_USERNAME PASSWORD=YOUR_PASSWORD
```

## Deployment

### Local Deployment

To deploy the application, you will need a postgres database running somewhere else. You do this locally by consulting the documentation [here](https://www.postgresql.org/docs/current/server-start.html),
or by using the provided ```compose.yaml``` file and only starting the service postgres as following:

```bash
docker compose up --build postgres
```

After this step, make sure the [application properties file](src/main/resources/application.properties) has proper configuration for datasource properties.
Then, you can proceed to run the application with the command:

```bash
./mvnw spring-boot:run
```

### Docker Deployment

To deploy the application in a fully containerised manner, make sure the update the [application properties file](src/main/resources/application.properties)
```datasource.url``` property to point to the database instance in the docker network, and deploy the database and the application together as following:

```bash
docker compose up --build
```

To stop the application, run

```bash
docker compose down
```

## Development

### Documentation

The project uses the maven-javadoc plugin to generate a static javadoc site. The documentation for the application itself
and REST endpoints can all be found in the generated javadocs. To generate them, run:

```bash
mvn javadoc:javadoc
```

To view the documentation, you can run

```bash
open PATH_TO_TARGET/target/site/index.html
``` 

### Performance Tests

There are performance tests written in [here](/src/test/java/org/dockit/performance), which uses the gatling framework. To run
them, you can use the command:

```bash
mvn gatling:test
```