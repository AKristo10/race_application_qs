FROM maven:3.9.1-eclipse-temurin-17 as builder

WORKDIR /app
RUN apt-get update && apt-get -y install jq
COPY pom.xml .
COPY src ./src

COPY params.json params.json

RUN mvn clean package \
    -DdatasourceUrl=$(cat params.json | jq -r '."spring.datasource.url"') \
    -DdatasourceUsername=$(cat params.json | jq -r '."spring.datasource.username"') \
    -DdatasourcePassword=$(cat params.json | jq -r '."spring.datasource.password"') \
    -DdatasourceName=$(cat params.json | jq -r '."spring.datasource.name"') \
    -Dmaven.test.skip=true \
    --batch-mode --no-transfer-progress

FROM amazoncorretto:17-alpine
WORKDIR /app

COPY --from=builder /app/target/race_application_qs-0.0.1-SNAPSHOT.jar ./race_application_qs-0.0.1-SNAPSHOT.jar

EXPOSE 8080

CMD ["java", "-jar", "race_application_qs-0.0.1-SNAPSHOT.jar"]
