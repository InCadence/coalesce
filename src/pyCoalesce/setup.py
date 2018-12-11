import setuptools

from version import Coalesce_version, author


with open("README.md", "r") as fh:
    long_description = fh.read()

setuptools.setup(
    name = "pyCoalesce",
    version = Coalesce_version,
    author = author,
    author_email = "sorr@incadencecorp.com",
    description = "A python wrapper for coalesce objects",
    long_description = long_description,
    long_description_content_type = "text/markdown",
    url = "https://github.com/InCadence/coalesce/wiki",
    packages = ["pyCoalesce", "pyCoalesce.classes", "pyCoalesce.utilities"],
    install_requires = ["simplejson", "xmltodict", "requests", "urllib3"],
    classifiers = (
        "Programming Language :: Python :: 2.7",
        "License :: OSI Approved :: Apache License 2.0",
        "Operating System :: OS Independent",
    ),
)