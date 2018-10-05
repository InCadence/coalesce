#!/usr/bin/env python
"""

This subclass allows the creation of template objects that can be used to
create new instances of class "entity", along with their child objects.  The
subclass can be imported directly from the :mod:`pyCoalesce.classes` module.

The :meth:`~pyCoalesce.classes.coalesce_entity_template.CoalesceEntityTemplate.new_entity`
method doesn't properly with iPython's auto-reload feature.  This problem can
be overcome by restarting the kernel and reimporting everything at once.

"""

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
    new entity, thereby avoiding repeated calls of
    :func:`pyCoalesce.classes.entity_utilities.parseString`.

    :ivar XML:  an XML representation of the template, stored as a string.
        Unlike the other attributes, this one isn't part of the template on
        the server.
    :ivar key:  a UUID key as a string, formed by the server by hashing the
        template's :attr:`name`, :attr:`source`, and :attr:`version`
    :ivar name:  the name of the template; with :attr:`source` and
        :attr:`version`, one of the three attributes that uniquely identify
        the template
    :ivar source:  the source (e.g., a particular project) of the template;
        with :attr:`.name` and :attr:`version`, one of the three attributes
        that uniquely identify the template
    :ivar version:  the version of the template; with :attr:`name` and
        :attr:`version`, one of the three attributes that uniquely identify
        the template
    :ivar section:  a list of the template's :class:`sections
        <pyCoalesce.classes.coalesce_entity.sectionSub>`, and by
        extension, the sections of any entity created from the template
    :ivar noindex:  a boolean indicating whether or not to index the template
        for search
    :ivar disablehistory:  a boolean that serves as a default value for the
        same attribute for entities created from the template, specifying
        whether or not each entity's history of revisions should be retained
    :ivar datecreated:  the date on which the template was created.  If not
        specified, this attribute will be set by the server.
    :ivar entityid:  a unique, searchable ID used by some specific
        applications
    :ivar entityidtype:  the type of entityid in use
    :ivar status:  the current status (active, read only, or deleted) of the
        entity.  While "status" is set by the server, and therefore not
        included in the class constructor, the attribute can be set directly,
        and the server will accept modifications to its value in an entity update.
    :ivar lastmodified:  the date on which the template was last modified
        (set by the server)
    :ivar modifiedby:  the last user to modify the template (set by the
         server)
    :ivar modifiedbyip:  the ip of the last user to modify the template (set
        by the server)
    :ivar linkagesection:  a child object of class
        :class:`~pyCoalesce.classes.coalesce_entity.CoalesceLinkageSection`
        that contains an entity's :class:`linkages
        <pyCoalesce.classes.coalesce_entity.linkageSub>`; included in templates,
        but not used in any way

    """

    def __init__(self, key = None, name = None, source = None, version = None,
                 section = None, noindex = None, disablehistory = None,
                 datecreated = None, entityid = None, entityidtype = None):

        super(CoalesceEntityTemplate, self). \
            __init__(key = key, name = name, source = source, version = version,
                     section = section, noindex = noindex,
                     disablehistory = disablehistory, datecreated = datecreated,
                     entityid = entityid, entityidtype = entityidtype,
                     title = None, linkagesection = None)

        self.XML = to_XML_string(self, 1)


    def build(self, node):
        """
        Adds the storage of an XML representation of the template to the
        parent method.

        """

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


    def new_entity(self, key = None, populate_min_records = True, silence = True):
        """
        Creates a new instance of class
        :class:`pyCoalesce.classes.coalesce_entity.CoalesceEntity` based on the
        template.  The method adds the template object as an attribute of the
        new entity.

        :param key:  a UUID key, as a string.  For most applications, there's
            no real reason to supply one, since the server returns the key
            when a new entity is created
        :param populate_min_records:  if ``True``, build a number of records in
            each recordset equal to "minrecords" for that recordset.
        :param silence:  if ``False``, print the XML representation of the new
            entity to ``stdout``.

        :returns:  a new instance of class
            :class:`~pyCoalesce.classes.coalesce_entity.CoalesceEntity`

        """

        new_entity = parseString(self.XML, object_class = type(self).mro()[1],
                                 silence = silence)

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
