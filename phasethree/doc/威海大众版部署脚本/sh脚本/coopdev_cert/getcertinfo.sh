#! /bin/sh
storefile=$1; # absolute path
storepass=$2;
storetype=$3; # PKCS12 or JKS
/usr/java/default/bin/keytool -list -v -keystore $storefile -storepass $storepass -storetype $storetype
