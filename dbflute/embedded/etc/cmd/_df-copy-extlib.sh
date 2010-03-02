#!/bin/sh

if [ -d ./extlib ]; then
  cp -Rf ./extlib $DBFLUTE_HOME/lib/extlib
fi
