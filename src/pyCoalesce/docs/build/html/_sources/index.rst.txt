.. pyCoalesce documentation master file, created by
   sphinx-quickstart on Wed Sep 26 16:45:10 2018.
   You can adapt this file completely to your liking, but it should at least
   contain the root `toctree` directive.

pyCoalesce
======================================
:mod:`pyCoalesce` is a Python wrapper for the `Coalesce RESTful API 
<https://github.com/InCadence/coalesce/wiki/REST-API>`_.  The package includes
functions for interacting with the API's search and CRUD controllers 
(:mod:`pyCoalesce.coalesce_request`) and object classes for working with XML 
representations of Coalesce entities (:mod:`pyCoalesce.classes`).  The Coalesce
API also supports JSON representations of entities, but, since JSON objects 
closely resemble native Python :class:`dict` and :class:`list` objects, the 
package assumes that no special handling is required for interacting with the 
API using JSON, with the exception of a class for handling JSON representations
of Coalesce linkages (:class:`~pyCoalesce.classes.coalesce_json.CoalesceAPILinkage`).

:Version: |release|

.. codeauthor:: Dhruva Venkat, Scott Orr <sorr@incadencecorp.com>


Contents
--------

.. toctree::
   :maxdepth: 0

   pyCoalesce

Indices and Search
------------------

* :ref:`genindex`
* :ref:`modindex`
* :ref:`search`
