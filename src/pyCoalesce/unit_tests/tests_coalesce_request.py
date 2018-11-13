# -*- coding: utf-8 -*-
"""
Created on Fri Jul  6 11:39:12 2018

@author: dvenkat & sorr
"""

from sys import stdout
from ConfigParser import SafeConfigParser
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
TEMPLATE1_XML = '<entity ' + \
                   'classname="test1" ' + \
                   'name="TestEntity1" ' + \
                   'source="pyCoalesceTest" ' + \
                   'version="0.1"> ' + \
                   '<linkagesection name="Linkages"/> ' + \
                   '<section name="TestSection"> ' + \
                       '<recordset maxrecords="1" minrecords="1" ' + \
                           'name="TestRecordset1"> ' + \
                           '<fielddefinition datatype="string" ' + \
                               'defaultclassificationmarking="U" ' + \
                               'name="Field1"/> ' + \
                           '<fielddefinition datatype="string" ' + \
                               'defaultclassificationmarking="U" ' + \
                               'name="Field2"/> ' + \
                           '<fielddefinition datatype="string" ' + \
                               'defaultclassificationmarking="U" ' + \
                               'name="Field3"/> ' + \
                       '</recordset> ' + \
                   '</section> ' + \
               '</entity>'
TEMPLATE2_XML = '<entity ' + \
                   'classname="test2" ' + \
                   'name="TestEntity2" ' + \
                   'source="pyCoalesceTest" ' + \
                   'version="0.1"> ' + \
                   '<linkagesection name="Linkages"/> ' + \
                   '<section name="TestSection"> ' + \
                       '<recordset maxrecords="1" minrecords="1" ' + \
                           'name="TestRecordset2"> ' + \
                           '<fielddefinition datatype="string" ' + \
                               'defaultclassificationmarking="U" ' + \
                               'name="Field1"/> ' + \
                           '<fielddefinition datatype="string" ' + \
                               'defaultclassificationmarking="U" ' + \
                               'name="Field2"/> ' + \
                           '<fielddefinition datatype="string" ' + \
                               'defaultclassificationmarking="U" ' + \
                               'name="Field3"/> ' + \
                       '</recordset> ' + \
                   '</section> ' + \
               '</entity>'
