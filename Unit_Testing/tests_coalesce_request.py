# -*- coding: utf-8 -*-
"""
Created on Fri Jul  6 11:39:12 2018

@author: dvenkat & sorr
"""

from unittest import TestCase, TestSuite
from uuid import uuid4

import xmltodict
import simplejson as json

from pyCoalesce.coalesce_request import *
from pyCoalesce.classes import parseString, CoalesceEntityTemplate, \
                               to_XML_string, CoalesceAPILinkage, CoalesceLinkage


SERVER_URL = u"http://localhost:8181/cxf"
CRUD_PERSISTOR = "data"
SEARCH_PERSISTOR = "data"
TEMPLATE1_XML = '<entity ' + \
                   'classname="test1" ' + \
                   'name="TestEntity1" ' + \
                   'source="pyCoalesceTest" ' + \
                   'version="0.1"> ' + \
                   '<linkagesection name="Linkages"/> ' + \
                   '<section name="TestSection"> ' + \
                       '<recordset maxrecords="1" minrecords="1" ' + \
                           'name="TestRecordset"> ' + \
                           '<fielddefinition datatype="string" ' + \
                               'defaultclassificationmarking="U" ' + \
                               'name="Field1"/> ' + \
                           '<fielddefinition datatype="string" ' + \
                               'defaultclassificationmarking="U" ' + \
                               'name="Field2"/> ' + \
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
                           'name="TestRecordset"> ' + \
                           '<fielddefinition datatype="string" ' + \
                               'defaultclassificationmarking="U" ' + \
                               'name="Field1"/> ' + \
                           '<fielddefinition datatype="string" ' + \
                               'defaultclassificationmarking="U" ' + \
                               'name="Field2"/> ' + \
                       '</recordset> ' + \
                   '</section> ' + \
               '</entity>'
