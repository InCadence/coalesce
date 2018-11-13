# -*- coding: utf-8 -*-
"""
Created on Thu Sep 13 11:19:16 2018

@author: sorr
"""

from unittest import TextTestRunner

from unit_tests import pyCoalesce_test_suite

result = TextTestRunner(verbosity = 2).run(pyCoalesce_test_suite())