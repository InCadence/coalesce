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
from lxml import etree as etree_

import ??? as supermod

def parsexml_(infile, parser=None, **kwargs):
    if parser is None:
        # Use the lxml ElementTree compatible parser so that, e.g.,
        #   we ignore comments.
        parser = etree_.ETCompatXMLParser()
    doc = etree_.parse(infile, parser=parser, **kwargs)
    return doc

#
# Globals
#

ExternalEncoding = ''

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
# end class fieldhistorySub


class fieldSub(supermod.field):
    def __init__(self, key=None, datecreated=None, lastmodified=None, name=None, status=None, noindex=None, modifiedby=None, modifiedbyip=None, objectversion=None, objectversionstatus=None, previoushistorykey=None, datatype=None, classificationmarking=None, label=None, value=None, inputlang=None, disablehistory=None, fieldhistory=None):
        super(fieldSub, self).__init__(key, datecreated, lastmodified, name, status, noindex, modifiedby, modifiedbyip, objectversion, objectversionstatus, previoushistorykey, datatype, classificationmarking, label, value, inputlang, disablehistory, fieldhistory, )
supermod.field.subclass = fieldSub
# end class fieldSub


class recordSub(supermod.record):
    def __init__(self, key=None, datecreated=None, lastmodified=None, name=None, status=None, noindex=None, modifiedby=None, modifiedbyip=None, objectversion=None, objectversionstatus=None, previoushistorykey=None, disablehistory=None, history=None, field=None):
        super(recordSub, self).__init__(key, datecreated, lastmodified, name, status, noindex, modifiedby, modifiedbyip, objectversion, objectversionstatus, previoushistorykey, disablehistory, history, field, )
supermod.record.subclass = recordSub
# end class recordSub


class constraintSub(supermod.constraint):
    def __init__(self, key=None, datecreated=None, lastmodified=None, name=None, status=None, noindex=None, modifiedby=None, modifiedbyip=None, objectversion=None, objectversionstatus=None, previoushistorykey=None, type_=None, value=None):
        super(constraintSub, self).__init__(key, datecreated, lastmodified, name, status, noindex, modifiedby, modifiedbyip, objectversion, objectversionstatus, previoushistorykey, type_, value, )
supermod.constraint.subclass = constraintSub
# end class constraintSub


class fielddefinitionSub(supermod.fielddefinition):
    def __init__(self, key=None, datecreated=None, lastmodified=None, name=None, status=None, noindex=None, modifiedby=None, modifiedbyip=None, objectversion=None, objectversionstatus=None, previoushistorykey=None, defaultclassificationmarking=None, defaultvalue=None, datatype=None, label=None, disablehistory=None, constraint=None):
        super(fielddefinitionSub, self).__init__(key, datecreated, lastmodified, name, status, noindex, modifiedby, modifiedbyip, objectversion, objectversionstatus, previoushistorykey, defaultclassificationmarking, defaultvalue, datatype, label, disablehistory, constraint, )
supermod.fielddefinition.subclass = fielddefinitionSub
# end class fielddefinitionSub


class recordsetSub(supermod.recordset):
    def __init__(self, key=None, datecreated=None, lastmodified=None, name=None, status=None, noindex=None, modifiedby=None, modifiedbyip=None, objectversion=None, objectversionstatus=None, previoushistorykey=None, disablehistory=None, history=None, minrecords=0, maxrecords=0, fielddefinition=None, record=None):
        super(recordsetSub, self).__init__(key, datecreated, lastmodified, name, status, noindex, modifiedby, modifiedbyip, objectversion, objectversionstatus, previoushistorykey, disablehistory, history, minrecords, maxrecords, fielddefinition, record, )
supermod.recordset.subclass = recordsetSub
# end class recordsetSub


class sectionSub(supermod.section):
    def __init__(self, key=None, datecreated=None, lastmodified=None, name=None, status=None, noindex=None, modifiedby=None, modifiedbyip=None, objectversion=None, objectversionstatus=None, previoushistorykey=None, disablehistory=None, history=None, recordset=None, section_member=None):
        super(sectionSub, self).__init__(key, datecreated, lastmodified, name, status, noindex, modifiedby, modifiedbyip, objectversion, objectversionstatus, previoushistorykey, disablehistory, history, recordset, section_member, )
supermod.section.subclass = sectionSub
# end class sectionSub


class historySub(supermod.history):
    def __init__(self, key=None, datecreated=None, lastmodified=None, name=None, status=None, noindex=None, modifiedby=None, modifiedbyip=None, objectversion=None, objectversionstatus=None, previoushistorykey=None):
        super(historySub, self).__init__(key, datecreated, lastmodified, name, status, noindex, modifiedby, modifiedbyip, objectversion, objectversionstatus, previoushistorykey, )
supermod.history.subclass = historySub
# end class historySub


class linkageSub(supermod.linkage):
    def __init__(self, key=None, datecreated=None, lastmodified=None, name=None, status=None, noindex=None, modifiedby=None, modifiedbyip=None, objectversion=None, objectversionstatus=None, previoushistorykey=None, disablehistory=None, history=None, entity1key=None, entity1name=None, entity1source=None, entity1version=None, linktype=None, entity2key=None, entity2name=None, entity2source=None, entity2version=None, entity2objectversion=None, classificationmarking=None, inputlang=None, label=None):
        super(linkageSub, self).__init__(key, datecreated, lastmodified, name, status, noindex, modifiedby, modifiedbyip, objectversion, objectversionstatus, previoushistorykey, disablehistory, history, entity1key, entity1name, entity1source, entity1version, linktype, entity2key, entity2name, entity2source, entity2version, entity2objectversion, classificationmarking, inputlang, label, )
