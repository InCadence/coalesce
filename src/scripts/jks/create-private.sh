#!/bin/bash 

ALIAS=$1
HOST=$2
IP=$3
STOREPASS=$4
KEYPASS=$5
CAPASS=$6

if [[ -z ${ALIAS} ]] ; then
    read -p "Alias: " ALIAS
    read -p "Host: " HOST
    read -p "IP: " IP
    read -p "Storepass: " STOREPASS
    read -p "Keypass (${STOREPASS}): " KEYPASS

    if [[ -z ${KEYPASS} ]] ; then
        KEYPASS=${STOREPASS}
    fi
fi

# Create keystore
keytool -genkeypair -validity 730 -alias ${ALIAS} -keyalg RSA -keystore java/${ALIAS}.jks -dname "cn=${HOST}, ou=coalesce, o=InCadence, l=Manassas, st=Virginia, c=US" -ext san=ip:${IP} -keypass ${KEYPASS} -storepass ${STOREPASS}

# Create signing request
keytool -keystore java/${ALIAS}.jks -certreq -alias ${ALIAS} -keyalg RSA -ext san=ip:158.187.254.155 -file ${ALIAS}.csr -storepass ${STOREPASS} -keypass ${KEYPASS}

# Create signed certificate
openssl x509 -req -CA ca/cacert.pem -CAkey ca/cacert.key -in ${ALIAS}.csr -out ${ALIAS}-signed.cer -days 730 -CAcreateserial -extfile openssl.cnf -extensions v3_req -passin pass:${CAPASS}

# Create certificate chain
cat ${ALIAS}-signed.cer ca/cacert.pem > cer/${ALIAS}.cer

# Import signed certificate
keytool -import -noprompt -file cer/${ALIAS}.cer -alias ${ALIAS} -keystore java/${ALIAS}.jks -storepass ${STOREPASS} -keypass ${KEYPASS}

# Cleanup
rm ${ALIAS}.csr
rm ${ALIAS}-signed.cer

