cd %~p0

call mvn generate-sources

call ant -f build.xml reflect



cd ..\dfjava\dbflute_ldb

@echo off

call _project.bat

rem /nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn
rem Specify the file path to be used as build-properties.
rem nnnnnnnnnn/
set MY_PROPERTIES_PATH=build-%MY_PROJECT_NAME%.properties

rem /nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn
rem Execute {JDBC}.
rem nnnnnnnnnn/
call %DBFLUTE_HOME%\etc\cmd\_df-jdbc.cmd %MY_PROPERTIES_PATH%

rem /nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn
rem Execute {Document}.
rem nnnnnnnnnn/
call %DBFLUTE_HOME%\etc\cmd\_df-doc.cmd %MY_PROPERTIES_PATH%

rem /nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn
rem Execute {Generate}.
rem nnnnnnnnnn/
call %DBFLUTE_HOME%\etc\cmd\_df-generate.cmd %MY_PROPERTIES_PATH%


cd ..\..\dfncsharp\dbflute_nldb

@echo off

call _project.bat

rem /nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn
rem Specify the file path to be used as build-properties.
rem nnnnnnnnnn/
set MY_PROPERTIES_PATH=build-%MY_PROJECT_NAME%.properties

rem /nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn
rem Execute {JDBC}.
rem nnnnnnnnnn/
call %DBFLUTE_HOME%\etc\cmd\_df-jdbc.cmd %MY_PROPERTIES_PATH%

rem /nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn
rem Execute {Document}.
rem nnnnnnnnnn/
call %DBFLUTE_HOME%\etc\cmd\_df-doc.cmd %MY_PROPERTIES_PATH%

rem /nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn
rem Execute {Generate}.
rem nnnnnnnnnn/
call %DBFLUTE_HOME%\etc\cmd\_df-generate.cmd %MY_PROPERTIES_PATH%




pause