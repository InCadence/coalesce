FROM openjdk:8u171-jdk-alpine3.8


ARG TARBALL=*.tar.gz
ARG TAR_PATH=./target/$TARBALL


RUN apk add --no-cache bash && \ 
    apk add supervisor && \  
    mkdir -p /opt/karaf /var/log/supervisor

ADD $TAR_PATH /opt/karaf
COPY supervisord.conf /etc/supervisord.conf

EXPOSE 8101
EXPOSE 8181

CMD ["/usr/bin/supervisord"] 
