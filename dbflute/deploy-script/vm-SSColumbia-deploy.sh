cd ..
ant -f build.xml reflect-to-sqlserver

export answer=y

cd ../../dbflute-example-database/dbflute-sqlserver-example/dbflute_exampledb
rm ./log/*.log
. manage.sh renewal
