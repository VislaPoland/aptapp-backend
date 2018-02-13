FROM openjdk:8u151-jdk-slim
EXPOSE 8080
RUN mkdir -p /app/
ARG DIST_FILE=/build/distribution/aptapp-backend-1.0.0.tar
ADD ${DIST_FILE} /app
ENTRYPOINT ["/app/aptapp-backend-1.0.0/bin/aptapp-backend"]
