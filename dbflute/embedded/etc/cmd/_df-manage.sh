#!/bin/bash

NATIVE_PROPERTIES_PATH=$1

FIRST_ARG=$2
SECOND_ARG=$3
taskReturnCode=0

if [ "$FIRST_ARG" = "renewal" ];then
  echo "/nnnnnnnnnnnnnnnnnnnnnnnn"
  echo "Execute the Renewal task."
  echo "nnnnnnnnnn/"
  sh $DBFLUTE_HOME/etc/cmd/_df-renewal.sh $NATIVE_PROPERTIES_PATH
  taskReturnCode=$?
fi

if [ "$FIRST_ARG" = "regenerate" ];then
  echo "/nnnnnnnnnnnnnnnnnnnnnnnnnnn"
  echo "Execute the Regenerate task."
  echo "nnnnnnnnnn/"
  sh $DBFLUTE_HOME/etc/cmd/_df-regenerate.sh $NATIVE_PROPERTIES_PATH
  taskReturnCode=$?
fi

if [ "$FIRST_ARG" = "refresh" ];then
  echo "/nnnnnnnnnnnnnnnnnnnnnnnn"
  echo "Execute the Refresh task."
  echo "nnnnnnnnnn/"
  sh $DBFLUTE_HOME/etc/cmd/_df-refresh.sh $NATIVE_PROPERTIES_PATH $SECOND_ARG
  taskReturnCode=$?
fi

if [ $taskReturnCode -ne 0 ];then
  exit $taskReturnCode;
fi
