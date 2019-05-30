#!/usr/bin/env python
"""
@author: Scott Orr

This module contains the main subclasses for the classes in
:mod:`pyCoalesce.classes.entity`.  Applications should import these
subclasses, rather than the base classes in the
:mod:`~pyCoalesce.classes.entity` module.  All subclasses can be imported
directly from the :mod:`pyCoalesce.classes` module.

Both :mod:`~pyCoalesce.classes.entity` and this module were autogenerated
from Coalesce's entity XSD by the :mod:`generateDS` package, and then
custom code was added to this module.  The structure of these modules
allows :mod:`~pyCoalesce.classes.entity` to be re-generated from an updated
XSD without overwriting the custom code.

The convoluted structure of the code generated by
:mod:`~pyCoalesce.classes.entity` produces two notable quirks:

    * The names of the subclasses can't be changed, because the methods
      in :mod:`~pyCoalesce.classes.entity` use introspection to call them,
      and in order for this to work, they must follow the pattern
      "<original_class>Sub".  To overcome this limitation, each subclass
      has been aliased with a more meaningful name, and the code elsewhere
      in the :mod:`pyCoalesce` package references these aliases.
    * Some of the functions and methods in this module and the other
      :mod:`pyCoalesce.classes` modules may not work properly with
      iPython's auto-reload feature.  Refactoring so that input tests no
      longer rely on inheritance has removed some and possibly all of these
      cases, but, should the problem appear, it can most likely be overcome
      by restarting the kernel and reimporting everything at once.

"""

#
# Generated Tue Aug 14 15:53:54 2018 by generateDS.py version 2.29.19.
# Python 2.7.15 |Anaconda, Inc.| (default, May  1 2018, 18:37:09) [MSC v.1500 64 bit (AMD64)]
#
# Command line options:
#   ('-o', 'entity.py')
#   ('-s', 'entity_subclasses.py')
#
# Command line arguments:
#   Entity.xsd
#
# Command line:
#   C:/Anaconda2/Scripts/generateDS.py -o "entity.py" -s "entity_subclasses.py" Entity.xsd
#
# Current working directory (os.getcwd()):
#   XSD
#

import sys

import entity as supermod
from entity_utilities import parse

#
# Globals
#

ExternalEncoding = ''

DATA_TYPE_MAP = {"string": unicode, "short": int, "int": int, "long": long,
                 "float": float, "double": float, "boolean": bool,
                 "stringlist": list}
"""
Maps XSD types to Python types.

"""

LINKAGE_TYPES = {"UNDEFINED": "Undefined", "IS_CHILD_OF": "IsChildOf",
                 "IS_PARENT_OF": "IsParentOf", "CREATED": "Created",
                 "WAS_CREATED_BY": "WasCreatedBy", "HAS_MEMBER": "HasMember",
                 "IS_A_MEMBER_OF": "IsAMemberOf",
                 "HAS_PARTICIPANT": "HasParticipant",
                 "IS_A_PARTICIPANT_OF": "IsAParticipantOf",
                 "IS_WATCHING": "IsWatching",
                 "IS_BEING_WATCHED_BY": "IsBeingWatchedBy",
                 "IS_A_PEER_OF": "IsAPeerOf", "IS_OWNED_BY": "IsOwnedBy",
                 "HAS_OWNERSHIP_OF": "HasOwnershipOf", "IS_USED_BY": "IsUsedBy",
                 "HAS_USE_OF": "HasUseOf", "SUCCESSOR": "Successor",
                 "PREDECESSOR": "Predecessor",
                 "CROSS_DOMAIN_SOURCE": "CrossDomainSource",
                 "CROSS_DOMAIN_TARGET": "CrossDomainTarget",
                 "IS_INPUT_PARAMETER_TO": "IsInputParameterTo",
                 "HAS_INPUT_PARAMETER_OF": "HasInputParameterOf",
                 "IS_INPUT_TO": "IsInputTo", "HAS_INPUT_OF": "HasInputOf",
                 "IS_OUTPUT_TO": "IsOutputTo", "HAS_OUTPUT_OF": "HasOutputOF",
                 "IS_PRODUCT_OF": "IsProductOf", "HAS_PRODUCT": "HasProduct"}
"""
The Coalesce XML and JSON API's use different values for the same link
types; the keys are the JSON versions, and the values are the XML versions.
Eventually, this map should be available through the "property" API , at
which point it can be downloaded rather than hard-coded.

"""


