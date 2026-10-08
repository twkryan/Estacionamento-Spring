FROM eclipse-temurin:25-jdk-jammy AS build
WORKDIR /build

COPY .mvn .mvn
COPY mvnw pom.xml ./
COPY src src
RUN sed -i 's/\r$//' mvnw \
    && chmod +x mvnw \
    && ./mvnw --batch-mode --no-transfer-progress -DskipTests package

FROM eclipse-temurin:25-jre-jammy
WORKDIR /app

RUN groupadd --system --gid 10001 app \
    && useradd --system --uid 10001 --gid app app \
    && mkdir -p /app/database \
    && chown app:app /app/database

COPY --from=build --chown=app:app /build/target/estacionamento-0.0.1-SNAPSHOT.jar /app/app.jar

ENV SPRING_PROFILES_ACTIVE=render \
    TZ=America/Sao_Paulo \
    JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=60.0 -XX:InitialRAMPercentage=20.0 -XX:+UseSerialGC -Duser.timezone=America/Sao_Paulo"

USER app:app
EXPOSE 10000
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
