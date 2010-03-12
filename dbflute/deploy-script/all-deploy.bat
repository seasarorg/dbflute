cd %~p0
cd ..

call .\embedded\ant\bin\ant -f build.xml reflect-to-all-for-win
call .\embedded\ant\bin\ant -f buildnet.xml reflect-to-basic
call .\embedded\ant\bin\ant -f buildnet.xml reflect-to-multipledb-quill
call .\embedded\ant\bin\ant -f buildnet.xml reflect-to-asp.net

pause