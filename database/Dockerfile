FROM openjdk:8-jre-alpine
COPY target/database-1.1.jar /database.jar
EXPOSE 8083
CMD ["sh", "-c", "sleep 10 && /usr/bin/java -jar /database.jar"]