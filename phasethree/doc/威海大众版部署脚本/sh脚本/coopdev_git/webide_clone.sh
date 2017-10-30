# $1 userId
# $2 repo relativePath

mkdir -p /mnt/glfs/share/personal_gitroot/$1$2
cd /mnt/glfs/share/personal_gitroot/$1$2
git clone http://appcanadmin:appcanGitAdminResu@10.1.1.211$2 /mnt/glfs/share/personal_gitroot/$1$2
