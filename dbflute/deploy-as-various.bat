cd %~p0

call ant -f build.xml dist
call ant -f build.xml reflectvarious

pause