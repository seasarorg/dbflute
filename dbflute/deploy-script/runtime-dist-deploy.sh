cd ../../dbflute-runtime
mvn -e clean deploy

cd ../dbflute
ant -f build.xml runtime-dist