

set NATIVE_PROPERTIES_PATH=%1

call %DBFLUTE_HOME%\etc\cmd\_df-copy-properties.cmd %NATIVE_PROPERTIES_PATH%

IF "%answer%"=="" SET /P answer=Database will be initialized. Are you ready?Åiy/nÅj
IF "%answer%"=="y" call ant -f %DBFLUTE_HOME%\build-torque.xml replace-schema

