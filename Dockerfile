# formula
FROM maven:alpine as builder

ADD ./pom.xml pom.xml

ADD ./src src/

VOLUME /var/maven/.m2

RUN mvn -DskipTests clean package

FROM openjdk:8-alpine

ARG BUILD_APP_NAME="formula"
ARG BUILD_APP_GIT_TAG="master"
ARG BUILD_APP_GIT_HASH="master"
ARG BUILD_APP_BUILDER="admin"
ARG TIME_ZONE=Asia/Shanghai
ARG APK_REPOSITORIES=mirrors.aliyun.com

RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.aliyun.com/g' /etc/apk/repositories

RUN apk add --update --no-cache tzdata font-adobe-100dpi ttf-dejavu fontconfig \
    && echo "${TIME_ZONE}" > /etc/timezone \
    && ln -sf /usr/share/zoneinfo/${TIME_ZONE} /etc/localtime \
    && rm -rf /var/cache/apk/*

MAINTAINER "<qyvlik@qq.com>"

VOLUME /tmp

WORKDIR /home/www

RUN adduser -D -u 1000 www www \
    && chown www:www -R /home/www \

COPY --from=builder target/*.jar formula.jar

EXPOSE 8120

USER www

ENV JAVA_OPTS=""
ENV app_name=$BUILD_APP_NAME
ENV app_git_tag=$BUILD_APP_GIT_TAG
ENV app_git_hash=$BUILD_APP_GIT_HASH
ENV app_builder=$BUILD_APP_BUILDER

ENTRYPOINT exec java $JAVA_OPTS -jar /home/www/formula.jar
