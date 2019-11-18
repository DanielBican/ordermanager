ARG PROJECT_DIR
FROM openjdk:8-jre-alpine
EXPOSE 8080/tcp
ADD $PROJECT_DIR/build/libs/ordermanager-*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
