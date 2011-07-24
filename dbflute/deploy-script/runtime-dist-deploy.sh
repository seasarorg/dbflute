cd ../../dbflute-runtime
mvn -e clean source:jar javadoc:jar deploy -DupdateReleaseInfo=true

cd ../dbflute
ant -f build.xml runtime-dist