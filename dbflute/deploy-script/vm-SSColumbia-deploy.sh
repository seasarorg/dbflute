cd ..
ant -f build.xml reflect-to-sqlserver

cd ../dbflute-sqlserver-example/dbflute_exampledb
rm ./log/*.log
. jdbc.sh
. doc.sh
. generate.sh
. sql2entity.sh
. outside-sql-test.sh
