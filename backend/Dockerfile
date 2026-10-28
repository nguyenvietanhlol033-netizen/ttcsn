FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Install curl and tzdata for Vietnam timezone
RUN apk add --no-cache curl tzdata && \
    cp /usr/share/zoneinfo/Asia/Ho_Chi_Minh /etc/localtime && \
    echo "Asia/Ho_Chi_Minh" > /etc/timezone

# Copy compiled Spring Boot JAR
COPY target/melia-hotel-backend-1.0.0.jar app.jar

# Expose Spring Boot port
EXPOSE 8080

# Default environment configuration
ENV SPRING_PROFILES_ACTIVE=h2 \
    JAVA_OPTS="-Xms256m -Xmx512m -Dfile.encoding=UTF-8"

# Healthcheck using rooms API
HEALTHCHECK --interval=15s --timeout=5s --start-period=25s --retries=3 \
  CMD curl -f http://localhost:8080/api/v1/rooms/types || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar"]
