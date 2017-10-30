# $1 repo absolute path
# $2 file relative path

# mkdir -p /mnt/glfs/share/personal_gitroot/$1
# change directory to persional repo
cd $1
# execute git rm <file>
git rm --f $2
git add $2
