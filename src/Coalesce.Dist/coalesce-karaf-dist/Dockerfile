FROM openjdk:8u171-jdk-alpine3.8


ARG TARBALL=*.tar.gz
ARG TAR_PATH=./target/$TARBALL


RUN mkdir -p /opt/karaf
ADD $TAR_PATH /opt/karaf


RUN apk add --no-cache bash

EXPOSE 8101
EXPOSE 8181

ENTRYPOINT ["bash", "-c", "/opt/karaf/bin/karaf"]
