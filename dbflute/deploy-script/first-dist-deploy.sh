cd ..
ant -f build.xml dist
ant -f build.xml reflect-to-spring

cd ../dbflute-spring-example/dbflute_exampledb
rm ./log/*.log
. jdbc.sh
. doc.sh
. generate.sh
. sql2entity.sh

cd ../../dbflute-spring-example
ant
