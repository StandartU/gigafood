FROM openjdk:17-jdk-slim

WORKDIR /app

COPY pom.xml .
COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN ./mvnw dependency:go-offline -B

COPY src ./src

RUN ./mvnw clean package -DskipTests

RUN mkdir -p /app/build
RUN cp target/*.jar /app/build/app.jar

ENTRYPOINT ["java", "-jar", "/app/build/app.jar"]