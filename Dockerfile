FROM maven:4.0.0-openjdk-24
WORKDIR /tests
COPY . .
CMD mvn clean test