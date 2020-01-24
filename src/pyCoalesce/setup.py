"""
Copyright 2018-9, InCadence Strategic Solutions

"""


import re
from shutil import copyfile
from os.path import isfile
import setuptools


COALESCE_LICENCE_PATH = "../../LICENSE"
LICENSE_FILENAME = "LICENSE"
LICENSE_NAME = "Apache License 2.0"

# This constant can be changed to a custom label.
LOCAL_VERSION_LABEL = "local"

# Read in the README file.
with open("README.md", "r") as fh:
    long_description = fh.read()

# Read in the last pyCoalesce version number.
try:
    with open("pyCoalesce/version.txt", "r") as version_file:
        last_version = version_file.read()
except:
    last_version = "0.0.0.dev0"

# Try to read a version number from the larger Coalesce package, and then
# process it to create a PyPi-compliant version number.  This won't work in
# the sdist (tar.gz) version downloaded from PyPi--indeed, the "version.py"
# module won't even be present.

try:

    # We do this here because the module in question won't exist in the
    # standalone distribution.  This is the actual line caught in the
    # "except".  The rest of this block is included here, rather than in
    # an "else" block, for readability.
    from version import Coalesce_version

    # Parse the Coalesce version.
    version_match = \
        re.match("([0-9]+\.[0-9]+\.[0-9]+)([^a-zA-Z0-9\.]?)([a-zA-Z0-9\.]*)",
                 Coalesce_version)
    main_version = version_match.group(1)
    suffix = version_match.group(3).lower()

    # Check the previous pyCoalesce version.
    last_version_list = last_version.split(".dev")
    last_main_version = last_version_list[0]
    try:
        last_dev_number = int(last_version_list[1])
    except IndexError:
        last_dev_number = None

    # Transform the Coalesce version number into a pyCoalesce version
    # number compliant with PEP 440 and PyPi.

    # If the current Coalesce version is a snapshot:
    if suffix == "snapshot":

        # If the last version of pyCoalesce was a dev version based on the
        # current main version of Coalesce, in which case this must be the
        # second or later dev version of the current main version:
        if main_version == last_main_version:
            dev_number = last_dev_number + 1

        # If the last version of pyCoalesce was a dev version based on the
        # the previous main version of Coalesce (or a snapshot thereof), in
        # which case this must be the first dev version of the current
        # main version:
        else:
            dev_number = 1

    # If the current version of Coalesce is a release:
    else:

        # Which has the latest main version, the current Coalesce version
        # of the last pyCoalesce version?  Usually, the current Coalesce
        # version will be the same as or later than the last pyCoalesce
        # version, but it's possible for the pyCoalesce main version to be
        # later in rare cases, when two or more pyCoalesce versions are
        # created before any Coalesce snapshot has been created.

        # Parse the version number.
        main_version_list = main_version.split(".")
        last_main_version_list = last_main_version.split(".")
        major = int(main_version_list[0])
        minor = int(main_version_list[1])
        patch = int(main_version_list[2])
        last_major = int(last_main_version_list[0])
        last_minor = int(last_main_version_list[1])
        last_patch = int(last_main_version_list[2])

        # Compare major, minor, and patch numbers.

        if major > last_major:
            latest = "Coalesce"

        elif last_major > major:
            latest = "pyCoalesce"

        else:

            if minor > last_minor:
                latest = "Coalesce"

            elif last_minor > minor:
                latest = "pyCoalesce"

            else:

                if patch > last_patch:
                    latest = "Coalesce"

                elif last_patch > patch:
                    latest = "pyCoalesce"

                else:
                    latest= "both"

        # If the Coalesce version number is higher (meaning the previous
        # version of pyCoalesce was part of the previous Coalesce release),
        # in which case this version of pyCoalesce must be part of the
        # larger Coalesce release:
        if latest == "Coalesce":
            dev_number = None

        # If the Coalesce and last pyCoalesce version numbers are the same:
        elif latest == "both":

            # If the last pyCoalesce version was a dev version of this
            # Coalesce release, in which case this version of pyCoalesce
            # must be part of the larger Coalesce release:
            if last_dev_number:
                pre_number = None

            # If the last pyCoalesce version was part of the current
            # pyCoalesce release (a rare occurrence that can only happen
            # when a pyCoalesce version is created before a snapshot has
            # been generated for a given Coalesce version):
            else:

                # We're going to increment the main version number here.
                # In rare cases, this could produce an inaccurate version
                # number:  if, #1, we're producing a pyCoalesce dev version
                # before any Coalesce snapshot has created and, #2, the
                # next release of Coalesce will be a new major or minor
                # version.
                new_patch = patch + 1
                main_version = ".".join([str(major), str(minor),
                                         str(new_patch)])

                dev_number = 1

        # If the last pyCoalesce version number is higher (an extremely
        # rare case that can occur if two or more pyCoalesce versions are
        # created before the first Coalesce snaphost has been generated for
        # given main version):
        else:
            main_version = last_main_version
            dev_number = last_dev_number + 1

    # Form the pyCoalesce version number:
    if dev_number:
        version = main_version + ".dev" + str(dev_number)
    else:
        version = main_version

# If we can't read in a Coalesce version number, this must be a local
# installation, and therefore we'll assign a local version number (note
# that we can't actually check to see if any code has been changed).  The
# following section can be modified to create a customized pattern for
# local version numbers.

except ImportError:

    # To obtain the distributed version number, split off the local suffix.
    version_list = Coalesce_version.split("+" + LOCAL_VERSION_LABEL)
    distributed_version = version_list[0]

    # Find the last local number, if any.
    try:
        last_local_number = version_list[1]
    except IndexError:
        local_number = 1
    else:
        local_number = int(last_local_number) + 1

    # Construct a new local version number.
    version = distributed_version + "+" + LOCAL_VERSION_LABEL + \
              str(local_number)

with open("pyCoalesce/version.txt", "w") as version_file:
    version_file.write(version)

# Try to copy the Coalesce license file.  If that doesn't work (because
# we're not inside the main Coalesce repo), keep any license file that
# already exists, or create a new one containing only the license name.
try:
    copyfile(COALESCE_LICENCE_PATH, LICENSE_FILENAME)
except IOError:
    if not isfile(LICENSE_FILENAME):
        with open(LICENSE_FILENAME, "w") as license_file:
            license_file.write(LICENSE_NAME)

# Try importing the current project and author info from "version" as well.
try:
    from version import project, author
except ImportError:
    project = "pyCoalesce"
    author = "Dhruva Venkat, Scott Orr"

setuptools.setup(
    name = project,
    version = version,
    author = author,
    author_email = "sorr@incadencecorp.com",
    description = "A python wrapper for coalesce objects",
    license = LICENSE_NAME,
    long_description = long_description,
    long_description_content_type = "text/markdown",
    url = "https://github.com/InCadence/coalesce/wiki",
    packages = ["pyCoalesce", "pyCoalesce.classes", "pyCoalesce.utilities"],
    install_requires = ["simplejson", "xmltodict", "requests", "urllib3"],
    classifiers = (
        "Programming Language :: Python :: 3.7",
        "License :: OSI Approved :: Apache Software License",
        "Operating System :: OS Independent",
    ),
)