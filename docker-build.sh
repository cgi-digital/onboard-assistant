./mvnw package
docker build . -t onboard-assistant
docker run --network host onboard-assistant
