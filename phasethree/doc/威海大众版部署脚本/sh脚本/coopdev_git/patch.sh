#!/bin/bash
#
#$2 oldTag
#$3 newTag
echo $1_$2_$3_$4 > /mnt/glfs/coopDevelopment_online/zipfile/appCode/testinginfo.txt
cd /mnt/glfs/share/gitroot$1
git pull
result=`git diff  $2  $3 --name-status | xargs zip $4.zip`
echo $result
