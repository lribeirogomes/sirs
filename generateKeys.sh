#! /bin/bash
if [ $# -eq 0 ]
then
	echo "Arguments: self-signed | request"
	exit 1
fi

echo Enter your name:
read name
echo Enter your phone number:
read phonenumber
echo Enter your email:
read email

function generatePrivateKeys {
	openssl ecparam -name secp224k1 -genkey -param_enc explicit -out privateECkey.pem
	openssl ec -aes256 -in privateECkey.pem -out privateECkey.pem 
	openssl genrsa -aes256 -out privateRSAkey.pem 2048
}

if [[ -f "privateECkey.pem" ]] && [[ -f "privateRSAkey.pem" ]] ; then
	echo Create new private keys? \(this will OVERWRITE the existing ones\) \(y/n\)
	read overwrite
	if [[ $overwrite == "y" ]] ; then
		generatePrivateKeys
	fi
else
	generatePrivateKeys
fi

echo Please enter the same password everytime you are asked!

signsubj="/C=PT/ST=Lisbon/L=Lisbon/O=$name/OU=Signing/CN=$phonenumber/emailAddress=$email"
encryptsubj="/C=PT/ST=Lisbon/L=Lisbon/O=$name/OU=Encryption/CN=$phonenumber/emailAddress=$email"


if [[ $1 == "self-signed" ]] ; then
	openssl req -new -x509 -key privateECkey.pem -out ECcert.pem -days 365 -subj "$signsubj"
	openssl req -new -x509 -key privateRSAkey.pem -out RSAcert.pem -days 365 -subj "$encryptsubj"

	cat ECcert.pem RSAcert.pem > certificates.pem
	rm ECcert.pem RSAcert.pem
elif [[ $1 == "request" ]] ; then
	openssl req -new -sha256 -key privateECkey.pem -out signing.csr -days 365 -subj "$signsubj"
	openssl req -new -sha256 -key privateRSAkey.pem -out encryption.csr -days 365 -subj "$encryptsubj"
fi
