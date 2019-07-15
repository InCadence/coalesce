# -*- coding: utf-8 -*-
"""

This script reads the Coalesce version from the Coalesce POM file and
transforms it into a Python variable, so that it can be imported to other
scripts in the package ("setup.py", and "docs/source/conf.py" for the
documentation).  It also stores authorship and copyright data.

Note that, unlike the pyCoalesce package itself, this script requires
the lxml package (the "xpath" method doesn't exist in the core xml
package).

@author: Scott Orr

"""

from os import path

try:
    from lxml import etree as etree_
except ImportError:
    from xml.etree import ElementTree as etree_

# Specify the path of the POM file.  We need a path relative to the
# directory of this script, not the directory it was run from.
file_dir = path.dirname(path.abspath(__file__))
POM_path = path.join(file_dir, '../../pom.xml')

# Read and parse the POM file.
try:
    POM_XML = etree_.parse(POM_path)

# If no POM can be found, provide a default.
except IOError:
    Coalesce_version = "0.0.39.snapshot"

# Get the version string (working around the namespace prefixes of element
# tags).
else:
    Coalesce_version = \
        POM_XML.xpath("/*[local-name()='project']/*[local-name()='version']")[0].text
