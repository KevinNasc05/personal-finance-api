FROM maven:3.8.8-amazoncorretto-21-al2023 AS build
WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

FROM amazoncorretto:21
WORKDIR /app
COPY --from=build /build/target/*.jar financeapi.jar

EXPOSE 8080

ENV TZ='America/Sao_Paulo'

ENTRYPOINT ["java","-jar","financeapi.jar"]