#
# Data representation classes
#

class coalesceObjectTypeSub(supermod.coalesceObjectType):
    def __init__(self, key=None, datecreated=None, lastmodified=None, name=None, status=None, noindex=None, modifiedby=None, modifiedbyip=None, objectversion=None, objectversionstatus=None, previoushistorykey=None, extensiontype_=None):
        super(coalesceObjectTypeSub, self).__init__(key, datecreated, lastmodified, name, status, noindex, modifiedby, modifiedbyip, objectversion, objectversionstatus, previoushistorykey, extensiontype_, )

supermod.coalesceObjectType.subclass = coalesceObjectTypeSub

# end class coalesceObjectTypeSub


class coalesceObjectHistoryTypeSub(supermod.coalesceObjectHistoryType):
    def __init__(self, key=None, datecreated=None, lastmodified=None, name=None, status=None, noindex=None, modifiedby=None, modifiedbyip=None, objectversion=None, objectversionstatus=None, previoushistorykey=None, disablehistory=None, history=None, extensiontype_=None):
        super(coalesceObjectHistoryTypeSub, self).__init__(key, datecreated, lastmodified, name, status, noindex, modifiedby, modifiedbyip, objectversion, objectversionstatus, previoushistorykey, disablehistory, history, extensiontype_, )

supermod.coalesceObjectHistoryType.subclass = coalesceObjectHistoryTypeSub

# end class coalesceObjectHistoryTypeSub


class coalesceFieldTypeSub(supermod.coalesceFieldType):
    def __init__(self, key=None, datecreated=None, lastmodified=None, name=None, status=None, noindex=None, modifiedby=None, modifiedbyip=None, objectversion=None, objectversionstatus=None, previoushistorykey=None, datatype=None, classificationmarking=None, label=None, value=None, inputlang=None, extensiontype_=None):
        super(coalesceFieldTypeSub, self).__init__(key, datecreated, lastmodified, name, status, noindex, modifiedby, modifiedbyip, objectversion, objectversionstatus, previoushistorykey, datatype, classificationmarking, label, value, inputlang, extensiontype_, )

supermod.coalesceFieldType.subclass = coalesceFieldTypeSub

# end class coalesceFieldTypeSub


class fieldhistorySub(supermod.fieldhistory):
    def __init__(self, key=None, datecreated=None, lastmodified=None, name=None, status=None, noindex=None, modifiedby=None, modifiedbyip=None, objectversion=None, objectversionstatus=None, previoushistorykey=None, datatype=None, classificationmarking=None, label=None, value=None, inputlang=None):
        super(fieldhistorySub, self).__init__(key, datecreated, lastmodified, name, status, noindex, modifiedby, modifiedbyip, objectversion, objectversionstatus, previoushistorykey, datatype, classificationmarking, label, value, inputlang, )

supermod.fieldhistory.subclass = fieldhistorySub

# Alias "fieldhistorySub" to something more meaningful.
CoalesceFieldHistory = fieldhistorySub

# end class fieldhistorySub


class fieldSub(supermod.field):

    def __init__(self, key=None, datecreated=None, lastmodified=None, name=None, status=None, noindex=None, modifiedby=None, modifiedbyip=None, objectversion=None, objectversionstatus=None, previoushistorykey=None, datatype=None, classificationmarking=None, label=None, value=None, inputlang=None, disablehistory=None, fieldhistory=None):

        super(fieldSub, self).__init__(key, datecreated, lastmodified, name, status, noindex, modifiedby, modifiedbyip, objectversion, objectversionstatus, previoushistorykey, datatype, classificationmarking, label, value, inputlang, disablehistory, fieldhistory, )

        self.field_definition = None


    @classmethod
    def create_from_definition(cls, field_definition):

        # Instantiate the new field.

        try:
            new_field = cls(name = field_definition.name,
                            value = field_definition.defaultvalue,
                            datatype = field_definition.datatype,
                            classificationmarking =
                                field_definition.defaultclassificationmarking,
                            label = field_definition.label,
                            noindex = field_definition.noindex,
                            disablehistory = field_definition.disablehistory)

        except AttributeError:
            raise TypeError('The argument of "create_from_definition" must ' +
                            'be an instance of class "fielddefinition or one ' +
                            'of its subclasses.')

        return new_field


supermod.field.subclass = fieldSub

# Alias "fieldSub" to something more meaningful.
CoalesceField = fieldSub

