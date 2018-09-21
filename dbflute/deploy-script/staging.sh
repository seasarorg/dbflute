cd ..

if [ `uname` = "Darwin" ]; then
  export JAVA_HOME=$(/usr/libexec/java_home -v 1.6)
fi

ant -f build.xml stage
ant -f buildnet.xml stage