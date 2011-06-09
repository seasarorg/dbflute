#!/bin/bash

cd `dirname $0`
. _project.sh

FIRST_ARG=$1
SECOND_ARG=$2
taskReturnCode=0

if [ "$FIRST_ARG" = "DBRenewal" ];then
  echo "/nnnnnnnnnnnnnnnnnnnnnnnnnn"
  echo "Execute the DBRenewal task."
  echo "nnnnnnnnnn/"
  sh $DBFLUTE_HOME/etc/cmd/_df-db-renewal.sh $MY_PROPERTIES_PATH
  taskReturnCode=$?
fi

if [ "$FIRST_ARG" = "RefreshResource" ];then
  echo "/nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn"
  echo "Execute the RefreshResource task."
  echo "nnnnnnnnnn/"
  sh $DBFLUTE_HOME/etc/cmd/_df-refresh-resource.sh $MY_PROPERTIES_PATH $SECOND_ARG
  taskReturnCode=$?
fi

if [ $taskReturnCode -ne 0 ];then
  exit $taskReturnCode;
fi
