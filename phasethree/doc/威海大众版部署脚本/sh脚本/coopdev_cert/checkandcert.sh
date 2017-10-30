#! /bin/sh
curpath=$(cd `dirname $0`; pwd);
cd $curpath;
storefile=$1; # absolute path
storepass=$2;
keypass=$3;
aliasname=$4;
/usr/java/default/bin/jarsigner -keystore $storefile -storepass $storepass -keypass $keypass -signedjar $curpath/yqm/test_1.jar test.jar $aliasname
