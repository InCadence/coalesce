# -*- coding: utf-8 -*-
"""
Created on Fri Jul  6 11:39:12 2018

@author: dvenkat
"""

import unittest
from coalesce_request import *

class test_search(unittest.TestCase):
    def setUp(self):
        self.search = search()
    
    def test_response_status(self):
        self.assertEqual(type(self.search), unicode)

class test_read(unittest.TestCase):
    def setUp(self):
        self.read = read()
        
    def test_response_status(self):
        with self.assertRaise(ValueError):
            read()
        
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
