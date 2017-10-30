#!/bin/bash
#
cd /mnt/glfs/share/gitroot$1
git branch $2 2> /tmp/branch.tmp
read str < /tmp/branch.tmp
rm -rf /tmp/branch.tmp
[[ $str =~ "already" ]] && echo "branch $2 already exists" 
[[ $str =~ "already" ]] || git push --set-upstream origin $2 
