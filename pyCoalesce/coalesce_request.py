# -*- coding: utf-8 -*-
"""
@author: dvenkat & sorr
"""

from uuid import UUID, uuid4
from numbers import Number
import pprint
import collections

import simplejson as json
from pandas import Series

from utilities.logger import package_logger
from classes import URL, CoalesceEntity, CoalesceEntityTemplate, parseString, \
                    set_entity_fields
from utilities.API_request import get_response

# Set up logging.
logger = package_logger.getChild(__name__)

# Set constants.
ENDPOINTS = {
        u"search" : u"data/search/complex",
        u"CRUD" : u"data/entity/",
        u"templates" : u"templates/",
        u"property" : u"property/",
        u"linkage" : u"linkage/"
    }
OPERATIONS = {
        u"search" : (ENDPOINTS[u"search"], u"post"),
        u"create" : (ENDPOINTS[u"CRUD"], u"put"),
        u"read" : (ENDPOINTS[u"CRUD"], u"get"),
        u"update" : (ENDPOINTS[u"CRUD"], u"post"),
        u"delete" : (ENDPOINTS[u"CRUD"], u"delete"),
        u"create_link" : (ENDPOINT[u"linkage"], u"put"),
        u"read_link" : (ENDPOINT[u"linkage"], u"get"),
        u"delete_link" : (ENDPOINT[u"linkage"], u"delete"),
        u"create_template" : (ENDPOINTS[u"templates"], u"put"),
        u"read_template" : (ENDPOINTS[u"templates"], u"get"),
        u"get_new_entity" : (ENDPOINTS[u"templates"], u"get"),
        u"update_template" : (ENDPOINTS[u"templates"], u"post"),
        u"delete_template" : (ENDPOINTS[u"templates"], u"delete"),
    }
FORMATS = (u"JSON", u"XML", u"dict", u"full_dict", u"entity")
SEARCH_OPERATORS = {
        u"EqualTo": 1,
        u"GreaterThan": 1,
        u"GreaterThanOrEqualTo": 1,
        u"LessThan": 1,
        u"LessThanOrEqualTo": 1,
        u"NotEqualTo": 1,
        u"Like": 1,
        u"Between": 2,
        u"During": 2,
        u"After": 1,
        u"Before": 1,
        u"BBOX": None,
        u"NullCheck": 0
    }
SEARCH_FORMATS = (u"JSON", u"list", u"full_dict")


def case_operator(input_operator):
    """
    Makes search operators case-insensitive by matching and replacing them
    with operators from a predefined list, which are usually in upper
    camcelcase ("BBOX" is an exception).

    :param input_operator:  ASCII or Unicode string

    :return:  a Unicode string
    """

    # First, test for an input operator that doesn't need to be changed.
    if input_operator in SEARCH_OPERATORS:
        return unicode(input_operator)

    # Check for a valid operator, and substitute the proper form from the
    # predefined list.
    for search_operator in SEARCH_OPERATORS:
        if input_operator.lower() == search_operator.lower():
            return search_operator

    # This is a fallback for operators that exist in the Coalesce RESTful
    # API but not in this wrapper.
    return unicode(input_operator)


