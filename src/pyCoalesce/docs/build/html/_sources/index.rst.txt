.. pyCoalesce documentation master file, created by
   sphinx-quickstart on Wed Sep 26 16:45:10 2018.
   You can adapt this file completely to your liking, but it should at least
   contain the root `toctree` directive.

:mod:`pyCoalesce`
======================================
:mod:`pyCoalesce` is a Python wrapper for the `Coalesce RESTful API 
<https://github.com/InCadence/coalesce/wiki/REST-API>`_.  The package includes
functions for interacting with the API's search and CRUD controllers 
(:mod:`pyCoalesce.coalesce_request`) and objects for working with XML 
representations of Coalesce entities (:mod:`pyCoalesce.classes`).  The Coalesce
API also supports JSON representations of entities, but, since JSON objects 
closely resemble native Python :class:`dict` and :class:`list` objects, the 
packages assumes that no special handling is required interacting with the API
using JSON.

.. toctree::
   :maxdepth: 2
   :caption: Contents:



API Requests Module
----------

.. toctree::

   pyCoalesce.coalesce_request

Subpackages
-----------

.. toctree::

    pyCoalesce.classes
    pyCoalesce.utilities

Indices and Search
-----------

* :ref:`genindex`
* :ref:`modindex`
* :ref:`search`
