

set NATIVE_PROPERTIES_PATH=%1

call %S2DAOGEN_HOME%\etc\cmd\_copy-properties.cmd %NATIVE_PROPERTIES_PATH%

call ant -f %S2DAOGEN_HOME%\build-torque.xml sql2entity

