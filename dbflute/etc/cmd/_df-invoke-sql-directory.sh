#!/bin/sh

NATIVE_PROPERTIES_PATH=$1

sh $DBFLUTE_HOME/etc/cmd/_df-copy-properties.sh $NATIVE_PROPERTIES_PATH

$DBFLUTE_HOME/ant/bin/ant -f $DBFLUTE_HOME/build-torque.xml invoke-sql-directory

