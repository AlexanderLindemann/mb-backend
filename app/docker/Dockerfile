FROM bellsoft/liberica-openjdk-alpine:11.0.11

#Установка временой зоны
RUN apk add --no-cache tzdata
ENV TZ=Europe/Moscow

WORKDIR /app

ENV MEM_ARGS "-Xms128M -Xmx128M"
ENV JVM_ARGS ""
ENV APP_ARGS ""

VOLUME /app/logs

CMD java \
    -XX:-OmitStackTraceInFastThrow \
    -XX:+UseGCOverheadLimit \
	-XX:+HeapDumpOnOutOfMemoryError \
	-XX:HeapDumpPath=/app/logs/oom_heapdump.hprof \
    -Xlog:gc*,gc+ref=debug,gc+heap=debug,gc+age=trace:file=/app/logs/gc-%p-%t.log:tags,uptime,time,level:filecount=10,filesize=10m \
    -Dcom.sun.management.jmxremote.port=8003 \
    -Dcom.sun.management.jmxremote.rmi.port=8003 \
    -Dcom.sun.management.jmxremote.ssl=false \
    -Dcom.sun.management.jmxremote.authenticate=false \
    -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8004 \
    $MEM_ARGS \
    $JVM_ARGS \
	-jar app.jar \
	--server.port=8080 \
	$APP_ARGS

HEALTHCHECK \
  CMD wget --spider --quiet http://localhost:8080/actuator/health || exit 1

#App port
EXPOSE 8080

#JMX Port
EXPOSE 8003

#DEBUG Port
EXPOSE 8004

COPY app.jar /app