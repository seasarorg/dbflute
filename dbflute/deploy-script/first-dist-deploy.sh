cd ..

if [ `uname` = "Darwin" ]; then
  export JAVA_HOME=$(/usr/libexec/java_home -v 1.6)
fi

ant -f build.xml dist
ant -f build.xml reflect-to-spring
ant -f build.xml reflect-to-guice

cd ../../dbflute-example-container/dbflute-spring-example/dbflute_exampledb
rm ./log/*.log
. jdbc.sh
. doc.sh
. generate.sh
. sql2entity.sh
cd ..
mvn -e compile

cd ../dbflute-guice-example/dbflute_exampledb
rm ./log/*.log
. manage.sh regenerate
cd ..
mvn -e compile
