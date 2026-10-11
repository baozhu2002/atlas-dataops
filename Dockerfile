FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
COPY atlas-domain/pom.xml atlas-domain/pom.xml
COPY atlas-api/pom.xml atlas-api/pom.xml
RUN mvn -q -pl atlas-api -am dependency:go-offline
COPY atlas-domain/src atlas-domain/src
COPY atlas-api/src atlas-api/src
RUN mvn -q -pl atlas-api -am package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/atlas-api/target/atlas-api-0.1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
