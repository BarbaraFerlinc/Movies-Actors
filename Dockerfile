FROM maven:3.9-eclipse-temurin-25

WORKDIR /app

COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q package -DskipTests
RUN useradd --system --create-home app && mkdir /data && chown -R app:app /app /data

USER app
ENV DB_URL=jdbc:h2:file:/data/moviesdb
VOLUME /data
EXPOSE 8080

CMD ["java", "-jar", "target/quarkus-app/quarkus-run.jar"]