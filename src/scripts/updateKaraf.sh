#!/bin/bash

defaultdir="/c/apache-karaf-4.0.8"

read -p "Karaf Root Directory (${defaultdir}): " dir

if [ -z "${dir}" ] ; then

    dir=${defaultdir}

fi

while true; do
    read -p "Deploy (core / crud / done)? " option
    case $option in
        core) cp ../karaf/etc/org.apache.karaf.features.cfg ${dir}/etc/.;;
	crud)  cp ../karaf/deploy/service-blueprint.xml ${dir}/instances/crud/deploy/.;;
 	done) break;;
	* ) echo "Invalid Selction";;
    esac
done
