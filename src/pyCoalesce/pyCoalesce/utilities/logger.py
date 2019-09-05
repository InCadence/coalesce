"""
@author: Scott Orr

Unless :data:`pyCoalesce.utilities.logger.package_logger` is imported by
the main app, all log messages for the package are piped to
:class:`logging.NullHandler`--that is, there's no output at all.


"""

import logging

package_logger = logging.getLogger(__name__)
"""
Import this logger and assign a handler to it in order to process log
messages from pyCoalesce modules.

"""

# This lets any handlers' levels control what's logged.  Weirdly, setting
# the level to "NOTSET" (0) doesn't work--it causes the logger in question
# to inherit the root logger's default value of "WARN" (30), which will
# pose a problem in applications not using the root logger.
package_logger.setLevel(1)

package_logger.addHandler(logging.NullHandler())