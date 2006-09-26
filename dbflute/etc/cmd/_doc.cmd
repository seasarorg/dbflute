

set NATIVE_PROPERTIES_PATH=%1

call %S2DAOGEN_HOME%\etc\cmd\_copy-properties.cmd %NATIVE_PROPERTIES_PATH%

call ant -f %S2DAOGEN_HOME%\build-torque.xml doc

mkdir .\output\doc
copy %S2DAOGEN_HOME%\output\doc\project-schema-%MY_PROJECT_NAME%.html .\output\doc\project-schema-%MY_PROJECT_NAME%.html
