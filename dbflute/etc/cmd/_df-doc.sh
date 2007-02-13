#!/bin/sh

NATIVE_PROPERTIES_PATH=$1

sh $DBFLUTE_HOME/etc/cmd/_df-copy-properties.sh $NATIVE_PROPERTIES_PATH

ant -f $DBFLUTE_HOME/build-torque.xml doc

mkdir ./output/doc
cp $DBFLUTE_HOME/output/doc/project-schema-${MY_PROJECT_NAME}.html ./output/doc/project-schema-${MY_PROJECT_NAME}.html
