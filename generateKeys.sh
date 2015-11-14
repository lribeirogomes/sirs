#! /bin/bash
echo Enter your name:
read name
echo Enter your phone number:
read phonenumber
echo Enter your email:
read email
echo Please enter the same password everytime you are asked!

subj="/C=PT/ST=Lisbon/L=Lisbon/O=SecureSMS/OU=$name/CN=$phonenumber/emailAddress=$email"

openssl ecparam -name secp224k1 -genkey -param_enc explicit -out privateECkey.pem
openssl ec -aes256 -in privateECkey.pem -out privateECkey.pem 
openssl req -new -x509 -key privateECkey.pem -out ECcert.pem -days 365 -subj "$subj"

openssl genrsa -aes256 -out privateRSAkey.pem 2048
openssl req -new -x509 -key privateRSAkey.pem -out RSAcert.pem -days 365 -subj "$subj"

cat ECcert.pem RSAcert.pem > certificates.pem
rm ECcert.pem RSAcert.pem


