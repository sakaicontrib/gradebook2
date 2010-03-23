README
--------------------------------------------------------------------------

GRADEBOOK2
*Compatible with Sakai 2.5.x, 2.6.x, 2.7.x, trunk*


GETTING STARTED

As of the 1.2.0 release of GB2 we will be cutting our full build tags against 
the current development version of Sakai. For 1.2.0 this will be Sakai 2.8-SNAPSHOT.

Simply by modifying the <version> tag in the root GB2 pom.xml file, you should be able to build
against any 2.7.x version of Sakai as well. 

For earlier versions of Sakai, including the 2.5.x and 2.6.x series, there are a couple of 
additional steps required:
(1) Patch the "gradebook" and "sam" modules in the existing Sakai release
(2) Add the -Psakai2.6 or -Psakai2.5 profile to your mvn command when building


INSTALLATION INSTRUCTIONS

More specifics on installing are available under one of the following files:
- sakai/2-5-x/INSTALL.txt
- sakai/2-6-x/INSTALL.txt
- sakai/2-7-x/INSTALL.txt


SUPPORT

The best place to look for bugs, feature requests, and upcoming changes is JIRA:
http://jira.sakaiproject.org/browse/GRBK

