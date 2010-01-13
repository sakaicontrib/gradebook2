README
--------------------------------------------------------------------------

GRADEBOOK2
*Compatible with Sakai 2.5.x, 2.6.x, 2.7.x, trunk*


HOW IT WORKS

As of the 1.2.0 release of GB2 we will be cutting our full build tags against 
the current development version of Sakai. For 1.2.0 this will be Sakai 2.8-SNAPSHOT
and it means that institutions that wish to build GB2 against earlier versions
will need to apply one or more patches from the sakai/[version] directory prior 
to building. These patches are against the following modules:
- gradebook : the original gradebook module that we share a data model with
- sam : the samigo module that interacts with GB2 to store grades
- gradebook2 : the GB2 module itself, mostly to take into account the kernel 
dependency changes

We are also planning (as of 1.2.0) to release separate tags for each compatible
Sakai version. These tags will be available under the "tags" directory in a subdirectory
labeled with the current gb2 version. Each tag will be labelled with the sakai version,

tags/
	1.2.0/
		sakai-2.5.5
		sakai-2.6.1
		sakai-2.6.2
		sakai-2.7.0

This will be our recommended method of running the GB2 code, since it will
eliminate the need to apply any local patches against the GB2 module itself.

Note that not every tag will include a check in for every compatible version of 
Sakai. We'll make every effort to include them for the stable releases. Please
get in touch if you see one missing that you need.


GETTING STARTED

- To install, follow instructions under the sakai/[version] directory
e.g. sakai/2-5-x/INSTALL.txt
- Alternatively, you can run the script sakai/[version]/install.sh 




