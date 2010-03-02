cd %~p0
cd ..

call .\ant\bin\ant -f build.xml reflect-win-db2

pause