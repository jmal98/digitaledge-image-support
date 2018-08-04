FROM java:openjdk-8-jre

ADD container-service-2.1.2-SNAPSHOT.jar /tmp/

COPY system-base.tar.gz gateway.tar.gz activemq.tar.gz ingest.tar.gz transport.tar.gz custom.tar.gz /tmp/

# The docker-java 3.0.0-RC1 requires this path to exist
RUN mkdir -p /root/.docker/certs

WORKDIR /tmp/

EXPOSE 8080

CMD ["java","-jar","container-service-2.1.2-SNAPSHOT.jar"]
