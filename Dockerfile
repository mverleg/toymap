
FROM 3.9.2-eclipse-temurin-11-alpine

WORKDIR /code
COPY ./ /code/

RUN mvn clean package -T1C

