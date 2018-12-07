# -*- coding: utf-8 -*-
"""
@author: Scott Orr

This class is simple a subclass of :class:`unicode` with a :meth:`__new__`
method that adds a check for a valid URL scheme.


"""

from urlparse import urlsplit


class URL(unicode):
    """
    Adds a check to the vanilla "unicode" constructor to make sure the
    value is a valid and supported URL of a scheme appropriate for a
    RESTful server (that is, HTTP or HTTPS).

    """

    VALID_SCHEMES = (u"http", u"https")

    def __new__(cls, value):
        """
        :param value:  a URL as an ASCII or Unicode string

        """

        scheme = urlsplit(value).scheme
        if scheme == u"":
            raise ValueError("The provided address is not a URL.")
        elif scheme.lower() not in cls.VALID_SCHEMES:
                raise ValueError('"' + scheme + '" is not a valid URL scheme.')

        self = super(URL, cls).__new__(cls, value)
        return self

