# $1 repo relativePath

cd $1
git add --all
git commit -m $2
git push --set-upstream origin $3
