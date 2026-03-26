# --- Build stage ---
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build

RUN echo "=== Pre-copy: inhoud van werkdirectory ===" && ls -la && echo "==="

# Kopieer de pom.xml (vervangt build.gradle en settings.gradle)
COPY pom.xml ./

RUN echo "=== Na COPY van build config ===" && ls -la && echo "==="

# Download dependencies zodat ze in de Docker cache blijven (vervangt gradle dependencies)
RUN mvn dependency:go-offline -B || true

# Kopieer de broncode
COPY src ./src

RUN echo "=== Na COPY van src ===" && ls -la src || echo "⚠️ Geen src gevonden" && echo "==="

# Bouw de applicatie en sla de tests over (vervangt gradle bootJar -x test)
RUN mvn clean package -DskipTests

RUN echo "=== Controle: build output ===" && \
    ls -la target || echo "⚠️  Geen target map gevonden"

# --- Runtime stage ---
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

RUN apk add --no-cache curl bash

# Maven zet de gebouwde .jar standaard in de 'target' map (in plaats van build/libs/)
COPY --from=build /build/target/*.jar spring-boot-application.jar

EXPOSE 3000
ENTRYPOINT ["java", "-jar", "spring-boot-application.jar"]