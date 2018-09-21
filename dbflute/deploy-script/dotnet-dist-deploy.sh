cd ..

if [ `uname` = "Darwin" ]; then
  export JAVA_HOME=$(/usr/libexec/java_home -v 1.6)
fi

ant -f buildnet.xml dist
