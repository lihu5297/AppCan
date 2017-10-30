# $1 userId
# $2 repo relativePath
# $3 branch name

#mkdir -p /mnt/glfs/share/personal_gitroot/$1$2
# change directory to persional repo
cd /mnt/glfs/share/personal_gitroot/$1$2
#
git fetch
git checkout $3
