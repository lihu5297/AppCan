sleep 20
cd $1
result=`git pull --all`
echo $result > /mnt/glfs/share/notify_pull_result
