# -*- coding: utf-8 -*-
"""
@author: sorr
"""

from urlparse import urlsplit
import pprint
from pandas import Series

import simplejson as json

from pyCoalesce import package_logger
from API.API_request import get_response


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

    def __init__(self, server_URL = u"http://52.61.62.87:8181/cxf/",
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
           SUB_OPERATIONS = u"simple", OPERATOR = ["AND"], OPERATORS = ["!=", "!="], 
           VALUES = ["hello", "max"], FIELDS = ["name", "objectkey"]):
                              
    """
    :param parameters: search parameters included in the search
    :param operation: the operation to be preformed on the database
    :param method: the operation used to recieve data(GET, POST, etc)
    :param SUB_OPERATIONS: the type of search to be preformed(ie. simple, complex, custom?)
    :param OPERATOR: only applies to a complex search, ties together 
        multiple searches in the type of results desired back
    :param FIELDS: the type of value that is passed through
    :param VALUES: the search value
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
                            "group": 
                                {
                                        "operator": OPERATOR[0], 
                                        "criteria": CRITERIA
                                                     }}
                data = json.dumps(data)
                print(data)
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
                return response.text
        
    if SUB_OPERATIONS == u"complex":
        #Add a check on the searh fuction to see what the user passes through
            """
            Type of search involving multiple fields
            Has multiple type of operators as well
            Can be found in the search_parser.py file
            """
            
            server = server.URL
            headers = {
                    "Connection" : u"keep-alive",
                    "Content-Type" : u"application/json; charset=utf-8"
                    }
            data = {
                            "pageSize":200,"pageNumber":1,
                            "propertyNames": PROPERTYNAMES, #PASS THROUGH YOUR PROPERTIES HERE
                            "group": 
                                {
                                        "operator": OPERATOR, #PASS THROUGH YOUR OPERATORS HERE
                                        "criteria": CRITERIA #PASS THROUGH CRITERIA HERE
                                                     }}
            response = get_response(URL = server + OPERATIONS[operation][0],
                                    method = method,
                                    params = params,
                                    data = data,
                                    headers = headers,
                                    delay = 1,
                                    max_attempts = 2)
                
            
def read(ARTIFACT = ['GDELTArtifact'], KEY = '700014b5-0d8b-44f1-9139-e58113bfc537'):
        
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
    return response.status

def create():
    
    serverobj = CoalesceServer()
    server = serverobj.URL
    headers = {
            "Connection" : u"keep-alive",
            "content-type" : u"application/json; charset=utf-8" #come back to this to find the bug
        }
    ENTITYTEMPLATES = {
                   "WordPressMetadata": "templates/94ea887b-4817-3585-a972-420236224cbc/new",
                   "Spider_Results": "templates/ac2a8881-2b19-39a7-aad3-adb956e3a4c5/new", 
                   "SpiderQuery": "templates/13654d11-ab3b-30a4-88c7-f795e4a5182d/new", 
                   "Enumeration": "templates/0d7e02d1-e706-3f6e-b728-dae18bb9ac24/new",
                   "GDELT": "templates/cc239b9d-7c9b-33ec-a5ee-545a83904ecd/new", 
                   "OEAgent": "templates/d9fb4c93-36fd-39a3-916d-53966677ca1a/new", 
                   "OEDocument": "templates/688174ea-8753-3e56-9887-92dd733a048f/new", 
                   "NLPPipeline": "templates/d7c77fa5-bf9f-3ecf-a313-201d8f1ebc5e/new", 
                   "OERolePlayer": "templates/70a9eb16-4b21-3e02-90fc-e356674b7231/new",
                   "OEEvent": "templates/4a10c22f-7738-340d-bc73-31466a91e8e2/new"}
    params = None
    operation = u"create"
    method = OPERATIONS[operation][0]
    
    response = get_response(URL = server+ "data/{}".format(ENTITYTEMPLATES["Enumeration"]), #Add UUID for this because of keys
                            method = 'get',
                            params = params,
                            data = None,
                            headers = headers,
                            delay = 1,
                            max_attempts = 2
                            )
    
    TEMPLATE = (json.dumps(json.loads(response.text), indent = 4, sort_keys = True))
    file = open("Template.txt", 'w')
    file.write(TEMPLATE)
    raw_input("Press enter once edited the Template in the new file...")
    file.close()
    
    file = open("Template.txt", "r")
    data = (json.dumps(json.load(file)))
    file.close()
    
    response = get_response(URL = server + OPERATIONS[operation][0] + 
                            json.loads(data)["key"],
                            method = method,
                            params = params,
                            data = data,
                            headers = headers,
                            delay = 1,
                            max_attempts = 2)
    if response.status == 204:
        print("You're request has been succsessful. A new entity has been created.")
        
def update():
        serverobj = CoalesceServer()
        server = serverobj.URL
        headers = {
                "Connection" : u"keep-alive",
                "content-type" : u"application/json; charset=utf-8"
                }
        read_payload = {
            'values': ['GDELTArtifact'], 'entityKey': 'e32ceba6-fbd1-4067-8803-fa17b2ffed39'} #Please enter your specific entity key
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
        response = json.loads((response.text))
       
        with open('Data.txt', 'w') as outfile:  
            json.dump(response, outfile, indent = 4, sort_keys = True)
        print("Please make changes to the script passed through in the data file")
        raw_input("Press a key here once finished...")
        
        file = open("Data.txt", 'r')
        data = json.dumps(json.load(file))
        file.close()
        
        response = get_response(URL = server + OPERATIONS[operation][0] +
                                read_payload['entityKey'],
                                method = method,
                                params = params,
                                data = data,
                                headers = headers,
                                delay = 1,
                                max_attempts = 2)
        return response
#else: 
#    print("\nI don't know what you entered, you've broken the system")
#    print("Please enter a valid option")


print(search())

        
        
