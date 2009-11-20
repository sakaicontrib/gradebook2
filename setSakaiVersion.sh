#!/bin/sh

echo -n 'Current version: '
read oldVersion
echo -n 'Enter new version: '
read newVersion

sed -i -e"s/<version>$oldVersion<\/version>/<version>$newVersion<\/version>/"  pom.xml model/pom.xml shared/pom.xml server/pom.xml api/pom.xml client/pom.xml war/pom.xml help/pom.xml

sed -i -e"s/-$oldVersion/-$newVersion/g" .classpath
sed -i -e"s/$oldVersion\//$newVersion\//g" .classpath

