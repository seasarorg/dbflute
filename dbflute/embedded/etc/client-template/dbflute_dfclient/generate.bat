@echo off

setlocal
%~d0
cd %~p0
call _project.bat

rem /nnnnnnnnnnnnnnnnnnnnnnnnn
rem Execute the Generate task.
rem nnnnnnnnnn/
call %DBFLUTE_HOME%\etc\cmd\_df-generate.cmd %MY_PROPERTIES_PATH%

if "%pause_at_end%"=="y" (
  pause
)
