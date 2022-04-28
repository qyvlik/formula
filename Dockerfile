FROM maven:3-jdk-8 as builder

WORKDIR /home/www/formula

ADD pom.xml /home/www/formula

RUN ["/usr/local/bin/mvn-entrypoint.sh", "mvn", "verify", "clean", "--fail-never"]

ADD . /home/www/formula

RUN mvn -DskipTests package

# https://stackoverflow.com/questions/42208442/maven-docker-cache-dependencies

FROM openjdk:8-alpine

ARG TIME_ZONE=Asia/Shanghai
ARG APK_REPOSITORIES=mirrors.aliyun.com

RUN sed -i "s/dl-cdn.alpinelinux.org/${APK_REPOSITORIES}/g" /etc/apk/repositories

RUN apk add --update --no-cache tzdata font-adobe-100dpi ttf-dejavu fontconfig \
    && echo "${TIME_ZONE}" > /etc/timezone \
    && ln -sf /usr/share/zoneinfo/${TIME_ZONE} /etc/localtime \
    && rm -rf /var/cache/apk/*

ARG BUILD_APP_NAME="formula"
ARG BUILD_APP_GIT_TAG="master"
ARG BUILD_APP_GIT_HASH="master"
ARG BUILD_APP_BUILDER="qyvlik"

MAINTAINER "<qyvlik@qq.com>"

VOLUME /tmp

WORKDIR /home/www

RUN adduser -D -u 1000 www www \
    && chown www:www -R /home/www

COPY --from=builder /home/www/formula/target/*.jar formula.jar

EXPOSE 8120

USER www

ENV JAVA_OPTS=""
ENV app_name=$BUILD_APP_NAME
ENV app_git_tag=$BUILD_APP_GIT_TAG
ENV app_git_hash=$BUILD_APP_GIT_HASH
ENV app_builder=$BUILD_APP_BUILDER

ENTRYPOINT exec java $JAVA_OPTS -jar /home/www/formula.jar
