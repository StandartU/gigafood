FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY pom.xml mvnw ./
COPY .mvn .mvn

RUN chmod +x mvnw

RUN ./mvnw dependency:go-offline -B

COPY src ./src

RUN ./mvnw clean package -DskipTests

RUN mkdir -p /app/build
RUN cp target/*.jar /app/build/app.jar

ENTRYPOINT ["java", "-jar", "/app/build/app.jar"]
