#! /bin/sh
openssl ecparam -name secp224k1 -genkey -param_enc explicit -out privateECkey.pem
openssl ec -aes256 -in privateECkey.pem -out privateECkey.pem 
openssl req -new -x509 -key privateECkey.pem -out ECcert.pem -days 365 

openssl genrsa -aes256 -out privateRSAkey.pem 2048
openssl req -new -x509 -key privateRSAkey.pem -out RSAcert.pem -days 365

cat ECcert.pem RSAcert.pem > certificates.pem
rm ECcert.pem RSAcert.pem


