# -*- coding: utf-8 -*-
"""
Created on Mon Aug 20 11:53:00 2018

@author: sorr
"""

from urlparse import urlsplit


class URL(unicode):
    """
    Adds a check to the vanilla "unicode" constructor to make sure the
    value is a valid and supported URL of a scheme appropriate for a
    RESTful server (that is, HTTP or HTTPS).
    """

    VALID_SCHEMES = [u"http", u"https"]

    def __new__(cls, value):

        scheme = urlsplit(value).scheme
        if scheme == u"":
            raise ValueError("The provided address is not a URL.")
        elif scheme.lower() not in cls.VALID_SCHEMES:
                raise ValueError('"' + scheme + '" is not a valid URL scheme.')

        self = super(URL, cls).__new__(cls, value)
        return self

