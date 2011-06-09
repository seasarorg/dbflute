#!/bin/bash

NATIVE_PROPERTIES_PATH=$1

FIRST_ARG=$2
SECOND_ARG=$3
taskReturnCode=0

if [ "$FIRST_ARG" = "DBRenewal" ];then
  echo "/nnnnnnnnnnnnnnnnnnnnnnnnnn"
  echo "Execute the DBRenewal task."
  echo "nnnnnnnnnn/"
  sh $DBFLUTE_HOME/etc/cmd/_df-db-renewal.sh $NATIVE_PROPERTIES_PATH
  taskReturnCode=$?
fi

if [ "$FIRST_ARG" = "ReGenerate" ];then
  echo "/nnnnnnnnnnnnnnnnnnnnnnnnnn"
  echo "Execute the ReGenerate task."
  echo "nnnnnnnnnn/"
  sh $DBFLUTE_HOME/etc/cmd/_df-re-generate.sh $NATIVE_PROPERTIES_PATH
  taskReturnCode=$?
fi

if [ "$FIRST_ARG" = "RefreshResource" ];then
  echo "/nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn"
  echo "Execute the RefreshResource task."
  echo "nnnnnnnnnn/"
  sh $DBFLUTE_HOME/etc/cmd/_df-refresh-resource.sh $NATIVE_PROPERTIES_PATH $SECOND_ARG
  taskReturnCode=$?
fi

if [ $taskReturnCode -ne 0 ];then
  exit $taskReturnCode;
fi
