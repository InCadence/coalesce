FROM java:8-jre
ARG tarball=coalesce-karaf-dist-0.0.28-SNAPSHOT.tar.gz

# Add Maven dependencies (not shaded into the artifact; Docker-cached)
# ADD target/lib           /usr/share/myservice/lib
# Add the service itself
RUN mkdir /tmp/install
COPY target/coalesce-karaf-dist-0.0.28-SNAPSHOT.tar.gz /tmp/install

RUN mkdir /opt/karaf
RUN tar -xzvf /tmp/install/coalesce-karaf-dist-0.0.28-SNAPSHOT.tar.gz -C /opt/karaf/
RUN rm -rf /tmp/install/${tarball}

EXPOSE  8101 8181

ENTRYPOINT ["/opt/karaf/bin/karaf"]
