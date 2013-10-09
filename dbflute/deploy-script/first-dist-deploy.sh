cd ..
ant -f build.xml dist
ant -f build.xml reflect-to-spring

cd ../../dbflute-example-container/dbflute-spring-example/dbflute_exampledb
rm ./log/*.log
. jdbc.sh
. doc.sh
. generate.sh
. sql2entity.sh
cd ..
mvn -e compile
