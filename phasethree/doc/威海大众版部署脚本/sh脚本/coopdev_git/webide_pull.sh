# $1 userId
# $2 repo relativePath

cd /mnt/glfs/share/personal_gitroot/$1$2
git pull --all
