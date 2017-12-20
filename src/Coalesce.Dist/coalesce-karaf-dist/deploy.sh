#!/bin/bash

SERVER=bdpgeoserver.bdpdev.incadencecorp.com
DIR=$1

echo ${DIR}

scp -i ~/BDP_key.pem target/coalesce-karaf-dist-0.0.25-SNAPSHOT.tar.gz root@${SERVER}:~/.
ssh -i  ~/BDP_key.pem  root@${SERVER} mkdir ${DIR}
ssh -i  ~/BDP_key.pem  root@${SERVER} tar -xf coalesce-karaf-dist-0.0.25-SNAPSHOT.tar.gz -C ${DIR}/.
