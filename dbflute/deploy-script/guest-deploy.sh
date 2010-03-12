cd ..
ant -f build.xml reflect-to-hibernate
ant -f build.xml reflect-to-s2jdbc

cd ../dbflute-hibernate-example/dbflute_exampledb
rm ./log/*.log
. jdbc.sh
. doc.sh
. generate.sh

cd ../../dbflute-s2jdbc-example/dbflute_exampledb
rm ./log/*.log
. jdbc.sh
. doc.sh
. generate.sh
. sql2entity.sh
