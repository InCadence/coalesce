# -*- coding: utf-8 -*-
"""
Created on Fri Jul  6 11:39:12 2018

@author: Dhruva Venkat
@author: Scott Orr

"""

from sys import stdout
from configparser import SafeConfigParser
from unittest import TestCase, TestSuite
from copy import copy
from uuid import uuid4

import xmltodict
import simplejson as json
from requests import HTTPError

from pyCoalesce.coalesce_request import *
from pyCoalesce.coalesce_request import _test_key
from pyCoalesce.classes import parseString, CoalesceEntityTemplate, \
                               find_child, get_child_attrib, to_XML_string, \
                               CoalesceAPILinkage, CoalesceLinkage


# Get the server address from the config file.
CONFIG_FILE = "test_config.ini"
config = SafeConfigParser()
config.optionxform = str
config.readfp(open(CONFIG_FILE))
SERVER_URL = config.get("Coalesce RESTful API server", "server_URL")
CRUD_PERSISTOR = config.get("Coalesce RESTful API server", "CRUD_persistor")
SEARCH_PERSISTOR = config.get("Coalesce RESTful API server", "search_persistor")

# Set other constants.
FIELD1_NAME = "Field1"
FIELD2_NAME = "Field2"
FIELD3_NAME = "Field3"
FAKE_FIELD_NAME = "Fake"
RECORDSET1 = "TestRecordset1"
RECORDSET2 = "TestRecordset2"
RECORDSET3 = "TestRecordset3"
TEMPLATE1_NAME = 'TestEntity1'
TEMPLATE2_NAME = 'TestEntity2'
TEMPLATE3_NAME = 'TestEntity3'
TEMPLATE1_XML = '<entity ' + \
                   'classname="test1" ' + \
                   'name="' + TEMPLATE1_NAME + '" ' + \
                   'source="pyCoalesceTest" ' + \
                   'version="0.1"> ' + \
                   '<linkagesection name="Linkages"/> ' + \
                   '<section name="TestSection"> ' + \
                       '<recordset maxrecords="1" minrecords="1" ' + \
                           'name="' + RECORDSET1 + '"> ' + \
                           '<fielddefinition datatype="string" ' + \
                               'defaultclassificationmarking="U" ' + \
                               'name="' + FIELD1_NAME + '"/> ' + \
                           '<fielddefinition datatype="string" ' + \
                               'defaultclassificationmarking="U" ' + \
                               'name="' + FIELD2_NAME + '"/> ' + \
                           '<fielddefinition datatype="string" ' + \
                               'defaultclassificationmarking="U" ' + \
                               'name="' + FIELD3_NAME + '"/> ' + \
                           '<fielddefinition datatype="string" ' + \
                               'defaultclassificationmarking="U" ' + \
                               'name="' + FAKE_FIELD_NAME + '"/> ' + \
                       '</recordset> ' + \
                   '</section> ' + \
               '</entity>'
TEMPLATE2_XML = '<entity ' + \
                   'classname="test2" ' + \
                   'name="' + TEMPLATE2_NAME + '" ' + \
                   'source="pyCoalesceTest" ' + \
                   'version="0.1"> ' + \
                   '<linkagesection name="Linkages"/> ' + \
                   '<section name="TestSection"> ' + \
                       '<recordset maxrecords="1" minrecords="1" ' + \
                           'name="' + RECORDSET2 + '"> ' + \
                           '<fielddefinition datatype="string" ' + \
                               'defaultclassificationmarking="U" ' + \
                               'name="' + FIELD1_NAME + '"/> ' + \
                           '<fielddefinition datatype="string" ' + \
                               'defaultclassificationmarking="U" ' + \
                               'name="' + FIELD2_NAME + '"/> ' + \
                           '<fielddefinition datatype="string" ' + \
                               'defaultclassificationmarking="U" ' + \
                               'name="' + FIELD3_NAME + '"/> ' + \
                       '</recordset> ' + \
                   '</section> ' + \
               '</entity>'
TEMPLATE3_DICT = {
                    'className': 'test3',
                    'name': TEMPLATE3_NAME,
                    'source': 'pyCoalesceTest',
                    'version': '0.1',
                    'linkageSection': {
                        'name': 'Linkages',
                        'linkagesAsList': [
                        ]
                    },
                    'sectionsAsList': [
                        {
                            'name': 'TestSection',
                            'recordsetsAsList': [
                                {
                                    'maxRecords': 1,
                                    'minRecords': 1,
                                    'name': RECORDSET3,
                                    'fieldDefinitions': [
                                        {
                                            'dataType': 'string',
                                            'defaultClassificationMarking': 'U',
                                            'name': FIELD1_NAME
                                        },
                                        {
                                            'dataType': 'string',
                                            'defaultClassificationMarking': 'U',
                                            'name': FIELD2_NAME
                                        },
                                        {
                                            'dataType': 'long',
                                            'defaultClassificationMarking': 'U',
                                            'name': FIELD3_NAME
                                        }
                                    ]
                                }
                            ]
                        }
                    ]
                }
