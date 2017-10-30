#!/bin/bash
#
#$2 oldTag
#$3 newTag
cd  /mnt/glfs/share/gitroot$1
git pull
git checkout $3
result=`git diff  $2  $3 --name-only | xargs zip $4.zip`
mkdir /tmp/$5
unzip $4.zip -d /tmp/$5
rm -rf  $4.zip
cd /tmp
zip $4.zip -r $5
rm -rf /tmp/$5
echo $result
