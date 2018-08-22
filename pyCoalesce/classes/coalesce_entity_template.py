#!/usr/bin/env python

#
# This subclass allows the creation of template objects that can be used to
# create new instances of class "entity", along with their child objects.

import sys
from uuid import uuid4

from coalesce_entity import CoalesceEntity
from entity_utilities import parseString, to_XML_string


#
# Globals
#

ExternalEncoding = ''


#
# Data representation classes
#

class CoalesceEntityTemplate(CoalesceEntity):
    """
    Adds a method to create a regular entity from the template, and overrides
    the "build" method to store the XML representation needed to build the
    new entity.
    """

    def __init__(self, key=None, datecreated=None, lastmodified=None, name=None,
                 status=None, noindex=None, modifiedby=None, modifiedbyip=None,
                 objectversion=None, objectversionstatus=None,
                 previoushistorykey=None, disablehistory=None, history=None,
                 source=None, version=None, entityid=None, entityidtype=None,
                 title=None, linkagesection=None, section=None):

        super(CoalesceEntityTemplate, self).__init__(key, datecreated,
              lastmodified, name, status, noindex, modifiedby, modifiedbyip,
              objectversion, objectversionstatus, previoushistorykey,
              disablehistory, history, source, version, entityid, entityidtype,
              title, linkagesection, section)

        self.XML = to_XML_string(self, 1)


    def build(self, node):

        super(type(self), self).build(node)

        # Store an XML representation of the template to use in building
        # entities based on the template.
        self.XML = to_XML_string(self, 1)

        return self


    def _populate_min_records(self, current_section):

        for subsection in current_section.section:
            self._populate_min_records(subsection)

        for current_recordset in current_section.recordset:

            # Create and populate the new records.
            for i in xrange(current_recordset.minrecords):

                current_recordset.create_record_from_definitions()

        return


    def new_entity(self, key = None, populate_min_records = True):
        """
        Creates a new instance of class "entity" based on the template.

        :param key:  a UUID.  For most applications, there's no real reason to
            supply one.
        :param populate_min_records:  if True, build a number of records in
            each recordset equal to "minrecords" for that recordset.

        :return:  a new instance of class "entity".
        """

        new_entity = parseString(self.XML, object_class = type(self).mro()[1])

        if key:
            new_entity.key = key
        else:
            new_entity.key = uuid4()

        new_entity.template = self

        if populate_min_records:
            for current_section in new_entity.section:
                self._populate_min_records(current_section)

        return new_entity

# end class CoalesceEntityTemplate


USAGE_TEXT = """
Usage: python entity_template.py <infilename>
"""


def usage():
    print(USAGE_TEXT)
    sys.exit(1)


def main():
    args = sys.argv[1:]
    if len(args) != 1:
        usage()
    infilename = args[0]
    parse(infilename)


if __name__ == '__main__':
    #import pdb; pdb.set_trace()
    main()
