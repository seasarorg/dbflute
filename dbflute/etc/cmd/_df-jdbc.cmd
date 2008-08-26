

set NATIVE_PROPERTIES_PATH=%1
IF "%DBFLUTE_ENVIRONMENT_TYPE%"=="" set DBFLUTE_ENVIRONMENT_TYPE=""

call %DBFLUTE_HOME%\etc\cmd\_df-copy-properties.cmd %NATIVE_PROPERTIES_PATH%

call %DBFLUTE_HOME%\ant\bin\ant -Ddfenv=%DBFLUTE_ENVIRONMENT_TYPE% -f %DBFLUTE_HOME%\build-torque.xml jdbc

copy .\schema\project-schema-%MY_PROJECT_NAME%.xml %DBFLUTE_HOME%\schema\project-schema-%MY_PROJECT_NAME%.xml
