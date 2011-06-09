
setlocal
set NATIVE_PROPERTIES_PATH=%1
set FIRST_ARG=%2
if "%FIRST_ARG%"=="" set FIRST_ARG=""
set SECOND_ARG=%3
if "%SECOND_ARG%"=="" set SECOND_ARG=""

if "%FIRST_ARG%"=="DBRenewal" (
  rem /nnnnnnnnnnnnnnnnnnnnnnnnnn
  rem Execute the DBRenewal task.
  rem nnnnnnnnnn/
  call %DBFLUTE_HOME%\etc\cmd\_df-db-renewal.cmd %NATIVE_PROPERTIES_PATH% %SECOND_ARG%
)

if "%FIRST_ARG%"=="ReGenerate" (
  rem /nnnnnnnnnnnnnnnnnnnnnnnnnn
  rem Execute the ReGenerate task.
  rem nnnnnnnnnn/
  call %DBFLUTE_HOME%\etc\cmd\_df-re-generate.cmd %NATIVE_PROPERTIES_PATH% %SECOND_ARG%
)

if "%FIRST_ARG%"=="RefreshResource" (
  rem /nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn
  rem Execute the RefreshResource task.
  rem nnnnnnnnnn/
  call %DBFLUTE_HOME%\etc\cmd\_df-refresh-resource.cmd %NATIVE_PROPERTIES_PATH% %SECOND_ARG%
)
