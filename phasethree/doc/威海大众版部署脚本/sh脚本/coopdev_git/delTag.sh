cd /mnt/glfs/share/gitroot$1
git tag -d $2
git push origin :refs/tags/$2
