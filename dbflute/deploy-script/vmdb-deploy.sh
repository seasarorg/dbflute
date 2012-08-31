cd ..
ant -f build.xml reflect-to-oracle

cd ../dbflute-oracle-example/dbflute_exampledb
rm ./log/*.log
. jdbc.sh
. doc.sh
. generate.sh
. sql2entity.sh
. outside-sql-test.sh
