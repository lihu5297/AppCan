cd $1
git checkout $2
git merge $3 2> /tmp/merge.tmp
read str < /tmp/merge.tmp
echo ======================================$str
rm -rf /tmp/merge.tmp
if [[ "$str" =~ "fatal" ]];then
echo "failed"
exit
else
git commit -m "merge"
git push --set-upstream origin
fi