class CoalesceServer(object):
    """
    Provides configuration information for a Coalesce server, so that
    there's no need to input it again for each new request.  Note that
    this class is _not_ an open connection--it's just a container for
    common request parameters.

    :ivar URL:  the URL of the Coalesce server
    :ivar base_headers:  headers that don't need to change between
        requests.  Specifically, this dict includes "Connection" and
        "Content-type".  The value of "Content-type" is set as a
        constant, since the rest of the code in the package always
        submits API requests as JSON; "Connection" can be set by the
        calling application, but there's probably no need to do so.
    :ivar max_attempts:  the number of times to attempt each request,
        using the exponential backoff coded into API_request.get_response
    """

    VALID_CONNECTION_TYPES = (u"keep-alive", u"close")

    def __init__(self, server_URL = u"http://localhost:8181/cxf/",
                 CRUD_persistor = "data", search_persistor = "data",
                 connection = u"keep-alive", max_attempts = 4):
        """
        Arguments:
        :param URL:  the URL of the Coalesce server
        :param CRUD_persistor:  "data" here directs Coalesce to use the
            default persistor for CRUD operations (this may be different from
            the search persistor), while any other value directs Coalesce to
            use the matching secondary persistor.
        :param search_persistor:  "data" here directs Coalesce to use the
            default persistor for search operations (this may be different
            from the CRUD persistor), while any other value directs Coalesce
            to use the matching secondary persistor.
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

        # Set the Coalesce server URL's.

        if not server_URL[-1] == u"/":
            server_URL += u"/"

        try:
            CRUD_URL = server_URL + CRUD_persistor + "/"
            self.CRUD_URL = URL(CRUD_URL)
        except ValueError:
            raise ValueError(CRUD_URL + " is not a valid URL.")

        try:
            search_URL = server_URL + search_persistor + "/"
            self.search_URL = URL(search_URL)
        except ValueError:
            raise ValueError(search_URL + " is not a valid URL.")

        # Set the base headers as a dict.
        if connection not in self.VALID_CONNECTION_TYPES:
            raise ValueError('The connection type must be either ' +
                             '"keep-alive" or "close."')
        self.base_headers = {"Connection" : connection}

        # Set the maximum number of connection attempts
        try:
            self.max_attempts = int(max_attempts)
        except ValueError:
            raise TypeError('Parameter "max_attempts" must be a positive ' +
                            'integer.')
        if self.max_attempts < 0:
            raise ValueError('Parameter "max_attempts" must be a positive ' +
                             'integer.')


def construct_entity(template = None, server = None, key = None, fields = None):
    """
    A convenience function to retreive a template from the Coalesce server,
    construct an entity using that template, and (optionally) fill any or
    all of the entity's fields with specified values.  Return an instance of
    CoalesceEntity (which can then be submitted to the server with the
    "create" function), including the minimum number of records in each
    recordset, and a template object as an attribute of the entity object.

    :param template:  a Coalesce template (UUID) key, an iterable containing
        the template's name, source, and version (in that order), or a
        CoalesceEntityTemplate object.
    :param server:  a CoalesceServer object or the URL of a Coalesce server.
        If "template" is an instance of CoalesceEntityTemplate, no server is
        needed.
    :param key:  a UUID to serve as the key for the new entity, either as an
        instance of the "UUID" class, or any string or integer that could
        serve as input to the UUID class constructor.  If this argument is
        omitted, a random key is generated.
    :param fields:  a dict-like of fields possessed by the new entity, and
        values to set on those fields.  The keys can be either string (ASCII
        or Unicode) names (in which case the function searches for each field,
        and throws an error if duplicates are found) or path lists,
        alternating between child object type and list index.  The values of
        the dict-like must be the values to be set on the "value" attribute
        of each field--use another method for setting other attributes.

    :return:  an instance of CoalesceEntity
    """

    # Check for a valid key, and change it into a string if necessary.
    if not isinstance(UUID, key):

        try:
            key_obj = UUID(key)
        except AttributeError:
            key_obj = UUID(int = key)
        except ValueError:
            key_obj = UUID(bytes = key)
        else:
            key_obj = key

        key_str = unicode(key_obj)

    # If we don't have a template already, we'll need to get one.

    if not isinstance(CoalesceTemplateEntity, template):

        # Make a request to server for the entity template.
        #
        # First, set the URL and headers.  The URL will be a different if
        # "template" is a key or an iterable of name/value/version.

        if not server:
            raise ValueError('The argument "server" must be a URL or an ' +
                             'instance of CoalesceServer.')
        elif isinstance(server, CoalesceServer):
            server_obj = server
        else:
            server_obj = CoalesceServer(server)

        headers = server_obj.base_headers
        operation = u"read_template"

        # Figure out whether "template" is a key or an iterable, check the
        # validity of any key, and set the request URL accordingly.

        if len(template) == 3:
            name, source, version = template
            URL = server_obj.URL + OPERATIONS[operation][0] + name + "/" + \
                  source + "/" + version + ".xml"

        else:

            if not isinstance(UUID, template):
                try:
                    template_key_obj = UUID(template)
                except AttributeError:
                    template_key_obj = UUID(int = template)
                except ValueError:
                    template_key_obj = UUID(bytes = template)
                else:
                    template_key_obj = template

            template_key = unicode(template_key_obj)
            URL = server_obj.URL + OPERATIONS[operation][0] + template_key + \
                  ".xml"

        method = OPERATIONS[operation][1]

        # Submit the request.
        response = get_response(URL = URL, method = method, headers = headers,
                                delay = 1, max_attempts = 4)

        # Get the XML and strip off the XML declaration--the lxml package
        # really doesn't like the encoding declaration.
        template_XML = response.text[response.text.index("<entity"):]

        # Instantiate a template object from the template.
        template = parseString(template_XML, object_type = CoalesceEntityTemplate,
                               silence = True)

    # Build a new entity from the template.
    new_entity = template.new_entity(key = key_str)

    # If field values have been specified, set the fields in question.
    if fields:
        set_entity_fields(new_entity, fields = fields, match_case = False)

    return new_entity


def create(new_entity, server = None, key = None):

    """
    Puts a new entity on the Coalesce server.

    Arguments:
    :param new_entity:  the entity to put on the server.  This can be a JSON
        or XML representation of an entity, a nested dict-like in the same
        format as a JSON respresentation, or an instance of CoalesceEntity
        (or a subclass); the function automatically detects the input type
        and adjusts the RESTful endpoint and requests headers accordingly.
    :param server:  a CoalesceServer object or the URL of a Coalesce server
    :param key:  a UUID to serve as the key for the new entity, either as an
        instance of the "UUID" class, or any string or integer that could
        serve as input to the UUID class constructor.  If the entity itself
        already includes a key, this argument will be ignored; if no key is
        supplied, the function randomly generates one.
    """

    # Check the entity input, and it's a Python object, transform it into
    # a JSON or XML string.  If the entity includes a key, extract it.

    entity_error_msg = 'The first argument supplied to "create" must be a ' + \
                       'JSON or XML representation of an entity, a nested ' + \
                       'dict-like in the same format as a JSON ' + \
                       'respresentation, or an instance of CoalesceEntity (or ' + \
                       'a subclass).'

    if isinstance(basestring, new_entity):

        try:
            json.loads(new_entity)

        except:
            try:



    elif isinstance(CoalesceEntity, new_entity):
        data = to_XML_string(new_entity)

    else:
        try:
            data = json.dumps(new_entity)
        except TypeError:
            raise TypeError(entity_error_msg)

    # Check the server input, and create a server object if needed.
    if not server:
        raise ValueError('The argument "server" must be a URL or an ' +
                         'instance of CoalesceServer.')
    elif isinstance(server, CoalesceServer):
        server_obj = server
    else:
        server_obj = CoalesceServer(server)

    server_obj = CoalesceServer()
    server = server_obj.URL
    headers = {
            "Connection" : u"keep-alive",
            "content-type" : u"application/json; charset=utf-8"
        }

    params = None
    operation = u"create"
    method = OPERATIONS[operation][1]

    response = get_response(URL = server + "data/templates",
                            method = 'get',
                            params = params,
                            data = None,
                            headers = headers,
                            delay = 1,
                            max_attempts = 4
                            )
    TEMPLATE_KEYS = json.loads(response.text)

    for i in range(len(TEMPLATE_KEYS)):
        if TEMPLATE_KEYS[i][u"name"] == TYPE:
            key = TEMPLATE_KEYS[i][u"key"]

    response = get_response(URL = server + "data/templates/{}/new".format(key),
                            method = 'get',
                            params = params,
                            data = None,
                            headers = headers,
                            delay = 1,
                            max_attempts = 4
                            )

    TEMPLATE = json.loads(response.text)
    for item in FIELDSADDED:
        tmp = update_template(TEMPLATE, item)
    data = json.dumps(tmp)

    response = get_response(URL = server + OPERATIONS[operation][0] +
                            json.loads(data)["key"],
                            method = method,
                            params = params,
                            data = data,
                            headers = headers,
                            delay = 1,
                            max_attempts = 4)


def update(value = None, key = None,
           newvalues = None,
           TESTING = "false"):

        """
        Arguments:
        :value: The type of entity being requested
        :key: The specific entity key
        :newvalues: The fields that require updating, only key required if it is
        present in the first or second nest, else provide an entire path to ensure
        repeating keys are not confused
        """
        if (value or key or newvalues) == None:
            raise ValueError("Make sure no input parameters are None and retry")

        server_obj = CoalesceServer()
        server = server_obj.URL
        headers = {
                "Connection" : u"keep-alive",
                "content-type" : u"application/json; charset=utf-8"
                }
        read_payload = {
            'values': value, 'entityKey': key} #Please enter your specific entity key
        params = None
        operation = u"update"
        method = OPERATIONS[operation][1]
        response = get_response(URL = server + OPERATIONS[u"read"][0] + read_payload['entityKey'],
                                method = OPERATIONS[u"read"][1],
                                params = params,
                                data = read_payload,
                                headers = headers,
                                delay = 1,
                                max_attempts = 4
                                )
        TEMPLATE = json.loads(response.text)

        if len(newvalues) > 0:
            for item in newvalues:
                tmp = update_template(TEMPLATE, item)
            data = tmp

        elif len(newvalues) == 0:
            data = TEMPLATE

        elif len(newvalues) < 0:
            raise ValueError("HOW?")

        response = get_response(URL=server + OPERATIONS[operation][0] +
                                    read_payload['entityKey'],
                                method=method,
                                params=params,
                                data=json.dumps(data),
                                headers=headers,
                                delay=1,
                                max_attempts=2)

        if TESTING == "true":
            return response, data

        return response

def read(server = None, key = None, output = "dict"):
    """
    Arguments:
    :param server:  a CoalesceServer object or the URL of a Coalesce server
    :param key:  the UUID assigned to the entity
    :param output:  If this argument is "dict", return the results as a
        Python dict; for "full_dict", return the full response, including
        metadata, as a dict.  For "entity", return the result as an instance
        of class "CoalesceEntity".  For "JSON" or "XML" return the full
        response, unparsed, including metadata, as a Unicode string of the
        corresponding type.

    :return:  a dict, CoalesceEntity, JSON object, or XML object, depending
        on the value of "output".
    """

    # Set the request parameters.

    if isinstance(server, CoalesceServer):
        server_obj = server
    else:
        server_obj = CoalesceServer(server)

    if not key:
        raise ValueError("Please specify a UUID key.")

    output = output.lower()
    if not output in FORMATS:
        raise ValueError('The argument "output" must take one of the ' +
                         'following values:  ' + str(FORMATS) + '.')

    headers = server_obj.base_headers
    operation = u"read"
    URL = server_obj.CRUD_URL + OPERATIONS[operation][0] + key
    if output == u"xml":
        URL += output
    method = OPERATIONS[operation][1]

    # Submit the request.
    response = get_response(URL = URL, method = method, headers = headers,
                            delay = 1, max_attempts = 4)

    # Return the type of output specified by "output".
    if output == u"dict":
        response_dict = \
          json.loads(response.text)['sectionsAsList'][0]['recordsetsAsList']
        return response_dict

    elif output == u"full_dict":
        response_dict = json.loads(response.text)
        return response_dict

    elif output == u"entity":
        entity_XML = response.text[response.text.index("<entity"):]
        response_entity = parseString(entity)
        return response_entity

    else: # The requested output format was "JSON" or "XML".
        return response.text


def delete(server = None, key = None):

    """
    :param server:  A CoalesceServer object or the URL of a Coalesce server
    :param key:  The UUID of the entity being deleted

    :return:  True if the returned status code is 200 (which should
        indicate a successful deletion), False if the status code has any
        other value in the 200's.  (Any values outside the 200's will raise
        an error.)
    """

    if isinstance(server, CoalesceServer):
        server_obj = server
    else:
        server_obj = CoalesceServer(server)

    if not key:
        raise ValueError("Please specify a UUID key.")

    headers = server_obj.base_headers
    operation = u"delete"
    URL = server_obj.CRUD_URL + OPERATIONS[operation][0] + key
    method = OPERATIONS[operation][1]

    # Submit the request.
    response = get_response(URL = URL, method = method, headers = headers,
                            delay = 1, max_attempts = 4)

    # Check for the appropriate status code--we do this for deletion
    # because checking the code is easier than checking the response
    # content, and we don't need the content for anything else.
    success = response.status_code == 200
    return success


def search_simple(server = None, recordset = "coalesceentity", field = "name",
           operator = "Like", value = None, match_case = False,
           property_names = ["coalesceentity.name"], page_size = 200,
           page_number = 1, output = "list"):

    """
    Constructs a query with a single criteria set, which mirrors the
    deprecated simple search in the Java API.

    Arguments:
    :param server:  A CoalesceServer object or the URL of a Coalesce server
    :param recordset:  the recordset that contains the search field
    :param field:  the field to search.  This value is passed directly to
        the appropriate persistor underlying Coalesce, and for most
        persistors, field names are case-insensitive.
    :param operator:  the search operation; valid values can be found in
        the constant pyCoalesce.SEARCH_OPERATIONS.
    :param value:  the value to search for.  Must be a string or number
        (for operators requiring a single argument), an iterable of two
        strings or numbers (for operators requiring a pair of arguments),
        or None (for operators that take no arguments).
    :param match_case:  if True, results should match the case of "value".
        Some of the persistors underlying Coalesce are case-insensitive
        when matching values, regardless of the value of "match_case."
    :param property_names:  an iterable naming the properties (fields) to
        return for each search result.  The values are passed directly to
        the appropriate persistor(s) underlying Coalesce, and for most
        persistors, these names are case-insensitive.  Note that the
        "entityKey" (identical to "coelesceentity.objectkey") property is
        always returned, regardless of the value of this argument.
    :param page_size:  the number of results to return
    :param page_number:  used to retrieve results deeper in the list.  For
        example, a query with "page_size" 250 and "page_number" 3 returns
        results 501 to 750.
    :param output:  If this argument is "list", return the results as a
        Python list; for "full_dict", return the full response, including
        metadata, as a dict.  For "JSON" return the full response,
        unparsed, including metadata, as a Unicode string.

     :return:  a list, dict, or JSON object, depending on the value of
         "output".
    """

    # Set the query parameters.

    if isinstance(server, CoalesceServer):
        server_obj = server
    else:
        server_obj = CoalesceServer(server)

    output = output.lower()
    if not output in SEARCH_FORMATS:
        raise ValueError('The argument "output" must take one of the ' +
                         'following values:  ' + str(SEARCH_FORMATS) + '.')
    headers = server_obj.base_headers
    headers["Content-type"] = "application/json"
    operation = u"search"
    URL = server_obj.search_URL + OPERATIONS[operation][0] + key
    method = OPERATIONS[operation][1]

    # Attempt to convert the operator to the proper case, if necessary,
    # by substituting one of the predefined operators.  Then, make sure
    # the operator is in the predefined list.  ("case_operator" returns
    # the input operator if it can't match an operator in the list.)
    operator = case_operator(operator)
    if operator not in SEARCH_OPERATORS:
        raise ValueError("The search operator must be one of the following:\n" +
                         str(SEARCH_OPERATORS))

    # Form the criteria set, less the search value(s).
    criteria = [{
                 u"recordset": recordset,
                 u"field": field,
                 u"operator": operator,
                 u"matchCase": match_case
               }]

    # Check and parse the search value(s), and add them to the criteria
    # set.  Note that checking value types is beyond the scope of this
    # wrapper.
    num_values = SEARCH_OPERATORS[operator]
    if num_values:
        if num_values == 1:
            criteria[0]["value"] = value
        elif num_values == 2:
            try:
                criteria[0]["value"] = value[0] + u" " + value[1]
            except:
                try:
                    split_value = value.split(" ")
                    if len(split_value) == 2:
                        criteria[0]["value"] = value
                    else:
                        raise ValueError
                except (AttributeError, ValueError):
                    raise TypeError('The "value" argument for search '  +
                                    'operator "' + operator + '" must be an ' +
                                    'iterable with exactly two elements.')
    else:
        raise ValueError('Search operator "' + operator + '" has not been ' +
                         'implemented in "search_simple".')

    # Form the search query.
    query = {
             "operator": "AND",
             "criteria": criteria
            }

    # Form the request.
    data = {
            "pageSize": page_size, "pageNumber": page_number,
            "propertyNames": property_names,
            "group": query
           }
    data = json.dumps(data)

    # Submit the request.
    response=get_response(URL = server + OPERATIONS[operation][0],
                          method = method,
                          data = data,
                          headers = headers,
                          delay = 1,
                          max_attempts = 4)

    # Return the type of output specified by "output".
    if output == u"list":
        response_dict = \
          json.loads(response.text)['hits']
        return response_dict

    elif output == u"full_dict":
        response_dict = json.loads(response.text)
        return response_dict

    else: # The requested output format was "JSON".
        return response.text


def search(server = None, query = None,
           property_names = ["coalesceentity.name"], page_size = 200,
           page_number = 1, output = "list", check_case = True):

    """
    Submits a query using the full Coalesce RESTful API.  The user
    submits the query itself, and the function constructs the full request
    by adding other (user-configurable) fields.

    Arguments:
    :param server:  A CoalesceServer object or the URL of a Coalesce server
    :param query:  the search query (the value of "group" in a Coalesce
        search request object), as dict-like or a JSON object (string).
        For the most part, this query, must follow the Coalesce format
        exactly, but operators not in the right form (upper camelcase)
        are replaced with the proper forms, provided they're included in
        the SEARCH_OPERATORS constants.
    :param property_names:  an iterable naming the properties (fields) to
        return for each search result.  The values are passed directly to
        the appropriate persistor(s) underlying Coalesce, and for most
        persistors, these names are case-insensitive.  Note that the
        "entityKey" (identical to "coelesceentity.objectkey") property is
        always returned, regardless of the value of this argument.
    :param page_size:  the number of results to return
    :param page_number:  used to retrieve results deeper in the list.  For
        example, a query with "page_size" 250 and "page_number" 3 returns
        results 501 to 750.
    :param output:  If this argument is "list", return the results as a
        Python list; for "full_dict", return the full response, including
        metadata, as a dict.  For "JSON" return the full response,
        unparsed, including metadata, as a Unicode string.
    :param check_case: If this argument is False, do not check search
        operators for the correct case.  Skipping this check improves speed
        (important if an application is making a lot of queries),
        especially for complex queries.

     :return:  a list, dict, or JSON object, depending on the value of
         "output".
    """

    def _case_operators(query_fragment, fragment_is_criteria = False):
        """
        Recursively find all search operators in a search query, and
        replace any in the wrong case with the correct forms, if those
        forms are found in a predefined list.

        :param query_fragment:  dict-like; either a search group or a
            search criteria set
        :param fragment_is_criteria:  used by the function to determine
            whether to treat "query_fragment" as a search group or a
            criteria set; this makes it possible to avoid checking the
            case of search group operators, which are case-insensitive
            in the Coalesce RESTful API.

        :return:  a possibly modified version of the input object
        """

        # If "query_fragment" is a criteria set, case its operator.
        if fragment_is_criteria:
            try:
                query_fragment["operator"] = \
                    case_operator(query_fragment["operator"])
            except KeyError:
                pass

        # Otherwise, parse "query_fragment".
        else:

            # Check for and fix sub-groups.
            if "groups" in query_fragment:
                for i, group in enumerate(query_fragment["groups"]):
                    query_fragment["groups"][i] = _case_operators(group)

            # Check for and fix criteria sets.
            if "criteria" in query_fragment:
                for i, criteria_set in enumerate(query_fragment["criteria"]):
                    query_fragment["criteria"][i] = \
                        _case_operators(criteria_set,
                                        fragment_is_criteria = True)

        return query_fragment


    # Set the query parameters.

    if isinstance(server, CoalesceServer):
        server_obj = server
    else:
        server_obj = CoalesceServer(server)

    output = output.lower()
    if not output in SEARCH_FORMATS:
        raise ValueError('The argument "output" must take one of the ' +
                         'following values:  ' + str(SEARCH_FORMATS) + '.')
    headers = server_obj.base_headers
    headers["Content-type"] = "application/json"
    operation = u"search"
    URL = server_obj.search_URL + OPERATIONS[operation][0] + key
    method = OPERATIONS[operation][1]

    # Form the request.  Note that we don't test the validity of the
    # query--we let the API do that, which prevents the wrapper from
    # blocking the usage of new features.
    #
    # In the case of a query submitted as a JSON object, converting the
    # JSON to dict and then back again makes this particular coder twitch,
    # but doing it this way allows us to make the search operators case-
    # insensitive, since the recursive search called below relies on a
    # dict-like input.
    if isinstance(query, basestring):
        query = json.loads(query)
    data = {
            "pageSize": page_size, "pageNumber": page_number,
            "propertyNames": property_names,
            "group": query
           }
    data_json = json.dumps(data)

    # Convert any search operator not in the proper case, recursively
    # searching through the search criteria sets in the query ("group").
    if check_case:
        data["group"] = _case_operators(data["group"])

    # Submit the request.
    response = get_response(URL = server + OPERATIONS[operation][0],
                            method = method,
                            data = data_json,
                            headers = headers,
                            delay = 1,
                            max_attempts = 4)

    # Return the type of output specified by "output".
    if output == u"list":
        response_dict = \
          json.loads(response.text)['hits']
        return response_dict

    elif output == u"full_dict":
        response_dict = json.loads(response.text)
        return response_dict

    else: # The requested output format was "JSON".
        return response.text


def update_template(orignal, change):
    for key, value in change.iteritems():
        if isinstance(value, collections.Mapping):
            placeholder = update_template(orignal.get(key), value)
            orignal[key] = placeholder
        elif isinstance(value, list):
            for key1 in value:
                index = value.index(key1)
                change[key] = value[index]
                placeholder = [update_template(orignal[key][index], change[key])]
                orignal[key] = placeholder
        else:
            orignal[key] = change[key]
    return  orignal


class CoalesceEntity(object):

    def __init__(self, GUID=None, entity_type = None, template=None, records={},
                 links=[], server=CoalesceServer(), created = False):
        if (GUID and entity_type) == None:
            raise ValueError("The GUID and entity type can not be equal to none.\n"
                             "If a new entity needs to be created, leave created as its default.")
        elif created == True:
            self.text = read(key = GUID, value = entity_type)
            self.key = GUID

        elif created == False:
            self.response = create(TYPE=entity_type, links = [], TESTING="true")
            self.text = self.response[1]
            self.key = self.text["key"]

        self.data = json.loads(self.text)
        self.URL = server.URL
        self.headers = server.base_headers

    def add_data(self,data, values=[], open_as_file = "false"):
        if open_as_file == "true":
            with open("data.txt", "w") as file:
                raw_input("Please press a key once done editing template in data.txt")
        if len(values) > 0:
            self.data = update_template(data, values)
        if len(values) == 0:
            pass

    def delete(self, GUID, entity_type):
        self.response = delete(type = entity_type, key = GUID)

    def get_links(self, GUID):
        data =
        links = get_response(URL= self.URL + ENDPOINTS["linkages"] +
                                    self.key,
                                method="get",
                                params=params,
                                data=json.dumps(data),
                                headers=headers,
                                delay=1,
                                max_attempts=2)
        return links

    def update_record(records=[{}], record_keys=True, linkage_present = "false"):
        if linkage_present == "true":
            for item in records:
                try:
                    get_links(item)
                except:
                    create_link(item)

        if record_keys and not have_keys:
            self.data = self.read()

        update_template(self.data, records)

    def create_link():
        for i in links:
            payload = {
                "Something": i
            }
            get_response(URL = ?,
                        method =?,
                        params=None,
                        data = payload,
                        headers = headers,
                        delay = 1
                        max_attempts = 1)

    def delete_links():
        for i in links:
            payload = {
                "Something": i
            }
            get_response(URL = ?,
                        method =?,
                        params=None,
                        data = payload,
                        headers = headers,
                        delay = 1
                        max_attempts = 1)


    def send():
        response = update(key = GUID, value=entity_type)









