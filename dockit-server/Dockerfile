FROM openjdk:17-oracle
EXPOSE 8080
ARG WAR_FILE=target/dockit-server-0.0.1.war
ADD ${WAR_FILE} dockit-server.war
ENV USERNAME = "admin"
ENV PASSWORD = "changeit"
ENTRYPOINT ["java","-jar","/dockit-server.war"]