

set NATIVE_PROPERTIES_PATH=%1

call %DBFLUTE_HOME%\etc\cmd\_df-copy-properties.cmd %NATIVE_PROPERTIES_PATH%

call ant -f %DBFLUTE_HOME%\build-torque.xml jdbc

copy .\schema\project-schema-%MY_PROJECT_NAME%.xml %DBFLUTE_HOME%\schema\project-schema-%MY_PROJECT_NAME%.xml
