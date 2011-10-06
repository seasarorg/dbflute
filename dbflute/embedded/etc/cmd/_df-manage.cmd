
setlocal
set NATIVE_PROPERTIES_PATH=%1
set FIRST_ARG=%2
if "%FIRST_ARG%"=="" set FIRST_ARG=""
set SECOND_ARG=%3
if "%SECOND_ARG%"=="" set SECOND_ARG=""

if "%FIRST_ARG%"=="renewal" (
  echo /nnnnnnnnnnnnnnnnnnnnnnnnnn
  echo ...Calling the Renewal task
  echo nnnnnnnnnn/
  call %DBFLUTE_HOME%\etc\cmd\_df-renewal.cmd %NATIVE_PROPERTIES_PATH% %SECOND_ARG%
)

if "%FIRST_ARG%"=="regenerate" (
  echo /nnnnnnnnnnnnnnnnnnnnnnnnnnnnn
  echo ...Calling the Regenerate task
  echo nnnnnnnnnn/
  call %DBFLUTE_HOME%\etc\cmd\_df-regenerate.cmd %NATIVE_PROPERTIES_PATH% %SECOND_ARG%
)

if "%FIRST_ARG%"=="refresh" (
  echo /nnnnnnnnnnnnnnnnnnnnnnnnnn
  echo ...Calling the Refresh task
  echo nnnnnnnnnn/
  call %DBFLUTE_HOME%\etc\cmd\_df-refresh.cmd %NATIVE_PROPERTIES_PATH% %SECOND_ARG%
)

if "%FIRST_ARG%"=="take-assert" (
  echo /nnnnnnnnnnnnnnnnnnnnnnnnnnnnn
  echo ...Calling the TakeAssert task
  echo nnnnnnnnnn/
  call %DBFLUTE_HOME%\etc\cmd\_df-take-assert.cmd %NATIVE_PROPERTIES_PATH% %SECOND_ARG%
)
