# -*- coding: utf-8 -*-
"""
@author: sorr
"""

from urlparse import urlsplit

import simplejson as json

from pyCoalesce import package_logger
from API_request import get_response


# Set up logging.
logger = package_logger.getChild(__name__)


class URL(unicode):
    """
    Adds a check to the vanilla "unicode" constructor to make sure the
    value is a valid and supported URL (currently "http[s]", "ftp[s]",
    or "file").
    """

    VALID_SCHEMES = [u"http", u"https", u"ftp", u"sftp", u"file"]

    def __new__(cls, value):

        scheme = urlsplit(value).scheme
        if scheme == u"": # That is, the scheme slot is empty.
            raise ValueError("The provided address is not a URL.")
        elif scheme not in cls.VALID_SCHEMES:
                raise ValueError('"' + scheme + '" is not a valid URL scheme.')

        self = super(URL, cls).__new__(cls, value)
        return self


class CoalesceServer(object):
    """
    Provides configuration information for a Coalesce server, so that
    there's no need to input it again for each new request.  Note that
    this class is _not_ an open connection--it's just a container for
    common request parameters.

    :ivar URL:  the URL of the Coalesce server
    :ivar base_headers:  headers that don't need to change between
        requests.  Specifically, this dict includes "Connection" and
        "content-type".  The value of "content-type" is set as a
        constant, since the rest of the code in the package always
        submits API requests as JSON; "Connection" can be set by the
        calling application, but there's probably no need to do so.
    :ivar max_attempts:  the number of times to attempt each request,
        using the exponential backoff coded into API_request.get_response
    """

    CONTENT_TYPE = u"application/json; charset=utf-8"
    VALID_CONNECTION_TYPES = (u"keep-alive", u"close")

    def __init__(self, server_URL = u"http://0.0.0.0",
                 connection = u"keep_alive", max_attempts = 4):
        """
        Arguments:
        :param URL:  the URL of the Coalesce server
        :param connection:  headers that don't need to change between
            requests.  Specifically, this dict includes "Connection" and
            "content-type".  The value of "content-type" is set as a
            constant, since the rest of the code in the package always
            submits API requests as JSON; "Connection" can be set by the
            calling application, but there's probably no need to do so.
        :param max_attempts:  the number of times to attempt each request,
            using the exponential backoff coded into
            API_request.get_response
    """

        # Set the Coalesce server URL.
        self.URL = URL(server_URL)

        # Set the base headers as a dict.
        if not connection in self.VALID_CONNECTION_TYPES:
            raise ValueError('The connection type must be either ' +
                             '"keep-alive" or "close".')
        self.base_headers = {"Connection" : connection,
                             "content-type" : self.CONTENT_TYPE}

        # Set the maximum number of connection attempts.
        try:
            self.max_attempts = int(max_attempts)
        except ValueError:
            raise TypeError('Parameter "max_attempts" must be a positive ' +
                            'integer.')
        if self.max_attempts < 0:
            raise ValueError('Parameter "max_attempts" must be a positive ' +
                             'integer.')


# If all strings use Unicode rather than ASCII, we don't have to worry
# we can use "==" instead of "is" without having to worry about which
# type of string we're dealing with in even give place.
ENDPOINTS = {u"search" : u"data/search/complex", u"CRUD" : u"entity",
             u"templates" : u"templates", u"property" : u"property"}
OPERATIONS = {u"search" : (ENDPOINTS[u"search"], u"post"),
              u"create" : (ENDPOINTS[u"CRUD"], u"put"),
              u"read" : (ENDPOINTS[u"CRUD"], u"get"),
              u"update" : (ENDPOINTS[u"CRUD"], u"post"),
              u"delete" : (ENDPOINTS[u"CRUD"], u"delete")}
FORMATS = (u"XML", u"JSON", u"python_dict")


def coalesce_request(server = u"http://0.0.0.0", operation = u"search",
                     search_criteria = None, results_page_size = 200,
                     results_page_number = 1, results_property_names = None,
                     key = None, name = None, source = None, version = None,
                     recordset_key = None, response_format = u"python_dict"):
    """
    Provides a Python wrapper for a Coalesce RESTful API request.  Since
    the call to API_request.get_response already handles exponential
    backup (in other words, you aren't going to make the same request
    twice), there's no real need to instantiate the request as an object,
    rather than making it a function.

    :param server:  either a server URL, or a CoalesceServer object.  If
        this is a URL, the function instantiates a CoalesceServer object
        with default settings.
    :param operation:  the type of operation to perform on the database
    :param search_critera:  a dict-like of search criteria, or a
        SearchGroup of such criteria (which may include nested
        SearchGroups)
    :param search_page_number:  the page number of search results to
        return, with each page.
        For example, if
    :param key:  the UID database key of the entity or template to be
        operated on.  For templates, the key is equivalent to
        <name>/<source>/<version>.
    :param name:  the name of the entity template to use when performing
        the requested operation
    :param source:  the entity source; used to perform searches,and in
        specifying templates
    :param version:  the entity template version
    :param recordset_key:  the key of a specific recordset; used in a
        request for the fields of that recordset
    :param response_format:  the format of the return object.  If the
        selected format is "XML" or "JSON", the function requests that
        format from the Coalesce server, and returns the server's response
        unchanged; if the selected format is "python_dict", the function
        converts the response to a Python dict, which includes only
        fields likely to be relevant to a calling application.

    Implementation of Coalesce endpoints and operations is ongoing.
    """

    # Convert a server URL to a CoalesceServer object.  This also serves
    # as a check for a properly formed URL.
    if not isinstance(server, CoalesceServer):
        server = CoalesceServer(server_URL = server)

    if not OPERATIONS.haskey(operation):
        raise ValueError('The parameter "operation" must take one of the ' +
                         'following values:\n' + u", ".join(OPERATIONS.keys()))

    if operation == u"search":
