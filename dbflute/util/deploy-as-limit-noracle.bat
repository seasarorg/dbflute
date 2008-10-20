cd %~p0
cd ..

call .\ant\bin\ant -f build.xml dist
call .\ant\bin\ant -f build.xml reflect-win-limit-noracle

pause