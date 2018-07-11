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

class prepare_coalesce(unittest.TestCase):
    def setUp(self):
        self.new = create(TYPE = "OEEvent",VALUES = {"flatten":"false"})
        self.key = self.new.data["key"]

class test_search(unittest.TestCase):
    #Search tests done
    def test_response_status(self):
        self.search = search(VALUES = [key], FIELDS = ["objectkey"])
        self.assertEqual(type(self.search), unicode)

    def test_response_simple_search(self):
        self.search = search(FIELDS = ["objectkey"])
        self.assertEqual(type(self.search), unicode)

    def test_response_complex_search(self):
        self.search = search(SUB_OPERATIONS=u"complex", query=[{}])

class test_read(unittest.TestCase):
        
    def test_default_response_status(self):
        with self.assertRaise(ValueError):
            read()

    def test_created_response_status(self):
        self.asserEqual(read(ARTIFACT="OEEvent", KEY=key), True)
    #Test a read function with my function
class test_delete(unittest.TestCase):
    def setUp(self):
        self.delete = delete()
        
    def test_response_status(self):
        self.assertEqual(self.delete.status_code, 204)

class test_update(unittest.TestCase):
    def setUp(self):
        self.update = update()
        
    def test_response_status(self):
        self.assertEqual(self.update.status_code, 204)
        
class test_create(unittest.TestCase):
    def setUp(self):
        self.create = create()
        
    def test_response_status(self):
        self.assertEqual(self.create.status_code, 204)

class revert_coalesce(unittest.TestCase):
    def tearDown(self):
        self.delete = delete(TYPE = ["OEEvent"], KEY = key)
