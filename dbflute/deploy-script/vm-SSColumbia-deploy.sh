cd ..
ant -f build.xml reflect-to-sqlserver

cd ../dbflute-sqlserver-example/dbflute_exampledb
rm ./log/*.log
. manage.sh renewal
