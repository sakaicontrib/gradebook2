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


GB2-SPECIFIC SAKAI PROPERTIES

Since v1.1.x:
-------------

gb2.help.url=<url to HTML help text>
- e.g. gb2.help.url=http://somelocation.edu/gradebook-help.html

gb2.enabled.grade.types= [ points, percentages, letters]
- points = points grade mode
- percentage = percentage grade mode
- letters = letters grade mode
- e.g. gb2.enabled.grade.types=points,percentages,letters

gb2.gradable.role.names=<sakai role name(s)>
- e.g. gb2.gradable.role.names=Student,access



Since v1.2.x:
-------------

gb2.enable.scaled.extra.credit = [OFF, TRUE, FALSE, INSTRUCTOR, ADMIN] (case insensitive, true will operate as ADMIN-only) 
- default: OFF
- OFF = "scale extra credit" is disabled
- FALSE = "scale extra credit" is disabled
- TRUE = only admin can enable "scale extra credit"
- ADMIN = only admin can enable "scale extra credit"
- INSTRUCTOR = only instructor can enable "scale extra credit"

gb2.limit.scaled.extra.credit = [ CATEGORIES, WEIGHTED CATEGORIES] (case insensitive) 
- default: CATEGORIES,WEIGHTED CATEGORIES
-- This property only takes effect if gb2.enable.scaled.extra.credit is set to either [TRUE, INSTRUCTOR, ADMIN]

Since v1.3.x:
-------------

gb2.import.delete.missing.grades = [true, false]
- default: true
- For more details, please see http://jira.sakaiproject.org/browse/GRBK-619

gb2.security.enabled = [true, false]
- default: true

gb2.security.context.prefix = [/portal/, /xsl-portal/, ...]
- default: /portal/


SUPPORT

The best place to look for bugs, feature requests, and upcoming changes is JIRA:
http://jira.sakaiproject.org/browse/GRBK

Some more information about the project can be found in Confluence:
http://confluence.sakaiproject.org//x/LACo



