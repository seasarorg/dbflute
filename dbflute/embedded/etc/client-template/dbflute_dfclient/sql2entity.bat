@echo off

setlocal
%~d0
cd %~p0
call _project.bat

rem /nnnnnnnnnnnnnnnnnnnnnnnnnnn
rem Execute the Sql2Entity task.
rem nnnnnnnnnn/
call %DBFLUTE_HOME%\etc\cmd\_df-sql2entity.cmd %MY_PROPERTIES_PATH% %1

if "%pause_at_end%"=="y" (
  pause
)
