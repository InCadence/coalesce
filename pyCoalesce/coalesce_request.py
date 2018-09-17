# -*- coding: utf-8 -*-
"""
@author: dvenkat & sorr
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
from classes import URL, CoalesceEntity, CoalesceEntityTemplate, parseString, \
                    to_XML_string, set_entity_fields, CoalesceLinkage, \
                    CoalesceAPILinkage
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
        u"delete" : {u"endpoint": u"entity", u"method": u"delete",
                     u"final": "{key}"},
        u"create_template" : {u"endpoint": u"templates", u"method": u"post"},
        u"register_template" : {u"endpoint": u"templates", u"method": u"put",
                                u"final": "$key/register"},
        u"update_template" : {u"endpoint": u"templates", u"method": u"put",
                              u"final": "$key"},
        u"read_template_by_key": {u"endpoint": u"templates", u"method": u"get",
                                  u"final": "$key"},
        u"read_template_by_nsv": {u"endpoint": u"templates", u"method": u"get",
                                  u"final": "$name/$source/$version"},
        u"get_template_list": {u"endpoint": u"templates", u"method": u"get",
                               u"final": "$key"},
        u"get_new_entity" : {u"endpoint": u"templates", u"method": u"get"},
        u"delete_template" : {u"endpoint": u"templates", u"method": u"delete"},
        u"create_linkages" : {u"endpoint": u"linkage", u"method": u"put"},
        u"read_linkages" : {u"endpoint": u"linkage", u"method": u"get",
                            u"final": "$key"},
        u"delete_linkages" : {u"endpoint": u"linkage", u"method": u"delete"},
    }
ENTITY_UPLOAD_OPERATIONS = (u"create", u"update", u"save_template")

# Eventually, this hashmap should be available through the "property" API, at
# which point it can be downloaded rather than hard-coded.  However, hard-
# coding may still be necessary to specify the proper number of arguments.
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

SEARCH_OUTPUT_FORMATS = (u"json", u"list", u"full_dict")
CRUD_OUTPUT_FORMATS = (u"json", u"xml", u"dict", u"entity_object")
TEMPLATE_LIST_OUTPUT_FORMATS = (u"json", u"list")
LINK_CRUD_OUTPUT_FORMATS = (u"json", u"dict_list", u"API_list")


class UnexpectedResponseWarning(Warning):
    """
    This warning is used to indicate that an API operation has returned
    an unexpected status code.
    """
    pass


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
            raise TypeError('The argument "search_persistor" must be an ASCII ' +
                            'or Unicode string.')

        self.persistors = {u"CRUD": CRUD_persistor, u"search": search_persistor}

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


def _construct_URL(server_obj = None, operation = "read", key = None,
                   name_source_version = None):
    """
    A helper function to construct the RESTful API endpoint from the
    configuration constants.  This function doesn't handle the final
    "json" or "xml" element of some endpoints--since that element changes
    the format of the server's response, it's best to handle it within
    the calling functions, which also parse the response.

    :param server:  a CoalesceServer object
    :param operation:  one of the keys in the constant "OPERATIONS"
    :param name_source_version:  an entity's name, source, and version (in
        that order), as an iterable

    :return:  a URL as a Unicode string
    """

    operation_endpoint = ENDPOINTS[OPERATIONS[operation][u"endpoint"]]
    endpoint_str = server_obj.persistors[operation_endpoint[u"persistor"]] + \
                   operation_endpoint[u"controller"]
    API_URL = server_obj.URL + endpoint_str


    # If there's a "final" segement, add it to the URL, substituting values
    # as approriate.

    if "final" in operation:

        final_template = Template(operation["final"])
        sub_dict = {}

        if key:
            sub_dict["key"] = key

        if name_source_version:
            sub_dict["name"] = name_source_version[0]
            sub_dict["source"] = name_source_version[0]
            sub_dict["version"] = name_source_version[0]

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
        For the most part, this query must follow the Coalesce format
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


    # Set the request parameters.  We use a copy of the base headers so that
    # the header added here isn't preserved in the server object.

    if isinstance(server, CoalesceServer):
        server_obj = server
    else:
        server_obj = CoalesceServer(server)

    output = output.lower()
    if not output in SEARCH_OUTPUT_FORMATS:
        raise ValueError('The argument "output" must take one of the ' +
                         'following values:\n' + str(SEARCH_OUTPUT_FORMATS) + '.')
    operation = u"search"
    API_URL = _construct_URL(server_obj =  server_obj, operation = operation)
    method = OPERATIONS[operation][u"method"]
    headers = copy(server_obj.base_headers)
    headers["Content-type"] = "application/json"


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
    response = get_response(URL = API_URL, method = method, data = data_json,
                            headers = headers, delay = 1, max_attempts = 4)

    # Return the type of output specified by "output".

    if output == u"list":
        results_list = \
          json.loads(response.text)["hits"]
        return results_list

    elif output == u"full_dict":
        results_dict = json.loads(response.text)
        return results_dict

    else: # The requested output format was "JSON".
        return response.text


def search_simple(server = None, recordset = "coalesceentity", field = "name",
           operator = "Like", value = None, match_case = False,
           property_names = ["coalesceentity.name"], page_size = 200,
           page_number = 1, output = "list"):

    """
    Constructs a query with a single criteria set, then calls the "search"
    function using that query.  This mirrors the deprecated simple search in
    the Java API.

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

    # Make sure the operator is in the predefined list.
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

    # Call the full search function.
    results = search(server = server, query = query,
                     property_names = property_names, page_size = page_size,
                     page_number = page_number, output = output,
                     check_case = True)

    # Return the results.
    return results


