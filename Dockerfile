# Multi-stage build para compilar y ejecutar
FROM maven:3.9.4-eclipse-temurin-17 AS build

WORKDIR /app

# Copiar archivos de configuración de Maven
COPY pom.xml .
COPY .mvn .mvn

# Copiar código fuente
COPY src ./src

# Compilar el proyecto
RUN mvn clean package -DskipTests

# Imagen final
FROM eclipse-temurin:17-jre-alpine

# Información del mantenedor
LABEL maintainer="sebastian@panaderia.com"
LABEL description="API REST para gestión de panadería"

# Instalar curl para healthcheck
RUN apk add --no-cache curl

# Crear directorio de aplicación
WORKDIR /app

# Crear usuario no-root para seguridad
RUN addgroup -S spring && adduser -S spring -G spring

# Variables de entorno
ENV SPRING_PROFILES_ACTIVE=production
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Crear directorios necesarios
RUN mkdir -p /app/logs /app/uploads && \
    chown -R spring:spring /app

# Copiar el JAR compilado desde la etapa de build
COPY --from=build /app/target/panaderia-api-1.0.0.jar app.jar

# Dar permisos al usuario spring
RUN chown spring:spring app.jar

# Cambiar al usuario no-root
USER spring

# Exponer puerto
EXPOSE 8080

# Comando de inicio con configuraciones optimizadas
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar"]

# Healthcheck
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/api/productos/test || exit 1