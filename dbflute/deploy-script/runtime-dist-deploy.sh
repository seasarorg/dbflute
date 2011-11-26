cd ../../dbflute-runtime

# deploy process
mvn -e clean deploy

# to avoid snapshot problem of eclipath
mvn -e install

cd ../dbflute
ant -f build.xml runtime-dist