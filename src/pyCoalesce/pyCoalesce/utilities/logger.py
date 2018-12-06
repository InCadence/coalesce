"""
@author: Scott Orr

Unless :data:`pyCoalesce.utilities.logger.package_logger` is imported by
the main app, all log messages for the package are piped to
:class:`logging.NullHanlder`--that is, there's no output at all.


"""

import logging

package_logger = logging.getLogger(__name__)
"""
Import this logger and assign a handler to it in order to process log
messages from pyCoalesce modules.

"""

package_logger.addHandler(logging.NullHandler())