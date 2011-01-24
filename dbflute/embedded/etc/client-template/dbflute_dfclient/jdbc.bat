@echo off

setlocal
%~d0
cd %~p0
call _project.bat

rem /nnnnnnnnnnnnnnnnnnnnn
rem Execute the JDBC task.
rem nnnnnnnnnn/
call %DBFLUTE_HOME%\etc\cmd\_df-jdbc.cmd %MY_PROPERTIES_PATH%

if "%pause_at_end%"=="y" (
  pause
)
