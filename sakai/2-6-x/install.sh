#!/bin/sh
cd ..
patch -p0 < gradebook2/sakai/2-6-x/sakai.patch
cd gradebook
patch -p0 < ../gradebook2/sakai/2-6-x/gradebook.patch
cd ../sam
patch -p0 < ../gradebook2/sakai/2-6-x/sam.patch
cd ..
