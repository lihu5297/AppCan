#! /bin/sh
URRPATH=`pwd`
TMPFILE1=$CURRPATH/tmp1
TMPFILE2=$CURRPATH/tmp2
    echo > $TMPFILE1
    echo > $TMPFILE2
    if [ $# -eq 1 ]
    then
    /usr/bin/tail -n 1000 $1 >> $TMPFILE2
    fi
    if [ $# -eq 2 ]
    then
    /usr/bin/tail -n $2 $1 >> $TMPFILE2
    else
    exit 1
    fi
    while read LINE
    do
    echo $LINE | grep '^[M,T,W,T,F,S][o,u,e,h,r,a][n,e,d,u,i,t,n] ' >/dev/null
    FLAG=$?
    if [ $FLAG -eq 0 ]
    then
    echo "$CACHELINE" >> $TMPFILE1
    if cat $TMPFILE1 | grep [E,e]rror >> /dev/null
    then
    cat $TMPFILE1
    fi
    echo > $TMPFILE1
    fi
    if [ $FLAG -eq 1 ]
    then
    echo $CACHELINE >> $TMPFILE1
    fi
    CACHELINE=$LINE
    done < $TMPFILE2


