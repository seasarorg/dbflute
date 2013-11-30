#!/bin/bash

if [ -e ./extlib/*.jar ]; then
  cp -Rf ./extlib $DBFLUTE_HOME/lib/extlib
fi
