# $1 userId
# $2 repo relativePath

#mkdir -p /mnt/glfs/share/personal_gitroot/$1$2
# change directory to persional repo
cd /mnt/glfs/share/personal_gitroot/$1$2
#
git fetch -p
git branch -r