def _test_key(key):
    """
    Determines the format of an input UUID key, and, if necessary, transforms
    the input into a string.  If the input is an iterable or JSON array of
    keys, the function throws an error, thereby serving as a test for whether
    the input is a single key or an iterable.

    :param key:  the key to be tested and transformed into a string

    :return:  "key" as a string
    """

    key_error_msg = 'The argument "key" must be a UUID key, as an instance of ' + \
                     'the class uuid.UUID, or any string or integer that ' + \
                     'could serve as input for that class\'s .'

    if isinstance(key, UUID):
        key_obj = key

    else:
        try:
            UUID(key)

        except AttributeError:
            key_obj = UUID(int = key)

        except ValueError:
            try:
                key_obj = UUID(bytes = key)

            except ValueError:
                if isinstance(key, basestring): # "key" is probably a JSON array.
                    raise ValueError(key_error_msg)
                else: # "key" is probably an iterable of keys.
                    raise TypeError(key_error_msg)

        else:
            return key

    key_str = unicode(key_obj)

    return key_str


def construct_entity(server = None, template = None, key = None, fields = None):
    """
    A convenience function to retreive a template from the Coalesce server,
    construct an entity using that template, and (optionally) fill any or
    all of the entity's fields with specified values.  Return an instance of
    CoalesceEntity (which can then be submitted to the server with the
    "create" function), including the minimum number of records in each
    recordset, and a template object as an attribute of the entity object.

    This function replaces a "GET" endpoint for the RESTful API's templates
    controller that produces the equivalent result of returning a new entity
    that has not yet been saved in a database.  When the server creates such
    an entity, it assigns keys to fields and other child objects, and these
    keys prevent the entity from being copied on the client side in order to
    create multiple entities from the same template without calling the
    endpoint each time, a problem not shared by entities created through this
    function.

    :param server:  a CoalesceServer object or the URL of a Coalesce server.
        If "template" is an instance of CoalesceEntityTemplate, no server is
        needed.
    :param template:  a Coalesce template (UUID) key, an iterable containing
        the template's name, source, and version (in that order), or a
        CoalesceEntityTemplate object
    :param key:  a UUID to serve as the key for the new entity, either as an
        instance of the "UUID" class, or as any string or integer that could
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

    # Check for a valid key, and if necessary change it into a string.
    if key:
        key_str = _test_key(key)

    # If no key was submitted, pass the None to the build method, which will
    # randomly generate one.
    else:
        key_str = None

    # If we don't have a template already, we'll need to get one.
    if not isinstance(template, CoalesceEntityTemplate):
        template = read_template(server_obj =  server, template = template,
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

    Arguments:
    :param server:  a CoalesceServer object or the URL of a Coalesce server
    :param entity:  the entity to upload to the server.  This can be a JSON or
        XML representation of an entity, a nested dict-like in the same format
        as a JSON respresentation, or an instance of CoalesceEntity (or a
        subclass); the function automatically detects the input type and
        adjusts the RESTful endpoint and requests headers accordingly.
    :param key:  a UUID to serve as the key for the entity, either as an
        instance of the "UUID" class, or as any string or integer that could
        serve as input to the UUID class constructor.  Alternately, the key
        can be contained in "entity"; however, if "entity" contains a
        key _and_ a key is supplied separately (as the "key" argumet), the
        two keys must match, or an error will be raised.  When creating a new
        regular entity, supplying a key (either as part of "entity" or
        separately) is optional:  if no key is supplied, the server generates
        one randomly.  In the case of a new template, the server generates a
        key by hashing the name, source, and version.  Because of this, a key
        should never be supplied for a new template, and it is important to
        note that modifying the name, source, and version of a template will
        change its key, in which case it must be treated as a new template
        (and any entities created from the template will remain associated
        with the old version); submiting such a modified template via the
        update endpoint will result in a server error.
    :param operation:  "create" (POST), "update" (PUT), "create_template"
        (POST), or "update_template" (PUT).

    return:  A Python requests Response object.  When an entity is created,
        the server returns the new entity's key (attribute "text"), with a
        status code (attribute "status_code") of 200.  When an entity is
        updated, the server returns a status code of 204.
    """

    # Check the server input, and create a server object if needed.
    if not server:
        raise ValueError('The argument "server" must be a URL or an ' +
                         'instance of CoalesceServer.')
    elif isinstance(server, CoalesceServer):
        server_obj = server
    else:
        server_obj = CoalesceServer(server)

    # Check for a separately submitted key, check it for validity, and if
    # necessary change it into a string.
    if key:
        key_str = _test_key(key)

    # Check the entity input, and it's a Python object, transform it into
    # a JSON or XML string, and set the corresponding API input format.  If
    # the entity includes a key, extract it.  If a key was supplied as "key"
    # but not within "entity", insert the key into the entity object (this is
    # necessary for a "create" operation).

    entity_error_msg = 'The argument "new_entity" must be a ' + \
                       'JSON or XML representation of an entity, a nested ' + \
                       'dict-like in the same format as a JSON ' + \
                       'respresentation, or an instance of CoalesceEntity (or ' + \
                       'a subclass).'

    if entity:

        if isinstance(entity, basestring):

            try:
                entity_dict = json.loads(entity)
                data = entity
                input_format = "JSON"
                try:
                    entity_key = entity_dict.key
                except KeyError:
                    if key:
                        entity_dict.key = key_str
                        entity_key = key_str

            except JSONDecodeError:
                try:
                    entity_dict = xmltodict.parse(entity).values()[0]
                    input_format = "XML"
                    try:
                        entity_key = entity_dict["entity"]["@key"]
                        data = entity
                    except:
                        if key:
                            entity_dict["entity"]["@key"] = key_str
                            data = xmltodict.unparse(entity_dict)
                            entity_key = key_str

                except:
                    raise ValueError(entity_error_msg)

        elif isinstance(entity, CoalesceEntity):
            input_format = "XML"
            if not entity.key and key:
                    entity.key = key_str
            data = to_XML_string(entity)
            entity_key = entity.key

        else:
            try:
                input_format = "JSON"
                entity_key = entity["key"]
            except KeyError:
                if key:
                    entity["key"] = key_str
                    entity_key = key_str
                else:
                    entity_key = None
            except TypeError:
                raise TypeError(entity_error_msg)
            else:
                 data = json.dumps(entity)

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
    # used by "_construct_URL", depending on the pattern set for the endpoint
    # in question.  Note also that, since we'll be adding headers to the base
    # headers, we copy the base headers, so that the extra header isn't
    # preserved in the server object.

    API_URL = _construct_URL(server_obj =  server_obj, operation = operation,
                                 key = entity_key)
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
    Uploads a new entity to the Coalesce server using the "PUT" method.  This
    is a wrapper for the "upload_entity" function.

    Arguments:
    :param server:  a CoalesceServer object or the URL of a Coalesce server
    :param entity:  the entity to upload to the server.  This can be a JSON or
        XML representation of an entity, a nested dict-like in the same format
        as a JSON respresentation, or an instance of CoalesceEntity (or a
        subclass); the function automatically detects the input type and
        adjusts the RESTful endpoint and requests headers accordingly.
    :param key:  a UUID to serve as the key for the new entity, either as an
        instance of the "UUID" class, or any string or integer that could
        serve as input to the UUID class constructor.  If "entity" already
        includes a key, this argument must match that key, or an error will be
        raised.  If no key is supplied (either in this argument or as part of
        the entity), the server randomly generates one.
    :param full_response:  if True, return the full response from the server
        as a Python requests Response object, rather than a boolean.

    return:  the UUID key of the new entity if "full_response" is False.  If
        "full_response" is True, a Python requests Response object is returned
        instead.  Regardless of the value of "full_response", if the response
        status code has a value in the 200's other than 200, the function
        raises a warning.  (Any value outside the 200's will raise an
        exception.)
    """

    response = save_entity(server = server, entity = entity, key = key,
                             operation = u"create")

    status = response.status_code

    if not full_response:

        new_entity_key = response.text

        if status != 200:
            warn("The API server returned an unexpected status code, " + status +
                 ".  However, the entity might have been created on the server, " +
                 "or might be created after a delay.", UnexpectedResponseWarning)

        return new_entity_key

    else:
        if status != 200:
            warn("The API server returned an unexpected status code, " + status +
                 ".  However, the entity might have been created on the server, " +
                 "or might be created after a delay.", UnexpectedResponseWarning)
        return response


def read(server = None, key = None, output = "dict"):
    """
    :param server:  a CoalesceServer object or the URL of a Coalesce server
    :param key:  the UUID key assigned to the entity, either as an instance
        of the "UUID" class, or any string or integer that could serve as
        input to the UUID class constructor
    :param output:  If this argument is "dict", return the results as a
        Python dict; for "entity_object", return the result as an
        instance of class "CoalesceEntity".  For "JSON" or "XML" return the
        full response, unparsed, as a Unicode string in the corresponding
        format.

    :return:  a dict, CoalesceEntity, JSON object, or XML object, depending
        on the value of "output".
    """

    # Set the request parameters.

    if isinstance(server, CoalesceServer):
        server_obj = server
    else:
        server_obj = CoalesceServer(server)

    # Check for a valid key, and if necessary change it into a string.
    if key:
        key_str = _test_key(key)

    else:
        raise ValueError("Please specify a UUID key.")

    operation = u"read"
    API_URL = _construct_URL(server_obj =  server_obj, operation = operation,
                             key = key_str)

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
    Uploads a modified entity to the Coalesce server using the "POST" method.
    This is a wrapper for the "upload_entity" function.

    Arguments:
    :param server:  a CoalesceServer object or the URL of a Coalesce server
    :param entity:  the content of the entity to be updated on the server.
        This can be a JSON or XML representation of an entity, a nested dict-
        like in the same format as a JSON respresentation, or an instance of
        CoalesceEntity (or a subclass); the function automatically detects the
        input type and adjusts the RESTful endpoint and requests headers
        accordingly.
    :param key:  the UUID key of the entity to be updated, either as an
        instance of the "UUID" class, or any string or integer that could
        serve as input to the UUID class constructor.  If this argument is
        not supplied, "modified_entity" must include a key; if this argument
        is supplied, it must match any key found in "entity", or an error will
        be raised.
    :param full_response:  if True, return the full response from the server
        as a Python requests Response object, rather than a boolean.

    return:  a boolean if "full_response" is False:  True if the response
        status code is 204 (indicating a successful update), False otherwise.
        If "full_response" is True, a Python requests Response object is
        returned instead.  Regardless of the value of "full_response", if the
        response status code has a value in the 200's other than 204, the
        function raises a warning.  (Any value outside the 200's will raise an
        exception.)
    """

    response = save_entity(server = server, entity = entity, key = key,
                           operation = u"update")

    status = response.status_code

    if not full_response:

        if status == 204:
            return True

        else:
            warn("The API server returned an unexpected status code, " + status +
                 ".  However, the entity might have been modified on the " +
                 "server, or might be modified after a delay.",
                 UnexpectedResponseWarning)
            return False

    else:
        if status != 204:
            warn("The API server returned an unexpected status code, " + status +
                 ".  However, the entity might have been modified on the " +
                 "server, or might be modified after a delay.",
                 UnexpectedResponseWarning)
        return response


