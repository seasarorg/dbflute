@echo off

call _project.bat

rem /nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn
rem Specify the file path to be used as build-properties.
rem nnnnnnnnnn/
set MY_PROPERTIES_PATH=build-%MY_PROJECT_NAME%.properties

rem /nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn
rem Execute {Invoke Sql Directory}.
rem nnnnnnnnnn/
call %DBFLUTE_HOME%\etc\cmd\_df-invoke-sql-directory.cmd %MY_PROPERTIES_PATH%

pause


