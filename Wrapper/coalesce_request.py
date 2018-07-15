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
        u"property" : u"property"
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

def search(params = None, operation = u"search",
           SUB_OPERATIONS = u"simple", OPERATOR = "AND", OPERATORS = ["Like", "Like"], 
           VALUES = ["hello", "max"], FIELDS = ["name", "objectkey"], 
           query = [{"test": "test"}, {"NAME": "NAME"}], TESTING = "false"):

    """
    Arguments:
    :param parameters: search parameters included in the search
    :param operation: the operation to be preformed on the database
    :param method: the operation used to recieve data(GET, POST, etc)
    :param SUB_OPERATIONS: the type of search to be preformed(ie. simple, complex, custom?)
    :param OPERATOR: only applies to a complex search, ties together 
        multiple searches in the type of results desired back: AND or OR are valid
    :param OPERATORS: the type of operation preformed on each individual value
        Valid types are - 
        "=", "!=", "Like", 
    :param FIELDS: the type of value that is passed through
    :param VALUES: the search value
    :param query: for complex search only, input custom data for querty, 
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
                                    
            if len(FIELDS) == len(VALUES):
                for i in range(len(FIELDS)):
                    """
                    Setting the data payload format
                    """
                    CRITERIA.append(
                            {
                                    "key": i, 
                                    "recordset": "CoalesceEntity", 
                                    "field": FIELDS[i], 
                                    "operator": OPERATORS[i],
                                    "value": VALUES[i],
                                    "matchCase": "false"
                                    })
                
                for i in range(len(FIELDS)):
                    PROPERTYNAMES.append("CoalesceEntity" + "." + FIELDS[i])
                data = {
                            "pageSize":200,"pageNumber":1,
                            "propertyNames": PROPERTYNAMES,
                            "group":{"operator": OPERATOR, 
                                     "criteria": CRITERIA
                                     }}  
                data = json.dumps(data)

                response = get_response(URL = server + OPERATIONS[operation][0],
                                    method = method,
                                    params = params,
                                    data = data,
                                    headers = headers,
                                    delay = 1,
                                    max_attempts = 2)
                if TESTING == "true":
                    return response.text, data
                return response.text
            
            else:
                raise ValueError("You're inputted criteria are not in sync." 
                                 "\n Check individual fields to make sure"
                                 "\nthere are an equal number in each")

        
    if SUB_OPERATIONS == u"complex":
        #Add a check on the searh fuction to see what the user passes through
            """
            Type of search involving multiple fields
            Has multiple type of operators as well
            Can be found in the search_parser.py file
            """
            GROUP = query
            PROPERTYNAMES = []
            for i in range(len(FIELDS)):
                PROPERTYNAMES.append("CoalesceEntity" + "." + FIELDS[i])
            server = server.URL
            headers = {
                    "Connection" : u"keep-alive",
                    "Content-Type" : u"application/json; charset=utf-8"
                    }

            data = {
                "pageSize": 200, "pageNumber": 1,
                "propertyNames": PROPERTYNAMES,
                "group": {"operator": OPERATOR, "criteria": GROUP}}

            data = json.dumps(data, indent = 4, sort_keys = True)

            if type(query) == str:
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
                return response
            
def read(ARTIFACT = None, KEY = None, TESTING = "false"):
        
        """
        Arguments:
        :ARTIFACT: The type of entity being requested
        :KEY: The UUID assigned to the entity
        """
        
        if (ARTIFACT or KEY) == None:
            raise ValueError("Re-check parameters, make sure they are not none")
            
        serverobj = CoalesceServer()
        server = serverobj.URL
        headers = {
                "Connection" : u"keep-alive",
                "content-type" : u"application/json; charset=utf-8" #come back to this to find the bug
                }
        data = {
            'values': ARTIFACT, 'entityKey': KEY
            }
        operation = u"read"
        method = OPERATIONS[operation][1]
        params = None
        
        response = get_response(URL = server + OPERATIONS[operation][0] +
                                data[u'entityKey'],
                                method = method,
                                params = params,
                                data = data,
                                headers = headers,
                                delay = 1,
                                max_attempts = 2
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
        
        elif parameters == "data": #Removes all metadata
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

def delete(TYPE = None, KEY = None, TESTING = "false"):
    
    """
    :TYPE: The type of entity being deleted
    :KEY: The UUID of the entity being deleted
    """
    if (TYPE or KEY) == None:
        raise ValueError("Please ensure that any inputs are not none")

    serverobj = CoalesceServer()
    server = serverobj.URL
    headers = {
            "Connection" : u"keep-alive",
            "content-type" : u"application/json; charset=utf-8" #come back to this to find the bug
        }
    data = {'values': TYPE, 'entityKey': KEY}
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
    if TESTING == "true":
        return response, data
    return response

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
    :FIELDSADDED: The fields that are being created in the artifact, can pass through
    string path or individual keys ***Only metadata can be changed without a path
    """
    
    if TYPE == None:
        raise ValueError("Please specify a type of template to retrieve from the server"
                         )

    if FIELDSADDED == None:
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
        
def update(VALUE = None, KEY = None,
           NEWVALUES = None,
           TESTING = "false"):

        """
        Arguments:
        :VALUE: The type of entity being requested
        :KEY: The specific entity key
        :NEWVALUES: The fields that require updating, only key required if it is 
        present in the first or second nest, else provide an entire path to ensure 
        repeating keys are not confused
        """
        if (VALUE or KEY or NEWVALUES) == None:
            raise ValueError("Make sure no input parameters are None and retry")

        serverobj = CoalesceServer()
        server = serverobj.URL
        headers = {
                "Connection" : u"keep-alive",
                "content-type" : u"application/json; charset=utf-8"
                }
        read_payload = {
            'values': VALUE, 'entityKey': KEY} #Please enter your specific entity key
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

        if len(NEWVALUES) > 0:
            for item in NEWVALUES:
                tmp = update_template(TEMPLATE, item)
            data = tmp

        elif len(NEWVALUES) == 0:
            data = TEMPLATE

        elif len(NEWVALUES) < 0:
            raise ValueError("HOW?")

        response = get_response(URL = server + OPERATIONS[operation][0] +
                                read_payload['entityKey'],
                                method = method,
                                params = params,
                                data = json.dumps(data),
                                headers = headers,
                                delay = 1,
                                max_attempts = 2)

        if TESTING == "true":
            return response, data

        return response
