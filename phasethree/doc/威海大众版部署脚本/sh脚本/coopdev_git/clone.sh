mkdir -p /mnt/glfs/share/gitroot$1
cd /mnt/glfs/share/gitroot$1
git clone http://appcanadmin:appcanGitAdminResu@10.1.1.211$1 /mnt/glfs/share/gitroot$1
echo Desciption > readme.txt
git add readme.txt
git commit -m "init"
git push origin master
