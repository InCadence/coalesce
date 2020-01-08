#!/bin/bash

PASS=$1
CAPASS=$2
HOSTS=$(cat hosts.txt)

if [[ -z ${PASS} ]] ; then
    read -p "password: " PASS
    read -p "CA password (${PASS}): " CAPASS


fi

if [[ -z ${CAPASS} ]] ; then
    CAPASS=${PASS}
fi

for HOST in ${HOSTS}
do 
    ARGS=$(echo ${HOST} | tr ":" " ")
    ALIAS=$(echo ${ARGS} | cut -f 1 -d " ")

    if [ -f java/${ALIAS}.jks ]
    then
        echo ${ALIAS} Already Exists
    else
         echo Creating ${ALIAS}
        ./create-private.sh ${ARGS} ${PASS} ${PASS} ${CAPASS}		   
        ./create-key.sh ${ALIAS} ${PASS} ${PASS}	   
    fi

done

