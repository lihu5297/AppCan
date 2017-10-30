#!bin/sh

#$1 ip
#$2 port

/opt/openoffice4/program/soffice -headless -accept="socket,host=$1,port=$2;urp;" -nofirststartwizard &
