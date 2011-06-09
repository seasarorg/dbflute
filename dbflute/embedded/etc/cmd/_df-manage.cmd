
setlocal
set NATIVE_PROPERTIES_PATH=%1
set FIRST_ARG=%2
if "%FIRST_ARG%"=="" set FIRST_ARG=""
set SECOND_ARG=%3
if "%SECOND_ARG%"=="" set SECOND_ARG=""

if "%FIRST_ARG%"=="renewal" (
  rem /nnnnnnnnnnnnnnnnnnnnnnnn
  rem Execute the Renewal task.
  rem nnnnnnnnnn/
  call %DBFLUTE_HOME%\etc\cmd\_df-renewal.cmd %NATIVE_PROPERTIES_PATH% %SECOND_ARG%
)

if "%FIRST_ARG%"=="regenerate" (
  rem /nnnnnnnnnnnnnnnnnnnnnnnnnnn
  rem Execute the Regenerate task.
  rem nnnnnnnnnn/
  call %DBFLUTE_HOME%\etc\cmd\_df-regenerate.cmd %NATIVE_PROPERTIES_PATH% %SECOND_ARG%
)

if "%FIRST_ARG%"=="refresh" (
  rem /nnnnnnnnnnnnnnnnnnnnnnnn
  rem Execute the Refresh task.
  rem nnnnnnnnnn/
  call %DBFLUTE_HOME%\etc\cmd\_df-refresh.cmd %NATIVE_PROPERTIES_PATH% %SECOND_ARG%
)
