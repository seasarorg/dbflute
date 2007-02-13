#!/bin/sh

. _project.sh

echo "/nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn"
echo "Specify the file path to be used as build-properties."
echo "nnnnnnnnnn/"
export MY_PROPERTIES_PATH=build-${MY_PROJECT_NAME}.properties

echo "/nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn"
echo "Execute {Invoke Sql Directory}."
echo "nnnnnnnnnn/"
sh $DBFLUTE_HOME/etc/cmd/_df-sql2entity.sh $MY_PROPERTIES_PATH


