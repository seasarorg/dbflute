
set NATIVE_PROPERTIES_PATH=%1
IF "%DBFLUTE_ENVIRONMENT_TYPE%"=="" set DBFLUTE_ENVIRONMENT_TYPE=""

call %DBFLUTE_HOME%\etc\cmd\_df-copy-properties.cmd %NATIVE_PROPERTIES_PATH%

IF "%answer%"=="" SET /P answer=Database will be initialized. Are you ready? (y or n) 
IF "%answer%"=="y" (
  call %DBFLUTE_HOME%\etc\cmd\_df-copy-extlib.cmd

  call %DBFLUTE_HOME%\ant\bin\ant -Ddfenv=%DBFLUTE_ENVIRONMENT_TYPE% -f %DBFLUTE_HOME%\build-torque.xml replace-schema

  call %DBFLUTE_HOME%\etc\cmd\_df-delete-extlib.cmd
)
