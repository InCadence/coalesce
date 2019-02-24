#!/bin/bash

DIR=$1
FIRST=$2
LAST=$3

NAME=coalesce-synchronizer-drone
PATH=${NAME}*.jar

FIRST_DEFAULT=12
LAST_DEFAULT=25
DIR_DEFAULT='synchronizer'

if [ -z "${DIR}" ] ; then

    read -p "Directory (${DIR_DEFAULT}): " DIR

    if [ -z "${DIR}" ] ; then

        DIR=$DIR_DEFAULT

    fi

fi

if [ -z "${FIRST}" ] ; then

    read -p "First Node (${FIRST_DEFAULT}): " FIRST

    if [ -z "${FIRST}" ] ; then

        FIRST=$FIRST_DEFAULT

    fi

fi

if [ -z "${LAST}" ] ; then

    read -p "First Node (${LAST_DEFAULT}): " LAST

    if [ -z "${LAST}" ] ; then

        LAST=$LAST_DEFAULT

    fi

fi

function sync {

  for i in `seq -w ${FIRST} ${LAST}`; do

    nodename="oedevnode${i}"
    echo "Syncing ${nodename}:"

    ssh -t ${nodename} sudo mkdir -p /opt/${DIR} 2>/dev/null
    ssh -t ${nodename} sudo chown oe_admin:bdp_users /opt/${DIR} 2>/dev/null

    ssh ${nodename} mkdir -p /opt/${DIR}/config 2>/dev/null
    ssh ${nodename} mkdir -p /opt/${DIR}/db 2>/dev/null

    rsync -avS config ${nodename}:/opt/${DIR} 2>/dev/null
    rsync -avS db ${nodename}:/opt/${DIR} 2>/dev/null
    rsync -avS scripts/* ${nodename}:/opt/${DIR} 2>/dev/null
    rsync -avS etc/pki/truststore.jks ${nodename}:/opt/${DIR} 2>/dev/null
    rsync -avS ${PATH} ${nodename}:/opt/${DIR} 2>/dev/null

  done

}

function start {

  for i in `seq -w ${FIRST} ${LAST}`; do

    nodename="oedevnode${i}"
    echo "Starting on ${nodename}:"

    ssh ${nodename} /opt/${DIR}/drone_start.sh ${DIR} ${NAME}*.jar 2>/dev/null

  done

}

function stop {

  for i in `seq -w ${FIRST} ${LAST}`; do

    nodename="oedevnode${i}"
    echo "Stopping on ${nodename}:"

    ssh ${nodename} /opt/${DIR}/drone_stop.sh ${NAME} 2>/dev/null

  done

}


while true; do

        echo
        echo "Menu (${FIRST} to ${LAST}):"
        echo "1. Deploy"
        echo "2. Start"
        echo "3. Stop"
        echo "4. Monitor"
        echo "5. Exit"

        read -p ": " input

        case $input in

                1) sync
                   ;;
                2) start
                   ;;
                3) stop
                   ;;
                4) watch "ssh oedevnode26 /opt/kafka/kafka-current/bin/kafka-consumer-groups.sh --bootstrap-server oedevnode09:9092 --describe --group sync-drone "
                   ;;
                5) exit 0;;
                *) echo Invalid Input
        esac

done
