FROM openjdk:8-jre

RUN apt-get update && \
    apt-get install -y procps && \
    apt-get install -y vim

ENV KARAF_INSTALL_PATH /opt
ENV KARAF_HOME $KARAF_INSTALL_PATH/karaf
ENV PATH $PATH:$KARAF_HOME/bin

ENV JAVA_MIN_MEM=1G
ENV JAVA_MAX_MEM=1G

WORKDIR $KARAF_HOME

ADD ./target/*.tar.gz $KARAF_HOME

EXPOSE 8101 8181 5005

CMD ["karaf", "run"]
