#!/bin/sh

NATIVE_PROPERTIES_PATH=$1

sh $DBFLUTE_HOME/etc/cmd/_df-copy-properties.sh $NATIVE_PROPERTIES_PATH

# {From Mr.Akikusa}
read -p "Database will be initialized. Are you ready?Åiy/nÅj" answer
if [ $answer = "y" ] ;then
    ant -f $DBFLUTE_HOME/build-torque.xml replace-schema
fi

