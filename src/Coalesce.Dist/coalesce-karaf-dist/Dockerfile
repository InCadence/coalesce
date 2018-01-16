FROM java:8-jdk
ENV tarball=coalesce-karaf-dist-*.tar.gz

RUN apt-get update \
  && apt-get -y install maven \
  && apt-get clean \
  && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

RUN mkdir /tmp/install
COPY target/${tarball} /tmp/install

RUN mkdir /opt/karaf
RUN tar -xzvf /tmp/install/${tarball} -C /opt/karaf/
RUN rm -rf /tmp/install/${tarball}

EXPOSE 1099 8101 44444 8181

ENTRYPOINT ["/opt/karaf/bin/karaf"]