ENTITY1_FIELDS = {"Field1": "foo", "Field2": "bar"}
ENTITY2_FIELDS = {"Field1": "Spam", "Field2": "eggs"}
ENTITY3_DICT = {
                   'name': TEMPLATE2_NAME,
                   'source': 'pyCoalesceTest',
                   'version': '0.1',
                   'linkageSection': {
                       'linkagesAsList': [
                           ],
                       'name': 'Linkages'
                   },
                   'sectionsAsList': [
                       {
                           'name': 'TestSection',
                           'sectionsAsList': [],
                           'recordsetsAsList': [
                               {
                                   'name': RECORDSET2,
                                   'allRecords': [
                                       {
                                           'name': 'TestRecord',
                                           'fields': [
                                               {
                                                   'name': FIELD1_NAME,
                                                   'value': 'Sir',
                                                   'datatype': 'string',
                                               },
                                               {
                                                   'name': FIELD2_NAME,
                                                   'value': 'Robin',
                                                   'datatype': 'string',
                                               }
                                           ]
                                       }
                                   ]
                               }
                           ]
                       }
                   ]
               }
ENTITY4_DICT = {
                   'name': TEMPLATE3_NAME,
                   'source': 'pyCoalesceTest',
                   'version': '0.1',
                   'linkageSection': {
                       'linkagesAsList': [
                           ],
                       'name': 'Linkages'
                   },
                   'sectionsAsList': [
                       {
                           'name': 'TestSection',
                           'sectionsAsList': [],
                           'recordsetsAsList': [
                               {
                                   'name': RECORDSET3,
                                   'allRecords': [
                                       {
                                           'name': 'TestRecord',
                                           'fields': [
                                               {
                                                   'name': FIELD1_NAME,
                                                   'value': 'The',
                                                   'datatype': 'string',
                                               },
                                               {
                                                   'name': FIELD2_NAME,
                                                   'value': 'Larch',
                                                   'datatype': 'string',
                                               },
                                               {
                                                   'name': FIELD3_NAME,
                                                   'value': 6,
                                                   'datatype': 'long',
                                               }
                                           ]
                                       }
                                   ]
                               }
                           ]
                       }
                   ]
               }
FILTER1_DICT = {
                  "operator": "AND",
                  "criteria": [
                      {
                          "recordset": "coalesceentity",
                          "field": "name",
                          "operator": "EqualTo",
                          "value": TEMPLATE2_NAME,
                          "matchCase": False
                      }
                  ],
                  "groups": [
                      {
                          "operator": "OR",
                          "criteria": [
                              {
                                  "recordset": RECORDSET2.lower(),
                                  "field": FIELD1_NAME.lower(),
                                  "operator": "EqualTo",
                                  "value": "Sir",
                                  "matchCase": False
                              },
                              {
                                  "recordset": RECORDSET2.lower(),
                                  "field": FIELD2_NAME.lower(),
                                  "operator": "EqualTo",
                                  "value": "aardvark",
                                  "matchCase": False
                              }
                          ]
                      }
                  ]
              }
FILTER2_DICT = {
                  "operator": "AND",
                  "criteria": [
                      {
                          "recordset": "coalesceentity",
                          "field": "name",
                          "operator": "EqualTo",
                          "value": TEMPLATE3_NAME,
                          "matchCase": False
                      }
                  ],
                  "groups": [
                      {
                          "operator": "AND",
                          "criteria": [
                              {
                                  "recordset": RECORDSET3.lower(),
                                  "field": FIELD1_NAME.lower(),
                                  "operator": "EqualTo",
                                  "value": "aardvark",
                                  "matchCase": False,
                                  "not": True
                              },
                              {
                                  "recordset": RECORDSET3.lower(),
                                  "field": FIELD2_NAME.lower(),
                                  "operator": "Like",
                                  "value": "Larch",
                                  "matchCase": False,
                              }
                          ]
                      }
                  ]
              }
FILTER3_DICT = {
                  "operator": "AND",
                  "criteria": [
                      {
                          "recordset": "coalesceentity",
                          "field": "name",
                          "operator": "EqualTo",
                          "value": TEMPLATE2_NAME,
                          "matchCase": False
                      }
                  ],
              }
QUERY4_DICT = {
                    "pageSize": 200,
                    "propertyNames": [
                        "testrecordset2." + FIELD1_NAME.lower()
                    ],
                    "group": {

                         "operator": "AND",
                         "criteria": [
                             {
                                 "recordset": "coalesceentity",
                                 "field": "name",
                                 "operator": "EqualTo",
                                 "value": TEMPLATE2_NAME,
                                 "matchCase": False
                             }
                         ],
                         "groups": [
                             {
                                 "operator": "OR",
                                 "criteria": [
                                     {
                                         "recordset": RECORDSET2.lower(),
                                         "field": FIELD1_NAME.lower(),
                                         "operator": "EqualTo",
                                         "value": "Sir",
                                         "matchCase": False
                                     },
                                     {
                                         "recordset": RECORDSET2.lower(),
                                         "field": "field2",
                                         "operator": "EqualTo",
                                         "value": "aardvark",
                                         "matchCase": False
                                     }
                                 ]
                             }
                         ]
                     }
                 }


