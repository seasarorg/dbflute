cd ..
ant -f build.xml reflect-to-all-regulars
export answer = y

cd ../dbflute-basic-example
. sync-lib.sh
cd dbflute_exampledb
rm ./log/*.log
. jdbc.sh
. doc.sh
. generate.sh
. sql2entity.sh

cd ../../dbflute-spring-example
. sync-lib.sh
cd dbflute_exampledb
rm ./log/*.log
. jdbc.sh
. doc.sh
. generate.sh
. sql2entity.sh

cd ../../dbflute-guice-example/dbflute_exampledb
rm ./log/*.log
. jdbc.sh
. doc.sh
. generate.sh
. sql2entity.sh

cd ../../dbflute-cdi-example/dbflute_exampledb
rm ./log/*.log
. jdbc.sh
. doc.sh
. generate.sh
. sql2entity.sh

cd ../../dbflute-lucy-example/dbflute_exampledb
rm ./log/*.log
. jdbc.sh
. doc.sh
. generate.sh
. sql2entity.sh

cd ../../dbflute-mysql-example/dbflute_exampledb
rm ./log/*.log
. jdbc.sh
. doc.sh
. generate.sh
. sql2entity.sh
. outside-sql-test.sh

cd ../../dbflute-postgresql-example/dbflute_exampledb
rm ./log/*.log
. jdbc.sh
. doc.sh
. generate.sh
. sql2entity.sh
. outside-sql-test.sh

cd ../../dbflute-oracle-example/dbflute_exampledb
rm ./log/*.log
# needs environment set up so kick by yourself (refresh only here)
#. jdbc.sh
#. doc.sh
#. generate.sh
#. sql2entity.sh
#. outside-sql-test.sh
. manage.sh refresh

cd ../../dbflute-db2-example/dbflute_exampledb
rm ./log/*.log
. jdbc.sh
. doc.sh
. generate.sh
. sql2entity.sh
. outside-sql-test.sh

# needs environment set up so kick by yourself (refresh only here)
#cd ../../dbflute-sqlserver-example/dbflute_exampledb
#rm ./log/*.log
#. jdbc.sh
#. doc.sh
#. generate.sh
#. sql2entity.sh
#. outside-sql-test.sh
. manage.sh refresh

# cannot do on Mac
#cd ../../dbflute-msaccess-example/dbflute_exampledb
#rm ./log/*.log
#. manage.sh refresh

cd ../../dbflute-multipledb-seasar-example/dbflute_librarydb
rm ./log/*.log
. jdbc.sh
. doc.sh
. generate.sh
. sql2entity.sh
cd ../dbflute_memberdb
rm ./log/*.log
. jdbc.sh
. doc.sh
. generate.sh
. sql2entity.sh

cd ../../dbflute-multipledb-spring-example/dbflute_librarydb
rm ./log/*.log
. jdbc.sh
. doc.sh
. generate.sh
. sql2entity.sh
cd ../dbflute_memberdb
rm ./log/*.log
. jdbc.sh
. doc.sh
. generate.sh
. sql2entity.sh

cd ../../dbflute-flexserver-example
. sync-lib.sh
cd dbflute_exampledb
rm ./log/*.log
. jdbc.sh
. doc.sh
. generate.sh
. sql2entity.sh

cd ../../dbflute-ymir-example
cd dbflute_exampledb
rm ./log/*.log
. jdbc.sh
. doc.sh
. generate.sh
. sql2entity.sh

cd ../../dbflute-sastruts-example
cd dbflute_exampledb
rm ./log/*.log
. jdbc.sh
. doc.sh
. generate.sh
. sql2entity.sh
. manage.sh freegen

cd ../../dbflute-sqlite-example/dbflute_exampledb
rm ./log/*.log
. jdbc.sh
. doc.sh
. generate.sh
. sql2entity.sh

cd ../../dbflute-mysql-example/dbflute_exampledb
. bhvap-doc.sh
. bhvap-generate.sh
. bhvap-sql2entity.sh
. bhvap-outside-sql-test.sh

cd ../../dbflute-postgresql-example/dbflute_exampledb
. bhvap-doc.sh
. bhvap-generate.sh
. bhvap-sql2entity.sh
. bhvap-outside-sql-test.sh

cd ../../dbflute-sqlite-example/dbflute_exampledb
. bhvap-doc.sh
. bhvap-generate.sh
. bhvap-sql2entity.sh
. bhvap-outside-sql-test.sh

cd ..
cd ../dbflute-basic-example/
ant

cd ../dbflute-spring-example/
ant
