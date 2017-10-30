# $1 userId
# $2 repo relativePath
# $3 message
# $4 filePath
cd /mnt/glfs/share/personal_gitroot/$1$2
# git add $4
#git commit -m $3 -i $4
git commit -m $3 $4