class ServerTest(TestCase):
    """
    Provides a server object for the other TestCase classes.

    """

    @classmethod
    def setUpClass(cls):
        cls.server = CoalesceServer(server_URL = SERVER_URL,
                                    CRUD_persistor = CRUD_PERSISTOR,
                                    search_persistor = SEARCH_PERSISTOR)


class EntityTests(ServerTest):
    """
    Methods that set values used by later methods must be class methods.

    """

    def test_create_template(self):

        cls = self.__class__

        cls.template1_key = \
            create_template(server = cls.server, template = TEMPLATE1_XML)

        template2 = parseString(TEMPLATE2_XML,
                                object_class = CoalesceEntityTemplate,
                                silence = True)
        cls.template2_key = \
            create_template(server = cls.server, template = template2)

        cls.template3_key = \
            create_template(server = cls.server, template = TEMPLATE3_DICT)

        # Did the server return valid UUID keys?

        tested_key1 = _test_key(cls.template1_key)
        self.assertEqual(cls.template1_key, tested_key1)

        tested_key2 = _test_key(cls.template2_key)
        self.assertEqual(cls.template2_key, tested_key2)

        tested_key3 = _test_key(cls.template3_key)
        self.assertEqual(cls.template3_key, tested_key3)


    def test_register_template(self):

        success1 = register_template(server = self.server,
                                     key = self.template1_key)
        self.assertTrue(success1)

        success2 = register_template(server = self.server,
                                     key = self.template2_key)
        self.assertTrue(success2)

        success3 = register_template(server = self.server,
                                     key = self.template3_key)
        self.assertTrue(success3)


    def test_read_template(self):
        """
        This tests read via key; the "test_create_entity" method implicitly
        tests read via name/source/version.

        """

        orig_template1 = xmltodict.parse(TEMPLATE1_XML)
        field1_1_name = \
            orig_template1["entity"]["section"]["recordset"]["fielddefinition"][0]["@name"]

        orig_template2 = xmltodict.parse(TEMPLATE2_XML)
        field2_2_name = \
            orig_template2["entity"]["section"]["recordset"]["fielddefinition"][1]["@name"]

        orig_template3 = TEMPLATE3_DICT
        template3_nsv = [orig_template3["name"], orig_template3["source"],
                         orig_template3["version"]]
        field3_1_name = \
            orig_template3["sectionsAsList"][0]["recordsetsAsList"][0]["fieldDefinitions"][0]["name"]

        template2_dict = read_template(server = self.server,
                                      template = self.template2_key,
                                      output = "dict")
        dict_field2_2_name = \
            template2_dict["sectionsAsList"][0]["recordsetsAsList"][0]["fieldDefinitions"][1]["name"]
        self.assertEqual(dict_field2_2_name, field2_2_name)

        template_entity_object = read_template(server = self.server,
                                               template = self.template1_key,
                                               output = "entity_object")
        obj_field1_1_name = \
            template_entity_object.section[0].recordset[0].fielddefinition[0].name
        self.assertEqual(obj_field1_1_name, field1_1_name)

        text_JSON = read_template(server = self.server,
                                  template = self.template2_key, output = "JSON")
        template_JSON = json.loads(text_JSON)
        JSON_field2_2_name = \
            template_JSON["sectionsAsList"][0]["recordsetsAsList"][0]["fieldDefinitions"][1]["name"]
        self.assertEqual(JSON_field2_2_name, field2_2_name)

        text_XML = read_template(server = self.server,
                                 template = template3_nsv, output = "XML")
        template_XML = xmltodict.parse(text_XML)
        XML_field3_1_name = \
            template_XML["entity"]["section"]["recordset"]["fielddefinition"][0]["@name"]
        self.assertEqual(XML_field3_1_name, field3_1_name)


    def test_get_template_list(self):

        template_list = get_template_list(server = self.server, output = "list")
        key_list = [template["key"] for template in template_list]
        self.assertTrue(self.template1_key in key_list)
        self.assertTrue(self.template2_key in key_list)
        self.assertTrue(self.template3_key in key_list)

        templates_JSON = get_template_list(server = self.server,
                                           output = "JSON")
        template_JSON_list = json.loads(templates_JSON)
        key_JSON_list = [template["key"] for template in template_JSON_list]
        self.assertTrue(self.template1_key in key_JSON_list)
        self.assertTrue(self.template2_key in key_JSON_list)
        self.assertTrue(self.template3_key in key_JSON_list)


    def test_update_template(self):

        new_template1_XML = TEMPLATE1_XML.replace(FIELD3_NAME, "FieldX")
        success1 = update_template(server = self.server,
                                   template = new_template1_XML,
                                   key = self.template1_key)
        self.assertTrue(success1)

        new_template2_XML = TEMPLATE2_XML.replace(FIELD3_NAME, "FieldY")
        new_template2 = parseString(new_template2_XML,
                                    object_class = CoalesceEntityTemplate,
                                    silence = True)
        success2 = update_template(server = self.server,
                                   template = new_template2,
                                   key = self.template2_key)
        self.assertTrue(success1)

        new_template3_dict = copy(TEMPLATE3_DICT)
        new_template3_dict["sectionsAsList"][0]["recordsetsAsList"][0]["name"] = \
            "Fun Records!"
        success3 = update_template(server = self.server,
                                   template = new_template3_dict,
                                   key = self.template3_key)
        self.assertTrue(success3)


    def test_delete_template(self):

        # The default Derby persistor doesn't implement template deletion.
        not_implemented_msg = "Server Error"

        try:
            success1 = delete_template(server = self.server,
                                       key = self.template1_key)
        except HTTPError as err:
            if str(err).find(not_implemented_msg) > -1:
                success1 = True
            else:
                raise
        else:
            self.assertTrue(success1)

        try:
            success2 = delete_template(server = self.server,
                                       key = self.template2_key)
        except HTTPError as err:
            if str(err).find(not_implemented_msg) > -1:
                success1 = True
            else:
                raise
        else:
            self.assertTrue(success2)

        try:
            success3 = delete_template(server = self.server,
                                       key = self.template3_key)
        except HTTPError as err:
            if str(err).find(not_implemented_msg) > -1:
                success1 = True
            else:
                raise
        else:
            self.assertTrue(success3)


    def test_construct_entity(self):

        cls = self.__class__

        # It's best to create random keys while the test suite is running,
        # so that we don't accidentally access entities from an earlier
        # test.
        cls.entity1_key = str(uuid4())
        entity2_UUID = uuid4()
        cls.entity3_key = str(uuid4())
        cls.entity4_key = str(uuid4())

        # The chance of a collision is infinitesimal, but it would make
        # debugging tough.
        while len(set([cls.entity1_key, str(entity2_UUID), cls.entity3_key,
                       cls.entity4_key])) != 4:
            cls.entity1_key = str(uuid4())
            entity2_UUID = uuid4()
            cls.entity3_key = str(uuid4())
            cls.entity4_key = str(uuid4())

        cls.entity2_key = str(entity2_UUID)

        cls.entity1 = construct_entity(server = cls.server,
                                       template = cls.template1_key,
                                       key = cls.entity1_key,
                                       fields = ENTITY1_FIELDS)

        cls.entity2 = construct_entity(server = cls.server,
                                       template = cls.template2_key,
                                       key = entity2_UUID,
                                       fields = ENTITY2_FIELDS)

        entity1_field1_path = find_child(cls.entity1, FIELD1_NAME)[0]
        entity1_field1 = get_child_attrib(cls.entity1, path = entity1_field1_path)
        self.assertEqual(entity1_field1, ENTITY1_FIELDS[FIELD1_NAME])
        entity2_field2_path = find_child(cls.entity2, FIELD2_NAME)[0]
        entity2_field2 = get_child_attrib(cls.entity2, path = entity2_field2_path)
        self.assertEqual(entity2_field2, ENTITY2_FIELDS[FIELD2_NAME])


    def test_create(self):

        response1 = create(server = self.server, entity = self.entity1,
                           full_response = True)
        self.assertEqual(self.entity1.key, response1.text)

        entity2_XML = to_XML_string(self.entity2)
        new_entity_key2 = create(server = self.server, entity = entity2_XML)
        self.assertTrue(self.entity2.key, new_entity_key2)

        new_entity_key3 = create(server = self.server, entity = ENTITY3_DICT,
                          key = self.entity3_key)
        self.assertTrue(self.entity3_key, new_entity_key3)

        entity4_JSON = json.dumps(ENTITY4_DICT)
        response4 = create(server = self.server, entity = entity4_JSON,
                           key = self.entity4_key, full_response = True)
        self.assertEqual(self.entity4_key, response4.text)


    def test_read(self):

        orig_name1 = self.entity1.name
        orig_name2 = self.entity2.name
        orig_name3 = ENTITY3_DICT["name"]
        orig_name4 = ENTITY4_DICT["name"]

        entity1_dict = read(server = self.server, key = self.entity1_key,
                            output = "dict")
        name1 = entity1_dict["name"]
        self.assertEqual(name1, orig_name1)

        entity2_object = read(server = self.server, key = self.entity2_key,
                              output = "entity_object")
        name2 = entity2_object.name
        self.assertEqual(name2, orig_name2)

        JSON_text = read(server = self.server, key = self.entity3_key,
                         output = "JSON")
        entity3_JSON = json.loads(JSON_text)
        name3 = entity3_JSON["name"]
        self.assertEqual(name3, orig_name3)

        XML_text = read(server = self.server, key = self.entity4_key,
                        output = "XML")
        entity4_XML = xmltodict.parse(XML_text)
        name4 = entity4_XML["entity"]["@name"]
        self.assertEqual(name4, orig_name4)


    def test_update(self):

        self.entity1.title = "Gumby #1"
        success1 = update(server = self.server, entity = self.entity1)
        self.assertTrue(success1)

        self.entity2.title = "Gumby #2"
        entity2_XML = to_XML_string(self.entity2)
        success2 = update(server = self.server, entity = entity2_XML)
        self.assertTrue(success2)

        ENTITY3_DICT["title"] = "Gumby #3"
        success3 = update(server = self.server, entity = ENTITY3_DICT,
                          key = self.entity3_key)
        self.assertTrue(success3)

        ENTITY4_DICT["title"] = "Gumby #4"
        entity4_new_JSON = json.dumps(ENTITY4_DICT)
        response4 = update(server = self.server, entity = entity4_new_JSON,
                           key = self.entity4_key, full_response = True)
        status4 = response4.status_code
        self.assertEqual(status4, 204)


    def test_delete(self):

        success1 = delete(server = self.server, keys = self.entity1_key)
        self.assertTrue(success1)

        delete_list = [self.entity2_key, self.entity3_key, self.entity4_key]
        success2 = delete(server = self.server, keys = delete_list)
        self.assertTrue(success2)


    @classmethod
    def test_linkage_manipulation(cls):
        """
        Creates one set of test linkages for each type of input, with
        three linkages in each set, to allow testing both single-linkage
        and multiple-linkage input.  This also tests the linkage classes
        and their constructor and output methods.

        Note that this method doesn't feature any asserts--it just throws
        an error if something goes wrong.

        """

        # Set for dict input
        cls.linkage01_dict = {"source": cls.entity1_key,
                              "target": cls.entity2_key, "type": "UNDEFINED"}
        cls.linkage02_dict = {"source": cls.entity2_key,
                              "target": cls.entity3_key, "label": "Brain hurts",
                              "type": "IS_PARENT_OF"}
        cls.linkage03_dict = {"source": cls.entity3_key,
                              "target": cls.entity4_key,
                              "type": "IS_BEING_WATCHED_BY",
                              "biDirectional": True}

        # Set for JSON input
        linkage11_dict = {"source": cls.entity2_key, "target": cls.entity3_key,
                          "type": "UNDEFINED"}
        cls.linkage11_JSON = json.dumps(linkage11_dict)
        linkage12_dict = {"source": cls.entity3_key, "target": cls.entity4_key,
                          "label": "Brain hurts", "type": "IS_PARENT_OF"}
        linkage13_dict = {"source": cls.entity4_key, "target": cls.entity1_key,
                          "type": "IS_BEING_WATCHED_BY", "biDirectional": True}
        cls.linkage1_array_JSON = json.dumps([linkage12_dict, linkage13_dict])

        # Set for CoalesceAPILinkage input
        cls.linkage21_API = CoalesceAPILinkage(source = cls.entity3_key,
                                               target = cls.entity4_key,
                                               linkage_type = "WAS_CREATED_BY")
        linkage22_dict = {"source": cls.entity4_key, "target": cls.entity1_key,
                          "label": "Brain hurts", "type": "IS_PARENT_OF"}
        cls.linkage22_API = CoalesceAPILinkage.from_dict(linkage22_dict)
        linkage23_XSD = CoalesceLinkage(entity1key = cls.entity1_key,
                                        entity2key = cls.entity2_key,
                                        linktype = "IsBeingWatchedBy")
        cls.linkage23_API = linkage23_XSD.to_API(biDirectional = True)

        # Set for XML input
        linkage31_XSD = CoalesceLinkage(entity1key = cls.entity4_key,
                                        entity2key = cls.entity1_key)
        cls.linkage31_XML = to_XML_string(linkage31_XSD)
        linkage3_API = CoalesceAPILinkage(source = cls.entity2_key,
                                          target = cls.entity3_key,
                                          linkage_type = "IS_BEING_WATCHED_BY",
                                          biDirectional = True)
        cls.linkage32_XML = to_XML_string(linkage3_API.to_XSD())
        cls.linkage33_XML = to_XML_string(linkage3_API.reverse_to_XSD())

        # Set for CoalesceLinkage input
        cls.linkage41_XSD = CoalesceLinkage(entity1key = cls.entity1_key,
                                            entity2key = cls.entity2_key,
                                            label = "Brain hurts",
                                            linktype = "IsParentOf")
        linkage4_API = CoalesceAPILinkage(source = cls.entity1_key,
                                          target = cls.entity4_key,
                                          linkage_type = "IS_A_PEER_OF",
                                          biDirectional = True)
        cls.linkage42_XSD = linkage4_API.to_XSD()
        cls.linkage43_XSD = linkage4_API.reverse_to_XSD()


    def test_create_linkages(self):

        success01 = create_linkages(server = self.server,
                                    linkages = self.linkage01_dict)
        self.assertTrue(success01)
        success02 = create_linkages(server = self.server,
                                    linkages = [self.linkage02_dict,
                                                self.linkage03_dict])
        self.assertTrue(success02)

        success11 = create_linkages(server = self.server,
                                    linkages = self.linkage11_JSON)
        self.assertTrue(success11)
        success12 = create_linkages(server = self.server,
                                    linkages = self.linkage1_array_JSON)
        self.assertTrue(success12)

        success21 = create_linkages(server = self.server,
                                    linkages = self.linkage21_API)
        self.assertTrue(success21)
        success22 = create_linkages(server = self.server,
                                    linkages = [self.linkage22_API,
                                                self.linkage23_API])
        self.assertTrue(success22)

        success31 = create_linkages(server = self.server,
                                    linkages = self.linkage31_XML)
        self.assertTrue(success31)
        success32 = create_linkages(server = self.server,
                                    linkages = [self.linkage32_XML,
                                                self.linkage33_XML])
        self.assertTrue(success32)

        success41 = create_linkages(server = self.server,
                                    linkages = self.linkage41_XSD)
        self.assertTrue(success41)
        success42 = create_linkages(server = self.server,
                                    linkages = [self.linkage42_XSD,
                                                self.linkage43_XSD])
        self.assertTrue(success42)


    def test_read_linkages(self):

        linkages1 = read_linkages(server = self.server, key = self.entity1_key,
                                  output  = "JSON")
        num_links1 = len(json.loads(linkages1))
        self.assertEqual(num_links1, 5)

        linkages2 = read_linkages(server = self.server, key = self.entity2_key,
                                  output  = "dict_list")
        num_links2 = len(linkages2)
        self.assertEqual(num_links2, 4)

        linkages3 = read_linkages(server = self.server, key = self.entity3_key,
                                  output  = "API_list")
        num_links3 = len(linkages3)
        self.assertEqual(num_links3, 4)

        linkages4 = read_linkages(server = self.server, key = self.entity4_key,
                                  output  = "JSON")
        num_links4 = len(json.loads(linkages4))
        self.assertEqual(num_links4, 5)


    def test_delete_linkages(self):

        success01 = delete_linkages(server = self.server,
                                    linkages = self.linkage01_dict)
        self.assertTrue(success01)
        success02 = delete_linkages(server = self.server,
                                    linkages = [self.linkage02_dict,
                                                self.linkage03_dict])
        self.assertTrue(success02)

        success11 = delete_linkages(server = self.server,
                                    linkages = self.linkage11_JSON)
        self.assertTrue(success11)
        success12 = delete_linkages(server = self.server,
                                    linkages = self.linkage1_array_JSON)
        self.assertTrue(success12)

        success21 = delete_linkages(server = self.server,
                                    linkages = self.linkage21_API)
        self.assertTrue(success21)
        success22 = delete_linkages(server = self.server,
                                    linkages = [self.linkage22_API,
                                                self.linkage23_API])
        self.assertTrue(success22)

        success31 = delete_linkages(server = self.server,
                                    linkages = self.linkage31_XML)
        self.assertTrue(success31)
        success32 = delete_linkages(server = self.server,
                                    linkages = [self.linkage32_XML,
                                                self.linkage33_XML])
        self.assertTrue(success32)

        success41 = delete_linkages(server = self.server,
                                    linkages = self.linkage41_XSD)
        self.assertTrue(success41)
        success42 = delete_linkages(server = self.server,
                                    linkages = [self.linkage42_XSD,
                                                self.linkage43_XSD])
        self.assertTrue(success42)


