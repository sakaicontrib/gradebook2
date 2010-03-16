#!/usr/bin/sh

# This outputs the version data to a javascript file for incorporation into the release
`svn info .. | grep "URL: " | grep -Eho "\/[^\/]*\/[^\/]*$" | sed 's/\//var gb2_version="/' | sed 's/$/";/' > version.js `



