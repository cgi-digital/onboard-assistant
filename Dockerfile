FROM anapsix/alpine-java
MAINTAINER James Loveday <james.lovveday@cgi.com>
EXPOSE 9000
COPY target/onboard-assistant-*.jar onboard-assistant.jar

ENTRYPOINT ["java", "-jar", "onboard-assistant.jar"]