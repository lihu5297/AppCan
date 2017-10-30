#$1  /000/000/appid/xxx.git
#$2  branchName
#$3  versionNo

cd /mnt/glfs/share/gitroot$1
git pull
git checkout $2
git tag -a $3 -m "mark tag $3 in branch $2"
git push origin $3
