

set NATIVE_PROPERTIES_PATH=%1

call %S2DAOGEN_HOME%\etc\cmd\_copy-properties.cmd %NATIVE_PROPERTIES_PATH%

copy .\playsql\playsql-%MY_PROJECT_NAME%.sql %S2DAOGEN_HOME%\playsql\playsql-%MY_PROJECT_NAME%.sql

call ant -f %S2DAOGEN_HOME%\build-torque.xml playsql

