# $1 userId
# $2 repo relativePath
# $3 relativepath

# change directory to persional repo
cd /mnt/glfs/share/personal_gitroot/$1$2
#
git diff HEAD $3
