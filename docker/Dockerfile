FROM openjdk:8

COPY build/libs/collector-0.1.0-SNAPSHOT-uber.jar /opt/collector/

WORKDIR /opt/collector

CMD ["java", "-jar", "collector-0.1.0-SNAPSHOT-uber.jar"]
