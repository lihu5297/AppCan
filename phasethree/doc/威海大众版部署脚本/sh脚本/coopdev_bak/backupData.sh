#!bash/shell
#$1 host
#$2 port
#$3 user
#$4 password
#$5 database
#$6 backup file

#/usr/bin/mysqldump -h10.0.2.2 -p3306 -ucoop_user -p cooperation > /mnt/glfs/coopDevelopment_online/backup/cooperation.sql

/usr/bin/mysqldump -h$1 -p$2 -u$3 -p$4 --database $5 > $6

echo '/usr/bin/mysqldump -h'$1' -p'$2' -u'$3' -p'$4' --database '$5' > '$6
