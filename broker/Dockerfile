FROM openjdk:8-jre-alpine
COPY target/broker-1.1.jar /broker.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "broker.jar"]
