

set NATIVE_PROPERTIES_PATH=%1

call %DBFLUTE_HOME%\etc\cmd\_df-copy-properties.cmd %NATIVE_PROPERTIES_PATH%

IF "%answer%"=="" SET /P answer=Database will be initialized. Are you ready? (y or n)
IF "%answer%"=="y" call %DBFLUTE_HOME%\ant\bin\ant -listener net.sf.antcontrib.perf.AntPerformanceListener -f %DBFLUTE_HOME%\build-torque.xml replace-schema

