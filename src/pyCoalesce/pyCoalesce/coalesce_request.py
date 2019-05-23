# -*- coding: utf-8 -*-
"""
@author: Dhruva Venkat
@author: Scott Orr

This is the main module for :mod:`pyCoalesce`, and provides search
functions for the Coalesce RESTful API's search controller, CRUD functions
for the entity, template, and linkage controllers, and a handful of helper
functions, as well as a class for storing server configurations.  See
https://github.com/InCadence/coalesce/wiki/REST-API for documentation of
the API itself.

Note that while the entity and template controllers support both XML and
JSON, the search and linkage controllers support *only* JSON.

At present, the functions in this module do not quite cover the entire API:
they omit the recordset and field endpoints for the template controller
(these endpoints provide nothing that can't be obtained by retrieving an
entire template), and the (rarely used) property contoller.

"""

from sys import stdout, stderr
from string import Template
from uuid import UUID
from copy import copy
from warnings import warn

import simplejson as json
from simplejson import JSONDecodeError
import xmltodict

from utilities.logger import package_logger
from utilities.URL_class import URL
from classes import CoalesceEntityTemplate, parseString, to_XML_string, \
                    set_entity_fields, CoalesceAPILinkage
from utilities.API_request import get_response

# Set up logging.
logger = package_logger.getChild(__name__)

# Set constants.

ENDPOINTS = {
        u"search" : {u"persistor": u"search", u"controller": u"/search/complex"},
        u"entity" : {u"persistor": u"CRUD", u"controller": u"/entity"},
        u"templates" : {u"persistor": u"CRUD", u"controller": u"/templates"},
        u"property" : {u"persistor": u"CRUD", u"controller": u"/property"},
        u"linkage" : {u"persistor": u"CRUD", u"controller": u"/linkage"},
    }
OPERATIONS = {
        u"search" : {u"endpoint": u"search", u"method": u"post"},
        u"create" : {u"endpoint": u"entity", u"method": u"post"},
        u"read" : {u"endpoint": u"entity", u"method": u"get", u"final": "$key"},
        u"update" : {u"endpoint": u"entity", u"method": u"put", u"final": "$key"},
        u"delete" : {u"endpoint": u"entity", u"method": u"delete"},
        u"create_template" : {u"endpoint": u"templates", u"method": u"post"},
        u"register_template" : {u"endpoint": u"templates", u"method": u"put",
                                u"final": "$key/register"},
        u"update_template" : {u"endpoint": u"templates", u"method": u"put",
                              u"final": "$key"},
        u"read_template_by_key": {u"endpoint": u"templates", u"method": u"get",
                                  u"final": "$key"},
        u"read_template_by_nsv": {u"endpoint": u"templates", u"method": u"get",
                                  u"final": "$name/$source/$version"},
        u"get_template_list": {u"endpoint": u"templates", u"method": u"get"},
        u"get_new_entity" : {u"endpoint": u"templates", u"method": u"get"},
        u"delete_template" : {u"endpoint": u"templates", u"method": u"delete",
                              u"final": "$key"},
        u"create_linkages" : {u"endpoint": u"linkage", u"method": u"put"},
        u"read_linkages" : {u"endpoint": u"linkage", u"method": u"get",
                            u"final": "$key"},
        u"delete_linkages" : {u"endpoint": u"linkage", u"method": u"delete"},
    }
ENTITY_UPLOAD_OPERATIONS = (u"create", u"update", u"create_template",
                            u"update_template")

# Eventually, the following list of operations should be available through
# the "property" API, at which point it might be possible to downloaded it
# rather than hard-code it.  However, hard-coding may still be necessary to
# specify the proper numbers of arguments.  The docstring below this
# constant causes it to show up in the auto-generated Sphinx documentation,
# something that isn't needed for the other constants (the valid values
# they define are documented in docstrings of the corresponding functions,
# but this list is a little long for that treatment, and the operators are
# used by two different functions, "search" and "search_simple".
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
        u"BBOX": 1,
        u"NullCheck": 0
    }
"""
The keys are the valid search operations, and the values are the number of
arguments required for each operator.  The keys are used to correct cases
for both the :func:`~pyCoalesce.coalesce_request.search` and
:func:`~pyCoalesce.coalesce_request.search_simple` functions; providing an
unlisted operator to :func:`~pyCoalesce.coalesce_request.search_simple`
will result in an exception, while
:func:`~pyCoalesce.coalesce_request.search` will pass an unlisted
operator to the Coalesce API, albeit without any case correction (thus,
operators newly added to the Coalesce RESTful API can be used before
they've been listed here).  The (integer) values are used only to check
inputs for the :func:`~pyCoalesce.coalesce_request.search_simple` function
(a ``None`` can be used to indicate that an operation isn't yet implemented
for this function), but provide a useful reference for writing queries for
the full :func:`~pyCoalesce.coalesce_request.search_simple` function.

"""

SEARCH_SORT_ORDERS = (u"ASC", u"DESC")
SEARCH_OUTPUT_FORMATS = (u"json", u"list", u"full_dict")
CRUD_OUTPUT_FORMATS = (u"json", u"xml", u"dict", u"entity_object")
TEMPLATE_LIST_OUTPUT_FORMATS = (u"json", u"list")
LINK_CRUD_OUTPUT_FORMATS = (u"json", u"dict_list", u"api_list")


class UnexpectedResponseWarning(Warning):
    """
    This warning is used to indicate that an API operation has returned an
    unexpected status code (typically, a value in the 200's other than 200
    or 204).

    """

    pass


class CoalesceServer(object):
    """
    Provides configuration information for a Coalesce server, so that
    there's no need to input it again for each new request.  Note that
    this class is *not* an open connection--it's just a container for
    common request parameters.

    :ivar URL:  the URL of the Coalesce server
    :ivar base_headers:  headers that don't need to change between
        requests.  Specifically, this :class:`dict` includes "Connection".
        "Connection" can be set in the constructor call, but there's
        probably no need to set it to "close" rather than the default
        "keep-alive".
    :ivar max_attempts:  the number of times to attempt each request,
        using the exponential backoff coded into
        :func:`pyCoalesce.utilities.API_request.get_response`

    """

    _VALID_CONNECTION_TYPES = (u"keep-alive", u"close")

    def __init__(self, server_URL = u"http://localhost:8181/cxf/",
                 CRUD_persistor = "data", search_persistor = "data",
                 connection = u"keep-alive", max_attempts = 4):
        """
        :param URL:  the URL of the Coalesce server
        :param CRUD_persistor:  "data" here directs Coalesce to use the
            default persistor for CRUD operations (this may be different
            from the search persistor), while any other value directs
            Coalesce to use the matching secondary persistor.
        :param search_persistor:  "data" here directs Coalesce to use the
            default persistor for search operations (this may be different
            from the CRUD persistor), while any other value directs
            Coalesce to use the matching secondary persistor.
        :param connection:  a request header, with valid values of
            "keep-alive" and "close".  There's probably no reason not to
            use the default for this argument.
        :param max_attempts:  the number of times to attempt each request,
            using the exponential backoff coded into
            :func:'pyCoalesce.utilities.API_request.get_response'

        """

        # Set the Coalesce server URL and persistors.

        if not server_URL[-1] == u"/":
            server_URL += u"/"

        try:
            self.URL = URL(server_URL)
        except:
            raise ValueError('The argument "URL" must be a valid URL.')

        if not isinstance(CRUD_persistor, basestring):
            raise TypeError('The argument "CRUD_persistor" must be an ASCII ' +
                            'or Unicode string.')

        if not isinstance(search_persistor, basestring):
            raise TypeError('The argument "search_persistor" must be an ' +
                            'ASCII or Unicode string.')

        self.persistors = {u"CRUD": CRUD_persistor, u"search": search_persistor}

        # Set the base headers as a dict.
        if connection not in self._VALID_CONNECTION_TYPES:
            raise ValueError('The connection type must be either ' +
                             'or "close."')
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


