FROM maven:3.8.3-openjdk-17-slim
WORKDIR /app
COPY . /app
RUN mvn clean install
WORKDIR /app/target
EXPOSE 8080
ENTRYPOINT ["java","-jar","mavenwebapp.jar"]