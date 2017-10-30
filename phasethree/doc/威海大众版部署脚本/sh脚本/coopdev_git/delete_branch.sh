#!/bin/bash


cd /mnt/glfs/share/gitroot$1

git push origin :$2
git branch -d $2
git pull

