
FROM maven:3.9.2-eclipse-temurin-17-alpine

WORKDIR /code
COPY ./ /code/

RUN mvn clean package -T1C

