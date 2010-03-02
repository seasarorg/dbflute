cd ..
ant -f build.xml reflect-to-others-for-mac

cd ../dbflute-spring-example/dbflute_exampledb
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

#cd ../../dbflute-slim3-example/dbflute_exampledb
#rm ./log/*.log
#. jdbc.sh
#. doc.sh
#. generate.sh
#. sql2entity.sh

cd ../../dbflute-mysql-example/dbflute_exampledb
rm ./log/*.log
. jdbc.sh
. doc.sh
. generate.sh
. sql2entity.sh
. outside-sql-test.sh
. sql2entity-clientsql.sh
. outside-sql-test-clientsql.sh

cd ../../dbflute-postgresql-example/dbflute_exampledb
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

cd ../../dbflute-ymir-example/dbflute_exampledb
rm ./log/*.log
. jdbc.sh
. doc.sh
. generate.sh
. sql2entity.sh

#cd ../../dbflute-teeda-example/dbflute_exampledb
#rm ./log/*.log
#. jdbc.sh
#. doc.sh
#. generate.sh
#. sql2entity.sh

cd ../../dbflute-sastruts-example/dbflute_exampledb
rm ./log/*.log
. jdbc.sh
. doc.sh
. generate.sh
. sql2entity.sh

cd ../../dbflute-tricky-example/dbflute_exampledb
rm ./log/*.log
. jdbc.sh
. doc.sh
. generate.sh
. sql2entity.sh

cd ../
cd ../dbflute-spring-example/
ant

cd ../dbflute-lucy-example/
ant

cd ../dbflute-guice-example/
ant
