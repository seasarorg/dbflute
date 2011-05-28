#!/bin/bash

ANT_HOME=$DBFLUTE_HOME/ant
NATIVE_PROPERTIES_PATH=$1

sh $DBFLUTE_HOME/etc/cmd/_df-copy-properties.sh $NATIVE_PROPERTIES_PATH

if [ "$answer" = "y" ] ;then
  export answer=y
else
  # {thank you Mr.Akikusa}
  read -p "Database will be initialized. Are you ready? (y or n) " answer
fi
antReturnCode=0
if [ $answer = "y" ] ;then
  sh $DBFLUTE_HOME/etc/cmd/_df-copy-extlib.sh

  sh $DBFLUTE_HOME/ant/bin/ant -Ddfenv=$DBFLUTE_ENVIRONMENT_TYPE -f $DBFLUTE_HOME/build-torque.xml replace-schema
  antReturnCode=$?

  sh $DBFLUTE_HOME/etc/cmd/_df-delete-extlib.sh
fi

if [ $antReturnCode -ne 0 ];then
  exit $antReturnCode;
fi
