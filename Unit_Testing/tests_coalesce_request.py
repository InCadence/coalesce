# -*- coding: utf-8 -*-
"""
Created on Fri Jul  6 11:39:12 2018

@author: dvenkat
"""

Configurations = { "SetUp": {"TYPE": "OEEvent", "FIELDSADDED": [{"flatten":"true"}], "TESTING":"true"}, #SetUp is also used in tear down due to identical settings
                   "Search": {"FIELDS":["objectkey"], "OPERATORS":"="},
                   "Read": {},
                   "Create": {},
                   "Update": {},
                   "Delete": {}
                   }
"""
The configurations currently do not have any specific values in Read, Create, or Update due to the vast majority of tests
being done through SetUp.
"""

import unittest
from pyCoalesce.coalesce_request import *

class test_search(unittest.TestCase):

    def setUp(self):
        self.new = create(TYPE = Configurations["SetUp"]["TYPE"],FIELDSADDED= Configurations["SetUp"]["FIELDSADDED"],
                          TESTING=Configurations["SetUp"]["TESTING"])
        self.key = self.new[1]["key"]

    def test_response_status(self):
        self.assertEqual(type(search(FIELDS=Configurations["Search"]["FIELDS"], OPERATORS=Configurations["Search"]["OPERATORS"], VALUES=["{}".format(self.key)])), unicode)

    def test_response_simple_search(self):
        self.search = search(FIELDS=Configurations["Search"]["FIELDS"], VALUES=[self.key])
        self.assertEqual(type(self.search), unicode)

    def test_response_complex_search(self):
        self.search = search(SUB_OPERATIONS=u"complex", FIELDS= ['objectkey'],  query=[{"key":0,"recordset":"CoalesceEntity","field":"objectkey",
                                                                "operator":"EqualTo","value":"{}".format(self.key),"matchCase":"false"
                                                                 }])
    def tearDown(self):
        delete(key=self.create[1]["key"])

class test_read(unittest.TestCase):
    #Add tests for each field query
    def setUp(self):
        self.new = create(TYPE=Configurations["SetUp"]["TYPE"], FIELDSADDED=Configurations["SetUp"]["FIELDSADDED"],
                          TESTING=Configurations["SetUp"]["TESTING"])
        self.key = self.new[1]["key"]

    def test_default_response_status(self):
        with self.assertRaises(ValueError):
            read()

    def test_created_response_status(self):
        self.assertTrue(read(ARTIFACT=Configurations["SetUp"]["TYPE"], KEY=self.key))

    def tearDown(self):
        delete(key=self.create[1]["key"])

class test_delete(unittest.TestCase):

    def setUp(self):
        self.create = create(TYPE=Configurations["SetUp"]["TYPE"], FIELDSADDED=Configurations["SetUp"]["FIELDSADDED"],
                          TESTING=Configurations["SetUp"]["TESTING"])
        self.delete = delete(KEY=self.create[1]["key"], TYPE=Configurations["SetUp"]["TYPE"])

    def test_response_status(self):
        self.assertTrue(self.delete)

class test_update(unittest.TestCase):

    def setUp(self):
        self.create = create(TYPE=Configurations["SetUp"]["TYPE"], FIELDSADDED=Configurations["SetUp"]["FIELDSADDED"],
                          TESTING=Configurations["SetUp"]["TESTING"])
        self.key = self.create[1]["key"]
        self.update = update(VALUE =Configurations["SetUp"]["TYPE"], KEY=self.key, TESTING="true", NEWVALUES=Configurations["SetUp"]["FIELDSADDED"])

    def test_response_status(self):
        self.assertEqual(self.update[0].status_code, 204)

    def test_data(self):
        self.assertEqual(self.create[1]["flatten"], Configurations["SetUp"]["FIELDSADDED"][0]["flatten"])

    def tearDown(self):
        delete(key=self.create[1]["key"])

class test_create(unittest.TestCase):
    def setUp(self):
        self.create = create(TYPE=Configurations["SetUp"]["TYPE"], FIELDSADDED=Configurations["SetUp"]["FIELDSADDED"],
                             TESTING=Configurations["SetUp"]["TESTING"])
        self.key = self.create[1]["key"]

    def test_data(self):
        self.assertEqual(self.create[1]["flatten"], "true")

    def test_response_status(self):
        self.assertEqual(self.create[0].status_code, 204)

    def tearDown(self):
        delete(key=self.create[1]["key"])
