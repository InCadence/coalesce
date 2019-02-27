#!/bin/bash

DATE=`date "+%Y-%m-%d %H:%M:%S"`
HOST=`hostname -s`

echo "${HOST} ${DATE}"

cd $1

eval nohup java -jar $2 >/dev/null 2>&1 &

