#!/bin/sh

cwd=`pwd`

# Assume that we're somewhere below the base dir where sakai is installed 
while [ "$cwd" != "/" ] 
do 
	if [ -f $cwd/pom.xml ] 
	then
		result=`grep "<name>Sakai Core Project </name>" $cwd/pom.xml`
		if [ "$result" != "" ]
		then
			echo "Found Sakai base directory at $cwd . . . patching. "
			cd $cwd
			patch -p0 < gradebook2/sakai/2-7-x/sakai.patch
			cd gradebook2
			patch -p0 < sakai/2-7-x/gradebook2.patch

			exit 0
		else
			echo $result
		fi
	fi
	cwd=`dirname $cwd`
done

echo "Unable to find the Sakai root directory . . . aborting."
exit 1

