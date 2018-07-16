import setuptools

with open("README.md", "r") as fh:
    long_description = fh.read()

setuptools.setup(
    name="Coalesce_Wrapper",
    version="1.0.0",
    author="Dhruva Venkat",
    author_email="dvenkat@incadencecorp.com",
    description="A python wrapper for coalesce objects",
    long_description=long_description,
    long_description_content_type="text/markdown",
    url="https://github.com/InCadence/coalesce/wiki",
    packages=setuptools.find_packages(),
    classifiers=(
        "Programming Language :: Python :: 2.7",
        "License :: OSI Approved :: MIT License",
        "Operating System :: OS Independent",
    ),
)