# end class fieldSub


class recordSub(supermod.record):
    def __init__(self, key=None, datecreated=None, lastmodified=None, name=None, status=None, noindex=None, modifiedby=None, modifiedbyip=None, objectversion=None, objectversionstatus=None, previoushistorykey=None, disablehistory=None, history=None, field=None):
        super(recordSub, self).__init__(key, datecreated, lastmodified, name, status, noindex, modifiedby, modifiedbyip, objectversion, objectversionstatus, previoushistorykey, disablehistory, history, field, )

    @classmethod
    def create_from_definitions(cls, field_definition_list, name):

        # Check for proper name input.
        if not isinstance(name, basestring):
            raise TypeError('The second argument of "create_from_definition" ' +
                            'must be an ASCII or Unicode string.')

        # Initialize the record.
        new_record = cls(name = name)

        # Add fields, checking for proper field definitions in the process.

        field_definition_error_message = 'The first argument of ' + \
                                         '"create_from_definitions" must be ' + \
                                         'a list of instances of class ' + \
                                         '"fielddefinition" or its subclasses.'

        try:

            for current_field_definition in field_definition_list:

                if not isinstance(current_field_definition,
                                  supermod.fielddefinition):
                    raise TypeError(field_definition_error_message)

                new_field = \
                    CoalesceField.create_from_definition(current_field_definition)
                new_record.field.append(new_field)

        except IndexError:
            raise TypeError(field_definition_error_message)

        return new_record


supermod.record.subclass = recordSub

# Alias "recordSub" to something more meaningful.
CoalesceRecord = recordSub

# end class recordSub


class constraintSub(supermod.constraint):
    def __init__(self, key=None, datecreated=None, lastmodified=None, name=None, status=None, noindex=None, modifiedby=None, modifiedbyip=None, objectversion=None, objectversionstatus=None, previoushistorykey=None, type_=None, value=None):
        super(constraintSub, self).__init__(key, datecreated, lastmodified, name, status, noindex, modifiedby, modifiedbyip, objectversion, objectversionstatus, previoushistorykey, type_, value, )

supermod.constraint.subclass = constraintSub

# Alias "CoalesceConstraintSub" to something more meaningful.
CoalesceConstraint = constraintSub

# end class constraintSub


class fielddefinitionSub(supermod.fielddefinition):
    def __init__(self, key=None, datecreated=None, lastmodified=None, name=None, status=None, noindex=None, modifiedby=None, modifiedbyip=None, objectversion=None, objectversionstatus=None, previoushistorykey=None, defaultclassificationmarking=None, defaultvalue=None, datatype=None, label=None, disablehistory=None, constraint=None):
        super(fielddefinitionSub, self).__init__(key, datecreated, lastmodified, name, status, noindex, modifiedby, modifiedbyip, objectversion, objectversionstatus, previoushistorykey, defaultclassificationmarking, defaultvalue, datatype, label, disablehistory, constraint, )

supermod.fielddefinition.subclass = fielddefinitionSub

# Alias "fielddefinitionSub" to something more meaningful.
CoalesceFieldDefinition = fielddefinitionSub

# end class fielddefinitionSub


class recordsetSub(supermod.recordset):
    def __init__(self, key=None, datecreated=None, lastmodified=None, name=None, status=None, noindex=None, modifiedby=None, modifiedbyip=None, objectversion=None, objectversionstatus=None, previoushistorykey=None, disablehistory=None, history=None, minrecords=0, maxrecords=0, fielddefinition=None, record=None):
        super(recordsetSub, self).__init__(key, datecreated, lastmodified, name, status, noindex, modifiedby, modifiedbyip, objectversion, objectversionstatus, previoushistorykey, disablehistory, history, minrecords, maxrecords, fielddefinition, record, )

    def create_record_from_definitions(self, name = None):

        if not name:
            name = self.name + " Record"
        elif not isinstance(name, basestring):
            raise TypeError('The argument "name" must be an ASCII or Unicode ' +
                            'string.')

        new_record = \
            CoalesceRecord.create_from_definitions(self.fielddefinition, name)
        self.record.append(new_record)


supermod.recordset.subclass = recordsetSub

# Alias "recordsetSub" to something more meaningful.
CoalesceRecordset = recordsetSub

# end class recordsetSub


