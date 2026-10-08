FROM eclipse-temurin:25-jre

LABEL org.opencontainers.image.title="ferrio-bot"
LABEL org.opencontainers.image.authors="Andret2344"
LABEL org.opencontainers.image.description="Discord bot 'ferrio-bot' showing today's unusual holiday from Ferrio, written in Java 25, packaged as a runnable JAR and intended to run inside a container. Requires the FERRIO_TOKEN environment variable."
LABEL org.opencontainers.image.url="https://github.com/Andret2344/ferrio-bot"
LABEL org.opencontainers.image.source="https://github.com/Andret2344/ferrio-bot"
LABEL org.opencontainers.image.licenses="CC-BY-SA-4.0"

RUN groupadd --system --gid 10001 ferrio \
    && useradd --system --uid 10001 --gid ferrio --no-create-home --shell /usr/sbin/nologin ferrio

COPY build/libs/ferrio-bot.jar ferrio.jar

# Today's holiday and log timestamps follow Polish time, not the container's default UTC
ENV TZ=Europe/Warsaw

# The bot needs no privileges: it only makes outbound connections and logs to stdout
USER 10001:10001
ENTRYPOINT ["java", "-jar", "/ferrio.jar"]
