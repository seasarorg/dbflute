@echo off

setlocal
%~d0
cd %~p0
call _project.bat

set FIRST_ARG=%1
set SECOND_ARG=%2

if "%FIRST_ARG%"=="DBRenewal" (
  rem /nnnnnnnnnnnnnnnnnnnnnnnnnn
  rem Execute the DBRenewal task.
  rem nnnnnnnnnn/
  call %DBFLUTE_HOME%\etc\cmd\_df-db-renewal.cmd %MY_PROPERTIES_PATH%
)

if "%FIRST_ARG%"=="RefreshResource" (
  rem /nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn
  rem Execute the RefreshResource task.
  rem nnnnnnnnnn/
  call %DBFLUTE_HOME%\etc\cmd\_df-refresh-resource.cmd %MY_PROPERTIES_PATH% %SECOND_ARG%
)

if "%pause_at_end%"=="y" (
  pause
)