class SearchTests(ServerTest):

    def test_search(self):

        orig2_first_field = ENTITY2_FIELDS[FIELD1_NAME]
        orig3_first_field = ENTITY3_DICT["sectionsAsList"][0] \
                                        ["recordsetsAsList"][0] \
                                        ["allRecords"][0] \
                                        ["fields"][0] \
                                        ["value"]
        orig4_first_field = ENTITY4_DICT["sectionsAsList"][0] \
                                        ["recordsetsAsList"][0] \
                                        ["allRecords"][0] \
                                        ["fields"][0] \
                                        ["value"]

        request1_return_property = RECORDSET2.lower() + "." + \
                                       FIELD1_NAME.lower()
        results1_list = search(server = self.server, query = FILTER1_DICT,
                               return_property_names = \
                                   [request1_return_property],
                               output = "list")
        results1_first_field = results1_list[0]["values"][0]
        self.assertEqual(results1_first_field, orig3_first_field)

        filter2_JSON = json.dumps(FILTER2_DICT)
        request2_return_property = \
            RECORDSET3.lower() + "." + FIELD1_NAME.lower()
        results2_full_dict = search(server = self.server, query = filter2_JSON,
                                    return_property_names = \
                                        [request2_return_property],
                                    output = "full_dict")
        results2_first_field = results2_full_dict["hits"][0]["values"][0]
        self.assertEqual(results2_first_field, orig4_first_field)

        filter3_JSON = json.dumps(FILTER3_DICT)
        results3_list, query3 = search(server = self.server,
                                         query = filter3_JSON,
                                         sort_by =
                                             {"propertyName": request1_return_property,
                                              "sortOrder": "ASC"},
                                         return_property_names =
                                             [request1_return_property],
                                         output = "list",
                                         return_query = True)
        results3_first_fields = [hit["values"][0] for hit in results3_list]
        self.assertTrue(results3_first_fields.index(orig3_first_field) <
                        results3_first_fields.index(orig2_first_field))
        request3_return_property_out = query3["propertyNames"][0]
        self.assertEqual(request1_return_property, request3_return_property_out)

        results4_list, query4 = search(server = self.server,
                                         query = filter3_JSON,
                                         sort_by = request1_return_property,
                                         sort_order = "ASC",
                                         return_property_names =
                                             [request1_return_property],
                                         output = "list",
                                         return_query = True)
        results4_first_fields = [hit["values"][0] for hit in results3_list]
        self.assertTrue(results4_first_fields.index(orig3_first_field) <
                        results4_first_fields.index(orig2_first_field))
        request4_return_property_out = query4["propertyNames"][0]
        self.assertEqual(request1_return_property, request4_return_property_out)

        sort_by_JSON = json.dumps({"propertyName": request1_return_property,
                                   "sortOrder": "ASC"})
        results5_list, query5 = search(server = self.server,
                                         query = filter3_JSON,
                                         sort_by = sort_by_JSON,
                                         return_property_names =
                                             [request1_return_property],
                                         output = "list",
                                         return_query = True)
        results5_first_fields = [hit["values"][0] for hit in results3_list]
        self.assertTrue(results5_first_fields.index(orig3_first_field) <
                        results5_first_fields.index(orig2_first_field))
        request5_return_property_out = query5["propertyNames"][0]
        self.assertEqual(request1_return_property, request5_return_property_out)

        results6_JSON = search(server = self.server, query = QUERY4_DICT,
                               return_property_names = ["IgnoreThis"],
                               output = "JSON")
        results6_first_field = json.loads(results6_JSON)["hits"][0]["values"][0]
        self.assertEqual(results6_first_field, orig3_first_field)

        results7 = search(server = self.server, query = {},
                          template = TEMPLATE1_NAME)
        self.assertEqual(len(results7), 1)


    def test_search_simple(self):

        orig1_first_field = ENTITY1_FIELDS[FIELD1_NAME]
        orig2_first_field = ENTITY3_DICT["sectionsAsList"][0] \
                                        ["recordsetsAsList"][0] \
                                        ["allRecords"][0] \
                                        ["fields"][0] \
                                        ["value"]
        orig4_first_field = ENTITY4_DICT["sectionsAsList"][0] \
                                        ["recordsetsAsList"][0] \
                                        ["allRecords"][0] \
                                        ["fields"][0] \
                                        ["value"]
        orig4_third_field = ENTITY4_DICT["sectionsAsList"][0] \
                                        ["recordsetsAsList"][0] \
                                        ["allRecords"][0] \
                                        ["fields"][2] \
                                        ["value"]

        request1_return_property = RECORDSET2 + "." + FIELD1_NAME
        results1_list = search_simple(server = self.server,
                                      recordset = RECORDSET2,
                                      field = FIELD1_NAME, operator = "EqualTo",
                                      value = "Sir",
                                      return_property_names = \
                                          [request1_return_property],
                                      output = "list")
        results1_first_field = results1_list[0]["values"][0]
        self.assertEqual(results1_first_field, orig2_first_field)

        request2_return_property = RECORDSET3 + "." + FIELD1_NAME
        results2_full_dict = search_simple(server = self.server,
                                           recordset = RECORDSET3,
                                           field = FIELD2_NAME,
                                           operator = "Like",
                                           value = "Larch",
                                           return_property_names = \
                                               [request2_return_property],
                                           output = "full_dict")
        results2_first_field = results2_full_dict["hits"][0]["values"][0]
        self.assertEqual(results2_first_field, orig4_first_field)

        results3_JSON = search_simple(server = self.server,
                                      recordset = RECORDSET2,
                                      field = FIELD1_NAME, operator = "EqualTo",
                                      value = "Sir",
                                      return_property_names = \
                                          [request1_return_property],
                                      output = "JSON")
        results3_first_field = json.loads(results3_JSON)["hits"][0]["values"][0]
        self.assertEqual(results3_first_field, orig2_first_field)

        request4_return_property = RECORDSET3 + "." + FIELD3_NAME
        results4_list = search_simple(server = self.server,
                                      recordset = RECORDSET3,
                                      field = FIELD3_NAME, operator = "between",
                                      value = [5, 8],
                                      return_property_names = \
                                          [request4_return_property],
                                      output = "list")
        results4_third_field = results4_list[0]["values"][0]
        self.assertEqual(results4_third_field, str(orig4_third_field))

        results5_list = search_simple(server = self.server,
                                      recordset = RECORDSET3,
                                      field = FIELD3_NAME, operator = "between",
                                      values = [5, 8],
                                      return_property_names = \
                                          [request4_return_property],
                                      output = "list")
        results5_third_field = results5_list[0]["values"][0]
        self.assertEqual(results5_third_field, str(orig4_third_field))

        request6_return_property = RECORDSET1 + "." + FIELD1_NAME
        results6_list = search_simple(server = self.server,
                                      recordset = RECORDSET1,
                                      field = FAKE_FIELD_NAME,
                                      operator = "nullcheck",
                                      return_property_names = \
                                          [request6_return_property],
                                      output = "list")
        results6_first_field = results6_list[0]["values"][0]
        self.assertEqual(results6_first_field, str(orig1_first_field))


    def test_search_helpers(self):

        orig2_first_field = ENTITY2_FIELDS[FIELD1_NAME]
        orig2_second_field = ENTITY2_FIELDS[FIELD2_NAME]
        orig3_first_field = ENTITY3_DICT["sectionsAsList"][0] \
                                        ["recordsetsAsList"][0] \
                                        ["allRecords"][0] \
                                        ["fields"][0] \
                                        ["value"]

        filter3_criteria = FILTER3_DICT["criteria"][0]
        filter3_recordset = filter3_criteria["recordset"]
        field3 = filter3_criteria["field"]
        operator3 = filter3_criteria["operator"]
        values3 = [orig2_first_field, filter3_criteria["value"]]
        filter3a = create_search_group(filter3_recordset, field3, values3,
                                       operator3)
        request3a_return_property = RECORDSET2 + "." + FIELD1_NAME.lower()
        request3a_return_property_list = [request3a_return_property]
        results3a_list = search(server = self.server, query = filter3a,
                                sort_by =
                                    {"propertyName": request3a_return_property,
                                    "sortOrder": "ASC"},
                                return_property_names =
                                    request3a_return_property_list,
                                output = "list")
        results3a_first_fields = [hit["values"][0] for hit in results3a_list]
        self.assertTrue(results3a_first_fields.index(orig3_first_field) <
                        results3a_first_fields.index(orig2_first_field))
        request3a_return_property_out = request3a_return_property_list[0]
        self.assertEqual(request3a_return_property,
                         request3a_return_property_out)

        filter3b = add_search_filter(filter3a, RECORDSET2, FIELD1_NAME,
                                     orig2_first_field, operator3)
        request3b_return_property = RECORDSET2 + "." + FIELD2_NAME.lower()
        request3b_return_property_list = [request3a_return_property,
                                          request3b_return_property]
        results3b_list = search(server = self.server, query = filter3b,
                                sort_by =
                                    {"propertyName": request3b_return_property,
                                    "sortOrder": "ASC"},
                                return_property_names =
                                    request3b_return_property_list,
                                output = "list")
        results3b_second_field = results3b_list[0]["values"][1]
        self.assertEqual(results3b_second_field, orig2_second_field)

        filter3c = add_search_filter(FILTER3_DICT, RECORDSET2, FIELD1_NAME,
                                     orig2_first_field, operator3)
        request3c_return_property_list = [request3b_return_property]
        results3c_list = search(server = self.server, query = filter3c,
                                return_property_names =
                                    request3c_return_property_list,
                                output = "list")
        results3c_second_field = results3c_list[0]["values"][0]
        self.assertEqual(results3c_second_field, orig2_second_field)



TESTS = (EntityTests("test_create_template"),
         EntityTests("test_register_template"),
         EntityTests("test_read_template"),
         EntityTests("test_get_template_list"),
         EntityTests("test_construct_entity"), EntityTests("test_create"),
         EntityTests("test_read"), EntityTests("test_update"),
         EntityTests("test_linkage_manipulation"),
         EntityTests("test_create_linkages"),
         EntityTests("test_read_linkages"),
         SearchTests("test_search"), SearchTests("test_search_simple"),
         SearchTests("test_search_helpers"),
         EntityTests("test_delete_linkages"), EntityTests("test_delete"),
         EntityTests("test_update_template"),
         EntityTests("test_delete_template"))

def pyCoalesce_test_suite():
    return TestSuite(TESTS)