class sectionSub(supermod.section):
    def __init__(self, key=None, datecreated=None, lastmodified=None, name=None, status=None, noindex=None, modifiedby=None, modifiedbyip=None, objectversion=None, objectversionstatus=None, previoushistorykey=None, disablehistory=None, history=None, recordset=None, section_member=None):
        super(sectionSub, self).__init__(key, datecreated, lastmodified, name, status, noindex, modifiedby, modifiedbyip, objectversion, objectversionstatus, previoushistorykey, disablehistory, history, recordset, section_member, )

supermod.section.subclass = sectionSub

# Alias "sectionSub" to something more meaningful.
CoalesceSection = sectionSub

# end class sectionSub


class historySub(supermod.history):
    def __init__(self, key=None, datecreated=None, lastmodified=None, name=None, status=None, noindex=None, modifiedby=None, modifiedbyip=None, objectversion=None, objectversionstatus=None, previoushistorykey=None):
        super(historySub, self).__init__(key, datecreated, lastmodified, name, status, noindex, modifiedby, modifiedbyip, objectversion, objectversionstatus, previoushistorykey, )

supermod.history.subclass = historySub

# Alias "historySub" to something more meaningful.
CoalesceHistory = historySub

# end class historySub


class linkageSub(supermod.linkage):
    def __init__(self, entity1key = None, entity1name = None,
                 entity1source = None, entity1version = None,
                 linktype = "Undefined", entity2key = None, entity2name = None,
                 entity2source = None, entity2version = None,
                 entity2objectversion = None, label = None,
                 classificationmarking = None, inputlang = None, noindex = None,
                 disablehistory = None):

        # The XSD doesn't enforce this, and getting it to do so would be
        # difficult if not impossible, but currently all linkages have the
        # name "Linkage".
        super(linkageSub, self). \
            __init__(name = "Linkage", entity1key = entity1key,
                     entity1name = entity1name, entity1source = entity1source,
                     entity1version = entity1version, linktype = linktype,
                     entity2key = entity2key, entity2name = entity2name,
                     entity2source = entity2source,
                     entity2version = entity2version,
                     entity2objectversion = entity2objectversion, label = label,
                     classificationmarking = classificationmarking,
                     inputlang = inputlang, noindex = noindex,
                     disablehistory = disablehistory, key = None,
                     datecreated = None, lastmodified = None, modifiedby = None,
                     modifiedbyip = None, objectversion = None,
                     objectversionstatus = None, previoushistorykey = None,
                     history = None, status = None)

        # Check the link type, and convert a key to a value if necessary.
        if linktype in LINKAGE_TYPES:
            linktype = LINKAGE_TYPES[linktype]
        elif not self.linktype in LINKAGE_TYPES.values():
            raise ValueError('"' + unicode(linktype) + '" is not a valid ' +
                             'linkage type.')

    def buildAttributes(self, node, attrs, already_processed):
        """
        Adds a check for valid link type to the parent method.

        """

        super(linkageSub, self).buildAttributes(node = node, attrs = attrs,
                                                already_processed = already_processed)

        # Check the link type, and convert a key to a value if necessary.
        if self.linktype in LINKAGE_TYPES:
            self.linktype = LINKAGE_TYPES[self.linktype]
        elif not self.linktype in LINKAGE_TYPES.values():
            raise ValueError('"' + unicode(self.linktype) + '" is not a ' +
                             'valid linkage type.')


    def to_API(self, biDirectional = False):
        """
        Returns a version of the linkage as a (JSON-serializable) instance
        of :class:`.pyCoalesce.classes.coalesce_json.CoalesceAPILinkage`.

        :param bidirectional:  a boolean indicating whether or not the
            server should create a second linkage, from entity2 to entity1.

        :returns:  the linkage as an instance of
            :class:`~pyCoalesce.classes.coalesce_json.CoalesceAPILinkage`

        """

        # Performing this import here avoids a circular import.
        from coalesce_json import CoalesceAPILinkage

        API_linkage = CoalesceAPILinkage(self.entity1key, self.entity2key,
                                   label = self.label,
                                   linkage_type = self.linktype,
                                   biDirectional = biDirectional)
        API_linkage["status"] = self.status

        return API_linkage


supermod.linkage.subclass = linkageSub

# Alias "linkageSub" to something more meaningful.
CoalesceLinkage = linkageSub

# end class linkageSub


