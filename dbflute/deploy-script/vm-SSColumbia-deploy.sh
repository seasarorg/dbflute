cd ..

if [ `uname` = "Darwin" ]; then
  export JAVA_HOME=$(/usr/libexec/java_home -v 1.6)
fi

ant -f build.xml reflect-to-sqlserver

export answer=y

cd ../../dbflute-example-database/dbflute-sqlserver-example/dbflute_exampledb
rm ./log/*.log
. manage.sh renewal
