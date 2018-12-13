#!/bin/bash

#bdpgeoserver.bdpdev.incadencecorp.com
#oeservices

SERVER=$1
DIR=$2
VERSION=$3

if [ -z "${VERSION}" ] ; then

    read -p "Version: " VERSION

fi



SERVER_DEFAULT="oeservices"
DIR_DEFAULT="/opt/coalesce"
FILENAME="coalesce-karaf-dist-${VERSION}.tar.gz"

echo 

if [ -z "${SERVER}" ] ; then

    read -p "Server (${SERVER_DEFAULT}): " SERVER

    if [ -z "${SERVER}" ] ; then

        SERVER=$SERVER_DEFAULT

    fi 

fi

if [ -z "${DIR}" ] ; then

    read -p "Directory (${DIR_DEFAULT}): " DIR

    if [ -z "${DIR}" ] ; then

        DIR=$DIR_DEFAULT

    fi 

fi



while true; do

	

	echo
	echo "Menu (root@${SERVER}:${DIR}):"
	echo "1. Deploy (Auto Starts)"
	echo "2. Start"
	echo "3. Stop"
	echo "4. Remove"
	echo "5. Deploy Configuration"
	echo "6. Pull Tarball"
        echo "7. Build Docker"
	echo "8. Push Docker"
	echo "9. Exit"

	read -p ": " input

	case $input in 

		1)
			echo "Stopping"
			ssh -i  ~/BDP_key.pem root@${SERVER} ${DIR}/bin/stop
			echo "Deleting: ${DIR}"
			ssh -i  ~/BDP_key.pem root@${SERVER} rm -rf ${DIR}
			scp -i ~/BDP_key.pem target/${FILENAME} root@${SERVER}:~/.
			ssh -i  ~/BDP_key.pem root@${SERVER} mkdir ${DIR}
			echo "Extracting"
			ssh -i  ~/BDP_key.pem root@${SERVER} tar -xf ${FILENAME} -C ${DIR}/.
			echo "Starting"
			ssh -i  ~/BDP_key.pem root@${SERVER} ${DIR}/bin/start
			;;
		2) 	
			ssh -i  ~/BDP_key.pem root@${SERVER} ${DIR}/bin/start
			;;
		3) 
			ssh -i  ~/BDP_key.pem root@${SERVER} ${DIR}/bin/stop
			;;
		4) 
			ssh -i  ~/BDP_key.pem root@${SERVER} ${DIR}/bin/stop
			ssh -i  ~/BDP_key.pem root@${SERVER} rm -rf ${DIR}
			;;
		5) 
			read -p "Server (gdelt): " CONFIG_DIR

			if [ -z "${CONFIG_DIR}" ] ; then
		             CONFIG_DIR="gdelt"
			fi
	
			ssh -i  ~/BDP_key.pem root@${SERVER} ${DIR}/bin/stop
			scp -r -i ~/BDP_key.pem ${CONFIG_DIR}/deploy/* root@${SERVER}:${DIR}/deploy/.
			scp -r -i ~/BDP_key.pem ${CONFIG_DIR}/config/* root@${SERVER}:${DIR}/config/.
			scp -r -i ~/BDP_key.pem ${CONFIG_DIR}/etc/* root@${SERVER}:${DIR}/etc/.
			scp -r -i ~/BDP_key.pem ${CONFIG_DIR}/images/* root@${SERVER}:${DIR}/images/.
			ssh -i  ~/BDP_key.pem root@${SERVER} ${DIR}/bin/start clean
			;;
		6)      curl -XGET http://incadencenexus.incadencecorp.com:8081/nexus/service/local/repositories/releases/content/com/incadencecorp/coalesce/dist/coalesce-karaf-dist/${VERSION}/coalesce-karaf-dist-${VERSION}.zip > target/coalesce-karaf-dist-${VERSION}.zip
			unzip -q target/coalesce-karaf-dist-${VERSION}.zip -d target/tmp
			tar -czf target/coalesce-karaf-dist-${VERSION}.tar.gz -C target/tmp .
			rm -rf target/tmp
			;;
		7)      mvn dockerfile:build
			;;
		8)      mvn dockerfile:push
			;;
		9) exit 0;;
		*) echo Invalid Input
	esac 


done
