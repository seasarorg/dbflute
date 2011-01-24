@echo off

setlocal
%~d0
cd %~p0
call _project.bat

rem /nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn
rem Execute the OutsiteSqlTest task.
rem nnnnnnnnnn/
call %DBFLUTE_HOME%\etc\cmd\_df-outside-sql-test.cmd %MY_PROPERTIES_PATH% %1

if "%pause_at_end%"=="y" (
  pause
)
