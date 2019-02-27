#!/bin/bash

DATE=`date "+%Y-%m-%d %H:%M:%S"`
HOST=`hostname -s`

echo "${HOST} ${DATE}"

PROC=`ps -ef | grep $1 | grep java`

if [ -z "${PROC}" ]; then
   echo "No drones detected"
   exit 0
fi

echo "${PROC}"

PID=`awk '{ print $2 }' <<< ${PROC}`

if [ -z "${PID}" -o 0 -eq "${PID}" ]; then
  echo "ERROR: no pid for \"${PROC}\""
  exit 1
fi
kill -TERM ${PID}

while true; do
  sleep 2
  kill -0 ${PID}   # Test for existence of $PID
  if [ 1 -eq $? ]; then
    break;
  fi
done

