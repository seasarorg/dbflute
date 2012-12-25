cd ..
ant -f build.xml reflect-to-oracle

cd ../dbflute-oracle-example/dbflute_exampledb
rm ./log/*.log
. manage.sh renewal
