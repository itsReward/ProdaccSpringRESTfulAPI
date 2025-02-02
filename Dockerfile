FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY build/libs/*.jar app.jar
EXPOSE 5000
ENTRYPOINT ["java","-jar","app.jar"]