# ---- Build  ----
#Pentru a crea jar-urile si apoi imaginile in docker
FROM maven:3.9.8-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package -DskipTests
RUN mvn spring-boot:build-info

# ---- Run  ----
#Rularea aplicatiilor din imaginile create
FROM eclipse-temurin:21-jdk-alpine
RUN apk add --no-cache curl
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]

