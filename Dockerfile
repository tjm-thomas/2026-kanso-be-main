# Build with Maven inside Docker (no Java or Maven needed on the build machine), run on a small JRE.
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /src
COPY pom.xml .
RUN mvn -q -B dependency:go-offline
COPY src ./src
RUN mvn -q -B package

FROM eclipse-temurin:25-jre-alpine
# Build-time version, returned by GET /api/status
ARG VERSION=dev
ENV APP_VERSION=${VERSION}
# Size the JVM heap from the container's memory limit, leaving room for the rest of the JVM
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=60 -XX:+ExitOnOutOfMemoryError"
RUN addgroup -S app && adduser -S app -G app
USER app
WORKDIR /app
COPY --from=build /src/target/app.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
