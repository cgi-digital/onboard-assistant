FROM anapsix/alpine-java
EXPOSE 9000
COPY target/onboard-assistant-*.jar onboard-assistant.jar
#ADD . target
#RUN java -cp onboard-assistant.jar io.micronaut.graal.reflect.GraalClassLoadingAnalyzer
#RUN native-image --no-server \
#             --class-path onboard-assistant.jar \
#             --allow-incomplete-classpath \
#             -H:ReflectionConfigurationFiles=target/reflect.json \
#             -H:EnableURLProtocols=http \
#             -H:IncludeResources="logback.xml|application.yml|META-INF/services/*.*" \
#             -H:Name=onboard-assistant \
#             -H:Class=onboard.assistant.Application \
#             -H:+ReportUnsupportedElementsAtRuntime \
#             -H:+AllowVMInspection \
#             -H:-UseServiceLoaderFeature \
#             --rerun-class-initialization-at-runtime='sun.security.jca.JCAUtil$CachedSecureRandomHolder,javax.net.ssl.SSLContext,com.github.seratch.jslack.Slack' \
#             --delay-class-initialization-to-runtime=io.netty.handler.codec.http.HttpObjectEncoder,io.netty.handler.codec.http.websocketx.WebSocket00FrameEncoder,io.netty.handler.ssl.util.ThreadLocalInsecureRandom,com.sun.jndi.dns.DnsClient
#ENTRYPOINT ["./onboard-assistant"]

ENTRYPOINT ["java", "-jar", "onboard-assistant.jar"]