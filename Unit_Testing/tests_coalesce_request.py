# -*- coding: utf-8 -*-
"""
Created on Fri Jul  6 11:39:12 2018

@author: dvenkat
"""

import ConfigParser
import unittest
from wrapper.coalesce_request import *

config = ConfigParser.ConfigParser()
config.read('config.ini')


class test_search(unittest.TestCase):

    def setUp(self):
        self.new = create(TYPE = "OEEvent",FIELDSADDED= [{"flatten":"true"}], TESTING="true")
        self.key = self.new[1]["key"]

    def test_response_status(self):
        self.search = search(VALUES=[self.key], FIELDS=["objectkey"])
        self.assertEqual(type(search(FIELDS=["objectkey"], OPERATORS=["="], VALUES=["{}".format(self.key)])), unicode)

    def test_response_simple_search(self):
        self.search = search(FIELDS=["objectkey"], VALUES=[self.key])
        self.assertEqual(type(self.search), unicode)

    def test_response_complex_search(self):
        self.search = search(SUB_OPERATIONS=u"complex", FIELDS= ['objectkey'],  query=[{"key":0,"recordset":"CoalesceEntity","field":"objectkey",
                                                                "operator":"EqualTo","value":"{}".format(self.key),"matchCase":"false"
                                                                 }])
    def tearDown(self):
        delete(KEY=self.key, TYPE="OEEvent")

class test_read(unittest.TestCase):
    #Add tests for each field query
    def setUp(self):
        self.new = create(TYPE="OEEvent",FIELDSADDED=[{"flatten":"true"}], TESTING="true")
        self.key = self.new[1]["key"]

    def test_default_response_status(self):
        with self.assertRaises(ValueError):
            read()

    def test_created_response_status(self):
        self.assertTrue(read(ARTIFACT="OEEvent", KEY=self.key))

    def tearDown(self):
        delete(KEY=self.key, TYPE="OEEvent")

class test_delete(unittest.TestCase):

    def setUp(self):
        self.create = create(TYPE="OEEvent",FIELDSADDED=[{"flatten":"true"}], TESTING = "true")
        self.delete = delete(KEY=self.create[1]["key"], TYPE="OEEvent")

    def test_response_status(self):
        self.assertEqual(self.delete.status_code, 204)

class test_update(unittest.TestCase):

    def setUp(self):
        self.create = create(TYPE="OEEvent",FIELDSADDED=[{"flatten":"true"}], TESTING = "true")
        self.key = self.create[1]["key"]
        self.update = update(VALUE = "OEEvent", KEY=self.key, TESTING="true", NEWVALUES=[{"flatten":"true"}])

    def test_response_status(self):
        self.assertEqual(self.update[0].status_code, 204)

    def test_data(self):
        self.assertEqual(self.create[1]["flatten"], "true")

    def tearDown(self):
        delete(TYPE="OEEvent", KEY=self.create[1]["key"])
        
class test_create(unittest.TestCase):
    def setUp(self):
        self.create = create(TYPE="OEEvent",FIELDSADDED=[{"flatten":"true"}], TESTING = "true")
        self.key = self.create[1]["key"]

    def test_data(self):
        self.assertEqual(self.create[1]["flatten"], "true")

    def test_response_status(self):
        self.assertEqual(self.create[0].status_code, 204)

    def tearDown(self):
        delete(TYPE= "OEEvent", KEY=self.key)