def delete(server = None, keys = None):
    """
    Marks an entity or entities as deleted on the server.  Until an entity is
    permanently deleted (an administrative operation, not available through
    the RESTful API), it can still be accessed, but will not turn up in
    search results.

    :param server:  A CoalesceServer object or the URL of a Coalesce server
    :param keys:  a UUID key of the entity to be deleted, or an iterable of
        such keys.  Eech key can be an instance of the uuid.UUID class, or any
        string or integer that could serve as input to the UUID class
        constructor.

    :return:  True if the returned status code is 204 (indicating a successful
        deletion), False (with a warning) in the unlikely event that the
        server returns another status code in the 200's.  (Any value outside
        the 200's will raise an exception.)
    """

    if isinstance(server, CoalesceServer):
        server_obj = server
    else:
        server_obj = CoalesceServer(server)

    # Figure out whether we have one key or an iterable of them, check the
    # validity of each, and transform them into a JSON array.

    if keys:

        # Test for a single key--a list of keys or a JSON array as a string
        # will cause "_test_key" to throw an error.
        try:
            key_str = _test_key(keys)

        except TypeError: # "keys" is probably a list of keys.
            keys_str = "[" + _test_key(keys[0])
            for key in keys[1:]:
                keys_str += ", " + _test_key(key)
            keys_str += "]"

        except ValueError: # "keys" is probably a JSON array of keys.
            json.loads(keys) # A test to make sure "keys" is valid JSON.
            keys_str = keys

        else:
            keys_str = "[" + key_str + "]"

    operation = u"delete"
    API_URL = _construct_URL(server_obj =  server_obj, operation = operation,
                             key = key_str)
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

    Arguments:
    :param server:  a CoalesceServer object or the URL of a Coalesce server
    :param template:  the entity template to create on the server.  This can
        be a JSON or XML representation of a temaplte, a nested dict-like in
        the same format as a JSON respresentation, or an instance of
        CoalesceEntityTemplate (or a subclass); the function automatically
        detects the input type and adjusts the RESTful endpoint and requests
        headers accordingly.
    :param full_response:  if True, return the full response from the server
        as a Python requests Response object, rather than a boolean.

    return:  the UUID key of the new entity if "full_response" is False.  If
        "full_response" is True, a Python requests Response object is returned
        instead.  Regardless of the value of "full_response", if the response
        status code has a value in the 200's other than 200, the function
        raises a warning.  (Any value outside the 200's will raise an
        exception.)
    """

    response = save_entity(server = server, entity = template,
                           operation = u"create_template")

    status = response.status_code

    if not full_response:

        new_template_key = response.text

        if status != 200:
            warn("The API server returned an unexpected status code, " + status +
                 ".  However, the entity might have been created on the server, " +
                 "or might be created after a delay.", UnexpectedResponseWarning)

        return new_template_key

    else:
        if status != 200:
            warn("The API server returned an unexpected status code, " + status +
                 ".  However, the entity might have been created on the server, " +
                 "or might be created after a delay.", UnexpectedResponseWarning)
        return response


def register_template(server = None, key = None):
    """
    Registers a template that's already been saved, so that it can be used
    to define entities in the Coalesce databases.

    :param server:  A CoalesceServer object or the URL of a Coalesce server
    :param key:  The UUID of the entity to be registered

    :return:  True if the server returns "true"; false otherwise.  A value of
        False is unlikely, since any server response outside the 200's will
        raise an exception.
    """

    # Set the request parameters.

    if isinstance(server, CoalesceServer):
        server_obj = server
    else:
        server_obj = CoalesceServer(server)

    if not key:
        raise ValueError("Please specify a UUID key.")
    else:
        key_str = _test_key(key)

    operation = u"register_template"
    API_URL = _construct_URL(server_obj =  server_obj, operation = operation,
                             key = key_str)
    method = OPERATIONS[operation][u"method"]
    headers = server_obj.base_headers

    # Submit the request.
    response = get_response(URL = API_URL, method = method, headers = headers,
                            delay = 1, max_attempts = 4)

    # Convert the response to a boolean.
    try:
        success = json.loads(response.text)
    except JSONDecodeError:
        success = False
        warn("The server returned the following message:\n" + response.text,
             UnexpectedResponseWarning)

    return success


def read_template(server = None, template = None, output = "dict"):
    """
    :param server:  a CoalesceServer object or the URL of a Coalesce server
    :param template:  a Coalesce template (UUID) key, or an iterable
        containing the template's name, source, and version (in that order)
    :param output:  If this argument is "dict", return the results as a
        Python dict; for "entity_object", return the result as an instance of
        class "CoalesceEntityTemplate".  For "JSON" or "XML" return the full
        response, unparsed, as a Unicode string in the corresponding format.

    :return:  a dict, CoalesceEntity, JSON object, or XML object, depending
        on the value of "output".
    """

    # Set the request parameters.

    if isinstance(server, CoalesceServer):
        server_obj = server
    else:
        server_obj = CoalesceServer(server)

    if template:

        if len(template) == 3:
            operation = u"read_template_by_nsv"
            URL_kwarg = {"name_source_version": template}

        else:

            if isinstance(template, UUID):
                template_key_obj = template

            else:
                try:
                    template_key_obj = UUID(template)
                except AttributeError:
                    template_key_obj = UUID(int = template)
                except ValueError:
                    template_key_obj = UUID(bytes = template)

            operation = u"read_template_by_nsv"
            template_key = unicode(template_key_obj)
            URL_kwarg = {"key": template_key}

    else:
        raise TypeError('You must supply a value for "template".')

    output = output.lower()
    if not output in CRUD_OUTPUT_FORMATS:
        raise ValueError('The argument "output" must take one of the ' +
                         'following values:\n' + str(CRUD_OUTPUT_FORMATS) + '.')

    # Set the request parameters.
    operation = u"read_template"
    API_URL = _construct_URL(server_obj =  server_obj, operation = operation,
                             **URL_kwarg)
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

    :param server:  a CoalesceServer object or the URL of a Coalesce server
    :param output:  If this argument is "list", return the results as a
        Python list of dicts.  Each dict includes the UUID key, name,
        source, version, creation date, and last-modified date for a single
        template.  For "JSON", return the response unparsed, as a Unicode
        JSON object.

    :return:  a list of dicts, or a JSON object, depending on the value of
        "output".
    """

    # Set the request parameters.

    if isinstance(server, CoalesceServer):
        server_obj = server
    else:
        server_obj = CoalesceServer(server)

    operation = u"get_template_list"
    API_URL = _construct_URL(server_obj =  server_obj, operation = operation)

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

    Note that, since a template key is a hash of the template's name, source,
    and version, modifying any of these values produces a new template, with
    a different key, which must be uploaded via the "create_template"
    function (and any of the entities created from the old template remain
    associated with that one).

    Arguments:
    :param server:  a CoalesceServer object or the URL of a Coalesce server
    :param template:  the content of the entity to be updated on the server.
        This can be a JSON or XML representation of a template, a nested dict-
        like in the same format as a JSON respresentation, or an instance of
        CoalesceEntityTemplate (or a subclass); the function automatically
        detects the input type and adjusts the RESTful endpoint and requests
        headers accordingly.
    :param key:  the UUID key of the template to be updated, either as an
        instance of the "UUID" class, or any string or integer that could
        serve as input to the UUID class constructor.  If this argument is
        not supplied, "modified_entity" must include a key; if this argument
        is supplied, it must match any key found in "template", or an error
        will be raised.
    :param full_response:  if True, return the full response from the server
        as a Python requests Response object, rather than a boolean.

    return:  a boolean if "full_response" is False:  True if the response
        status code is 204 (indicating a successful update), False otherwise.
        If "full_response" is True, a Python requests Response object is
        returned instead.  Regardless of the value of "full_response", if the
        response status code has a value in the 200's other than 204, the
        function raises a warning.  (Any value outside the 200's will raise an
        exception.)
    """

    response = save_entity(server = server, entity = template,
                           operation = u"update_template")

    status = response.status_code

    if not full_response:

        if status == 204:
            return True

        else:
            warn("The API server returned an unexpected status code, " + status +
                 ".  However, the entity might have been modified on the " +
                 "server, or might be modified after a delay.",
                 UnexpectedResponseWarning)
            return False

    else:
        if status != 204:
            warn("The API server returned an unexpected status code, " + status +
                 ".  However, the entity might have been modified on the " +
                 "server, or might be modified after a delay.",
                 UnexpectedResponseWarning)
        return response


def delete_template(server = None, key = None):

    """
    Marks the template as deleted on the server.  Until the template is
    permanently deleted (an administrative operation, not available through
    the RESTful API), it can still be accessed, but will not turn up in
    search results.

    :param server:  A CoalesceServer object or the URL of a Coalesce server
    :param key:  The UUID of the entity being deleted

    :return:  True if the returned status code is 200 (which should
        indicate a successful deletion), False (with a warning) if the status
        code has any other value in the 200's.  (Any values outside the 200's
        will raise an error.)
    """

    if isinstance(server, CoalesceServer):
        server_obj = server
    else:
        server_obj = CoalesceServer(server)

    if not key:
        raise ValueError("Please specify a UUID key.")

    else:
        key_str = _test_key(key)

    operation = u"delete_template"
    API_URL = _construct_URL(server_obj =  server_obj, operation = operation,
                             key = key_str)
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
             ".  However, the template might have been deleted on the server, " +
             "or might be deleted after a delay.", UnexpectedResponseWarning)
        success =  False

    return success


def _JSONify_linkage_list(linkages):
    """
    Determines the format of an input linkage or iterable of linkages, and
    transforms the input into a JSON object.

    :param linkages:  the linkage or iterable of linkages to be JSONified

    :return:  the input linkage(s) as a JSON array
    """

    def _test_linkage(linkage):
        """
        Determines the format of an input linkage, and, if necessary,
        transforms the input into a JSON object.  If the input is an iterable
        of linkages, the function throws an error, thereby serving as a test
        for whether the input is a single linkage or an iterable.

        :param linkage:  the linkage to be tested and JSONified

        :return:  "linkage" as a JSON object, and a string indicating the
        format of "linkage".
        """

        link_error_msg = 'The argument "linkages" must be a Coalesce linkage, ' + \
                         'in the form or an XML or JSON entity, a dict in the ' + \
                         'same format as a JSON linkage, an instance of ' + \
                         'CoalesceLinkage (or a subclass), or an instance of ' + \
                         'CoalesceAPILinkage (or a subclass); or an iterable ' + \
                         'of such linkages.'

        if isinstance(linkage, basestring):

            try:
                linkage_Python = json.loads(linkage)

            except JSONDecodeError:
                try:
                    linkage_XSD = xmltodict.parse(linkage)
                except:
                    raise ValueError(link_error_msg)
                else:
                    linkage_JSON = json.dumps(linkage_XSD.to_API())
                    linkage_format = "XML"

            # If the "json.loads" works, we need to determine if "linkage" is
            # a single linkage or an array of them.  If we can't access an
            # element of the JSON-loaded version of "linkage" by index, it
            # must be a dict, and therefore a single linkage.
            else:
                linkage_JSON = linkage
                try:
                    linkage_Python[0]
                except KeyError:
                    linkage_format = "JSON"
                else:
                    linkage_format = "JSON_array"

        elif isinstance(linkage, CoalesceLinkage):
            linkage_JSON = json.dumps(linkage.to_API())
            linkage_format = "XSD"

        elif isinstance(linkage, CoalesceAPILinkage):
            linkage_JSON = json.dumps(linkage)
            linkage_format = "API"

        else:
            try:
                linkage_JSON = json.dumps(linkage)
                linkage_format = "dict"
            except TypeError:
                raise TypeError(link_error_msg)

        return linkage_JSON, linkage_format


    def _parse_linkage(linkage, linkage_format):
        """
        Transforms the input linkage into a JSON object.

        :param linkage:  the linkage to be JSONified
        :param linkage_format:  a string indicating the type of object of
            which "linkage" is an instance.  Possible values are "JSON",
            "XML", "dict", "XSD", and "API".

        :return:  the linkage as a JSON object
        """

        if linkage_format == "JSON":
            linkage_JSON = linkage

        elif linkage_format == "XML":
            linkage_XSD = xmltodict.parse(linkage)
            linkage_JSON = json.dumps(linkage_XSD.to_API())

        elif linkage_format == "XSD":
            linkage_JSON = json.dumps(linkage.to_API())

        elif linkage_format == "API":
            linkage_JSON = json.dumps(linkage)

        else:
            raise ValueError('"' + str(linkage_format) + '" is not a valid ' +
                             'linkage format.')

        return linkage_JSON

    # Test for a single linkage or a JSON array--a list of linkages will
    # cause "_test_linkage" to throw an error.
    try:
        linkage_JSON, linkage_format = _test_linkage(linkages)
    except (TypeError, ValueError):
        pass
    else:
        if linkage_format == "JSON_array":
            return linkages
        else:
            linkage_array = "[" + linkage_JSON + "]"
            return linkage_array

    # If there are multiple linkages, test the format of the first one, and
    # then parse the rest using that format.

    linkage_JSON, linkage_format = _test_linkage(linkages[0])
    linkage_array = "[" + linkage_JSON

    for linkage in linkages[1:]:
        linkage_JSON = _parse_linkage(linkage, linkage_format)
        linkage_array += ", " + linkage_JSON

    linkage_array += "]"

    return linkage_array


def create_linkages(server = None, linkages = None):
    """
    Creates one or more linkages between Coalesce entities.  Linkages must
    be submitted in a special format used only for this endpoint--it
    corresponds to the Java GraphLink class, and the
    pyCoalesce.classes.CoalesceAPILinkage class.  This format's keys
    match the attribues in the GraphLink class, which correspond to a subset
    of the full set of keys/attributes in the entity model, but which use
    different names.

    :param server:  A CoalesceServer object or the URL of a Coalesce server
    :param linkages:  a Coalesce linkage, in the form XML or JSON entity, a
        dict in the same format as a JSON linkage, an instance of
        CoalesceLinkage (or a subclass), or an instance of CoalesceAPILinkage
        (or a subclass); or an iterable (or JSON array) of such linkage
        objects, all of the same type

    :return:  True if the linkages are created successfully (status code 204).
    """

    # Set the request parameters.

    if isinstance(server, CoalesceServer):
        server_obj = server
    else:
        server_obj = CoalesceServer(server)

    operation = u"create_linkages"
    API_URL = _construct_URL(server_obj =  server_obj, operation = operation)

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
             ".  However, the linkages might have been created on the server, " +
             "or might be created after a delay.", UnexpectedResponseWarning)
        success =  False

    return success


def read_linkages(server = None, key = None, output = "dict_list"):
    """
    Retrieves all of the linkages for the Coalesce entity with the UUID
    key matching "key".  Linkages are returned in a special format used
    only for this endpoint--it corresponds to the Java GraphLink class,
    and the pyCoalesce.classes.CoalesceAPILinkage class.  This format's keys
    match the attribues in the GraphLink class, which correspond to a subset
    of the full set of keys/attributes in the entity model, but which use
    different names.

    :param server:  A CoalesceServer object or the URL of a Coalesce server
    :param key:  the UUID assigned to the entity
    :param output:  If this argument is "JSON", return the results as a JSON
        array (string).  If the argument is "dict_list" or "API_list", return
        the results as a list of dicts or instances of
        pyCoalesce.classes.CoalesceAPILinkage, respectively.

    :return:  The entity's linkages, as either a JSON array (string), or a
        list of dicts or CoalesceAPILinkage objects.
    """

    # Set the request parameters.

    if isinstance(server, CoalesceServer):
        server_obj = server
    else:
        server_obj = CoalesceServer(server)

    if isinstance(key, UUID):
        key_obj = key

    else:
        try:
            key_obj = UUID(key)
        except AttributeError:
            key_obj = UUID(int = key)
        except ValueError:
            key_obj = UUID(bytes = key)

    key_str = unicode(key_obj)

    operation = u"read_linkages"
    API_URL = _construct_URL(server_obj =  server_obj, operation = operation,
                             key = key_str)

    method = OPERATIONS[operation][u"method"]
    headers = server_obj.base_headers

    output = output.lower()
    if not output in LINK_CRUD_OUTPUT_FORMATS:
        raise ValueError('The argument "output" must take one of the following ' +
                         'values:\n' + str(LINK_CRUD_OUTPUT_FORMATS) + '.')

    # Submit the request.
    response = get_response(URL = API_URL, method = method, headers = headers,
                            delay = 1, max_attempts = 4)

    # Return the type of output specified by "output".

    if output == u"JSON":
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
    pyCoalesce.classes.CoalesceAPILinkage class.  This format's keys match the
    attribues in the GraphLink class, which correspond to a subset of the full
    set of keys/attributes in the entity model, but which use different names.

    :param server:  A CoalesceServer object or the URL of a Coalesce server
    :param linkages:  a Coalesce linkage, in the form XML or JSON entity, a
        dict in the same format as a JSON linkage, an instance of
        CoalesceLinkage (or a subclass), or an instance of CoalesceAPILinkage
        (or a subclass); or an iterable of such linkages

    :return:  True if the linkages are deleted successfully (status code 204).
    """

    # Set the request parameters.

    if isinstance(server, CoalesceServer):
        server_obj = server
    else:
        server_obj = CoalesceServer(server)

    operation = u"delete_linkages"
    API_URL = _construct_URL(server_obj =  server_obj, operation = operation)

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
             ".  However, the linkages might have been deleted on the server, " +
             "or might be deleted after a delay.", UnexpectedResponseWarning)
        success =  False

    return success
