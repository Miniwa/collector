FROM openjdk:8

COPY build/libs/collector-0.1.1-uber.jar /opt/collector/

WORKDIR /opt/collector

CMD ["java", "-jar", "collector-0.1.1-uber.jar"]
