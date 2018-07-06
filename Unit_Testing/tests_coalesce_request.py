# -*- coding: utf-8 -*-
"""
Created on Fri Jul  6 11:39:12 2018

@author: dvenkat
"""

import unittest

class test_search(unittest.TestCase):
    
    def test_header(self):
        self.assertEqual(type(search.headers), dict)
        
    def test_URL(self):
        self.assertEqual(type(URL), str)
    
    def test_data(self):
        self.assertEqual(type(data), dict)
        self.assertIn("group", data)
        
    def test_method(self):
        self.assertEqual(search.method, "post")
    
class test_read(unittest.TestCase):
    
    def test_header(self):
        self.assertEqual(type(read.headers), dict)
    
    def test_URL(self):
        self.assertEqual(type(URL), str)
    
    def test_data(self):
        self.assertEqual(type(data), dict)
        self.assertIn("group", data)
    
    def test_method(self):    
        self.assertEqual(read.method, "get")
        

class test_delete(unittest.TestCase):

class test_update(unittest.TestCase):

class test_create(unittest.TestCase):
    
    
if __name__ == '__main__':
    unittest.main()