def _construct_URL(server_obj = None, operation = "read", key = None,
                   name_source_version = None):
    """
    A helper function to construct the RESTful API endpoint from the
    configuration constants.  This function doesn't handle the final "json"
    or "xml" element of some endpoints--since that element changes the
    format of the server's response, it's best to handle it within the
    calling functions, which also parses the response.

    :param server:  a :class:`~pyCoalesce.coalesce_request.CoalesceServer`
        object
    :param operation:  one of the keys in the constant "OPERATIONS"
    :param key:  a string version of an entity's UUID key
    :param name_source_version:  an entity's name, source, and version (in
        that order), as an iterable

    :returns:  a URL as a Unicode string

    """

    URL_elements = OPERATIONS[operation]

    operation_endpoint = ENDPOINTS[URL_elements[u"endpoint"]]
    endpoint_str = server_obj.persistors[operation_endpoint[u"persistor"]] + \
                   operation_endpoint[u"controller"]
    API_URL = server_obj.URL + endpoint_str


    # If there's a "final" segement, add it to the URL, substituting values
    # as approriate.

    if "final" in URL_elements:

        final_template = Template(URL_elements["final"])
        sub_dict = {}

        if key:
            sub_dict["key"] = key

        if name_source_version:
            sub_dict["name"] = name_source_version[0]
            sub_dict["source"] = name_source_version[1]
            sub_dict["version"] = name_source_version[2]

        try:
            final_str = final_template.substitute(sub_dict)
        except KeyError:
            raise KeyError("Forming the endpoint for the requested opertion " +
                           "requires additional information:  either a UUID " +
                           "entity key or a name, source, and version, " +
                           "depending on the operation in question.")

        API_URL += "/" + final_str

    return API_URL


