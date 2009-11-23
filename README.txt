README
--------------------------------------------------------------------------

GRADEBOOK2
*Compatible with Sakai 2.5.x, 2.6.x, 2.7.x*


HOW IT WORKS

As of the 1.2.0 release of GB2 we will be cutting our full build tags against 
the current development version of Sakai. For 1.2.0 this will be Sakai 2.7.x, 
and it means that institutions that wish to build GB2 against earlier versions
will need to apply one or more patches from the sakai/[version] directory prior 
to building. These patches are against the following modules:
- gradebook : the original gradebook module that we share a data model with
- sam : the samigo module that interacts with GB2 to store grades
- gradebook2 : the GB2 module itself, mostly to take into account the kernel 
dependency changes

We are also planning (as of 1.2.0) to release pre-built artifacts against 
Sakai 2.5.x, 2.6.x, and 2.7.x and make these available via maven repository.
This will be our recommended method of running the GB2 code, since it will
eliminate the need to apply any local patches against the GB2 module itself.


GETTING STARTED

- To install, follow instructions under the sakai/[version] directory
e.g. sakai/2-5-x/INSTALL.txt
- Alternatively, you can run the script sakai/[version]/install.sh 




