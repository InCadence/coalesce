#!/usr/bin/env python

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

        if not isinstance(field_definition, supermod.fielddefinition):
            raise TypeError('The argument of "create_from_definition" must be ' +
                            'an instance of class "fielddefinition or one of ' +
                            'its subclasses.')

        # Instantiate the new field.
        new_field = cls(name = field_definition.name,
                        value = field_definition.defaultvalue,
                        classificationmarking = field_definition.defaultclassificationmarking,
                        label = field_definition.label,
                        noindex = field_definition.noindex,
                        disablehistory = field_definition.disablehistory)

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
                                         '"create_from_definitions" must be a ' + \
                                         'list of instances of class ' + \
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

        new_record = CoalesceRecord.create_from_definitions(self.fielddefinition,
                                                            name)
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
    def __init__(self, key=None, datecreated=None, lastmodified=None, name=None, status=None, noindex=None, modifiedby=None, modifiedbyip=None, objectversion=None, objectversionstatus=None, previoushistorykey=None, disablehistory=None, history=None, entity1key=None, entity1name=None, entity1source=None, entity1version=None, linktype=None, entity2key=None, entity2name=None, entity2source=None, entity2version=None, entity2objectversion=None, classificationmarking=None, inputlang=None, label=None):
        super(linkageSub, self).__init__(key, datecreated, lastmodified, name, status, noindex, modifiedby, modifiedbyip, objectversion, objectversionstatus, previoushistorykey, disablehistory, history, entity1key, entity1name, entity1source, entity1version, linktype, entity2key, entity2name, entity2source, entity2version, entity2objectversion, classificationmarking, inputlang, label, )

supermod.linkage.subclass = linkageSub

# Alias "linkageSub" to something more meaningful.
CoalesceLinkage = linkageSub

# end class linkageSub


class linkagesectionSub(supermod.linkagesection):
    def __init__(self, key=None, datecreated=None, lastmodified=None, name=None, status=None, noindex=None, modifiedby=None, modifiedbyip=None, objectversion=None, objectversionstatus=None, previoushistorykey=None, disablehistory=None, history=None, linkage=None):
        super(linkagesectionSub, self).__init__(key, datecreated, lastmodified, name, status, noindex, modifiedby, modifiedbyip, objectversion, objectversionstatus, previoushistorykey, disablehistory, history, linkage, )

supermod.linkagesection.subclass = linkagesectionSub

# Alias "linkagesectionSub" to something more meaningful.
CoalesceLinkageSection = linkagesectionSub

# end class linkagesectionSub


class entitySub(supermod.entity):
    def __init__(self, key=None, datecreated=None, lastmodified=None, name=None, status=None, noindex=None, modifiedby=None, modifiedbyip=None, objectversion=None, objectversionstatus=None, previoushistorykey=None, disablehistory=None, history=None, source=None, version=None, entityid=None, entityidtype=None, title=None, linkagesection=None, section=None):
        super(entitySub, self).__init__(key, datecreated, lastmodified, name, status, noindex, modifiedby, modifiedbyip, objectversion, objectversionstatus, previoushistorykey, disablehistory, history, source, version, entityid, entityidtype, title, linkagesection, section, )

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
