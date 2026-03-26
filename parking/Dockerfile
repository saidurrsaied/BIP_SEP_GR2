# --- Build stage ---
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build

RUN echo "=== Pre-build: current directory contents ===" && ls -la && echo "==="

# Copy pom.xml (replaces build.gradle and settings.gradle)
COPY pom.xml ./

RUN echo "=== After COPY build config ===" && ls -la && echo "==="

# Download dependencies so they remain in the Docker cache (replaces gradle dependencies)
RUN mvn dependency:go-offline -B || true

# Copy the source code
COPY src ./src

RUN echo "=== After COPY of src ===" && ls -la src || echo "⚠️ No src directory found" && echo "==="

# Build the application and skip tests (replaces gradle bootJar -x test)
RUN mvn clean package -DskipTests

RUN echo "=== Check: build output ===" && \
    ls -la target || echo "⚠️  No target directory found"

# --- Runtime stage ---
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

RUN apk add --no-cache curl bash

# Maven places the built .jar by default in the 'target' directory (instead of build/libs/)
COPY --from=build /build/target/*.jar spring-boot-application.jar

EXPOSE 3000
ENTRYPOINT ["java", "-jar", "spring-boot-application.jar"]