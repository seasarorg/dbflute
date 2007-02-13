#!/bin/sh

NATIVE_PROPERTIES_PATH=$1

sh $DBFLUTE_HOME/etc/cmd/_df-copy-properties.sh $NATIVE_PROPERTIES_PATH

cp ./playsql/playsql-${MY_PROJECT_NAME}.sql $DBFLUTE_HOME/playsql/playsql-${MY_PROJECT_NAME}.sql

ant -f $DBFLUTE_HOME/build-torque.xml playsql

