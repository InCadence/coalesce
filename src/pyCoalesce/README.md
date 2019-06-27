The pyCoalesce package itself is found in the pyCoalesce directory.  The 
other directories contain documentation (and source for creating
documentation), a script for generating classes from the the Coalesce entity
XSD, tests, and build tools.

The "lxml" package is recommended for manipulating XSD-based classes, but
it's not absolutely required.

The "unit tests" are not true unit tests:  later tests rely on the entities
created in earlier tests, thereby avoiding either repeated creation and 
deletion of entities, or testing against a mocked-up API.  (The latter would
be unnecessarily time-consuming to create, and wouldn't catch bugs in the API
itself, which was still a consideration when this wrapper was created.)

The full Coalesce project can be found at:  https://github.com/InCadence/coalesce

A standalone version of pyCoalesce can be found at:  https://pypi.org/project/pyCoalesce/

To install pyCoalesce via pip, simply "pip install pyCoalesce".

To recreate the distribution files, run the following command from this directory (typical options have been included):

python setup.py sdist bdist_wheel