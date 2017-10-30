# Created by yang.li 2015-09-12
# Specification for engine's name:
# 	Engine format: engine_{engineId}_{engineType}_{osType}_{version}
#       Plugin format: pluginVersion_{pluginVersionId}_{pluginType}_{osType}_{version}_{enName}
#       Plugin Resource format: pluginResource_{pluginVersionId}_{pluginType}_{osType}_{version}_{enName}
#       English words & digits ONLY
# Variables:
# 	$1 : path of engine file on disk
#	$2 : file's name in git repository
#       $3 : path of build repository ( where engine & plugin resources stored )
cd $3
echo y | cp $1 ./$2
echo A | unzip ./$2 -d ./d_$2
git add ./d_$2
git commit -m "add file"
git push --set-upstream origin
