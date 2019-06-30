import setuptools

from version import Coalesce_version, author


with open("README.md", "r") as fh:
    long_description = fh.read()

# PyPi won't accept "local" version identifiers, so we have to replace the
# "+snapshot" version identifier segment.
if "+" in Coalesce_version:
    try:
        with open("last_snapshot.txt", "r") as ss:
            last_snap_version = ss.read()
    except:
        last_snap_version = "0.0.0.post0"
    main_version = Coalesce_version.split("+")[0]
    last_snap_version_list = last_snap_version.split(".post")
    last_snap_main_version = last_snap_version_list[0]
    if main_version == last_snap_main_version:
        snap_number = int(last_snap_version_list[1]) + 1
    else:
        snap_number = 1
    version = main_version + ".post" + str(snap_number)
    with open("last_snapshot.txt", "w") as ss:
        ss.write(version)
else:
    version = Coalesce_version

setuptools.setup(
    name = "pyCoalesce",
    version = version,
    author = author,
    author_email = "sorr@incadencecorp.com",
    description = "A python wrapper for coalesce objects",
    license = "Apache License 2.0",
    long_description = long_description,
    long_description_content_type = "text/markdown",
    url = "https://github.com/InCadence/coalesce/wiki",
    packages = ["pyCoalesce", "pyCoalesce.classes", "pyCoalesce.utilities"],
    install_requires = ["simplejson", "xmltodict", "requests", "urllib3"],
    classifiers = (
        "Programming Language :: Python :: 2.7",
        "License :: OSI Approved :: Apache Software License",
        "Operating System :: OS Independent",
    ),
)