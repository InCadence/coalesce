#!/bin/bash

#bdpgeoserver.bdpdev.incadencecorp.com
#oeservices

SERVER=$1
DIR=$2

SERVER_DEFAULT="oeservices"
DIR_DEFAULT="/opt/coalesce"

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
	echo "6. Exit"

	read -p ": " input

	case $input in 

		1)
			echo "Stopping"
			ssh -i  ~/BDP_key.pem root@${SERVER} ${DIR}/bin/stop
			echo "Deleting: ${DIR}"
			ssh -i  ~/BDP_key.pem root@${SERVER} rm -rf ${DIR}
			scp -i ~/BDP_key.pem target/coalesce-karaf-dist-0.0.25-SNAPSHOT.tar.gz root@${SERVER}:~/.
			ssh -i  ~/BDP_key.pem root@${SERVER} mkdir ${DIR}
			echo "Extracting"
			ssh -i  ~/BDP_key.pem root@${SERVER} tar -xf coalesce-karaf-dist-0.0.25-SNAPSHOT.tar.gz -C ${DIR}/.
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

		6) exit 0;;
		*) echo Invalid Input
	esac 


done
