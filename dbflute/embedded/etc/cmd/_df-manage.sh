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

if [ "$FIRST_ARG" = "load-data-reverse" ];then
  echo "/nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn"
  echo "...Calling the LoadDataReverse task"
  echo "nnnnnnnnnn/"
  sh $DBFLUTE_HOME/etc/cmd/_df-doc.sh $NATIVE_PROPERTIES_PATH load-data-reverse $SECOND_ARG
  taskReturnCode=$?
fi

if [ "$FIRST_ARG" = "schema-sync-check" ];then
  echo "/nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn"
  echo "...Calling the SchemaSyncCheck task"
  echo "nnnnnnnnnn/"
  sh $DBFLUTE_HOME/etc/cmd/_df-doc.sh $NATIVE_PROPERTIES_PATH schema-sync-check $SECOND_ARG
  taskReturnCode=$?
fi

if [ "$FIRST_ARG" = "alter-check" ];then
  echo "/nnnnnnnnnnnnnnnnnnnnnnnnnnnnn"
  echo "...Calling the AlterCheck task"
  echo "nnnnnnnnnn/"
  sh $DBFLUTE_HOME/etc/cmd/_df-replace-schema.sh $NATIVE_PROPERTIES_PATH alter-check $SECOND_ARG
  taskReturnCode=$?
fi

if [ "$FIRST_ARG" = "save-previous" ];then
  echo "/nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn"
  echo "...Calling the SavePrevious task"
  echo "nnnnnnnnnn/"
  sh $DBFLUTE_HOME/etc/cmd/_df-replace-schema.sh $NATIVE_PROPERTIES_PATH save-previous $SECOND_ARG
  taskReturnCode=$?
fi

if [ $taskReturnCode -ne 0 ];then
  exit $taskReturnCode;
fi
