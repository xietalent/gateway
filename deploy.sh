#!/bin/bash

sh package.sh $1

ZIPFILE=`ls target/*.zip`
rm -rf server_deploy
unzip -d server_deploy $ZIPFILE

chmod u+x server_deploy/*.sh