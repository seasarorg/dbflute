

set NATIVE_PROPERTIES_PATH=%1

call %DBFLUTE_HOME%\etc\cmd\_df-copy-properties.cmd %NATIVE_PROPERTIES_PATH%

copy .\playsql\playsql-%MY_PROJECT_NAME%.sql %DBFLUTE_HOME%\playsql\playsql-%MY_PROJECT_NAME%.sql

call ant -f %DBFLUTE_HOME%\build-torque.xml playsql

