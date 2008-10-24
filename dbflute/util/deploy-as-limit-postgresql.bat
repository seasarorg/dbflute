cd %~p0
cd ..

rem call .\ant\bin\ant -f build.xml dist
call .\ant\bin\ant -f build.xml reflect-win-limit-postgresql

pause