cd %~p0
cd ..

call .\ant\bin\ant -f buildnet.xml reflect-win-nbasic

pause