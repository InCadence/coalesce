# -*- coding: utf-8 -*-
"""
@author: dvenkat
"""

from urlparse import urlsplit
import pprint
import collections


import simplejson as json
from pandas import Series

from utilities.API_request import get_response
from utilities.logger import package_logger

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
        if scheme == u"": 
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

    def __init__(self, server_URL = u"http://localhost:8181/cxf/",
                 connection = u"keep-alive", max_attempts = 4):
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
        if connection not in self.VALID_CONNECTION_TYPES: 
            raise ValueError('The connection type must be either ' +
                             '"keep-alive" or "close."')
        self.base_headers = {"Connection" : connection,
                             "content-type" : self.CONTENT_TYPE}

        # Set the maximum number of connection attempts
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
ENDPOINTS = {
        u"search" : u"data/search/complex", 
        u"CRUD" : u"data/entity/", 
        u"templates" : u"templates", 
        u"property" : u"property",
        u"linkages" : TBD
    } 
OPERATIONS = {
        u"search" : (ENDPOINTS[u"search"], u"post"),
        u"create" : (ENDPOINTS[u"CRUD"], u"put"),
        u"read" : (ENDPOINTS[u"CRUD"], u"get"),
        u"update" : (ENDPOINTS[u"CRUD"], u"post"),
        u"delete" : (ENDPOINTS[u"CRUD"], u"delete")
    }
FORMATS = (u"XML", u"JSON", u"python_dict")
    
# Convert a server URL to a CoalesceServer object. This also serves as a check 
# for a properly formed URL.   

def read(ARTIFACT=None, key=None, TESTING="false"):
    """
    Arguments:
    :ARTIFACT: The type of entity being requested
    :key: The UUID assigned to the entity
    """

    if (ARTIFACT or key) == None:
        raise ValueError("Re-check parameters, make sure botht the Artifact and UUID key have been entered")

    serverobj = CoalesceServer()
    server = serverobj.URL
    headers = {
        "Connection": u"keep-alive",
        "content-type": u"application/json; charset=utf-8"  # come back to this to find the bug
    }
    data = {
        'values': ARTIFACT, 'entityKey': key
    }
    operation = u"read"
    method = OPERATIONS[operation][1]
    params = None

    response = get_response(URL=server + OPERATIONS[operation][0] +
                                data[u'entityKey'],
                            method=method,
                            params=params,
                            data=data,
                            headers=headers,
                            delay=1,
                            max_attempts=2
                            )
    response = json.loads(response.text)

    parameters = "data"

    Response_dict = dict(response)

    """
    :parameters are what subcategory the user would like to draw from 
    the outputted data. They could be: "raw", "fields", "linkages" or
    "data"
    """

    if parameters == "raw":
        return response

    elif parameters == "linkages":
        Response_dict = (Series((Response_dict)['linkageSection']['linkagesAsList']))
        return Response_dict[0], Response_dict[1]

    elif parameters == "data":  # Removes all metadata
        response_dict = Response_dict['sectionsAsList'][0]['recordsetsAsList']
        return response_dict

    elif parameters == "fields":
        Response_dict = Response_dict['sectionsAsList'][0]['recordsetsAsList'][0]['allRecords'][0]['fields']
        Field_Attributes = [u'status', u'classificationMarkingAsString', u'label']
        for _ in range(len(Response_dict)):
            for i in Field_Attributes:
                response = Response_dict[_]
                print(pprint.pprint(response))
        return response

    else:
        ValueError("You have entered an invalid parameter. Check again.")

def delete(type = None, key = None, TESTING = "false"):

    """
    :type: The type of entity being deleted
    :key: The UUID of the entity being deleted
    """
    if (type or key) == None:
        raise ValueError("Please ensure that any inputs are not none")

    serverobj = CoalesceServer()
    server = serverobj.URL
    headers = {
            "Connection" : u"keep-alive",
            "content-type" : u"application/json; charset=utf-8" #come back to this to find the bug
        }
    data = {'values': type, 'entityKey': key}
    operation = u"delete"
    method = OPERATIONS[operation][1]
    params = None

    response = get_response(URL = server + OPERATIONS[operation][0] + data[u'entityKey'],
                            method = method,
                            params = params,
                            data = data,
                            headers = headers,
                            delay = 1,
                            max_attempts = 2)
    if TESTING == ("true" or "True" or "TRUE"):
        return response, data
    return response

