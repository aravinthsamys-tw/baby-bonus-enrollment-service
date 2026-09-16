FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /workspace

COPY gradlew gradlew.bat settings.gradle.kts build.gradle.kts ./
COPY gradle gradle

RUN chmod +x gradlew && ./gradlew --no-daemon dependencies

COPY src src

RUN ./gradlew --no-daemon bootJar

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN addgroup -S babybonus && adduser -S babybonus -G babybonus

COPY --from=build /workspace/build/libs/*.jar app.jar
COPY mock-data mock-data

USER babybonus:babybonus

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