TEMPLATE3_DICT = {
                    'className': 'test3',
                    'name': 'TestEntity3',
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
                                    'name': 'TestRecordset3',
                                    'fieldDefinitions': [
                                        {
                                            'dataType': 'string',
                                            'defaultClassificationMarking': 'U',
                                            'name': 'Field1'
                                        },
                                        {
                                            'dataType': 'string',
                                            'defaultClassificationMarking': 'U',
                                            'name': 'Field2'
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
                   'name': 'TestEntity2',
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
                                   'name': 'TestRecordset2',
                                   'allRecords': [
                                       {
                                           'name': 'TestRecord',
                                           'fields': [
                                               {
                                                   'name': 'Field1',
                                                   'value': 'Sir',
                                                   'datatype': 'string',
                                               },
                                               {
                                                   'name': 'Field2',
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
                   'name': 'TestEntity3',
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
                                   'name': 'TestRecordset3',
                                   'allRecords': [
                                       {
                                           'name': 'TestRecord',
                                           'fields': [
                                               {
                                                   'name': 'Field1',
                                                   'value': 'The',
                                                   'datatype': 'string',
                                               },
                                               {
                                                   'name': 'Field2',
                                                   'value': 'Larch',
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
QUERY1_DICT = {
                  "operator": "AND",
                  "criteria": [
                      {
                          "recordset": "coalesceentity",
                          "field": "name",
                          "operator": "EqualTo",
                          "value": "TestEntity2",
                          "matchCase": False
                      }
                  ],
                  "groups": [
                      {
                          "operator": "OR",
                          "criteria": [
                              {
                                  "recordset": "testrecordset2",
                                  "field": "field1",
                                  "operator": "EqualTo",
                                  "value": "Sir",
                                  "matchCase": False
                              },
                              {
                                  "recordset": "testrecordset2",
                                  "field": "field2",
                                  "operator": "EqualTo",
                                  "value": "aardvark",
                                  "matchCase": False
                              }
                          ]
                      }
                  ]
              }
QUERY2_DICT = {
                  "operator": "AND",
                  "criteria": [
                      {
                          "recordset": "coalesceentity",
                          "field": "name",
                          "operator": "EqualTo",
                          "value": "TestEntity3",
                          "matchCase": False
                      }
                  ],
                  "groups": [
                      {
                          "operator": "AND",
                          "criteria": [
                              {
                                  "recordset": "testrecordset3",
                                  "field": "field1",
                                  "operator": "EqualTo",
                                  "value": "aardvark",
                                  "matchCase": False,
                                  "not": True
                              },
                              {
                                  "recordset": "testrecordset3",
                                  "field": "field2",
                                  "operator": "Like",
                                  "value": "Larch",
                                  "matchCase": False,
                              }
                          ]
                      }
                  ]
              }
QUERY3_DICT = {
                  "operator": "AND",
                  "criteria": [
                      {
                          "recordset": "coalesceentity",
                          "field": "name",
                          "operator": "EqualTo",
                          "value": "TestEntity2",
                          "matchCase": False
                      }
                  ],
              }


class ServerTest(TestCase):
    """
    Provides a server for the other TestCase classes.
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

        new_template1_XML = TEMPLATE1_XML.replace("Field3", "FieldX")
        success1 = update_template(server = self.server,
                                   template = new_template1_XML,
                                   key = self.template1_key)
        self.assertTrue(success1)

        new_template2_XML = TEMPLATE2_XML.replace("Field3", "FieldY")
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
        cls.entity1_key = unicode(uuid4())
        entity2_UUID = uuid4()
        cls.entity3_key = unicode(uuid4())
        cls.entity4_key = unicode(uuid4())

        # The chance of a collision is infinitesimal, but it would make
        # debugging tough.
        while len(set([cls.entity1_key, unicode(entity2_UUID), cls.entity3_key,
                       cls.entity4_key])) != 4:
            cls.entity1_key = unicode(uuid4())
            entity2_UUID = uuid4()
            cls.entity3_key = unicode(uuid4())
            cls.entity4_key = unicode(uuid4())

        cls.entity2_key = unicode(entity2_UUID)

        cls.entity1 = construct_entity(server = cls.server,
                                       template = cls.template1_key,
                                       key = cls.entity1_key,
                                       fields = ENTITY1_FIELDS)

        cls.entity2 = construct_entity(server = cls.server,
                                       template = cls.template2_key,
                                       key = entity2_UUID,
                                       fields = ENTITY2_FIELDS)

        entity1_field1_path = find_child(cls.entity1, "Field1")[0]
        entity1_field1 = get_child_attrib(cls.entity1, path = entity1_field1_path)
        self.assertEqual(entity1_field1, ENTITY1_FIELDS["Field1"])
        entity2_field2_path = find_child(cls.entity2, "Field2")[0]
        entity2_field2 = get_child_attrib(cls.entity2, path = entity2_field2_path)
        self.assertEqual(entity2_field2, ENTITY2_FIELDS["Field2"])


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
        Create one set of test linkages for each types of input, with three
        linkages in each set, to allow testing both single-linkage and
        multiple-linkage input.  This also tests the linkage classes and
        their constructor and output methods.

        Note that this method doesn't feature any asserts--it just throws
        an error is something goes wrong.
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

        orig2_first_field = ENTITY2_FIELDS["Field1"]
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

        results1_list = search(server = self.server, query = QUERY1_DICT,
                               property_names = ["testrecordset2.field1"],
                               output = "list")
        results1_first_field = results1_list[0]["values"][0]
        self.assertEqual(results1_first_field, orig3_first_field)

        query2_JSON = json.dumps(QUERY2_DICT)
        results2_full_dict = search(server = self.server, query = query2_JSON,
                                    property_names = ["testrecordset3.field1"],
                                    output = "full_dict")
        results2_first_field = results2_full_dict["hits"][0]["values"][0]
        self.assertEqual(results2_first_field, orig4_first_field)

        query3_JSON = json.dumps(QUERY3_DICT)
        results3_list = search(server = self.server, query = query3_JSON,
                               sort_by = {"propertyName": "testrecordset2.field1",
                                          "sortOrder": "ASC"},
                               property_names = ["testrecordset2.field1"],
                               output = "list")
        results3_first_fields = [hit["values"][0] for hit in results3_list]
        self.assertTrue(results3_first_fields.index(orig3_first_field) <
                        results3_first_fields.index(orig2_first_field))

        results4_JSON = search(server = self.server, query = QUERY1_DICT,
                               property_names = ["testrecordset2.field1"],
                               output = "JSON")
        results4_first_field = json.loads(results4_JSON)["hits"][0]["values"][0]
        self.assertEqual(results4_first_field, orig3_first_field)


    def test_search_simple(self):

        orig1_first_field = ENTITY3_DICT["sectionsAsList"][0] \
                                        ["recordsetsAsList"][0] \
                                        ["allRecords"][0] \
                                        ["fields"][0] \
                                        ["value"]
        orig2_first_field = ENTITY4_DICT["sectionsAsList"][0] \
                                        ["recordsetsAsList"][0] \
                                        ["allRecords"][0] \
                                        ["fields"][0] \
                                        ["value"]

        results1_list = search_simple(server = self.server,
                                      recordset = "testrecordset2",
                                      field = "field1", operator = "EqualTo",
                                      value = "Sir",
                                      property_names = ["testrecordset2.field1"],
                                      output = "list")
        results1_first_field = results1_list[0]["values"][0]
        self.assertEqual(results1_first_field, orig1_first_field)

        results2_full_dict = search_simple(server = self.server,
                                           recordset = "testrecordset3",
                                           field = "field2", operator = "Like",
                                           value = "Larch",
                                           property_names = ["testrecordset3.field1"],
                                           output = "full_dict")
        results2_first_field = results2_full_dict["hits"][0]["values"][0]
        self.assertEqual(results2_first_field, orig2_first_field)

        results3_JSON = search_simple(server = self.server,
                                      recordset = "testrecordset2",
                                      field = "field1", operator = "EqualTo",
                                      value = "Sir",
                                      property_names = ["testrecordset2.field1"],
                                      output = "JSON")
        results3_first_field = json.loads(results3_JSON)["hits"][0]["values"][0]
        self.assertEqual(results3_first_field, orig1_first_field)


TESTS = (EntityTests("test_create_template"),
         EntityTests("test_register_template"), EntityTests("test_read_template"),
         EntityTests("test_get_template_list"),
         EntityTests("test_construct_entity"), EntityTests("test_create"),
         EntityTests("test_read"), EntityTests("test_update"),
         EntityTests("test_linkage_manipulation"),
         EntityTests("test_create_linkages"),
         EntityTests("test_read_linkages"),
         SearchTests("test_search"), SearchTests("test_search_simple"),
         EntityTests("test_delete_linkages"), EntityTests("test_delete"),
         EntityTests("test_update_template"),
         EntityTests("test_delete_template"))


def pyCoalesce_test_suite():
    return TestSuite(TESTS)