def search(params = None, operation = u"search",
           SUB_OPERATIONS = u"simple", OPERATOR = "AND", operators = ["Like", "Like"],
           values = ["hello", "max"], fields = ["name", "objectkey"],
           propertynames = ["coalesceentity", "coalesceentity"],
           query = [{"key":0,"recordset":"coalesceentity","field":"objectkey",
                     "operator":"EqualTo","value":"","matchCase":"false"}],
           TESTING = "false", page_size = 200, page_number = 1):

    """
    Arguments:
    :param operation: the operation to be preformed on the database
    :param SUB_OPERATIONS: the type of search to be preformed(ie. simple, complex)
    The simple takes all the queries specified above. Only the query is needed for
    complex search
    :param OPERATOR: only applies to a complex search, ties together
        multiple searches in the type of results desired back: AND or OR are valid
    :param operators: the type of operation preformed on each individual value
        Valid types are -
        "=", "!=", "Like", more on the github documentation
    :param fields: the type of value that is passed through
    :param values: the search value
    :param query: for complex search only, input custom data for query,
    if multiple values are desired, follow data format from simple
    """

    if not OPERATIONS.has_key(operation):
        raise ValueError('The parameter "operation" must take one of the ' +
                         'following values:\n' + u", ".join(OPERATIONS.keys()))

    server = CoalesceServer()
    method = OPERATIONS[operation][1]

    if SUB_OPERATIONS == u"simple":

            server = server.URL
            headers = {
                    "Connection" : u"keep-alive","content-type" : u"application/json; charset=utf-8"
                    }
            CRITERIA = []
            PROPERTYNAMES = []

            if len(fields) == len(values) == len(propertynames):
                for i in range(len(fields)):
                    """
                    Setting the data payload format
                    """
                    CRITERIA.append(
                            {
                                    "key": i,
                                    "recordset": propertynames[i],
                                    "field": fields[i],
                                    "operator": operators[i],
                                    "value": values[i],
                                    "matchCase": "false"
                                    })

                if len(fields) == len(propertynames):
                    for x, y in zip(fields, propertynames):
                        PROPERTYNAMES.append(y + "." + x)
                else:
                    raise ValueError("You do not have the same number of inputted properties and values")

                data = {
                            "pageSize":page_size,"pageNumber":page_number,
                            "propertyNames": PROPERTYNAMES,
                            "group":{"operator": OPERATOR,
                                     "criteria": CRITERIA
                                     }}
                data = json.dumps(data)

                response=get_response(URL = server + OPERATIONS[operation][0],
                                    method = method,
                                    params = params,
                                    data = data,
                                    headers = headers,
                                    delay = 1,
                                    max_attempts = 2)
                if TESTING == "true":
                    return response.text, data
                return json.loads(response.text)

            else:
                raise ValueError("You're inputted criteria are not in sync." 
                                 "\nCheck individual fields to make sure"
                                 "\nthere are an equal number in each")

    elif SUB_OPERATIONS == u"complex":
            """
            Type of search involving multiple fields
            Has multiple type of operators as well
            Can be found in the search_parser.py file
            """

            GROUP = query

            server = server.URL
            headers = {
                    "Connection" : u"keep-alive",
                    "Content-Type" : u"application/json; charset=utf-8"
                    }

            data = {
                "pageSize": page_number, "pageNumber": page_number,
                "group": {"criteria": GROUP}}

            data = json.dumps(data, indent = 4, sort_keys = True)

            if isinstance(query, basestring):
                raise ValueError("Please enter your query as a dictionary")

            data = json.dumps(json.loads(data))

            response = get_response(URL = server + OPERATIONS[operation][0],
                                    method = method,
                                    params = params,
                                    data = data,
                                    headers = headers,
                                    delay = 1,
                                    max_attempts = 2)

            if TESTING == "true":
                return response, data
            else:
                return json.loads(response.text)

    else:
        raise ValueError("The value entered for the SUB_OPERATION (type of search) must either be: "
                         "\n 'simple' or 'complex' ")

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

def create(TYPE = None, FIELDSADDED = None, TESTING = "false"):

    """
    Arguments:
    :TYPE: The type of artifact being looked for
    :fieldsadded: The fields that are being created in the artifact, can pass through
    string path or individual keys ***Only metadata can be changed without a path
    """

    if TYPE == None:
        raise ValueError("Please specify a type of template to retrieve from the server"
                         )

    if fieldsadded == None:
        raise ValueError("Please specify fields to add, or enter an empty list")

    serverobj = CoalesceServer()
    server = serverobj.URL
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
                            max_attempts = 2
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
                            max_attempts = 2
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
                            max_attempts = 2)
    if TESTING == "true":
        return response, json.loads(data)
    else:
        return response

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

        serverobj = CoalesceServer()
        server = serverobj.URL
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
                                max_attempts = 2
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

class CoalesceEntity(GUID=None, entity_type = None, template=None, records={},
                     links=[], server=CoalesceServer(), created = False):

    def __init__(self, GUID, server, entity_type, created):
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









