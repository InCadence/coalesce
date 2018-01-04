#!/bin/bash

#bdpgeoserver.bdpdev.incadencecorp.com
#oeservices

SERVER=$1
DIR=$2

echo 
echo root@${SERVER}:${DIR}

while true; do

	echo
	echo "Menu:"
	echo "1. Deploy (Auto Starts)"
	echo "2. Start"
	echo "3. Stop"
	echo "4. Remove"
	echo "5. Exit"

	read -p ": " input

	case $input in 

		1) 
			scp -i ~/BDP_key.pem target/coalesce-karaf-dist-0.0.25-SNAPSHOT.tar.gz root@${SERVER}:~/.
			ssh -i  ~/BDP_key.pem root@${SERVER} mkdir ${DIR}
			ssh -i  ~/BDP_key.pem root@${SERVER} tar -xf coalesce-karaf-dist-0.0.25-SNAPSHOT.tar.gz -C ${DIR}/.
			ssh -i  ~/BDP_key.pem root@${SERVER} ${DIR}/bin/start;;
		2) ssh -i  ~/BDP_key.pem root@${SERVER} ${DIR}/bin/start;;
		3) ssh -i  ~/BDP_key.pem root@${SERVER} ${DIR}/bin/stop;;
		4) ssh -i  ~/BDP_key.pem root@${SERVER} rm -rf ${DIR};;
		5) exit 0;;
		*) echo Invalid Input
	esac 


done
