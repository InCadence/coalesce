# -*- coding: utf-8 -*-
"""
@author: Scott Orr

This class is simple a subclass of :class:`str` with a
:meth:`~pyCoalesce.utilities.URL_class.__new__` method that adds a check for
 a valid URL scheme.


"""

from urllib.parse import urlsplit


class URL(str):
    """
    Adds a check to the vanilla string constructor to make sure the value
    is a valid and supported URL of a scheme appropriate for a RESTful
    server (that is, HTTP or HTTPS).

    """

    VALID_SCHEMES = ("http", "https")

    def __new__(cls, value):
        """
        :param value:  a URL as an ASCII or Unicode string

        """

        scheme = urlsplit(value).scheme
        if scheme == "":
            raise ValueError("The provided address is not a URL.")
        elif scheme.lower() not in cls.VALID_SCHEMES:
                raise ValueError('"' + scheme + '" is not a valid URL scheme.')

        self = super(URL, cls).__new__(cls, value)
        return self

