# -*- coding: utf-8 -*-
"""
@author: sorr
"""

import logging


# Set up a package master log and a nullHandler for it--unless the
# calling app sets up logging for the package, the logging won't
# do anything.
package_logger = logging.getLogger(__name__)
package_logger.addHandler(logging.NullHandler())