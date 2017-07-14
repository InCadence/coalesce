FROM java:8-jdk
RUN apt-get update \
  && apt-get -y install maven\
  && apt-get clean && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

RUN mkdir /opt/karaf;
RUN mkdir /tmp/install;

ADD . /tmp/install

RUN tar -xzvf /tmp/install/coalesce-karaf-dist-0.0.25-SNAPSHOT.tar.gz -C /opt/karaf/

EXPOSE 1099 8101 44444 8181

ENTRYPOINT ["/opt/karaf/bin/karaf"]
#CMD ["/usr/local/coalesce-dist/bin/karaf"]
