cd %~p0
cd ..

call .\embedded\ant\bin\ant -f build.xml reflect-to-oracle
call .\embedded\ant\bin\ant -f build.xml reflect-to-db2
call .\embedded\ant\bin\ant -f build.xml reflect-to-sqlserver
call .\embedded\ant\bin\ant -f build.xml reflect-to-msaccess
call .\embedded\ant\bin\ant -f build.xml reflect-to-buri
call .\embedded\ant\bin\ant -f build.xml reflect-to-doma
call .\embedded\ant\bin\ant -f buildnet.xml reflect-to-basic
call .\embedded\ant\bin\ant -f buildnet.xml reflect-to-multipledb-quill
call .\embedded\ant\bin\ant -f buildnet.xml reflect-to-asp.net

pause