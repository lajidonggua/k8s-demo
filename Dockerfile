FROM amazoncorretto:17

COPY app.jar .

CMD ["java", "-jar", "app.jar"]
