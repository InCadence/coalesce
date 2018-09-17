# -*- coding: utf-8 -*-
"""
Created on Sat Aug 11 18:14:28 2018

@author: sorr
"""

import sys
from cStringIO import StringIO
from copy import copy
from collections import deque

from lxml import etree as etree_

import entity as supermod
from entity import coalesceObjectType


OBJECT_TYPES = [u"field", u"record", u"fielddefinition", u"recordset",
                u"section"]


def parsexml_(infile, parser = None, **kwargs):
    if parser is None:
        # Use the lxml ElementTree compatible parser so that, e.g.,
        #   we ignore comments.
        parser = etree_.ETCompatXMLParser()
    doc = etree_.parse(infile, parser=parser, **kwargs)
    return doc


def get_root_tag(node):
    tag = supermod.Tag_pattern_.match(node.tag).groups()[-1]
    rootClass = None
    rootClass = supermod.GDSClassesMapping.get(tag)
    if rootClass is None and hasattr(supermod, tag):
        rootClass = getattr(supermod, tag)
    return tag, rootClass


# The parse functions below replace "rootClass.factory()" with a normal Python
# call to the class constructor, "rootClass()".  Aside from the unPythonic
# nature of the "factory" method, making it a static method prevents it from
# being overridden in a subclass, which we'd need to do here because the
# method is coded to instantiate the assigned subclass, rather than the class
# through which the method is being called.

def parse(inFilename, object_class = None, silence = False):
    parser = None
    doc = parsexml_(inFilename, parser)
    rootNode = doc.getroot()
    rootTag, parentClass = get_root_tag(rootNode)
    if not parentClass:
        rootTag = 'entity'
        parentClass = supermod.entity
    if object_class:
        rootClass = object_class
    else:
        rootClass = parentClass.subclass
    rootObj = rootClass()
    try:
        rootObj.build(rootNode)
    except AttributeError:
        raise ValueError(unicode(object_class) + " is not a Coalesce object.")
    # Enable Python to collect the space used by the DOM.
    doc = None
    if not silence:
        sys.stdout.write('<?xml version="1.0" ?>\n')
        rootObj.export(
            sys.stdout, 0, name_=rootTag,
            namespacedef_='',
            pretty_print=True)
    return rootObj


def parseEtree(inFilename, object_class = None, silence = False):
    parser = None
    doc = parsexml_(inFilename, parser)
    rootNode = doc.getroot()
    rootTag, parentClass = get_root_tag(rootNode)
    if not parentClass:
        rootTag = 'entity'
        parentClass = supermod.entity
    if object_class:
        rootClass = object_class
    else:
        rootClass = parentClass.subclass
    rootObj = rootClass()
    try:
        rootObj.build(rootNode)
    except AttributeError:
        raise ValueError(unicode(object_class) + " is not a Coalesce object.")
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


def parseString(inString, object_class = None, silence = False):
    """
    Parse a string, create the object tree, and export it.

    :param inString:  a string.  This XML fragment should not start with an XML
        declaration containing an encoding.
    :param object_class:  a subclass of any of the classes in "entity.py", or
        their subclasses in this module.  This argument allows the
        instantiation of a subclass besides the ones in this module.
    :param silence:  a boolean.  If False, export the object.

    :return:  the root object in the tree
    """

    if sys.version_info.major == 2:
        StringIOHandler = StringIO
    else:
        from io import BytesIO
        StringIOHandler = BytesIO
    parser = None
    doc = parsexml_(StringIOHandler(inString), parser)
    rootNode = doc.getroot()
    rootTag, parentClass = get_root_tag(rootNode)
    if not parentClass:
        rootTag = 'entity'
        parentClass = supermod.entity
    if object_class:
        rootClass = object_class
    else:
        rootClass = parentClass.subclass
    rootObj = rootClass()
    try:
        rootObj.build(rootNode)
    except AttributeError:
        raise ValueError(unicode(object_class) + " is not a Coalesce object.")
    # Enable Python to collect the space used by the DOM.
    doc = None
    if not silence:
        sys.stdout.write('<?xml version="1.0" ?>\n')
        rootObj.export(
            sys.stdout, 0, name_=rootTag,
            namespacedef_='')
    return rootObj


