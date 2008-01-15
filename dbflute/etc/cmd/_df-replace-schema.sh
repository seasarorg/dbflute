#!/bin/sh

NATIVE_PROPERTIES_PATH=$1

sh $DBFLUTE_HOME/etc/cmd/_df-copy-properties.sh $NATIVE_PROPERTIES_PATH

# {From Mr.Akikusa}
read -p "Database will be initialized. Are you ready?�iy/n�j" answer
if [ $answer = "y" ] ;then
    $DBFLUTE_HOME/ant/bin/ant -f $DBFLUTE_HOME/build-torque.xml replace-schema
fi

