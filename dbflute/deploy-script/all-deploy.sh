cd ..
ant -f build.xml reflect-to-all-regulars

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

cd ../../dbflute-lucy-example/dbflute_exampledb
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

cd ../../dbflute-db2-example/dbflute_exampledb
rm ./log/*.log
. jdbc.sh
. doc.sh
. generate.sh
. sql2entity.sh
. outside-sql-test.sh

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
. sync-lib.sh
cd dbflute_exampledb
rm ./log/*.log
. jdbc.sh
. doc.sh
. generate.sh
. sql2entity.sh

cd ../../dbflute-sastruts-example
. sync-lib.sh
cd dbflute_exampledb
rm ./log/*.log
. jdbc.sh
. doc.sh
. generate.sh
. sql2entity.sh

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

cd ../../dbflute-tricky-example/dbflute_exampledb
. bhvap-doc.sh
. bhvap-generate.sh
. bhvap-sql2entity.sh
. bhvap-outside-sql-test.sh

cd ../../dbflute-basic-example/
ant

cd ../../dbflute-spring-example/
ant