supermod.linkage.subclass = linkageSub
# end class linkageSub


class linkagesectionSub(supermod.linkagesection):
    def __init__(self, key=None, datecreated=None, lastmodified=None, name=None, status=None, noindex=None, modifiedby=None, modifiedbyip=None, objectversion=None, objectversionstatus=None, previoushistorykey=None, disablehistory=None, history=None, linkage=None):
        super(linkagesectionSub, self).__init__(key, datecreated, lastmodified, name, status, noindex, modifiedby, modifiedbyip, objectversion, objectversionstatus, previoushistorykey, disablehistory, history, linkage, )
supermod.linkagesection.subclass = linkagesectionSub
# end class linkagesectionSub


class entitySub(supermod.entity):
    def __init__(self, key=None, datecreated=None, lastmodified=None, name=None, status=None, noindex=None, modifiedby=None, modifiedbyip=None, objectversion=None, objectversionstatus=None, previoushistorykey=None, disablehistory=None, history=None, source=None, version=None, entityid=None, entityidtype=None, title=None, linkagesection=None, section=None):
        super(entitySub, self).__init__(key, datecreated, lastmodified, name, status, noindex, modifiedby, modifiedbyip, objectversion, objectversionstatus, previoushistorykey, disablehistory, history, source, version, entityid, entityidtype, title, linkagesection, section, )
supermod.entity.subclass = entitySub
# end class entitySub


def get_root_tag(node):
    tag = supermod.Tag_pattern_.match(node.tag).groups()[-1]
    rootClass = None
    rootClass = supermod.GDSClassesMapping.get(tag)
    if rootClass is None and hasattr(supermod, tag):
        rootClass = getattr(supermod, tag)
    return tag, rootClass


def parse(inFilename, silence=False):
    parser = None
    doc = parsexml_(inFilename, parser)
    rootNode = doc.getroot()
    rootTag, rootClass = get_root_tag(rootNode)
    if rootClass is None:
        rootTag = 'entity'
        rootClass = supermod.entity
    rootObj = rootClass.factory()
    rootObj.build(rootNode)
    # Enable Python to collect the space used by the DOM.
    doc = None
    if not silence:
        sys.stdout.write('<?xml version="1.0" ?>\n')
        rootObj.export(
            sys.stdout, 0, name_=rootTag,
            namespacedef_='',
            pretty_print=True)
    return rootObj


def parseEtree(inFilename, silence=False):
    parser = None
    doc = parsexml_(inFilename, parser)
    rootNode = doc.getroot()
    rootTag, rootClass = get_root_tag(rootNode)
    if rootClass is None:
        rootTag = 'entity'
        rootClass = supermod.entity
    rootObj = rootClass.factory()
    rootObj.build(rootNode)
    # Enable Python to collect the space used by the DOM.
    doc = None
    mapping = {}
    rootElement = rootObj.to_etree(None, name_=rootTag, mapping_=mapping)
    reverse_mapping = rootObj.gds_reverse_node_mapping(mapping)
    if not silence:
        content = etree_.tostring(
            rootElement, pretty_print=True,
            xml_declaration=True, encoding="utf-8")
        sys.stdout.write(content)
        sys.stdout.write('\n')
    return rootObj, rootElement, mapping, reverse_mapping


def parseString(inString, silence=False):
    if sys.version_info.major == 2:
        from StringIO import StringIO
    else:
        from io import BytesIO as StringIO
    parser = None
    doc = parsexml_(StringIO(inString), parser)
    rootNode = doc.getroot()
    rootTag, rootClass = get_root_tag(rootNode)
    if rootClass is None:
        rootTag = 'entity'
        rootClass = supermod.entity
    rootObj = rootClass.factory()
    rootObj.build(rootNode)
    # Enable Python to collect the space used by the DOM.
    doc = None
    if not silence:
        sys.stdout.write('<?xml version="1.0" ?>\n')
        rootObj.export(
            sys.stdout, 0, name_=rootTag,
            namespacedef_='')
    return rootObj


def parseLiteral(inFilename, silence=False):
    parser = None
    doc = parsexml_(inFilename, parser)
    rootNode = doc.getroot()
    rootTag, rootClass = get_root_tag(rootNode)
    if rootClass is None:
        rootTag = 'entity'
        rootClass = supermod.entity
    rootObj = rootClass.factory()
    rootObj.build(rootNode)
    # Enable Python to collect the space used by the DOM.
    doc = None
    if not silence:
        sys.stdout.write('#from ??? import *\n\n')
        sys.stdout.write('import ??? as model_\n\n')
        sys.stdout.write('rootObj = model_.rootClass(\n')
        rootObj.exportLiteral(sys.stdout, 0, name_=rootTag)
        sys.stdout.write(')\n')
    return rootObj


USAGE_TEXT = """
Usage: python ???.py <infilename>
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