def parseLiteral(inFilename, object_class = None, silence = False):
    parser = None
    doc = parsexml_(inFilename, parser)
    rootNode = doc.getroot()
    rootTag, parentClass = get_root_tag(rootNode)
    if not parentClass:
        rootTag = 'entity'
        parentClass = supermod.entity
    if object_class:
        rootClass = object_class
    else:
        rootClass = parentClass.subclass
    rootObj = rootClass()
    try:
        rootObj.build(rootNode)
    except AttributeError:
        raise ValueError(unicode(object_class) + " is not a Coalesce object.")
    # Enable Python to collect the space used by the DOM.
    doc = None
    if not silence:
        sys.stdout.write('#from ??? import *\n\n')
        sys.stdout.write('import ??? as model_\n\n')
        sys.stdout.write('rootObj = model_.rootClass(\n')
        rootObj.exportLiteral(sys.stdout, 0, name_=rootTag)
        sys.stdout.write(')\n')
    return rootObj


def to_XML_string(Coalesce_object, indent_level = 1, pretty_print = True):
    """
    Assigns the XML representation of a Coalesce object to a string.

    :param Coalesce_object:  a Coalesce object with an "export" method
    :param level:  multiply by 4 to determine the number of spaces for
        each level of indentation
    :param pretty_print:  if True, add indentation

    :return:  a Unicode XML string
    """

    # Initialize the StringIO object.
    string_file = StringIO()

    # Export the Coalesce object to the StringIO object as XML.
    Coalesce_object.export(string_file, indent_level, pretty_print = pretty_print)

    # Return the XML as a string.
    return string_file.getvalue()


def find_child(Coalesce_object, name, match_case = False,
               include_fielddefinitions = False):
    """
    Recursively searches a Coalesce object tree to find any objects matching
    "name".

    :param Coalesce_object:  a Coalesce object whose tree is to be searched
    :param name:  the (ASCII or Unicode) name of the object being searched
        for.
    :param match_case:  if True, match the case of "name".
    :param include_fielddefinitions:  if True, include "fielddefinition"
        objects in the results.  Normally, we want to exclude these,
        because they have the same names as fields, leading to ambiguity
        when using this function to find fields to set.

    :return:  a nested list containing paths to all matching objects.  If
        "Coalesce_object" itself matches "name", its "path" will be a list
        whose single item is "<root>".

    The function does not search linkages--these can always be found in the
    same place.
    """

    # Check input.

    if not isinstance(Coalesce_object, coalesceObjectType):
        raise TypeError('Argument "Coalesce_object" must be an instance of '
                        'CoalesceEntity or one of its child types.')

    if not isinstance(name, basestring):
        raise TypeError('Argument "name" must be an ASCII or Unicode string.')

    if not isinstance(match_case, bool):
        raise TypeError('Argument "match_case" must be a boolean.')

    if not match_case:
        object_name = Coalesce_object.name.lower()
        name = name.lower()
    else:
        object_name = Coalesce_object.name

    # Check the parent's own name, and initialize a return object.
    if  object_name == name:
        found = [[u"<root>"]]
    else:
        found = []

    # Search through all the possible types of child objects.  For the root
    # object, we need the "<root>" tag, since otherwise its path would be
    # empty, but we need to remove it for the child objects.

    object_types = copy(OBJECT_TYPES)
    if not include_fielddefinitions:
        object_types.remove("fielddefinition")

    for object_type in object_types:

        if hasattr(Coalesce_object, object_type):
            for i, child in enumerate(getattr(Coalesce_object, object_type)):
                new_finds = find_child(child, name, match_case)
                new_paths = []
                for find in new_finds:
                    find.insert(0, object_type)
                    find.insert(1, i)
                    if u"<root>" in find:
                        find.remove(u"<root>")
                    new_paths.append(find)
                found.extend(new_paths)

    return found


def get_child_attrib(Coalesce_object, path = [u"<root>"], attrib = "value"):
    """
    Retrieves the value of one attribute of a child Coalesce object specified
    by "path".

    :param Coalesce_object:  the root Coalesce object
    :param path:  a list of object types and indices that specifies the path
        to the child object whose attribute is to be retrieved.  If the first
        (and presumably only) item in this argument is "<root>", retrieve the
        attribute from "Coalesce_object" itself.
    :param attrib:  the name of the attribute to be retrieved.  Defaults to
        "value".

    :return:  the value of the target attribute
    """

    if not isinstance(Coalesce_object, coalesceObjectType):
        raise TypeError('Argument "Coalesce_object" must be an instance of '
                        'a CoalesceEntity or one of its child types.')

    if len(path) == 0:
        raise ValueError("The specified path is empty.")

    if not isinstance(attrib, basestring):
        raise TypeError('Argument "attrib" must be an ASCII or Unicode string.')

    target = Coalesce_object

    # We don't need the following loop if we don't need to descend into the
    # child objects.
    if not path[0] == u"<root>":

        # The "pop" operation used below is much slower with a list.
        path_queue = deque(path)

        # There are two entries for each level of the hierarchy, the level
        # itself, and the position in the list that makes up that level.
        path_length = len(path) / 2

        for i in xrange(path_length):
            child = path_queue.popleft()
            index = path_queue.popleft()
            target = getattr(target, child)[index]

    attrib_value = getattr(target, attrib)

    return attrib_value


