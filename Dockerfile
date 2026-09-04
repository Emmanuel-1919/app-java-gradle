# Etapa 1: build
FROM gradle:jdk21-alpine AS builder
WORKDIR /app-java-gradle/built-1
COPY settings.gradle .
COPY build.gradle .
COPY gradlew .
COPY gradle gradle
COPY src src
RUN ./gradlew --no-daemon clean build

# Etapa 2: runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app-java-gradle
COPY --from=builder /app-java-gradle/built-1/build/libs/app-java-gradle.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]