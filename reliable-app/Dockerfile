FROM java:openjdk-8-jre-alpine

LABEL version="1.0.0"

MAINTAINER Sim <8966188@qq.com>

ENV ACTIVE test
ENV JAVA_OPTS -Xms1024m -Xmx1024m -Xmn320m

ADD target/reliable-app-1.0.0.jar /data/deploy/reliable-app.jar


CMD ["mkdir -p /data/logs"]
VOLUME /data/logs /data/logs

EXPOSE 80

ENTRYPOINT ["java","-Dspring.profiles.active=${ACTIVE}","-jar", "/data/deploy/reliable-app.jar"]