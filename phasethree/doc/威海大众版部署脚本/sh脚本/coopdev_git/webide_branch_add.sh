# $1 userId
# $2 repo relativePath
# $3 branchName

# mkdir -p /mnt/glfs/share/personal_gitroot/$1$2
# change directory to persional repo
cd /mnt/glfs/share/personal_gitroot/$1$2
# create new branch base on current branch
git branch $3
