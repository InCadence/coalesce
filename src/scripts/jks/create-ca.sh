#!/bin/bash -x

openssl req -new -x509 -keyout ca/cacert.key -out ca/cacert.pem -days 730 
