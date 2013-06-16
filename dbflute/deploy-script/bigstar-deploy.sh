cd ..
ant -f build.xml reflect-to-basic
ant -f build.xml reflect-to-spring
ant -f build.xml reflect-to-guice
ant -f build.xml reflect-to-mysql
ant -f build.xml reflect-to-postgresql
export answer=y

cd ../dbflute-basic-example
. sync-lib.sh
cd dbflute_exampledb
rm ./log/*.log
. replace-schema.sh
. jdbc.sh
. doc.sh
. generate.sh
. sql2entity.sh
. outside-sql-test.sh

cd ../../dbflute-spring-example
. sync-lib.sh
cd dbflute_exampledb
rm ./log/*.log
. replace-schema.sh
. jdbc.sh
. doc.sh
. generate.sh
. sql2entity.sh
. outside-sql-test.sh

cd ../../dbflute-guice-example/dbflute_exampledb
rm ./log/*.log
. manage.sh renewal
. manage.sh load-data-reverse
. manage.sh schema-sync-check
. manage.sh freegen
. diffworld-test.sh

cd ../../dbflute-mysql-example/dbflute_exampledb
rm ./log/*.log
. replace-schema.sh
. jdbc.sh
. doc.sh
. generate.sh
. sql2entity.sh
. outside-sql-test.sh
. manage.sh load-data-reverse
. manage.sh freegen

cd ../../dbflute-postgresql-example/dbflute_exampledb
rm ./log/*.log
. jdbc.sh
. doc.sh
. generate.sh
. sql2entity.sh
. outside-sql-test.sh
. manage.sh load-data-reverse
