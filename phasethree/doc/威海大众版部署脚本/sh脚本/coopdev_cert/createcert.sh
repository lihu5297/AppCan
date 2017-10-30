#! /bin/sh
aliasName=$1;
keypass=$2;
storepass=$3;
validdays=$4;
certfile=$5;

username=$6;
department=$7;
company=$8;
city=$9;
province=$10;
country=$11;

/usr/java/default/bin/keytool -genkey -alias $aliasName -keyalg RSA -keysize 1024 -keypass $keypass -validity $validdays -keystore $certfile -storepass $storepass -dname CN=$username,OU=$department,O=$company,L=$city,ST=$province,C=$country
