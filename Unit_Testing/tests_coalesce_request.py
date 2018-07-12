# -*- coding: utf-8 -*-
"""
Created on Fri Jul  6 11:39:12 2018

@author: dvenkat
"""

#Write a test for anywhere I write a query
#   - Read is done, only need to check response
#   - Can test delete by deleting what I set up
#   - Test simple search by sending in simple query with the object created, test complex by searching specifically for key
#   - Create is tested by making sure object is created with fields
#   - Update can be tested by updating object then checking to see if fields were updated


import ConfigParser
import unittest
from wrapper.coalesce_request import *

config = ConfigParser.ConfigParser()
config.read('config.ini')

class test_search(unittest.TestCase):

    def setUp(self):
        self.new = create(TYPE = "OEEvent",FIELDSADDED= {"flatten":"false"}, TESTING="true")
        self.key = self.new[1]["key"]

    def test_response_status(self):
        self.search = search(VALUES=[self.key], FIELDS=["objectkey"])
        self.assertEqual(type(search(FIELDS=["objectkey"], OPERATORS=["="], VALUES=["{}".format(self.key)])), unicode)

    def test_response_simple_search(self):
        self.search = search(FIELDS=["objectkey"], VALUES=[self.key])
        self.assertEqual(type(self.search), unicode)

    def test_response_complex_search(self):
        self.search = search(SUB_OPERATIONS=u"complex", query=[{"field":"objectkey"},
                                                                {"key":0,"recordset":"CoalesceEntity","field":"objectkey",
                                                                "operator":"=","value":"{}".format(self.key),"matchCase":"false"
                                                                 }])
    def tearDown(self):
        delete(KEY=self.key, TYPE="OEEvent")

class test_read(unittest.TestCase):
    #Add tests for each field query
    def setUp(self):
        self.new = create(TYPE="OEEvent",FIELDSADDED={"flatten":"false"}, TESTING="true")
        self.key = self.new[1]["key"]

    def test_default_response_status(self):
        with self.assertRaises(ValueError):
            read()

    def test_created_response_status(self):
        self.assertTrue(read(ARTIFACT="OEEvent", KEY=self.key))
    # Test a read function with my function

    def tearDown(self):
        delete(KEY=self.key, TYPE="OEEvent")

class test_delete(unittest.TestCase):

    def setUp(self):
        self.create = create(TYPE="OEEvent")
        self.delete = delete(KEY=self.create[1]["key"], TYPE="OEEvent")
        
    def test_response_status(self):
        self.assertEqual(self.delete.status_code, 204)

class test_update(unittest.TestCase):
    def setUp(self):
        self.create = create(TYPE="OEEvent", TESTING="true")
        self.update = update(NEWVALUES={"flatten":"false"}, KEY=self.create[1]["key"])
        
    def test_response_status(self):
        self.assertEqual(self.update.status_code, 204)

    def tearDown(self):
        delete(TYPE="OEEvent", KEY=self.create["key"])
        
class test_create(unittest.TestCase):
    def setUp(self):
        self.create = create()
        self.key = create[1]["key"]
        
    def test_response_status(self):
        self.assertEqual(self.create[0].status_code, 204)

    def tearDown(self):
        self.delete(TYPE= "OEEvent", KEY=self.key)