def set_child_attrib(Coalesce_object, path = [u"<root>"], attrib = "value",
                     value = None):
    """
    Sets the value of one attribute of a child Coalesce object specified by
    "path".

    :param Coalesce_object:  the root Coalesce object
    :param path:  a list of object types and indices that specifies the path
        to the child object whose attribute is to be set.  If the first (and
        presumably only) item in this argument is "<root>", set the
        attribute on the "Coalesce_object" itself.
    :param attrib:  the name of the attribute to be retrieved.  Defaults to
        "value".  If the attribute does not already exist, it will be created.
    :param value:  the value to which the attribute is to be set

    :return:  True, indicating the attribute has been set successfully
    """

    if not isinstance(Coalesce_object, coalesceObjectType):
        raise TypeError('Argument "Coalesce_object" must be an instance of '
                        'a CoalesceEntity or one of its child types.')

    if len(path) == 0:
        raise ValueError("The specified path is empty.")

    if not isinstance(attrib, basestring):
        raise TypeError('Argument "attrib" must be an ASCII or Unicode string.')

    target = Coalesce_object

    # We don't need the following loop if we don't need to descend into the
    # child objects.
    if not path[0] == u"<root>":

        # The "pop" operation used below is much slower with a list.
        path_queue = deque(path)

        # There are two entries for each level of the hierarchy, the level
        # itself, and the position in the list that makes up that level.
        path_length = len(path) / 2

        for i in xrange(path_length):
            child = path_queue.popleft()
            index = path_queue.popleft()
            target = getattr(target, child)[index]

    setattr(target, attrib, value)

    return True


def set_entity_fields(Coalesce_entity = None, fields = None, match_case = False):
    """
    A convenience function to fill any or all of an entity's fields with
    specified values.

    :param Coalesce_entity:  an instance of CoalesceEntity or one of its
        subclasses.
    :param fields:  a dict-like of fields and values to set on those fields.
        The keys can be either string (ASCII or Unicode) names (in which case
        the function searches for each field, and throws an error if
        duplicates are found) or path lists, alternating between child object
        type and list index.  The values of the dict-like must be the values
        to be set on the "value" attribute of each field--use another method
        for setting other attributes.
    :param match_case:  if True, match the case of child object names in
        "fields".  This argument has no meaning if the keys of "fields" are
        paths.

    :return:  True if all fields have been set successfully

    Technically, the function will work for any child object of a Coalesce
    entity (or the entity itself) that has an attribute named "value", but
    this is unlikely to be the case for any actual Coalesce entity.
    """

    # Check for valid input.
    if not Coalesce_entity:
        raise ValueError('The argument "Coalesce_entity" must be an instance of ' +
                        'class CoalesceEntity or one of its subclasses.')
    elif not isinstance(Coalesce_entity, supermod.entity):
        raise TypeError('The argument "Coalesce_entity" must be an instance of ' +
                        'class CoalesceEntity or one of its subclasses.')
    try:
        fields_iter = fields.iteritems()
    except:
        raise TypeError('The argument "fields" must be a dict-like iterable ' +
                        'with field names or paths as keys and field values as ' +
                        'values.')

    if not isinstance(match_case, bool):
        raise TypeError('Argument "match_case" must be a boolean.')

    # Set each field in turn.
    for key, value in fields_iter:

        # If necessary, find the path to the field in question.
        if isinstance(key, basestring):
            matches = find_child(Coalesce_entity, key,
                                 include_fielddefinitions = False)
            num_matches = len(matches)
            if num_matches == 0:
                raise ValueError('Field "' + key + '" not found.')
            elif num_matches > 1:
                raise ValueError('The entity has more than one field named "' +
                                 key + '".  Try using field paths instead:  use ' +
                                 'classes.find_child to find these.')
            else:
                path = matches[0]

        else:
            path = key

        set_child_attrib(Coalesce_entity, path = path, value = value)

    return True


