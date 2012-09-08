#!/bin/bash

NATIVE_PROPERTIES_PATH=$1

FIRST_ARG=$2
SECOND_ARG=$3
taskReturnCode=0

if [ "$FIRST_ARG" = "" ];then
  echo ""
  echo " 1 : renewal (ReplaceSchema, JDBC, Doc, Generate, OutsideSqlTest, Sql2Entity)"
  echo " 2 : regenerate (JDBC, Doc, Generate, Sql2Entity)"
  echo ""
  echo " 4 : load-data-reverse"
  echo " 5 : schema-sync-check"
  echo ""
  echo " 7 : save-previous"
  echo " 8 : alter-check"
  echo " 9 : take-assert"
  echo ""
  echo " 11 : refresh"
  echo " 12 : freegen"
  echo ""

  echo \(input on your console\)
  echo What is your favorite task? \(number\):

  read FIRST_ARG
fi

if [ "$FIRST_ARG" = "1" ];then
  FIRST_ARG=renewal
elif [ "$FIRST_ARG" = "2" ];then
  FIRST_ARG=regenerate
elif [ "$FIRST_ARG" = "4" ];then
  FIRST_ARG=load-data-reverse
elif [ "$FIRST_ARG" = "5" ];then
  FIRST_ARG=schema-sync-check
elif [ "$FIRST_ARG" = "7" ];then
  FIRST_ARG=save-previous
elif [ "$FIRST_ARG" = "8" ];then
  FIRST_ARG=alter-check
elif [ "$FIRST_ARG" = "9" ];then
  FIRST_ARG=take-assert
elif [ "$FIRST_ARG" = "11" ];then
  FIRST_ARG=refresh
elif [ "$FIRST_ARG" = "12" ];then
  FIRST_ARG=freegen
fi

if [ "$FIRST_ARG" = "renewal" ];then
  echo "/nnnnnnnnnnnnnnnnnnnnnnnnnn"
  echo "...Calling the Renewal task"
  echo "nnnnnnnnnn/"
  sh $DBFLUTE_HOME/etc/cmd/_df-renewal.sh $NATIVE_PROPERTIES_PATH
  taskReturnCode=$?

elif [ "$FIRST_ARG" = "regenerate" ];then
  echo "/nnnnnnnnnnnnnnnnnnnnnnnnnnnnn"
  echo "...Calling the Regenerate task"
  echo "nnnnnnnnnn/"
  sh $DBFLUTE_HOME/etc/cmd/_df-regenerate.sh $NATIVE_PROPERTIES_PATH
  taskReturnCode=$?

elif [ "$FIRST_ARG" = "refresh" ];then

  if [ "$2" = "" ];then
    echo \(input on your console\)
    echo What is refresh project? \(name\):
    read SECOND_ARG
  fi

  echo "/nnnnnnnnnnnnnnnnnnnnnnnnnn"
  echo "...Calling the Refresh task"
  echo "nnnnnnnnnn/"
  sh $DBFLUTE_HOME/etc/cmd/_df-refresh.sh $NATIVE_PROPERTIES_PATH $SECOND_ARG
  taskReturnCode=$?

elif [ "$FIRST_ARG" = "take-assert" ];then
  echo "/nnnnnnnnnnnnnnnnnnnnnnnnnnnnn"
  echo "...Calling the TakeAssert task"
  echo "nnnnnnnnnn/"
  sh $DBFLUTE_HOME/etc/cmd/_df-take-assert.sh $NATIVE_PROPERTIES_PATH $SECOND_ARG
  taskReturnCode=$?

elif [ "$FIRST_ARG" = "freegen" ];then
  echo "/nnnnnnnnnnnnnnnnnnnnnnnnnn"
  echo "...Calling the FreeGen task"
  echo "nnnnnnnnnn/"
  sh $DBFLUTE_HOME/etc/cmd/_df-freegen.sh $NATIVE_PROPERTIES_PATH $SECOND_ARG
  taskReturnCode=$?

elif [ "$FIRST_ARG" = "load-data-reverse" ];then
  echo "/nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn"
  echo "...Calling the LoadDataReverse task"
  echo "nnnnnnnnnn/"
  sh $DBFLUTE_HOME/etc/cmd/_df-doc.sh $NATIVE_PROPERTIES_PATH load-data-reverse $SECOND_ARG
  taskReturnCode=$?

elif [ "$FIRST_ARG" = "schema-sync-check" ];then
  echo "/nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn"
  echo "...Calling the SchemaSyncCheck task"
  echo "nnnnnnnnnn/"
  sh $DBFLUTE_HOME/etc/cmd/_df-doc.sh $NATIVE_PROPERTIES_PATH schema-sync-check $SECOND_ARG
  taskReturnCode=$?

elif [ "$FIRST_ARG" = "alter-check" ];then
  echo "/nnnnnnnnnnnnnnnnnnnnnnnnnnnnn"
  echo "...Calling the AlterCheck task"
  echo "nnnnnnnnnn/"
  sh $DBFLUTE_HOME/etc/cmd/_df-replace-schema.sh $NATIVE_PROPERTIES_PATH alter-check $SECOND_ARG
  taskReturnCode=$?

elif [ "$FIRST_ARG" = "save-previous" ];then
  echo "/nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn"
  echo "...Calling the SavePrevious task"
  echo "nnnnnnnnnn/"
  sh $DBFLUTE_HOME/etc/cmd/_df-replace-schema.sh $NATIVE_PROPERTIES_PATH save-previous $SECOND_ARG
  taskReturnCode=$?
fi

if [ $taskReturnCode -ne 0 ];then
  exit $taskReturnCode;
fi
