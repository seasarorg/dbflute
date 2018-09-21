cd ../../dbflute-runtime

if [ `uname` = "Darwin" ]; then
  export JAVA_HOME=$(/usr/libexec/java_home -v 1.6)
fi

# deploy process
mvn -e clean deploy

# to avoid snapshot-unresolved problem
mvn -e install

cd ../dbflute
ant -f build.xml runtime-dist