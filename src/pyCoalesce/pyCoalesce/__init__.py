"""
@author: Dhruva Venkat
@author: Scott Orr

The :mod:`pyCoalesce` package includes a main module, with functions for
making requests to the Coalesce RESTful API, and two subpackages, one of which
provides classes for representing Coalesce entities and linkages, and the other
of which provides a handful of useful objects not directly related to the API.


"""

from coalesce_request import *

__all__ = ["coalesce_request"]