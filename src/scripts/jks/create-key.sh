
ALIAS=$1
KEYPASS=$2
STOREPASS=$3

if [[ -z ${ALIAS} ]] ; then
    read -p "Alias: " ALIAS
    read -p "Storepass: " STOREPASS
    read -p "Keypass (${STOREPASS}): " KEYPASS

    if [[ -z ${KEYPASS} ]] ; then
        KEYPASS=${STOREPASS}
    fi
fi

keytool -importkeystore -srckeystore java/${ALIAS}.jks -srckeypass ${KEYPASS} -srcstorepass ${STOREPASS} -srcalias ${ALIAS} -destalias ${ALIAS} -destkeystore keys/${ALIAS}.p12 -destkeypass ${KEYPASS} -deststorepass ${STOREPASS} -deststoretype PKCS12 
openssl pkcs12 -in keys/${ALIAS}.p12 -nodes -nocerts -out keys/${ALIAS}.pem -passin pass:${KEYPASS} -passout pass:${KEYPASS}
openssl pkcs12 -in keys/${ALIAS}.p12 -nokeys -out keys/${ALIAS}.pub -passin pass:${KEYPASS} -passout pass:${KEYPASS}



#keytool -export -alias ${ALIAS} -file keys/${ALIAS}.der -keystore java/${ALIAS}.jks -keypass ${KEYPASS} -storepass ${STOREPASS}

#openssl x509 -inform der -in keys/${ALIAS}.der -out keys/${ALIAS}.pem

#keytool -importkeystore -srckeystore java/${ALIAS}.jks -destkeystore keys/${ALIAS}.p12 -deststoretype PKCS12 -keypass ${KEYPASS} -storepass ${STOREPASS}

#openssl pkcs12 -in keys/${ALIAS}.p12  -nodes -nocerts -out keys/${ALIAS}.key

rm keys/${ALIAS}.p12
