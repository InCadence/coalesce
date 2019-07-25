#!/bin/bash -x

storepass=$1
keypass=$2

if [[ -z ${alias} ]] ; then
    read -p "Storepass: " storepass
    read -p "Keypass (${storepass}): " keypass

    if [[ -z ${keypass} ]] ; then
        keypass=$storepass
    fi
fi

keytool -importcert -trustcacerts -file ca/cacert.pem -alias cacert -keystore java/trusted.jks -storepass $storepass -keypass $keypass

