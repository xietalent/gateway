#!/bin/bash

if [ -z "$1" ]
then
    echo "pulling down latest version in current branch..."
    git pull
else
    echo "checking out $1..."
    git checkout $1
    git pull
fi

mvn -Dmaven.test.skip clean package -U -Pnexus