def case_operator(input_operator):
    """
    Makes search operators case-insensitive by matching and replacing them
    with operators from a predefined list, which are usually in upper
    camcelcase ("BBOX" is an exception).

    :param input_operator:  ASCII or Unicode string

    :returns:  a Unicode string

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


def search(server = None, query = None,
           return_property_names = ["coalesceentity.name"], template = None,
           sort_by = None, sort_order = "ASC", page_size = 200, page_number = 1,
           output = "list", check_case = True, return_query = False):

    """
    Submits a query using the full Coalesce RESTful API.  The user
    submits the query itself, and the function constructs the full request
    by adding other (user-configurable) fields.

    :param server:  A :class:`~pyCoalesce.coalesce_request.CoalesceServer`
        object or the URL of a Coalesce server
    :param query:  the search query, either the filter object (the value of
        "group" in a `Coalesce search request object
        <https://github.com/InCadence/coalesce/wiki/REST-API#search-query-data-format>`_)
        or the full query object, as dict-like or a JSON object (string);
        in the case of a full query object, the other arguments used to
        form a query ("sort_by", "sort_order", "return_property_names",
        "page_size", and "page_number") are ignored.  For the most part,
        the query must follow the Coalesce format exactly, but if
        "check_case" is ``True``, operators not in the right form (upper
        camelcase for most search operators, all caps for group operators)
        are replaced with the proper forms, provided they're included in
        the :const:`~pyCoalesce.coalesce_request.SEARCH_OPERATORS`
        constant.  It's possible to supply an empty query (a value of
        ``None`` or an empty string or dict), in which case the search will
        return all records in the recordset implied by one of more values
        of the "return_property_names" argument, or, failing that, the
        recordset corresponding to the "template" argument (see below).
    :param return_property_names:  an iterable naming the properties
        (fields) to return for each search result.  The values, each in the
        format `<recordset>.<field>`, are passed directly to the
        appropriate persistor(s) underlying Coalesce, and for most
        persistors, these names are case-insensitive  Note that the
        "entityKey" property is always returned, regardless of the value of
        this argument.
    :param template:  the name of a template whose records should be
        searched, if the template can't be infered from "query" or
        "return_property_names".  This value is *required* in such a case,
        but will be ignored otherwise.  Note that, while each template
        typically has a single corresponding recordset, the template and
        recordset have different (albeit usually similar) names.  This name
        is case-insensitive.  This is the "type" key in the Coalesce
        RESTful API format, but "type" is a reserved name in Python.
    :param sort_by:  this argument can take one of two forms:  a single
        property (field) on which the results should be sorted, as a string
        in the form `<recordset>.<field>`; or, a list-like or JSON array of
        of dict-like or JSON objects in the form
        `{"propertyName": <recordset>.<field>, "sortOrder": <sort order>}`.
        A single dict-like/JSON object in the latter format will also be
        accepted. In some circumstances (in particular, if Derby is the
        default persistor), the in the case of basic entity fields, such
        as "name" and "dateCreated", a field name should be supplied by
        itself, with no recordset.
    :param sort_order:  "ASC" for ascending, or "DESC" for descending.
        This argument is used only if "sort_by" is the name of a single
        field (rather than a full Coalesce "sortBy" object).
    :param page_size:  the number of results to return
    :param page_number:  used to retrieve results deeper in the list.  For
        example, a query with "page_size" 250 and "page_number" 3 returns
        results 501 to 750.
    :param output:  If this argument is "list", return the results as a
        Python :class:`list`; for "full_dict", return the full response,
        including metadata, as a :class:`dict`.  For "JSON" return the full
        response, unparsed, including metadata, as a Unicode string.
    :param check_case:  If this argument is ``False``, do not check search
        operators for the correct case.  Skipping this check improves speed
        (important if an application is making a lot of queries),
        especially for complex queries.
    :param return_query:  if this argument is ``True``, return the full
        Coalesce query object, as the second element of a tuple (the
        search results themselves will be the first element).  If the ouput
        format for the results is "JSON", return the request as a JSON
        object (string), otherwise, return it as a Python :class:`dict`.
        This option is useful for constructing search queries
        programmatically by making small modifications to the generated
        query objects; it can also be used for debugging.

    :returns:  a :class:`list`, :class:`dict`, or JSON object, depending on
        the value of "output", possibly followed (as the second element of
        a tuple) by the full request object, as a :class:`dict`, or JSON
        object  .

    """

    def _case_operators(query_fragment, fragment_is_criteria = False):
        """
        Recursively find all search operators in a search query, and
        replace any in the wrong case with the correct forms, if those
        forms are found in a predefined list.  Also uppercase group
        operators ("AND" and "OR").

        :param query_fragment:  dict-like; either a search group or a
            search criteria set
        :param fragment_is_criteria:  used by the function to determine
            whether to treat "query_fragment" as a search group or a
            criteria set; this makes it possible to avoid checking the case
            of search group operators, which are case-insensitive in the
            Coalesce RESTful API.

        :returns:  a possibly modified version of the input object

        """

        # If "query_fragment" is a criteria set, case its operator.
        if fragment_is_criteria:
            try:
                query_fragment["operator"] = \
                    case_operator(query_fragment["operator"])
            except KeyError:
                pass

        # Otherwise, "query_fragment" is a search group--parse it.
        else:

            # Check for and fix the group operator.
            if "operator" in query_fragment:
                query_fragment["operator"] = \
                    unicode(query_fragment["operator"]).upper()

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


    # Set the request parameters.  We use a copy of the base headers so
    # that the header added here isn't preserved in the server object.

    if isinstance(server, basestring):
        server_obj = CoalesceServer(server)
    else:
        server_obj = server

    output = output.lower()
    if not output in SEARCH_OUTPUT_FORMATS:
        raise ValueError('The argument "output" must take one of the ' +
                         'following values:\n' + str(SEARCH_OUTPUT_FORMATS) +
                         '.')
    operation = u"search"
    try:
        API_URL = _construct_URL(server_obj =  server_obj,
                                 operation = operation)
    except AttributeError as err:
        raise AttributeError(str(err) + '\n.This error can occur if the ' +
                              'argument "server" is not either a URL or a ' +
                              'CoalesceServer object.')
    method = OPERATIONS[operation][u"method"]
    headers = copy(server_obj.base_headers)
    headers["Content-type"] = "application/json"

    # Form the query.  Note that we don't test the validity of the query--
    # we let the API do that, which prevents the wrapper from blocking the
    # usage of new features.

    # In the case of a query submitted as a JSON object, converting the
    # JSON to dict and then back again makes this particular coder twitch,
    # but doing it this way allows us to make the search operators case-
    # insensitive, since the recursive search called below relies on a
    # dict-like input.
    if isinstance(query, basestring):
        query = json.loads(query)

    # If "query" is empty, set it to None.
    elif len(query) == 0:
        query = None

    # If "query" is a full query object, assign it directly as the
    # query ("data").  Otherwise, construct the query object.

    if query and "group" in query:
        data = query

    else:

        data = {"pageSize": page_size, "pageNumber": page_number,
                "propertyNames": return_property_names}

        # If there's a query (filter object), add it.  If there's no
        # query, create an empty object to use as the value of "group".
        if not query:
            query = {}
        data["group"] = query

        # If a "template" name was supplied (necessary if there's no
        ## query--or a query with only coalesceEntity metadata fields--
        # and no recordset in "return_property_names"), add it.
        if template:
            data["type"] = template

        # Add any sorting parameters.  We checkfor the (deprecated) earlier
        # form of the sort-by input, as a dict/JSON object identical to the
        # form accepted by the Coalesce RESTful API.

        if sort_by:

            # Check for a JSON object that needs to be decoded.

            if isinstance(sort_by, basestring):

                try:
                    sort_by = json.loads(sort_by)

                # If it's a string but not decodeable, treat it as a field
                # name, and construct the sort-by object.  This is the only
                # case in which the "sort_order" argument is used--in the
                # case of multiple sort-by fields, each entry in the
                # "sort_by" argument must include a "sortOrder" value (the
                # API equivalent of  "sort_order") as well as a
                # "propertyName" value (the sort-by field name).
                except JSONDecodeError:
                    sort_order = sort_order.upper()
                    if not sort_order in SEARCH_SORT_ORDERS:
                        raise ValueError('The argument "sort_by" must take ' +
                                         'one of the following values:\n' +
                                         str(SEARCH_SORT_ORDERS) + '.')
                    sort_by = \
                        [{"propertyName": sort_by, "sortOrder": sort_order}]

            # If this a is single sort-by object, wrap it in a list.
            if "propertyName" in sort_by and "sortOrder" in sort_by:
                sort_by = [sort_by]

            data["sortBy"] = sort_by

    # Convert any search operator not in the proper case, recursively
    # searching through the search criteria sets in the query ("group").
    # Do the same for the sort order(s).
    if check_case:
        data["group"] = _case_operators(data["group"])
        if "sortBy" in data:
            for i, entry in enumerate(data["sortBy"]):
                data["sortBy"][i]["sortOrder"] = entry["sortOrder"].upper()

    # Convert the query to JSON.
    data_JSON = json.dumps(data)

    # Submit the request.
    response = get_response(URL = API_URL, method = method, data = data_JSON,
                            headers = headers, delay = 1, max_attempts = 4)

    # Return the type of output specified by "output".

    if output == u"list":
        results_list = \
          json.loads(response.text)["hits"]
        results = results_list

    elif output == u"full_dict":
        results_dict = json.loads(response.text)
        results = results_dict

    else: # The requested output format was "JSON".
        results = response.text

    # If necessary, return the query object as well.  If "output" is
    # JSON, return the query in the form of a JSON object; otherwise
    # return it as a Python dict.

    if return_query:
        if output == u"JSON":
            return results, data_JSON
        else:
            return results, data

    else:
        return results


def search_simple(server = None, recordset = "coalesceentity", field = "name",
           operator = "Like", value = None, values = None, match_case = False,
           return_property_names = ["coalesceentity.name"], page_size = 200,
           page_number = 1, output = "list"):

    """
    Constructs a query with a single criteria set, then calls the "search"
    function using that query.  This mirrors the deprecated simple search
    in the Java API.

    :param server:  A :class:`~pyCoalesce.coalesce_request.CoalesceServer`
        object or the URL of a Coalesce server
    :param recordset:  the recordset that contains the search field
    :param field:  the field to search.  This value is passed directly to
        the appropriate persistor underlying Coalesce, and for most
        persistors, field names are case-insensitive.
    :param operator:  the search operation; valid values can be found in
        the constant :const:`~pyCoalesce.coalesce_request.SEARCH_OPERATORS`.
    :param value:  the value to search for.  Must be a string or number
        (for operators requiring a single argument), an iterable of strings
        or numbers (for operators requiring multiple arguments), or
        ``None`` (for operators that take no arguments).  If an operator
        takes multiple arguments and the "values" argument has been
        supplied, this argument is ignored.
    :param values:  multiple values to search for, as an iterable of
        strings or numbers.  This argument is used only when the search
        operator takes more than one value (see
        :const:`~pyCoalesce.coalesce_request.SEARCH_OPERATORS`).  For such
        operators, the iterable can also be provided as the "value"
        argument.
    :param match_case:  if ``True``, results should match the case of
        "value".  Some of the persistors underlying Coalesce are
        case-insensitive when matching values, regardless of the value of
        "match_case".
    :param return_property_names:  an iterable naming the properties
        (fields) to return for each search result.  The values, each in the
        format `<recordset>.<field>`, are passed directly to the
        appropriate persistor(s) underlying Coalesce, and for most
        persistors, these names are case-insensitive  Note that the
        "entityKey" property is always returned, regardless of the value of
        this argument.
    :param page_size:  the number of results to return
    :param page_number:  used to retrieve results deeper in the list.  For
        example, a query with "page_size" 250 and "page_number" 3 returns
        results 501 to 750.
    :param output:  If this argument is "list", return the results as a
        Python :class:`list`; for "full_dict", return the full response,
        including metadata, as a :class:`dict`.  For "JSON" return the
        full response, unparsed, including metadata, as a Unicode string.

    :returns:  a :class:`list`, :class:`dict`, or JSON object, depending on
        the value of "output".

    """

    # Make sure the operator is cased properly (assuming it's in the
    # pre-defined list--if it's not, an exception will occur later).
    operator = case_operator(operator)

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
            if value:
                criteria[0]["value"] = value
            else:
                raise ValueError("Please supply the value to be searched for.")



        else:

            # If the operator in question takes multiple values, "value"
            # will be either an iterable of values or a string of space-
            # separated values.  Using space-separated values is
            # deprecated in both Coalesce and pyCoalesce, and not included
            # in the documentation for this function.

            if not values:
                if value:
                    values = value
                else:
                    raise ValueError("Please supply the " + str(num_values) +
                                     " values to be searched for.")

            invalid_value_msg = 'The "value" argument for search operator "' + \
                                operator + '" must be an iterable with ' + \
                                'exactly ' + str(num_values) + ' elements, ' + \
                                'or the same number of values as a space-' + \
                                'separated string.'

            if isinstance(value, basestring):
                split_value = value.split(" ")
                if len(split_value) == num_values:
                    criteria[0]["values"] = values
                else:
                    raise ValueError(invalid_value_msg)

            elif len(value) == num_values:
                criteria[0]["values"] = values

            else:
                raise ValueError(invalid_value_msg)

    # If "num_values" is None.
    else:
        raise ValueError('Search operator "' + operator + '" has not been ' +
                         'implemented in "search_simple".')

    # Form the search query.
    query = {
             "operator": "AND",
             "criteria": criteria
            }

    # Call the full search function.  Note that we've already checked the
    # case of one one user-entered operator, and so we can leave that
    # argument at the default False in the call to "search".

    results = search(server = server, query = query,
                     return_property_names = return_property_names,
                     page_size = page_size, page_number = page_number,
                     output = output, check_case = False)

    # Return the results.
    return results


def _test_key(key):
    """
    Determines the format of an input UUID key, and, if necessary,
    transforms the input into a string.  If the input is an iterable or
    JSON array of keys, the function throws an error, thereby serving as a
    test for whether the input is a single key or an iterable.

    :param key:  the key to be tested and transformed into a string

    :returns:  "key" as a string

    """

    key_error_msg = 'The argument "key" must be a UUID key, as an instance ' + \
                     'of the class uuid.UUID, or any string or integer ' + \
                     'that could serve as input for that class\'s class ' + \
                     'constructor.'

    if isinstance(key, basestring):

        try:
            UUID(key)

        except ValueError:
            try:
                key_obj = UUID(bytes = key)

            except ValueError: # "key" is probably a JSON array.
                raise ValueError(key_error_msg)

        else:
            return key

    else:

        key_len = len(unicode(key))

        if key_len == 36:
            key_obj = key

        else:
            try:
                key_obj = UUID(int = key)
            except ValueError: # "key" is probably an iterable of keys.
                raise TypeError(key_error_msg)

    key_str = unicode(key_obj)

    return key_str


def construct_entity(server = None, template = None, key = None, fields = None):
    """
    A convenience function to retrieve a template from the Coalesce server,
    construct an entity using that template, and (optionally) fill any or
    all of the entity's fields with specified values.  Returns an instance
    of :class:`pyCoalesce.classes.coalesce_entity.CoalesceEntity` (which
    can then be submitted to the server with the "create" function),
    including the minimum number of records in each recordset, and a
    template object as an attribute of the entity object.

    This function replaces a "GET" endpoint for the RESTful API's templates
    controller that produces the equivalent result of returning a new
    entity that has not yet been saved in a database.  When the server
    creates such an entity, it assigns keys to fields and other child
    objects, and these keys prevent the entity from being copied on the
    client side in order to create multiple entities from the same template
    without calling the endpoint each time, a problem not shared by
    entities created through this function.  Alternately, supplying a
    :class:`~pyCoalesce.classes.coalesce_entity_template.CoalesceEntityTemplate`
    allows this function to be called multiple times without calling the
    server at all.

    :param server:  a :class:`~pyCoalesce.coalesce_request.CoalesceServer`
        object or the URL of a Coalesce server.  If "template" is an
        instance of
        :class:`~pyCoalesce.classes.coalesce_entity_template.CoalesceEntityTemplate`,
        no server is needed; this is one way of avoiding unnecessary calls
        to the API server when creating multiple entities.
    :param template:  a Coalesce template (UUID) key, an iterable containing
        the template's name, source, and version (in that order), or a
        :class:`~pyCoalesce.classes.coalesce_entity_template.CoalesceEntityTemplate`
        object
    :param key:  a UUID to serve as the key for the new entity, as either
        an instance of the :class:`uuid.UUID` class, or any string or
        integer that could serve as input to the :class:`UUID <uuid.UUID>`
        class constructor.  If this argument is omitted, the server will
        generate a random key when creating the entity.
    :param fields:  a dict-like of fields possessed by the new entity, and
        values to set on those fields.  The keys can be either string
        (ASCII or Unicode) names (in which case the function searches for
        each field, and throws an error if duplicates are found) or path
        lists, alternating between child object type and list index.  The
        values of the dict-like must be the values to be set on the "value"
        attribute of each field--use another method for setting other
        attributes.

    :returns:  an instance of
        :class:`~pyCoalesce.classes.coalesce_entity.CoalesceEntity`

    """

    # Check for a valid key, and if necessary change it into a string.
    if key:
        key_str = _test_key(key)

    # If no key was submitted, pass the None to the build method, which will
    # randomly generate one.
    else:
        key_str = None

    # If we don't have a template already, we'll need to get one.
    if not hasattr(template, "new_entity"):
        template = read_template(server =  server, template = template,
                                 output = "entity_object")

    # Build a new entity from the template.
    new_entity = template.new_entity(key = key_str)

    # If field values have been specified, set the fields in question.
    if fields:
        set_entity_fields(new_entity, fields = fields, match_case = False)

    return new_entity


def save_entity(server = None, entity = None, key = None, operation = u"create"):

    """
    Uploads a new entity or modified entity to the Coalesce server.  The
    "create" and "update" functions for both normal entities and templates
    are wrappers for this function.

    Unlike the wrapper functions, this one always returns the full response
    from the API server.

    :param server:  a :class:`~pyCoalesce.coalesce_request.CoalesceServer`
        object or the URL of a Coalesce server
    :param entity:  the entity to upload to the server.  This can be a JSON
        or XML representation of an entity, a nested dict-like in the same
        format as a JSON respresentation, or an instance of
        :class:'CoalesceEntity <pyCoalesce.classes.CoalesceEntity>' (or a
        subclass); the function automatically detects the input type and
        adjusts the RESTful endpoint and requests headers accordingly.
        In an update ("PUT") operation, ths entity *must* include not only
        the updated field(s), but all sections, recordsets, records, and
        fields in the original, and all of the original's UUID keys (not
        only the entity's keys, but all section, recordset, record, and
        field keys).  Failure to match keys will result in the creation of
        duplicate records within the modified entity.
    :param key:  a UUID to serve as the key for the entity, as either an
        instance of the :class:`uuid.UUID` class, or any string or integer
        that could serve as input to the :class:`UUID <uuid.UUID>` class
        constructor.  Alternately, the key can be contained in "entity";
        however, if "entity" contains a key *and* a key is supplied
        separately (as the "key" argumet), the two keys must match, or an
        error will be raised.  When creating a new regular entity,
        supplying a key (either as part of "entity" or separately) is
        optional:  if no key is supplied, the server generates one
        randomly.  In the case of a new template, the server generates a
        key by hashing the name, source, and version.  Because of this, a
        key should never be supplied for a new template, and it is
        important to note that modifying the name, source, and version of
        a template will change its key, in which case it must be treated as
        a new template (and any entities created from the template will
        remain associated with the old version); submiting such a modified
        template via the update endpoint will result in a server error.
    :param operation:  "create" (POST), "update" (PUT), "create_template"
        (POST), or "update_template" (PUT).

    :returns:  A Python :class:`requests.Response` object.  When an entity
        is created, the server returns the new entity's key (attribute
        "text"), with a status code (attribute "status_code") of 200.  When
        an entity is updated, the server returns a status code of 204.

    """

    # Check the server input, and create a server object if needed.
    if not server:
        raise ValueError('The argument "server" must be a URL or an ' +
                         'instance of CoalesceServer.')
    if isinstance(server, basestring):
        server_obj = CoalesceServer(server)
    else:
        server_obj = server

    # Check for a separately submitted key, check it for validity, and if
    # necessary change it into a string.
    if key:
        key_str = _test_key(key)

    # Check the entity input, and it's a Python object, transform it into
    # a JSON or XML string, and set the corresponding API input format.  If
    # the entity includes a key, extract it.  If a key was supplied as
    # "key" but not within "entity", insert the key into the entity object.

    entity_error_msg = 'The argument "entity" must be a JSON or XML ' + \
                       'of an entity, a nested dict-like in the same ' + \
                       'format as a JSON respresentation, or an instance ' + \
                       'of CoalesceEntity (or a subclass).'

    if entity:

        if isinstance(entity, basestring):

            try:

                entity_dict = json.loads(entity)
                data = entity

                try:
                    entity_key = entity_dict["key"]
                except KeyError:
                    if key:
                        entity_key = key_str
                        entity_dict["key"] = key_str
                        data= json.dumps(entity_dict)

                input_format = "JSON"

            except JSONDecodeError:

                try:
                    entity_dict = xmltodict.parse(entity)

                    try:
                        entity_key = entity_dict["entity"]["@key"]
                        data = entity

                    except:
                        if key:
                            entity_dict["entity"]["@key"] = key_str
                            data = xmltodict.unparse(entity_dict)
                            entity_key = key_str
                        else:
                            data = entity
                            entity_key = None

                except:
                    raise ValueError(entity_error_msg)

                input_format = "XML"

        # Duck test for a "CoalesceEntity" object or similar:  "entity" and
        # its subclasses are the only XSD-based Coalesce objects with a
        # "linkagesection" attribute.  Note that if we have to add a key,
        # we use a copy of "entity", to avoid changing the original object.

        elif hasattr(entity, "linkagesection"):

            if not entity.key and key:
                keyed_entity = copy(entity)
                keyed_entity.key = key_str

            else:
                keyed_entity = entity # No need for a copy in this case

            try:
                data = to_XML_string(keyed_entity)
            except:
                raise TypeError(entity_error_msg)

            entity_key = keyed_entity.key
            input_format = "XML"

        # As above, if we have to add a key, we use a copy of "entity", to
        # avoid changing the original dict.

        else:

            try:
                entity_key = entity["key"]
                data = json.dumps(entity)

            except KeyError:
                if key:
                    entity_key = key_str
                    keyed_entity = copy(entity)
                    keyed_entity["key"] = key_str
                    data = json.dumps(keyed_entity)
                else:
                    entity_key = None
                    data = json.dumps(entity)

            except TypeError:
                raise TypeError(entity_error_msg)

            input_format = "JSON"

    else:
        raise TypeError(entity_error_msg)


    # If the entity includes a key and a key was also supplied separately,
    # make sure that the keys match.
    if key and entity_key and entity_key != key_str:
        raise ValueError('The values of "key" and the key found in the ' +
                         'submitted entity do not match.')

    # Check the operation input.
    if not operation in ENTITY_UPLOAD_OPERATIONS:
        raise ValueError('The argument "operation" must take one of the ' +
                         'values:\n' + unicode(ENTITY_UPLOAD_OPERATIONS))

    # Set the request parameters.  Note that "entity_key" may or may not be
    # endpoint used by "_construct_URL", depending on the pattern set for
    # the in question.  Note also that, since we'll be adding headers to
    # the base headers, we copy the base headers, so that the extra header
    # isn't preserved in the server object.

    try:
        API_URL = _construct_URL(server_obj =  server_obj,
                                 operation = operation, key = entity_key)
    except AttributeError as err:
        raise AttributeError(str(err) + '\n.This error can occur if the ' +
                              'argument "server" is not either a URL or a ' +
                              'CoalesceServer object.')
    method = OPERATIONS[operation][u"method"]
    headers = copy(server_obj.base_headers)

    if input_format == u"JSON":
        headers[u"Content-type"] = "application/json"
    elif input_format == u"XML":
        API_URL += u"/xml"
        headers[u"Content-type"] = "application/xml"
    else: # This shouldn't be possible.
        raise ValueError('"' + input_format + '" is not a valid input format.')

    response = get_response(URL = API_URL, method = method, data = data,
                            headers = headers, delay = 1, max_attempts = 4)

    return response


def create(server = None, entity = None, key = None, full_response = False):

    """
    Uploads a new entity to the Coalesce server using the "PUT" method.
    This is a wrapper for the
    :func:`~pyCoalesce.coalesce_request.save_entity` function.

    :param server:  a :class:`~pyCoalesce.coalesce_request.CoalesceServer`
        object or the URL of a Coalesce server
    :param entity:  the entity to upload to the server.  This can be a JSON
        or XML representation of an entity, a nested dict-like in the same
        format as a JSON respresentation, or an instance of
        :class:`~pyCoalesce.classes.coalesce_entityCoalesceEntity` (or a
        subclass); the function automatically detects the input type and
        adjusts the RESTful endpoint and requests headers accordingly.
    :param key:  a UUID to serve as the key for the entity, as either an
        instance of the :class:`uuid.UUID` class, or any string or integer
        that could serve as input to the :class:`UUID <uuid.UUID>` class
        constructor.  If "entity" already includes a key, this argument
        must match that key, or an error will be raised.  If no key is
        supplied (either in this argument or as part of the entity), the
        server randomly generates one.
    :param full_response:  if ``True``, return the full response from the
        server as a Python :class:`requests.Response` object, rather than
        just the entity key.

    :returns:  the UUID key of the new entity if "full_response" is False.
        If "full_response" is ``True``, a Python :class:`requests.Response`
        object is returned instead.  Regardless of the value of
        "full_response", if the response status code has a value in the
        200's other than 200, the function raises a warning.  (Any value
        outside the 200's will cause an exception.)

    """

    response = save_entity(server = server, entity = entity, key = key,
                             operation = u"create")

    status = response.status_code

    if not full_response:

        new_entity_key = response.text

        if status != 200:
            warn("The API server returned an unexpected status code, " +
                 status + ".  However, the entity might have been created on " +
                 "on the server, or might be created after a delay.",
                 UnexpectedResponseWarning)

        return new_entity_key

    else:
        if status != 200:
            warn("The API server returned an unexpected status code, " +
                 status + ".  However, the entity might have been created on " +
                 "the server, or might be created after a delay.",
                 UnexpectedResponseWarning)
        return response


def read(server = None, key = None, output = "dict"):
    """
    Returns the entity identified by "key" in the requested output format.

    :param server:  a :class:`~pyCoalesce.coalesce_request.CoalesceServer`
        object or the URL of a Coalesce server
    :param key:  a UUID to serve as the key for the entity, as either an
        instance of the :class:`uuid.UUID` class, or any string or integer
        that could serve as input to the :class:`UUID <uuid.UUID>` class
        constructor
    :param output:  If this argument is "dict", return the results as a
        Python :class:`dict`; for "entity_object", return the result as an
        instance of class :class:`~pyCoalesce.classes.coalesce_entity.CoalesceEntity`.
        For "JSON" or "XML" return the full response, unparsed, as a
        Unicode string in the corresponding format.

    :returns:  a :class:`dict`,
        :class:`~pyCoalesce.classes.coalesce_entity.CoalesceEntity`,
        JSON object, or XML object, depending on the value of "output".

    """

    # Set the request parameters.

    if isinstance(server, basestring):
        server_obj = CoalesceServer(server)
    else:
        server_obj = server

    # Check for a valid key, and if necessary change it into a string.
    if key:
        key_str = _test_key(key)

    else:
        raise ValueError("Please specify a UUID key.")

    operation = u"read"
    try:
        API_URL = _construct_URL(server_obj =  server_obj,
                                 operation = operation, key = key_str)
    except AttributeError as err:
        raise AttributeError(str(err) + '\n.This error can occur if the ' +
                              'argument "server" is not either a URL or a ' +
                              'CoalesceServer object.')

    output = output.lower()
    if not output in CRUD_OUTPUT_FORMATS:
        raise ValueError('The argument "output" must take one of the ' +
                         'following values:\n' + str(CRUD_OUTPUT_FORMATS) + '.')
    if output == u"xml" or output == u"entity_object":
        API_URL += "/xml"

    method = OPERATIONS[operation][u"method"]
    headers = server_obj.base_headers

    # Submit the request.
    response = get_response(URL = API_URL, method = method, headers = headers,
                            delay = 1, max_attempts = 4)

    # Return the entity in the format specified by "output".

    if output == u"dict":
        response_dict = json.loads(response.text)
        return response_dict

    elif output == u"entity_object":
        entity_XML = response.text[response.text.index("<entity"):]
        response_entity = parseString(entity_XML, silence = True)
        return response_entity

    else: # The requested output format was "JSON" or "XML".
        return response.text


def update(server = None, entity = None, key = None, full_response = False):
    """
    Uploads a modified entity to the Coalesce server using the "PUT"
    method.  This is a wrapper for the "save_entity" function.

    :param server:  a :class:`~pyCoalesce.coalesce_request.CoalesceServer`
        object or the URL of a Coalesce server
    :param entity:  the content of the entity to be updated on the server.
        This can be a JSON or XML representation of an entity, a nested
        dict-like in the same format as a JSON respresentation, or an
        instance of :class:`~pyCoalesce.classes.coalesce_entity.CoalesceEntity`
        (or a subclass); the function automatically detects the input type
        and adjusts the RESTful endpoint and requests headers accordingly.
        This entity *must* include not only the updated field(s), but all
        sections, recordsets, records, and fields in the original, and all
        of the original's UUID keys (not only the entity's keys, but all
        section, recordset, record, and field keys).  Failure to match keys
        will result in the creation of duplicate records within the
        modified entity.
    :param key:  a UUID to serve as the key for the entity, as either an
        instance of the :class:`uuid.UUID` class, or any string or integer
        that could serve as input to the :class:`UUID <uuid.UUID>` class
        constructor.  If this argument is not supplied, "modified_entity"
        must include a key; if this argument is supplied, it must match any
        key found in "entity", or an error will be raised.
    :param full_response:  if ``True``, return the full response from the
        server as a Python :class:`requests.Response` object, rather than
        a boolean.

    :returns:  a boolean if "full_response" is ``False``:  ``True`` if the
        response status code is 204 (indicating a successful update),
        ``False`` otherwise.  If "full_response" is ``True``, a Python
        :class:`requests.Response` object is returned instead.  Regardless
        of the value of "full_response", if the response status code has a
        value in the 200's other than 204, the function raises a warning.
        (Any value outside the 200's will cause an exception.)

    """

    response = save_entity(server = server, entity = entity, key = key,
                           operation = u"update")

    status = response.status_code

    if not full_response:

        if status == 204:
            return True

        else:
            warn("The API server returned an unexpected status code, " +
                 status + ".  However, the entity might have been modified " +
                 "on the server, or might be modified after a delay.",
                 UnexpectedResponseWarning)
            return False

    else:
        if status != 204:
            warn("The API server returned an unexpected status code, " +
                 status + ".  However, the entity might have been modified " +
                 "on the server, or might be modified after a delay.",
                 UnexpectedResponseWarning)
        return response


def delete(server = None, keys = None):
    """
    Marks an entity or entities as deleted on the server.  Until an entity
    is permanently deleted (an administrative operation, not available
    through the RESTful API), it can still be accessed, but will not turn
    up in search results.

    :param server:  a :class:`~pyCoalesce.coalesce_request.CoalesceServer`
        object or the URL of a Coalesce server
    :param keys:  a UUID key of the entity to be deleted, or an iterable of
        such keys.  Each key can be an instance of the :class:`uuid.UUID`
        class, or any string or integer that could serve as input to the
        :class:`UUID <uuid.UUID>` class constructor.

    :returns:  ``True`` if the returned status code is 204 (indicating a
        successful deletion), ``False`` (with a warning) in the unlikely
        event that the server returns another status code in the 200's.
        (Any value outside the 200's will cause an exception.)

    """

    if isinstance(server, basestring):
        server_obj = CoalesceServer(server)
    else:
        server_obj = server

    # Figure out whether we have one key or an iterable of them, check the
    # validity of each, and transform them into a JSON array.

    if keys:

        # Test for a single key--a list of keys or a JSON array as a string
        # will cause "_test_key" to throw an error.
        try:
            key_str = _test_key(keys)

        except TypeError: # "keys" is probably a list of keys.
            keys_list = [_test_key(key) for key in keys]
            keys_str = json.dumps(keys_list)

        except ValueError: # "keys" is probably a JSON array of keys.
            json.loads(keys) # Make sure that "keys" is valid JSON.
            keys_str = keys

        else:
            keys_str = '["' + key_str + '"]'

    operation = u"delete"
    try:
        API_URL = _construct_URL(server_obj =  server_obj,
                                 operation = operation)
    except AttributeError as err:
        raise AttributeError(str(err) + '\n.This error can occur if the ' +
                              'argument "server" is not either a URL or a ' +
                              'CoalesceServer object.')
    method = OPERATIONS[operation][u"method"]
    headers = copy(server_obj.base_headers)
    headers["Content-type"] = "application/json"

    # Submit the request.
    response = get_response(URL = API_URL, method = method, data = keys_str,
                            headers = headers, delay = 1, max_attempts = 4)

    # Check for the appropriate status code.

    status = response.status_code

    if status == 204:
        success = True

    else:
        warn("The API server returned an unexpected status code, " + status +
             ".  However, the entity might have been deleted on the server, " +
             "or might be deleted after a delay.", UnexpectedResponseWarning)
        success =  False

    return success


def create_template(server = None, template = None, full_response = False):

    """
    Creates an entity template on the Coalesce server.  This function is a
    wrapper for the "save_entity" function.

    Note that the user need not supply a key for the template: the server
    will autogenerate the key as a hash of the template's name, source,
    and version attributes.

    :param server:  a :class:`~pyCoalesce.coalesce_request.CoalesceServer`
        object or the URL of a Coalesce server
    :param template:  the entity template to create on the server.  This
        can be a JSON or XML representation of a template, a nested
        dict-like in the same format as a JSON respresentation, or an
        instance of
        :class:`~pyCoalesce.classes.coalesce_entity_template.CoalesceEntityTemplate`
        (or a subclass); the function automatically detects the input type
        and adjusts the RESTful endpoint and requests headers accordingly.
    :param full_response:  if ``True``, return the full response from the
        server as a Python :class:`requests.Response` object, rather than
        just the entity key.

    :returns:  the UUID key of the new entity, as a string, if
        "full_response" is ``False``.  If "full_response" is ``True``, a
        Python :class:`requests.Response` object is returned instead.
        Regardless of the value of "full_response", if the response status
        code has a value in the 200's other than 200, the function raises a
        warning.  (Any value outside the 200's will cause an exception.)

    """

    response = save_entity(server = server, entity = template,
                           operation = u"create_template")

    status = response.status_code

    if not full_response:

        new_template_key = response.text

        if status != 200:
            warn("The API server returned an unexpected status code, " +
                 status + ".  However, the entity might have been created on " +
                 "the server, or might be created after a delay.",
                 UnexpectedResponseWarning)

        return new_template_key

    else:
        if status != 200:
            warn("The API server returned an unexpected status code, " +
                 status + ".  However, the entity might have been created on " +
                 "the server, or might be created after a delay.",
                 UnexpectedResponseWarning)
        return response


def register_template(server = None, key = None):
    """
    Registers a template that's already been saved, so that it can be used
    to define entities in the Coalesce databases.

    :param server:  a :class:`~pyCoalesce.coalesce_request.CoalesceServer`
        object or the URL of a Coalesce server
    :param key:  the UUID key of the template to be registered, as either
        an instance of the :class:`uuid.UUID` class, or any string or
        integer that could serve as input to the :class:`UUID <uuid.UUID>`
        class constructor
    :returns:  ``True`` if the server returns "true"; ``False`` otherwise.
        A value of ``False`` is unlikely, since any server response outside
        the 200's will cause an exception.

    """

    # Set the request parameters.

    if isinstance(server, basestring):
        server_obj = CoalesceServer(server)
    else:
        server_obj = server

    if not key:
        raise ValueError("Please specify a UUID key.")
    else:
        key_str = _test_key(key)

    operation = u"register_template"
    if isinstance(server, basestring):
        server_obj = CoalesceServer(server)
    else:
        server_obj = server

    try:
        API_URL = _construct_URL(server_obj =  server_obj,
                                 operation = operation, key = key_str)
    except AttributeError as err:
        raise AttributeError(str(err) + '\n.This error can occur if the ' +
                              'argument "server" is not either a URL or a ' +
                              'CoalesceServer object.')
    method = OPERATIONS[operation][u"method"]
    headers = server_obj.base_headers

    # Submit the request.
    response = get_response(URL = API_URL, method = method, headers = headers,
                            delay = 1, max_attempts = 4)

    # Check for the appropriate status code.

    status = response.status_code

    if status == 204:
        success = True

    else:
        warn("The API server returned an unexpected status code, " + status +
             ".  However, the entity might have been deleted on the server, " +
             "or might be deleted after a delay.", UnexpectedResponseWarning)
        success =  False

    return success


def read_template(server = None, template = None, output = "dict"):
    """
    Returns the template in question in the requested output format.

    :param server:  a :class:`~pyCoalesce.coalesce_request.CoalesceServer`
        object or the URL of a Coalesce server
    :param template:  the template's UUID key, as either an instance of the
        :class:`uuid.UUID` class, or any string or integer that could serve
        as input to the :class:`UUID <uuid.UUID>` class constructor; or an
        iterable containing the template's name, source, and version (in
        that order)
    :param output:  If this argument is "dict", return the results as a
        Python :class:`dict`; for "entity_object", return the result as an
        instance of class
        :class:`~pyCoalesce.classes.coalesce_entity_template.CoalesceEntityTemplate`.
        For "JSON" or "XML" return the full response, unparsed, as a
        Unicode string in the corresponding format.

    :returns:  a :class:`dict`,
        :class:`~pyCoalesce.classes.coalesce_entity_template.CoalesceEntityTemplate`,
        JSON object, or XML object, depending on the value of "output".

    """

    # Set the request parameters.

    if isinstance(server, basestring):
        server_obj = CoalesceServer(server)
    else:
        server_obj = server

    if template:

        if len(template) == 3:
            operation = u"read_template_by_nsv"
            URL_kwarg = {"name_source_version": template}

        else:

            try:
                template_key = _test_key(template)

            except ValueError:
                raise ValueError('The argument "template" must be an ' +
                                 'instance of the "uuid.UUID" class, any ' +
                                 'string or integer that could serve as ' +
                                 'input to the "UUID" class constructor, or ' +
                                 'an iterable containing the template\'s ' +
                                 'name, source, and version')

            operation = u"read_template_by_key"
            URL_kwarg = {"key": template_key}

    else:
        raise TypeError('You must supply a value for "template".')

    output = output.lower()
    if not output in CRUD_OUTPUT_FORMATS:
        raise ValueError('The argument "output" must take one of the ' +
                         'following values:\n' + str(CRUD_OUTPUT_FORMATS) + '.')

    # Set the request parameters.
    try:
        API_URL = _construct_URL(server_obj =  server_obj,
                                 operation = operation, **URL_kwarg)
    except AttributeError as err:
        raise AttributeError(str(err) + '\n.This error can occur if the ' +
                              'argument "server" is not either a URL or a ' +
                              'CoalesceServer object.')
    method = OPERATIONS[operation][u"method"]
    headers = server_obj.base_headers
    if output == u"xml" or output == u"entity_object":
        API_URL += "/xml"

    # Submit the request.
    response = get_response(URL = API_URL, method = method, headers = headers,
                            delay = 1, max_attempts = 4)

    # Return the template in the format specified by "output".

    if output == u"dict":
        response_dict = json.loads(response.text)
        return response_dict

    elif output == u"entity_object":
        entity_XML = response.text[response.text.index("<entity"):]
        response_entity = parseString(entity_XML,
                                      object_class = CoalesceEntityTemplate,
                                      silence = True)
        return response_entity

    else: # The requested output format was "JSON" or "XML".
        return response.text


def get_template_list(server = None, output = "list"):
    """
    Retrieves the list of all registered templates from the server.

    :param server:  a :class:`~pyCoalesce.coalesce_request.CoalesceServer`
        object or the URL of a Coalesce server
    :param output:  If this argument is "list", return the results as a
        Python :class:`list` of :class:`dicts <dict>`.  Each :class:`dict`
        includes the UUID key, name, source, version, creation date, and
        last-modified date for a single template.  For "JSON", return the
        response unparsed, as a Unicode JSON object.

    :returns:  a :class:`list` of :class:`dicts <dict>`, or a JSON object,
        depending on the value of "output".

    """

    # Set the request parameters.

    if isinstance(server, basestring):
        server_obj = CoalesceServer(server)
    else:
        server_obj = server

    operation = u"get_template_list"
    try:
        API_URL = _construct_URL(server_obj =  server_obj,
                                 operation = operation)
    except AttributeError as err:
        raise AttributeError(str(err) + '\n.This error can occur if the ' +
                              'argument "server" is not either a URL or a ' +
                              'CoalesceServer object.')

    output = output.lower()
    if not output in TEMPLATE_LIST_OUTPUT_FORMATS:
        raise ValueError('The argument "output" must take one of the ' +
                         'following values:\n' +
                         str(TEMPLATE_LIST_OUTPUT_FORMATS) + '.')

    method = OPERATIONS[operation][u"method"]
    headers = server_obj.base_headers

    # Submit the request.
    response = get_response(URL = API_URL, method = method, headers = headers,
                            delay = 1, max_attempts = 4)

    # Return the template in the format specified by "output".

    if output == u"list":
        response_list = json.loads(response.text)
        return response_list

    else: # The requested output format was "JSON".
        return response.text


def update_template(server = None, template = None, key = None,
                    full_response = False):

    """
    Updates an entity template on the Coalesce server.  This function is a
    wrapper for the "upload_entity" function.

    Note that, since a template key is a hash of the template's name,
    source, and version, modifying any of these values produces a new
    template, with a different key, which must be uploaded via the
    :func:`~pyCoalesce..coalesce_request.create_template` function (and
    any of the entities created from the old template remain associated
    with that one).

    Updating templates--rather than creating a new template, with a
    different version number--may have unpredictable consequences,
    depending on the persistor and its settings (especially caching
    settings). If a template update is unavoidable, a server/container
    restart or other measures may be necessary (for example, in
    Elasticsearch, it may be necessary to delete the index in question).

    :param server:  a :class:`~pyCoalesce.coalesce_request.CoalesceServer`
        object or the URL of a Coalesce server
    :param template:  the content of the entity to be updated on the
        server.  This can be a JSON or XML representation of a template,
        a nested dict-like in the same format as a JSON respresentation, or
        an instance of
        :class:`pyCoalesce.classes.coalesce_entity_template.CoalesceEntityTemplate`
        (or a subclass); the function automatically detects the input type
        and adjusts the RESTful endpoint and requests headers accordingly.
    :param key:  the UUID key of the template to be registered, as either
        an instance of the :class:`uuid.UUID` class, or any string or
        integer that could serve as input to the :class:`UUID <uuid.UUID>`
        class constructor.  If this argument is not supplied, the "template"
        argument must include a key; if this argument is supplied, it must
        match any key found in "template", or an error will be raised.
    :param full_response:  if ``True``, return the full response from the
        server as a Python :class:`requests.Response` object, rather than a
        boolean.

    :returns:  a boolean if "full_response" is ``False``:  ``True`` if the
        response status code is 204 (indicating a successful update),
        ``False`` otherwise.  If "full_response" is ``True``, a Python
        :class:`requests.Response` object is returned instead.  Regardless
        of the value of "full_response", if the response status code has a
        value in the 200's other than 204, the function raises a warning.
        (Any value outside the 200's will cause an exception.)

    """

    response = save_entity(server = server, entity = template, key = key,
                           operation = u"update_template")

    status = response.status_code

    if not full_response:

        if status == 204:
            return True

        else:
            warn("The API server returned an unexpected status code, " +
                 status + ".  However, the entity might have been modified " +
                 "on the server, or might be modified after a delay.",
                 UnexpectedResponseWarning)
            return False

    else:
        if status != 204:
            warn("The API server returned an unexpected status code, " +
                 status + ".  However, the entity might have been modified " +
                 "on the server, or might be modified after a delay.",
                 UnexpectedResponseWarning)
        return response


def delete_template(server = None, key = None):

    """
    Marks the template as deleted on the server.  Until the template is
    permanently deleted (an administrative operation, not available through
    the RESTful API), it can still be accessed, but will not turn up in
    search results.

    For some persistors, template deletes aren't implemented, and
    attempting to delete a template on such a persistor will result in an
    error.  Check the error message to verify that this is what occurred.

    :param server:  a :class:`~pyCoalesce.coalesce_request.CoalesceServer`
        object or the URL of a Coalesce server
    :param key:  the UUID key of the template to be registered, as either
        an instance of the :class:`uuid.UUID` class, or any string or
        integer that could serve as input to the :class:`UUID <uuid.UUID>`
        class constructor.

    :returns:  ``True`` if the returned status code is 200 (which should
        indicate a successful deletion), ``False`` (with a warning) if the
        status code has any other value in the 200's.  (Any value outside
        the 200's will cause an exception.)

    """

    if isinstance(server, basestring):
        server_obj = CoalesceServer(server)
    else:
        server_obj = server

    if not key:
        raise ValueError("Please specify a UUID key.")

    else:
        key_str = _test_key(key)

    operation = u"delete_template"
    if isinstance(server, basestring):
        server_obj = CoalesceServer(server)
    else:
        server_obj = server
    try:
        API_URL = _construct_URL(server_obj =  server_obj,
                                 operation = operation, key = key_str)
    except AttributeError as err:
        raise AttributeError(str(err) + '\n.This error can occur if the ' +
                              'argument "server" is not either a URL or a ' +
                              'CoalesceServer object.')
    method = OPERATIONS[operation][u"method"]
    headers = server_obj.base_headers

    # Submit the request.
    response = get_response(URL = API_URL, method = method, headers = headers,
                            delay = 1, max_attempts = 4)

    # Check for the appropriate status code.

    status = response.status_code

    if status == 204:
        success = True

    else:
        warn("The API server returned an unexpected status code, " + status +
             ".  However, the template might have been deleted on the " +
             "server, or might be deleted after a delay.",
             UnexpectedResponseWarning)
        success =  False

    return success


def _JSONify_linkage_list(linkages):
    """
    Determines the format of an input linkage or iterable of linkages, and
    transforms the input into a JSON object.

    :param linkages:  the linkage or iterable of linkages to be JSONified

    :returns:  the input linkage(s) as a JSON array

    """

    def _test_linkage(linkage):
        """
        Determines the format of an input linkage(s), and, if necessary,
        transforms the input into a JSON object.  If the input is an
        iterable of linkages, it may require further processing.

        :param linkage:  the linkage(s) to be tested and JSONified

        :returns:  "linkage" as a JSON object, and a string indicating the
            format of "linkage".

        """

        link_error_msg = 'The argument "linkages" must be a Coalesce ' + \
                         'linkage, in the form or an XML or JSON entity, a ' + \
                         'dict in the same format as a JSON linkage, an ' + \
                         'instance of CoalesceLinkage (or a subclass), or ' + \
                         'an instance of CoalesceAPILinkage (or a ' + \
                         'subclass); or an iterable of such linkages.'

        if isinstance(linkage, basestring):

           # Is "linkages" a JSON object?
            try:
                decoded_linkage = json.loads(linkage)

            except JSONDecodeError:

                # Is "linkages" a single XML linkage?
                try:
                    linkage_XSD = parseString(linkage, silence = True)
                except:
                    raise ValueError(link_error_msg)
                else:
                    parsed_linkage = json.dumps(linkage_XSD.to_API())
                    linkage_format = "XML"

            else: # If "linkages" is a JSON object...

                # Is it is an array?
                if linkage[0] == u"[":

                    # If it's an array, the array element are probably JSON
                    # objects--that is, what you get when you serialize
                    # Python dicts.  However, it's possible (if an edge
                    # case) that the elements are XML objects, and will
                    # thus need further processing.
                    inside_the_link = linkage[1:].strip()
                    if inside_the_link[0] == u"{":
                        parsed_linkage = linkage
                        linkage_format = "JSON_array"
                    else:
                        parsed_linkage = decoded_linkage
                        linkage_format = "list"

                elif linkage[0] == u"{":
                    parsed_linkage = linkage
                    linkage_format = "JSON"

                else:
                    raise ValueError(link_error_msg)

        # Duck test for a "CoalesceLinkage" object
        else:
            try:
                parsed_linkage = json.dumps(linkage.to_API())

            # If none of the above worked, this is probably a Python object
            # that's JSON-serializable (a category that includes
            # "CoalesceAPILinkage").
            except AttributeError:
                try:
                    parsed_linkage = json.dumps(linkage)
                    if parsed_linkage[0] == u"{":
                        linkage_format = "dict"
                    else:
                        if parsed_linkage[1] == u"{":
                            linkage_format = "JSON_array"
                        else:
                            linkage_format = "list"
                except TypeError:
                    raise TypeError(link_error_msg)

            else:
                linkage_format = "XSD"

        return parsed_linkage, linkage_format


    def _linkage_to_JSON(linkage, linkage_format):
        """
        Transforms the input linkage into a JSON object.

        :param linkage:  the linkage to be JSONified
        :param linkage_format:  a string indicating the type of object of
            which "linkage" is an instance.  Possible values are "XML" and
            "XSD".

        :return:  the linkage as a JSON object

        """

        if linkage_format == "XML":
            linkage_XSD = parseString(linkage, silence = True)

        elif linkage_format == "XSD":
            linkage_XSD = linkage

        else:
            raise ValueError('"' + str(linkage_format) + '" is not a valid ' +
                             'linkage format.')

        linkage_JSON = json.dumps(linkage_XSD.to_API())

        return linkage_JSON


    # Note that "_test_linkage" will throw an error if "linkages" is an
    # iterable that can't be JSON-serialized--that is, an iterable of
    # CoalesceLinkage objects.
    try:
        parsed_linkages, linkage_format = _test_linkage(linkages)
    except TypeError:
        parsed_linkages = linkages
        linkage_format = "list"

    # If "parsed_linkages" is a JSON_array, we don't need to parse it.
    if linkage_format ==  "JSON_array":
        linkage_array = parsed_linkages

    # If "parsed_linkages" is a list (or similar iterable), we need to test
    # the format of the first linkage, and then iterate through the rest
    # and parse each one.
    elif linkage_format == "list":
        linkage_JSON, linkage_format = _test_linkage(linkages[0])
        linkage_array = "[" + linkage_JSON
        for linkage in linkages[1:]:
            linkage_JSON = _linkage_to_JSON(linkage, linkage_format)
            linkage_array += ", " + linkage_JSON
        linkage_array += "]"

    # Otherwise, "parsed_linkages" is a single linkage, which has already
    # been JSONified.  We need to wrap it in brackets to make it an array.
    else:
        linkage_array = "[" + parsed_linkages + "]"
        return linkage_array

    return linkage_array


def create_linkages(server = None, linkages = None):
    """
    Creates one or more linkages between Coalesce entities.  Linkages must
    be submitted in a special format used only for this endpoint--it
    corresponds to the Java GraphLink class, and the
    :class:`pyCoalesce.classes.coalesce_json.CoalesceAPILinkage` class.
    This format's keys match the attributes in the GraphLink class, which
    correspond to a subset of the full set of keys/attributes in the entity
    model, but which use different names.

    :param server:  a :class:`~pyCoalesce.coalesce_request.CoalesceServer`
        object or the URL of a Coalesce server
    :param linkages:  a Coalesce linkage, in the form XML or JSON entity, a
        :class:`dict` in the same format as a JSON linkage, an instance of
        :class:`pyCoalesce.classes.coalesce_entity.CoalesceLinkage` (or a
        subclass), or an instance of
        :class:`~pyCoalesce.classes.coalesce_json.CoalesceAPILinkage` (or a
        subclass); or an iterable (or JSON array) of such linkage objects,
        all of the same type

    :returns:  ``True`` if the linkages are created successfully (status
        code 204), ``False`` (with a warning) if the status code has any
        other value in the 200's.  (Any value outside the 200's will cause
        an exception.)

    """

    # Set the request parameters.

    if isinstance(server, basestring):
        server_obj = CoalesceServer(server)
    else:
        server_obj = server

    operation = u"create_linkages"
    try:
        API_URL = _construct_URL(server_obj =  server_obj,
                                 operation = operation)
    except AttributeError as err:
        raise AttributeError(str(err) + '\n.This error can occur if the ' +
                              'argument "server" is not either a URL or a ' +
                              'CoalesceServer object.')

    linkage_array = _JSONify_linkage_list(linkages)

    method = OPERATIONS[operation][u"method"]
    headers = copy(server_obj.base_headers)
    headers["Content-type"] = "application/json"

    # Submit the request.
    response = get_response(URL = API_URL, data = linkage_array,
                            method = method, headers = headers, delay = 1,
                            max_attempts = 4)

    status = response.status_code

    if status == 204:
        success = True

    else:
        warn("The API server returned an unexpected status code, " + status +
             ".  However, the linkages might have been created on the " +
             "server, or might be created after a delay.",
             UnexpectedResponseWarning)
        success =  False

    return success


def read_linkages(server = None, key = None, output = "dict_list"):
    """
    Retrieves all of the linkages for the Coalesce entity with the UUID key
    matching "key".  Linkages are returned in a special format used only
    for this endpoint--it corresponds to the Java GraphLink class, and the
    :class:`pyCoalesce.classes.coalesce_json.CoalesceAPILinkage` class.
    This format's keys match the attribues in the GraphLink class, which
    correspond to a subset of the full set of keys/attributes in the entity
    model, but which use different names.

    :param server:  a :class:`~pyCoalesce.coalesce_request.CoalesceServer`
        object or the URL of a Coalesce server
    :param key:  the UUID key of the entity whose linkages are to be
        returned, as either an instance of the :class:`uuid.UUID` class, or
        any string or integer that could serve as input to the
        :class:`UUID <uuid.UUID>` class constructor.
    :param output:  If this argument is "JSON", return the results as a
        JSON array (string).  If the argument is "dict_list" or "API_list",
        return the results as a :class:`list` of :class:`dicts <dict>` or
        instances of
        :class:`~pyCoalesce.classes.coalesce_json.CoalesceAPILinkage`,
        respectively.

    :returns:  The entity's linkages, as either a JSON array (string), or a
        :class:`list` of :class:`dicts <dict>` or
        :class:`~pyCoalesce.classes.coalesce_json.CoalesceAPILinkage`
        objects.

    """

    # Set the request parameters.

    if isinstance(server, basestring):
        server_obj = CoalesceServer(server)
    else:
        server_obj = server

    key_str = _test_key(key)

    operation = u"read_linkages"
    try:
        API_URL = _construct_URL(server_obj =  server_obj,
                                 operation = operation, key = key_str)
    except AttributeError as err:
        raise AttributeError(str(err) + '\n.This error can occur if the ' +
                              'argument "server" is not either a URL or a ' +
                              'CoalesceServer object.')

    method = OPERATIONS[operation][u"method"]
    headers = server_obj.base_headers

    output = output.lower()
    if not output in LINK_CRUD_OUTPUT_FORMATS:
        raise ValueError('The argument "output" must take one of the ' +
                         'following values:\n' + str(LINK_CRUD_OUTPUT_FORMATS) +
                         '.')

    # Submit the request.
    response = get_response(URL = API_URL, method = method, headers = headers,
                            delay = 1, max_attempts = 4)

    # Return the type of output specified by "output".

    if output == u"json":
       return response.text

    else:
        linkage_list = json.loads(response.text)

        if output == u"dict_list":
            return linkage_list

        else: # The requested output format was "API_list".
            API_linkage_list = [CoalesceAPILinkage.from_dict(linkage) for
                                linkage in linkage_list]

            return API_linkage_list


def delete_linkages(server = None, linkages = None):
    """
    Marks one or more linkages between Coalesce entities for deletion.
    Linkages must be submitted in a special format used only for this
    endpoint--it corresponds to the Java GraphLink class, and the
    :class:`pyCoalesce.classes.coalesce_json.CoalesceAPILinkage` class.
    This format's keys match the attribues in the GraphLink class, which
    correspond to a subset of the full set of keys/attributes in the entity
    model, but which use different names.

    :param server:  a :class:`~pyCoalesce.coalesce_request.CoalesceServer`
        object or the URL of a Coalesce server
    :param linkages:  a Coalesce linkage, in the form XML or JSON entity, a
        :class:`dict` in the same format as a JSON linkage, an instance of
        :class:`pyCoalesce.classes.coalesce_entity.CoalesceLinkage` (or a
        subclass), or an instance of
        :class:`~pyCoalesce.classes.coalesce_json.CoalesceAPILinkage` (or a
        subclass); or an iterable (or JSON array) of such linkage objects,
        all of the same type

    :returns:  True if the linkages are deleted successfully (status code
        204), ``False`` (with a warning) if the status code has any other
        value in the 200's.  (Any value outside the 200's will cause an
        exception.)

    """

    # Set the request parameters.

    if isinstance(server, basestring):
        server_obj = CoalesceServer(server)
    else:
        server_obj = server

    operation = u"delete_linkages"
    try:
        API_URL = _construct_URL(server_obj =  server_obj,
                                 operation = operation)
    except AttributeError as err:
        raise AttributeError(str(err) + '\n.This error can occur if the ' +
                              'argument "server" is not either a URL or a ' +
                              'CoalesceServer object.')

    linkage_array = _JSONify_linkage_list(linkages)

    method = OPERATIONS[operation][u"method"]
    headers = copy(server_obj.base_headers)
    headers["Content-type"] = "application/json"

    # Submit the request.
    response = get_response(URL = API_URL, data = linkage_array,
                            method = method, headers = headers, delay = 1,
                            max_attempts = 4)

    status = response.status_code

    if status == 204:
        success = True

    else:
        warn("The API server returned an unexpected status code, " + status +
             ".  However, the linkages might have been deleted on the " +
             "server, or might be deleted after a delay.",
             UnexpectedResponseWarning)
        success =  False

    return success
