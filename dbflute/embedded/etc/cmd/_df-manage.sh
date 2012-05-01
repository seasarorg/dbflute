#!/bin/bash

NATIVE_PROPERTIES_PATH=$1

FIRST_ARG=$2
SECOND_ARG=$3
taskReturnCode=0

if [ "$FIRST_ARG" = "renewal" ];then
  echo "/nnnnnnnnnnnnnnnnnnnnnnnnnn"
  echo "...Calling the Renewal task"
  echo "nnnnnnnnnn/"
  sh $DBFLUTE_HOME/etc/cmd/_df-renewal.sh $NATIVE_PROPERTIES_PATH
  taskReturnCode=$?
fi

if [ "$FIRST_ARG" = "regenerate" ];then
  echo "/nnnnnnnnnnnnnnnnnnnnnnnnnnnnn"
  echo "...Calling the Regenerate task"
  echo "nnnnnnnnnn/"
  sh $DBFLUTE_HOME/etc/cmd/_df-regenerate.sh $NATIVE_PROPERTIES_PATH
  taskReturnCode=$?
fi

if [ "$FIRST_ARG" = "refresh" ];then
  echo "/nnnnnnnnnnnnnnnnnnnnnnnnnn"
  echo "...Calling the Refresh task"
  echo "nnnnnnnnnn/"
  sh $DBFLUTE_HOME/etc/cmd/_df-refresh.sh $NATIVE_PROPERTIES_PATH $SECOND_ARG
  taskReturnCode=$?
fi

if [ "$FIRST_ARG" = "take-assert" ];then
  echo "/nnnnnnnnnnnnnnnnnnnnnnnnnnnnn"
  echo "...Calling the TakeAssert task"
  echo "nnnnnnnnnn/"
  sh $DBFLUTE_HOME/etc/cmd/_df-take-assert.sh $NATIVE_PROPERTIES_PATH $SECOND_ARG
  taskReturnCode=$?
fi

if [ "$FIRST_ARG" = "freegen" ];then
  echo "/nnnnnnnnnnnnnnnnnnnnnnnnnn"
  echo "...Calling the FreeGen task"
  echo "nnnnnnnnnn/"
  sh $DBFLUTE_HOME/etc/cmd/_df-freegen.sh $NATIVE_PROPERTIES_PATH $SECOND_ARG
  taskReturnCode=$?
fi

if [ $taskReturnCode -ne 0 ];then
  exit $taskReturnCode;
fi
