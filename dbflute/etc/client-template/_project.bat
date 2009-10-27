@echo off

set ANT_OPTS=-Xmx256M

set MY_PROJECT_NAME=dfclient

set DBFLUTE_HOME=..\mydbflute\dbflute-@dbflute.version@

if "%finally_pause%"=="" set finally_pause=y