TEMPLATE3_DICT = {
                    'classname': 'test3',
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
                            'recordsetsAsList': [
                                {
                                    'fieldDefinitions': [
                                        {
                                            'datatype': 'string',
                                            'defaultclassificationmarking': 'U',
                                            'name': 'Field1'
                                        },
                                        {
                                            'datatype': 'string',
                                            'defaultclassificationmarking': 'U',
                                            'name': 'Field2'
                                        }
                                    ],
                                    'maxRecords': 1,
                                    'minRecords': 1,
                                    'name': 'TestRecordset'
                                }
                            ]
                        }
                    ]
                }
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
                           'recordsetsAsList': [
                               {
                                   'name': 'TestRecordset',
                                   'allRecords': [
                                       {
                                           'name': 'TestRecord',
                                           'fields': [
                                               {
                                                   'name': 'Field1',
                                                   'value': 'Sir'
                                               },
                                               {
                                                   'name': 'Field2',
                                                   'value': 'Robin'
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
                           'recordsetsAsList': [
                               {
                                   'name': 'TestRecordset',
                                   'allRecords': [
                                       {
                                           'name': 'TestRecord',
                                           'fields': [
                                               {
                                                   'name': 'Field1',
                                                   'value': 'The'
                                               },
                                               {
                                                   'name': 'Field2',
                                                   'value': 'Larch'
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
                  "operator": "OR",
                  "criteria": [
                      {
                          "recordset": "coalesceentity",
                          "field": "name",
                          "operator": "EqualTo",
                          "value": "testentity2",
                          "matchCase": False
                      }
                  ],
                  "groups": [
                      {
                          "operator": "AND",
                          "criteria": [
                              {
                                  "recordset": "testrecordset",
                                  "field": "field1",
                                  "operator": "EqualTo",
                                  "value": "sir",
                                  "matchCase": False
                              },
                              {
                                  "recordset": "testrecordset",
                                  "field": "field2",
                                  "operator": "EqualTo",
                                  "value": "robin",
                                  "matchCase": False
                              }
                          ]
                      }
                  ]
              }
QUERY2_DICT = {
                  "operator": "OR",
                  "criteria": [
                      {
                          "recordset": "coalesceentity",
                          "field": "name",
                          "operator": "EqualTo",
                          "value": "testentity3",
                          "matchCase": False
                      }
                  ],
                  "groups": [
                      {
                          "operator": "AND",
                          "criteria": [
                              {
                                  "recordset": "testrecordset",
                                  "field": "field1",
                                  "operator": "EqualTo",
                                  "value": "aardvark",
                                  "matchCase": False,
                                  "not": True
                              },
                              {
                                  "recordset": "testrecordset",
                                  "field": "field2",
                                  "operator": "Like",
                                  "value": "lar",
                                  "matchCase": False,
                              }
                          ]
                      }
                  ]
              }


class ServerTest(TestCase):
    """
    Provides a server for the other TestCase classes.
    """

    @classmethod
    def setUpClass(cls):
        cls.server = CoalecsceServer(server_URL = SERVER_URL,
                                     CRUD_persistor = CRUD_PERSISTOR,
                                     search_persistor = SEARCH_PERSISTOR)


class EntityTests(ServerTest):
    """
    Methods that set values used by later methods must be class methods.
    """

    @classmethod
    def test_save_template(cls):

        cls.template1_key = \
            save_template(server = cls.server, template = TEMPLATE1_XML)

        template2 = parseString(TEMPLATE2_XML,
                                object_class = CoalesceEntityTemplate)
        cls.template2_key = \
            save_template(server = cls.server, template = template2)

        cls.template3_key = \
            save_template(server = cls.server, template = TEMPLATE3_DICT)

        # Did the server return valid UUID keys?

        tested_key1 = _test_key(cls.template1_key)
        with cls.subTest():
            cls.assertEqual(cls.template1_key, tested_key1)

        tested_key2 = _test_key(cls.template2_key)
        with cls.subTest():
            cls.assertEqual(cls.template2_key, tested_key2)

        tested_key3 = _test_key(cls.template3_key)
        with cls.subTest():
            cls.assertEqual(cls.template3_key, tested_key3)


    def test_register_template(self):

        success1 = register_template(server = self.server,
                                     key = self.template1_key)
        with self.subTest():
            self.assertTrue(success1)
        success2 = register_template(server = self.server,
                                     key = self.template2_key)
        with self.subTest():
            self.assertTrue(success2)
        success3 = register_template(server = self.server,
                                     key = self.template3_key)
        with self.subTest():
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

        orig_template3 = json.loads(TEMPLATE3_DICT)
        template3_nsv = [orig_template3["name"], orig_template3["source"],
                         orig_template3["version"]]
        field3_1_name = \
            orig_template3["sectionsAsList"][0]["recordsetsAsList"][0]["fielddefinitions"][0]["name"]

        template2_dict = read_template(server = self.server,
                                      template = self.template2_key,
                                      output = "dict")
        dict_field2_2_name = \
            template2_dict["sectionsAsList"][0]["recordsetsAsList"][0]["fieldDefinitions"][1]["name"]
        with self.subTest():
            self.assertEqual(dict_field2_2_name, field2_2_name)

        template_entity_object = read_template(server = self.server,
                                               template = self.template1_key,
                                               output = "entity_object")
        obj_field1_1_name = \
            template_entity_object.section[0].recordset[0].fieldefinition[0].name
        with self.subTest():
            self.assertEqual(obj_field1_1_name, field1_1_name)

        text_JSON = read_template(server = self.server,
                                  template = self.template2_key, output = "JSON")
        template_JSON = json.loads(text_JSON)
        JSON_field2_2_name = \
            template_JSON["sectionsAsList"][0]["recordsetsAsList"][0]["fieldDefinitions"][1]["name"]
        with self.subTest():
            self.assertEqual(JSON_field2_2_name, field2_2_name)

        text_XML = read_template(server = self.server,
                                 template = template3_nsv, output = "XML")
        template_XML = xmltodict.parse(text_XML)
        XML_field3_1_name = \
            template_XML["entity"]["section"]["recordset"]["fielddefinition"][0]["@name"]
        with self.subTest():
            self.assertEqual(XML_field3_1_name, field3_1_name)


    def test_get_template_list(self):

        template_list = get_template_list(server = self.server, output = "list")
        key_list = [template["key"] for template in template_list]
        with self.subTest():
            self.asertTrue(self.template1_key in key_list)
        with self.subTest():
            self.asertTrue(self.template2_key in key_list)
        with self.subTest():
            self.asertTrue(self.template3_key in key_list)

        templates_JSON = get_template_list(server = self.server, output = "JSON")
        template_JSON_list = json.loads(templates_JSON)
        key_JSON_list = [template["key"] for template in template_JSON_list]
        with self.subTest():
            self.asertTrue(self.template1_key in key_JSON_list)
        with self.subTest():
            self.asertTrue(self.template2_key in key_JSON_list)
        with self.subTest():
            self.asertTrue(self.template3_key in key_JSON_list)


    def test_delete_template(self):

        success1 = delete_template(server = self.server,
                                   template = self.template1_key)
        with self.subTest():
            self.assertTrue(success1)

        success2 = delete_template(server = self.server,
                                   template = self.template2_key)
        with self.subTest():
            self.assertTrue(success2)

        success3 = delete_template(server = self.server,
                                   template = self.template3_key)
        with self.subTest():
            self.assertTrue(success3)


    @classmethod
    def test_construct_entity(cls):

        cls.entity1_key = unicode(uuid4())
        entity2_UUID = uuid4()
        cls.entity2_key = unicode(entity2_UUID)
        cls.entity3_key = unicode(uuid4())
        cls.entity4_key = unicode(uuid4())

        # The chance of a collision is infinitesimal, but it would make
        # debugging tough.
        while len(set([cls.entity1_key, cls.entity2_key, cls.entity2_key,
                       cls.entity2_key])) != 4:
            cls.entity1_key = unicode(uuid4())
            cls.entity2_key = unicode(uuid4())
            cls.entity3_key = unicode(uuid4())
            cls.entity4_key = unicode(uuid4())

        cls.entity1 = construct_entity(server = cls.server,
                                       template = cls.template1_key,
                                       key = cls.entity1_key,
                                       fields = {"Field1": "foo", "Field2": "bar"})

        cls.entity2 = construct_entity(server = cls.server,
                                       template = cls.template2_key,
                                       key = entity2_UUID,
                                       fields = {"Field1": "Spam", "Field2": "eggs"})

        entity1_field1 = \
            cls.entity1.section[0].recordset[0].record[0].field[0].value
        with cls.subTest():
            cls.assertEqual(entity1_field1, "foo")
        entity2_field2 = \
            cls.entity1.section[0].recordset[0].record[0].field[1].value
        with cls.subTest():
            cls.assertEqual(entity2_field2, "eggs")


    def test_create(self):

        success1 = create(server = self.server, new_entity = self.entity1)
        with self.subTest():
            self.assertTrue(success1)

        entity2_XML = to_XML_string(self.entity2)
        success2 = create(server = self.server, new_entity = entity2_XML)
        with self.subTest():
            self.assertTrue(success2)

        success3 = create(server = self.server, new_entity = ENTITY3_DICT,
                          key = self.entity3_key)
        with self.subTest():
            self.assertTrue(success3)

        entity4_JSON = json.dumps(ENTITY4_DICT)
        response4 = create(server = self.server, new_entity = entity4_JSON,
                           key = self.entity4_key, full_response = True)
        status4 = response4.status_code
        with self.subTest():
            self.assertEqual(status4, 204)


    def test_read(self):

        orig_name1 = self.entity1["name"]
        orig_name2 = self.entity2["name"]
        orig_name3 = ENTITY3_DICT["name"]
        orig_name4 = ENTITY4_DICT["name"]

        entity1_dict = read_template(server = self.server, key = self.entity1_key,
                                     output = "dict")
        name1 = entity1_dict["name"]
        with self.subTest():
            self.assertEqual(name1, orig_name1)

        entity2_object = read_template(server = self.server,
                                       key = self.entity2_key,
                                       output = "entity_object")
        name2 = entity2_object.name
        with self.subTest():
            self.assertEqual(name2, orig_name2)

        JSON_text = read_template(server = self.server, key = self.entity3_key,
                                  output = "JSON")
        entity3_JSON = json.loads(JSON_text)
        name3 = entity3_JSON["name"]
        with self.subTest():
            self.assertEqual(name3, orig_name3)

        XML_text = read_template(server = self.server, key = self.entity4_key,
                                 output = "XML")
        entity4_XML = xmltodict.parse(XML_text)
        name4 = entity4_XML["entity"]["@name"]
        with self.subTest():
            self.assertEqual(name4, orig_name4)


    def test_update(self):

        self.entity1.title = "Gumby #1"
        success1 = create(server = self.server, updated_entity = self.entity1)
        with self.subTest():
            self.assertTrue(success1)

        self.entity2.title = "Gumby #2"
        entity2_XML = to_XML_string(self.entity2)
        success2 = create(server = self.server, updated_entity = entity2_XML)
        with self.subTest():
            self.assertTrue(success2)

        ENTITY3_DICT["title"] = "Gumby #3"
        success3 = create(server = self.server, updated_entity = ENTITY3_DICT,
                          key = self.entity3_key)
        with self.subTest():
            self.assertTrue(success3)

        ENTITY4_DICT["title"] = "Gumby #4"
        entity4_new_JSON = json.dumps(ENTITY3_DICT)
        response4 = create(server = self.server, updated_entity = entity4_new_JSON,
                           key = self.entity4_key, full_response = True)
        status4 = response4.status_code
        with self.subTest():
            self.assertEqual(status4, 204)


    def test_delete(self):

        success1 = delete(server = self.server, keys = self.entity1_key)
        with self.subTest():
            self.assertTrue(success1)

        delete_list = [self.entity2_key, self.entity3_key, self.entity4_key]
        success2 = delete(server = self.server, keys = delete_list)
        with self.subTest():
            self.assertTrue(success2)


    @classmethod
    def test_linkage_manipulation(cls):
        """
        Create one set of test linkages for each types of input, with three
        linkages in each set, to allow testing both single-linkage and
        multiple-linkage input.  This also tests the linkage classes and their
        constructor and output methods.

        Note that this method doesn't feature any asserts--it just throws an
        error is something goes wrong.
        """

        # Set for dict input
        cls.linkage01_dict = {"source": cls.entity1_key, "target": cls.entity2.key}
        cls.linkage02_dict = {"source": cls.entity2_key, "target": cls.entity3.key,
                              "label": "Brain hurts", "type": "IS_PARENT_OF"}
        cls.linkage03_dict = {"source": cls.entity3_key, "target": cls.entity4.key,
                              "type": "IS_BEING_WATCHED_BY",
                              "isBiDirectional": True}

        # Set for JSON input
        linkage11_dict = {"source": cls.entity2_key, "target": cls.entity3.key}
        cls.linkage11_JSON = json.dumps(linkage11_dict)
        linkage12_dict = {"source": cls.entity3_key, "target": cls.entity4.key,
                          "label": "Brain hurts", "type": "IS_PARENT_OF"}
        linkage13_dict = {"source": cls.entity4_key, "target": cls.entity1.key,
                          "type": "IS_BEING_WATCHED_BY", "isBiDirectional": True}
        cls.linkage1_array_JSON = json.dumps([linkage12_dict, linkage13_dict])

        # Set for CoalesceAPILinkage input
        cls.linkage21_API = CoalesceAPILinkage(source = cls.entity3_key,
                                               target = cls.entity4.key,
                                               linkage_type = "WAS_CREATED_BY")
        linkage22_dict = {"source": cls.entity4_key, "target": cls.entity1.key,
                          "label": "Brain hurts", "type": "IS_PARENT_OF"}
        cls.linkage22_API = CoalesceAPILinkage.from_dict(linkage22_dict)
        linkage23_XSD = CoalesceLinkage(entity1key = cls.entity1_key,
                                        entity2key = cls.entity2.key,
                                        linktype = "IsBeingWatchedBy")
        cls.linkage23_API = linkage23_XSD.to_API(isBiDirectional = True)

        # Set for XML input
        linkage31_XSD = CoalesceLinkage(entity1key = cls.entity4_key,
                                        entity2key = cls.entity1.key)
        cls.linkage31_XML = to_XML_string(linkage31_XSD)
        linkage3_API = CoalesceAPILinkage(source = cls.entity2_key,
                                          target = cls.entity3.key,
                                          linkage_type = "IS_BEING_WATCHED_BY",
                                          isBiDirectional = True)
        cls.linkage32_XML = to_XML_string(linkage3_API.to_XSD())
        cls.linkage33_XML = to_XML_string(linkage3_API.reverse_to_XSD())

        # Set for CoalesceLinkage input
        cls.linkage41_XSD = CoalesceLinkage(entity1key = cls.entity1_key,
                                            entity2key = cls.entity2.key,
                                            label = "Brain hurts",
                                            linktype = "IsParentOf")
        linkage4_API = CoalesceAPILinkage(source = cls.entity1_key,
                                          target = cls.entity4.key,
                                          linkage_type = "IS_A_PEER_OF",
                                          isBiDirectional = True)
        cls.linkage42_XSD = linkage4_API.to_XSD()
        cls.linkage43_XSD = linkage4_API.reverse_to_XSD()


    def test_create_linkages(self):

        success01 = create_linkages(server = self.server,
                                    linkages = self.linkage01_dict)
        with self.subTest():
            self.assertTrue(success01)
        success02 = create_linkages(server = self.server,
                                    linkages = [self.linkage02_dict,
                                                self.linkage03_dict])
        with self.subTest():
            self.assertTrue(success02)

        success11 = create_linkages(server = self.server,
                                    linkages = self.linkage11_JSON)
        with self.subTest():
            self.assertTrue(success11)
        success12 = create_linkages(server = self.server,
                                    linkages = self.linkage1_array_JSON)
        with self.subTest():
            self.assertTrue(success12)

        success21 = create_linkages(server = self.server,
                                    linkages = self.linkage21_API)
        with self.subTest():
            self.assertTrue(success21)
        success22 = create_linkages(server = self.server,
                                    linkages = [self.linkage22_API,
                                                self.linkage23_API])
        with self.subTest():
            self.assertTrue(success22)

        success31 = create_linkages(server = self.server,
                                    linkages = self.linkage31_XML)
        with self.subTest():
            self.assertTrue(success31)
        success32 = create_linkages(server = self.server,
                                    linkages = [self.linkage32_XML,
                                                self.linkage33_XML])
        with self.subTest():
            self.assertTrue(success32)

        success41 = create_linkages(server = self.server,
                                    linkages = self.linkage41_XSD)
        with self.subTest():
            self.assertTrue(success41)
        success42 = create_linkages(server = self.server,
                                    linkages = [self.linkage42_XSD,
                                                self.linkage43_XSD])
        with self.subTest():
            self.assertTrue(success42)


    def test_read_linkages(self):

        linkages1 = read_linkages(server = self.server, key = self.entity1_key,
                                  output  = "JSON")
        num_links1 = len(json.loads(linkages1))
        with self.subTest():
            self.assertEqual(num_links1, 5)

        linkages2 = read_linkages(server = self.server, key = self.entity2_key,
                                  output  = "dict_list")
        num_links2 = len(linkages2)
        with self.subTest():
            self.assertEqual(num_links2, 4)

        linkages3 = read_linkages(server = self.server, key = self.entity3_key,
                                  output  = "API_list")
        num_links3 = len(linkages3)
        with self.subTest():
            self.assertEqual(num_links3, 4)

        linkages4 = read_linkages(server = self.server, key = self.entity4_key,
                                  output  = "JSON")
        num_links4 = len(json.loads(linkages4))
        with self.subTest():
            self.assertEqual(num_links4, 5)


    def test_delete_linkages(self):

        success01 = delete_linkages(server = self.server,
                                    linkages = self.linkage01_dict)
        with self.subTest():
            self.assertTrue(success01)
        success02 = delete_linkages(server = self.server,
                                    linkages = [self.linkage02_dict,
                                                self.linkage03_dict])
        with self.subTest():
            self.assertTrue(success02)

        success11 = delete_linkages(server = self.server,
                                    linkages = self.linkage11_JSON)
        with self.subTest():
            self.assertTrue(success11)
        success12 = delete_linkages(server = self.server,
                                    linkages = self.linkage1_array_JSON)
        with self.subTest():
            self.assertTrue(success12)

        success21 = delete_linkages(server = self.server,
                                    linkages = self.linkage21_API)
        with self.subTest():
            self.assertTrue(success21)
        success22 = delete_linkages(server = self.server,
                                    linkages = [self.linkage22_API,
                                                self.linkage23_API])
        with self.subTest():
            self.assertTrue(success22)

        success31 = delete_linkages(server = self.server,
                                    linkages = self.linkage31_XML)
        with self.subTest():
            self.assertTrue(success31)
        success32 = delete_linkages(server = self.server,
                                    linkages = [self.linkage32_XML,
                                                self.linkage33_XML])
        with self.subTest():
            self.assertTrue(success32)

        success41 = delete_linkages(server = self.server,
                                    linkages = self.linkage41_XSD)
        with self.subTest():
            self.assertTrue(success41)
        success42 = delete_linkages(server = self.server,
                                    linkages = [self.linkage42_XSD,
                                                self.linkage43_XSD])
        with self.subTest():
            self.assertTrue(success42)


class SearchTests(ServerTest):

    def test_search(self):

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

        results1_list = search(server = self.server, query = QUERY1_DICT,
                               property_names = ["testrecordset.field1"],
                               output = "list")
        results1_first_field = results1_list[0]["values"][0]
        with self.subTest():
            self.assertEqual(results1_first_field, orig1_first_field)

        query2_JSON = json.dumps(QUERY2_DICT)
        results2_full_dict = search(server = self.server, query = query2_JSON,
                                    property_names = ["testrecordset.field1"],
                                    output = "full_dict")
        results2_first_field = results2_full_dict["hits"][0]["values"][0]
        with self.subTest():
            self.assertEqual(results2_first_field, orig2_first_field)

        results3_JSON = search(server = self.server, query = QUERY1_DICT,
                               property_names = ["testrecordset.field1"],
                               output = "JSON")
        results3_first_field = json.loads(result3_JSON)["hits"][0]["values"][0]
        with self.subTest():
            self.assertEqual(results3_first_field, orig1_first_field)


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
                                      recordset = "testrecordset",
                                      field = "field1", operator = "EqualTo",
                                      value = "Sir",
                                      property_names = ["testrecordset.field1"],
                                      output = "list")
        results1_first_field = results1_list[0]["values"][0]
        with self.subTest():
            self.assertEqual(results1_first_field, orig1_first_field)

        results2_full_dict = search_simple(server = self.server,
                                           recordset = "testrecordset",
                                           field = "field2", operator = "Like",
                                           value = "lar",
                                           property_names = ["testrecordset.field1"],
                                           output = "full_dict")
        results2_first_field = results2_full_dict["hits"][0]["values"][0]
        with self.subTest():
            self.assertEqual(results2_first_field, orig2_first_field)

        results3_JSON = search_simple(server = self.server,
                                      recordset = "testrecordset",
                                      field = "field1", operator = "EqualTo",
                                      value = "Sir",
                                      property_names = ["testrecordset.field1"],
                                      output = "JSON")
        results3_first_field = json.loads(result3_JSON)["hits"][0]["values"][0]
        with self.subTest():
            self.assertEqual(results3_first_field, orig1_first_field)


TESTS = (EntityTests("test_save_template"), EntityTests("test_register_template"),
         EntityTests("test_read_template"), EntityTests("get_template_list"),
         EntityTests("test_construct_entity"), EntityTests("test_read"),
         EntityTests("test_update"), EntityTests("test_save_template"),
         EntityTests("test_create_linkages"), EntityTests("test_read_linkagees"),
         SearchTests("test_search"), SearchTests("test_search_simple"),
         EntityTests("test_delete_linkages"), EntityTests("test_delete"),
         EntityTests("test_delete_template"))


def Coalesce_test_suite():
    return TestSuite(TESTS)

