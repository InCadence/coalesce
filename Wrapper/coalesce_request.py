# -*- coding: utf-8 -*-
"""
@author: dvenkat
"""

from urlparse import urlsplit
import pprint
from pandas import Series

import simplejson as json

from API.API_request import get_response
from .API import package_logger

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
           query = [{"test": "test"}, {"NAME": "NAME"}]):
                              
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
                return response.text
            
            else:
                raise ValueError("You're inputted criteria are not in sync." 
                                 "\n Check individual fields to make sure"
                                 "\nthere are an equal number in each")
                return response
        
    if SUB_OPERATIONS == u"complex":
        #Add a check on the searh fuction to see what the user passes through
            """
            Type of search involving multiple fields
            Has multiple type of operators as well
            Can be found in the search_parser.py file
            """
            GROUP = [{}]
            server = server.URL
            headers = {
                    "Connection" : u"keep-alive",
                    "Content-Type" : u"application/json; charset=utf-8"
                    }
            data = {
                            "pageSize":200,"pageNumber":1,
                            "propertyNames": PROPERTYNAMES, 
                            "group": {"operator": OPERATOR, "criteria": GROUP}}
            
            if type(query) == dict:
                for key in query:
                    if key in data:
                        data[key] = query[key]
                        del query[key]
                    else: 
                        GROUP[0][key] = query[key]
                    data = json.dumps(data)
            
            elif type(query) == str:
                raise ValueError("Please enter your query as a dictionary")
            
            elif type(query) == list:
                GROUP = query
                data = json.dumps(data)
                
            response = get_response(URL = server + OPERATIONS[operation][0],
                                    method = method,
                                    params = params,
                                    data = data,
                                    headers = headers,
                                    delay = 1,
                                    max_attempts = 2)
            return response
            
def read(ARTIFACT = None, KEY = None):
        
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

def delete(TYPE = ['GDELTArtifact'], KEY = '30000105-9037-48d2-84be-ddb414d5748f'): 
    
    """
    :TYPE: The type of entity being deleted
    :KEY: The UUID of the entity being deleted
    """
    
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
    return response

def create(TYPE = "OEEvent", FIELDSADDED = {"flatten": "false", "['flatten']['sectionsAsList']":"false"}): #FIX NESTER AND BUG ON THE GET KEYS
    
    """
    Arguments:
    :TYPE: The type of artifact being looked for
    :FIELDSADDED: The fields that are being created in the artifact, can pass through
    string path or individual keys ***Only metadata can be changed without a path
    """
    
    if TYPE == None:
        raise ValueError("Please specify a type of template to retrieve from the server"
                         )
        
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
    
    TEMPLATE = (json.dumps(json.loads(response.text), indent = 4, sort_keys = True))
    
    def Nester(changes, dictionary):
        for key1, value1 in changes.iteritems():
            if '[' not in key1:
                for key2,value2 in dictionary.items():
                    if key1 == key2:
                        dictionary[key2] = changes[key1]
            elif ("[" and "]") in key1:
                path = key1.replace('][', ',').replace(']', '').replace('[','').replace("'","").split(',')
                str_integers = []
                for i in range(10):
                    str_integers += "{}".format(i)
                for i in path:
                    if i in str_integers:
                        i = int(i)
                    field = dictionary[i]
                changes[key1] = field
        return json.dumps(dictionary)
    
    data = json.loads(Nester(changes = FIELDSADDED, dictionary = json.loads(TEMPLATE)))
    
    response = get_response(URL = server + OPERATIONS[operation][0] +
                            data["key"],
                            method = method,
                            params = params,
                            data = TEMPLATE,
                            headers = headers,
                            delay = 1,
                            max_attempts = 2)
    return response
        
def update(VALUE =['GDELTArtifact'], KEY = '90001276-e620-4f9c-bf64-3907f7870cb9',
           NEWVALUES = {"namePath": "OEvent"}):
        
        """
        Arguments:
        :VALUE: The type of entity being requested
        :KEY: The specific entity key
        :NEWVALUES: The fields that require updating, only key required if it is 
        present in the first or second nest, else provide an entire path to ensure 
        repeating keys are not confused
        """
        
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
        
        TEMPLATE = (json.dumps(json.loads(response.text), indent = 4, sort_keys = True))
        
        def Nester(changes, dictionary):
            for key1, value1 in changes.iteritems():
                if '[' not in key1:
                    for key2,value2 in dictionary.items():
                        if key1 == key2:
                            dictionary[key2] = changes[key1]
                    
                elif ("[" and "]") in key1:
                    path = key1.replace('][', ',').replace(']', '').replace('[','').replace("'","").split(',')
                    str_integers = []
                    for i in range(10):
                        str_integers += "{}".format(i)
                    for i in path:
                        if i in str_integers:
                            i = int(i)
                        field = dictionary[i]
                    changes[key1] = field
                    
            TEMPLATE = json.dumps(dictionary)
            return TEMPLATE
            
        data = Nester(changes = NEWVALUES, dictionary = json.loads(TEMPLATE)) 
        response = get_response(URL = server + OPERATIONS[operation][0] +
                                read_payload['entityKey'],
                                method = method,
                                params = params,
                                data = data,
                                headers = headers,
                                delay = 1,
                                max_attempts = 2)
        return response
