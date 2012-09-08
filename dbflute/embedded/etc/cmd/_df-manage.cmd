
setlocal
set NATIVE_PROPERTIES_PATH=%1
set FIRST_ARG=%2
set SECOND_ARG=%3
set PURE_FIRST_ARG=%2

:: The code for compatibility
if "%FIRST_ARG%"=="""" (
  set FIRST_ARG=
)
:: The code for compatibility
if "%SECOND_ARG%"=="""" (
  set SECOND_ARG=
)
:: The code for compatibility
if "%PURE_FIRST_ARG%"=="""" (
  set PURE_FIRST_ARG=
)

if "%FIRST_ARG%"=="" (
  echo:
  echo  1 : renewal (ReplaceSchema, JDBC, Doc, Generate, OutsideSqlTest, Sql2Entity^)
  echo  2 : regenerate (JDBC, Doc, Generate, Sql2Entity^)
  echo:
  echo  4 : load-data-reverse
  echo  5 : schema-sync-check
  echo:
  echo  7 : save-previous
  echo  8 : alter-check
  echo  9 : take-assert
  echo:
  echo  11 : refresh
  echo  12 : freegen
  echo:

  echo (input on your console^)
  echo What is your favorite task? (number^):

  set /p FIRST_ARG=
)

if "%FIRST_ARG%"=="1" (
  set FIRST_ARG=renewal
) else if "%FIRST_ARG%"=="2" (
  set FIRST_ARG=regenerate
) else if "%FIRST_ARG%"=="4" (
  set FIRST_ARG=load-data-reverse
) else if "%FIRST_ARG%"=="5" (
  set FIRST_ARG=schema-sync-check
) else if "%FIRST_ARG%"=="7" (
  set FIRST_ARG=save-previous
) else if "%FIRST_ARG%"=="8" (
  set FIRST_ARG=alter-check
) else if "%FIRST_ARG%"=="9" (
  set FIRST_ARG=take-assert
) else if "%FIRST_ARG%"=="11" (
  set FIRST_ARG=refresh
) else if "%FIRST_ARG%"=="12" (
  set FIRST_ARG=freegen
)

if "%FIRST_ARG%"=="renewal" (
  echo /nnnnnnnnnnnnnnnnnnnnnnnnnn
  echo ...Calling the Renewal task
  echo nnnnnnnnnn/
  call %DBFLUTE_HOME%\etc\cmd\_df-renewal.cmd %NATIVE_PROPERTIES_PATH% %SECOND_ARG%

) else if "%FIRST_ARG%"=="regenerate" (
  echo /nnnnnnnnnnnnnnnnnnnnnnnnnnnnn
  echo ...Calling the Regenerate task
  echo nnnnnnnnnn/
  call %DBFLUTE_HOME%\etc\cmd\_df-regenerate.cmd %NATIVE_PROPERTIES_PATH% %SECOND_ARG%

) else if "%FIRST_ARG%"=="refresh" (

  if "%PURE_FIRST_ARG%"=="" echo (input on your console^)
  if "%PURE_FIRST_ARG%"=="" echo What is refresh project? (name^):
  if "%PURE_FIRST_ARG%"=="" set /p SECOND_ARG=

  echo /nnnnnnnnnnnnnnnnnnnnnnnnnn
  echo ...Calling the Refresh task
  echo nnnnnnnnnn/
  setlocal enabledelayedexpansion
  call %DBFLUTE_HOME%\etc\cmd\_df-refresh.cmd %NATIVE_PROPERTIES_PATH% !SECOND_ARG!
  endlocal
) else if "%FIRST_ARG%"=="take-assert" (
  echo /nnnnnnnnnnnnnnnnnnnnnnnnnnnnn
  echo ...Calling the TakeAssert task
  echo nnnnnnnnnn/
  call %DBFLUTE_HOME%\etc\cmd\_df-take-assert.cmd %NATIVE_PROPERTIES_PATH% %SECOND_ARG%

) else if "%FIRST_ARG%"=="freegen" (
  echo /nnnnnnnnnnnnnnnnnnnnnnnnnn
  echo ...Calling the FreeGen task
  echo nnnnnnnnnn/
  call %DBFLUTE_HOME%\etc\cmd\_df-freegen.cmd %NATIVE_PROPERTIES_PATH% %SECOND_ARG%

) else if "%FIRST_ARG%"=="load-data-reverse" (
  echo /nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn
  echo ...Calling the LoadDataReverse task
  echo nnnnnnnnnn/
  call %DBFLUTE_HOME%\etc\cmd\_df-doc.cmd %NATIVE_PROPERTIES_PATH% load-data-reverse %SECOND_ARG%

) else if "%FIRST_ARG%"=="schema-sync-check" (
  echo /nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn
  echo ...Calling the SchemaSyncCheck task
  echo nnnnnnnnnn/
  call %DBFLUTE_HOME%\etc\cmd\_df-doc.cmd %NATIVE_PROPERTIES_PATH% schema-sync-check %SECOND_ARG%

) else if "%FIRST_ARG%"=="alter-check" (
  echo /nnnnnnnnnnnnnnnnnnnnnnnnnnnnn
  echo ...Calling the AlterCheck task
  echo nnnnnnnnnn/
  call %DBFLUTE_HOME%\etc\cmd\_df-replace-schema.cmd %NATIVE_PROPERTIES_PATH% alter-check %SECOND_ARG%

) else if "%FIRST_ARG%"=="save-previous" (
  echo /nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn
  echo ...Calling the SavePrevious task
  echo nnnnnnnnnn/
  call %DBFLUTE_HOME%\etc\cmd\_df-replace-schema.cmd %NATIVE_PROPERTIES_PATH% save-previous %SECOND_ARG%

)
