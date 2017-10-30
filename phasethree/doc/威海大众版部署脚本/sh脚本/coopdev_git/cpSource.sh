# HTML APP 
# Copy the code from Source App
cd /mnt/glfs/share/gitroot$1
cp /mnt/glfs/share/gitroot$2/* -rf ./
git add -A
git commit -m "add from source app"
git push origin master
