#!/bin/bash

cd `dirname $0`
. _project.sh

echo "/nnnnnnnnnnnnnnnnnnnnnnnnnnnnnn"
echo "Execute the ReplaceSchema task."
echo "nnnnnnnnnn/"
sh $DBFLUTE_HOME/etc/cmd/_df-replace-schema.sh $MY_PROPERTIES_PATH
taskReturnCode=$?

if [ $taskReturnCode -ne 0 ];then
  exit $taskReturnCode;
fi
