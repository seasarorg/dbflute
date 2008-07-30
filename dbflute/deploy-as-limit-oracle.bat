cd %~p0

call .\ant\bin\ant -f build.xml dist
call .\ant\bin\ant -f build.xml reflect-win-limit-oracle

pause