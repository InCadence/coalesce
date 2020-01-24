Copyright 2018-9, InCadence Strategic Solutions


The package pyCoalesce is a wrapper for the Coalesce database abstraction layer,
which provides a common API for interacting with multiple databases.

A standalone version of pyCoalesce can be found at:
https://pypi.org/project/pyCoalesce/

To install pyCoalesce via pip, simply "pip install pyCoalesce".

The "lxml" package is recommended for manipulating XSD-based classes, but
it's not absolutely required.


The full Coalesce project can be found at:
https://github.com/InCadence/coalesce

The pyCoalesce version number corresponds to the Coalesce version number, but 
is modified to meet PEP 440 and PyPi rules:  release version numbers are 
identical, but for snapshot versions, "snapshot" is replaced with "devX", where
"X" is a sequential number incremented whenever a pyCoalesce snapshot is
distributed.

The pyCoalesce package is found in the "pyCoalesce" directory of the Coalesce
repository.  In addition to the pyCoalesce source files, this directory and its
sub-directories contain documentation (and source for creating documentation), a
script for generating classes from the the Coalesce entity XSD, tests, and 
distribution tools.

The "unit tests" are not true unit tests:  later tests rely on the entities
created in earlier tests, thereby avoiding either repeated creation and 
deletion of entities, or testing against a mocked-up API.  (The latter would
be unnecessarily time-consuming to create, and wouldn't catch bugs in the API
itself, which was still a consideration when this wrapper was created.)
Apprently, a recent change to Coalesce has caused one search test to fail,
due to the creation of multiple entities with each "create" call.  The test has
been retained while the issue is under investigation.

To recreate the distribution files, run the following command from the main 
pyCoalesce directory (typical options have been included):

python setup.py sdist bdist_wheel