FROM openjdk:8-jre-alpine
COPY target/broker-1.1.jar /broker.jar
EXPOSE 8080
CMD ["sh", "-c", "sleep 10 && /usr/bin/java -jar /broker.jar"]