class linkagesectionSub(supermod.linkagesection):
    def __init__(self, key=None, datecreated=None, lastmodified=None,
                 status=None, noindex=None, modifiedby=None, modifiedbyip=None,
                 objectversion=None, objectversionstatus=None,
                 previoushistorykey=None, disablehistory=None, history=None,
                 linkage=None):

        # The XSD doesn't enforce this, and doing so would be difficult if
        # not impossible, but currently all linkage sections have the name
        # "Linkages".
        super(linkagesectionSub, self).__init__(key, datecreated, lastmodified,
                                                "Linkages", status, noindex,
                                                modifiedby, modifiedbyip,
                                                objectversion,
                                                objectversionstatus,
                                                previoushistorykey,
                                                disablehistory, history,
                                                linkage)

supermod.linkagesection.subclass = linkagesectionSub

# Alias "linkagesectionSub" to something more meaningful.
CoalesceLinkageSection = linkagesectionSub

# end class linkagesectionSub


class entitySub(supermod.entity):
    """
    :ivar key:  a UUID key as a string.  Supplying a key is optional for a
        newly created entity; if none is supplied, the server generates one
        randomly.
    :ivar title:  the entity's title (if any--some types of entities use a
        record field instead); not to be confused with the entity's
        template :attr:`name`
    :ivar name:  the name of the entity's template; with :attr:`source` and
        :attr:`version`, one of the three attributes that uniquely identify
        the template
    :ivar source:  the source (e.g., a particular project) of the entity's
        template; with :attr:`name` and :attr:`version`, one of the three
        attributes that uniquely identify the template
    :ivar version:  the version of the entity's template; with :attr:`name`
        and :attr:`version`, one of the three attributes that uniquely
        identify the template
    :ivar linkagesection:  a child object of class
        :class:`~pyCoalesce.classes.coalesce_entity.CoalesceLinkageSection`
        that contains the entity's :class:`linkages
        <pyCoalesce.classes.coalesce_entity.linkageSub>`
    :ivar section:  a list of the entity's sections
    :ivar noindex:  a boolean indicating whether or not to index the entity
        for search
    :ivar disablehistory:  a boolean that determines whether or not the
        entity's history of revisions should be retained
    :ivar status:  the current status (active, read only, or deleted) of the
        entity.  While "status" is set by the server, and therefore not
        included in the class constructor, the attribute can be set directly,
        and the server will accept modifications to its value in an entity
        update.
    :ivar datecreated:  the date on which the entity was created.  If not
        specified, this attribute will be set by the server.
    :ivar template:  the
        :class:`~pyCoalesce.classes.coalesce_entity_template.CoalesceEntityTemplate`
        used to create the entity, if the entity was created via the template's
        :meth:`~pyCoalesce.classes.coalesce_entity_template.CoalesceEntityTemplate.new_entity`
        method.  If the entity was created in any other fashion, the value
        of this attribute is ``None``.
    :ivar entityid:  a unique, searchable ID used by some specific
        applications
    :ivar entityidtype:  the type of entityid in use
    :ivar lastmodified:  the date on which the entity was last modified
        (set by the server)
    :ivar modifiedby:  the last user to modify the entity (set by the
        server)
    :ivar modifiedbyip:  the ip of the last user to modify the entity (set
        by the server)
    :ivar objectversion:  the version (revision) of the entity (set by the
        server); not to be confused with entity's template attr:'version'
    :ivar objectversionstatus:  the current status (active or deleted) of
        the version (revision) of the entity (set by the server)
    :ivar previoushistorykey:  the UUID key of the previous history of the
        entity (set by the server)
    :ivar history:  the history of the entity's revisions (set by the
        server)

    """

    def __init__(self, key = None, title = None, name = None, source = None,
                 version = None, linkagesection = None, section = None,
                 noindex = None, disablehistory = None, datecreated = None,
                 entityid = None, entityidtype = None):

        super(entitySub, self). \
            __init__(key = key, title = title, name = name, source = source,
                     version = version, linkagesection = linkagesection,
                     section = section, noindex = noindex,
                     disablehistory = disablehistory, datecreated = datecreated,
                     entityid = entityid, entityidtype = entityidtype,
                     status = None, lastmodified = None, modifiedby = None,
                     modifiedbyip = None, objectversion = None,
                     objectversionstatus = None, previoushistorykey = None,
                     history = None)

        self.template = None


supermod.entity.subclass = entitySub

# Alias "entitySub" to something more meaningful.
CoalesceEntity = entitySub

# end class entitySub


USAGE_TEXT = """
Usage: python coalesce_entity.py <infilename>
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
