# -*- coding: utf-8 -*-
"""
Created on Tue Sep  4 16:53:38 2018

@author: sorr
"""

from uuid import UUID
from copy import copy

from coalesce_entity import LINKAGE_TYPES, CoalesceLinkage


class CoalesceAPILinkage(dict):
    """
    A JSON-serializable version of a Coalesce linkage, intended for use with
    the linkage CRUD endpoints of the RESTful API, which allow the creation,
    retreival, and deletion of links independently of their corresponding
    entities.  This capability makes it possible to submit links in bulk jobs,
    and also allows the submission of bi-directional links (which Coalesce
    instantiates by creating links in each of the corresponding entities).

    While this class is used to create Coalesce linkages, its format is
    different from the representation of those linkages in the databases
    themselves, and in the Java and RESTful entity API's:  this class's keys
    match the attribues in the Java GraphLink class, which correspond to a
    subset of the full set of keys/attributes in the entity model, but which
    use different names (thus, though this class is JSON-serializable, it
    doesn't produce a JSON object that looks the same as a nested linkage
    object in a JSON representation of an entity).

    Although this is a subclass of dict (in order to make it JSON-
    serializable), only five keys can be set, each corresponding to one of the
    attributes included in the GraphLink class (the RESTful API doesn't use a
    sixth attribute, "status").  The __setitem__ and setdefault methods
    enforce this restriction, and the update method has been disabled.

    Keys:
    source:  the UUID key of the source entity, either as an instance of
        uuid.UUID or as a string or integer that can be used as input to the
        UUID class constructor.  The linkage created on the server will be
        part of this entity.
    target:  the UUID key of the target entity, either as an instance of
        uuid.UUID or as a string or integer that can be used as input to the
        UUID class constructor.  The linkage created on the server will not be
        part of this entity, unless the link is bi-directional.
    label:  a string label for the linkage.  This attribute isn't used by
        Coalesce, and may be set to any value required by an application.
    type:  the (string) type of linkage.  The value of this attribute must be
        one the keys in pyCoalesce.classes.LINKAGE_TYPES.
    isBiDirectional:  a boolean indicating whether or not the server should
        create a second linkage, from the target to the source.  The
        unPythonic name of this key is due to to need to match the attribute
        name of the Java GraphLink class.
    """

    # Define the list of valid keys.
    VALID_KEYS = ("source", "target", "label", "type", "isBiDirectional")
    INVALID_KEY_ERROR_MSG = "Only the following keys may be set:\n" + \
                            unicode(VALID_KEYS)


    def __init__(self, source = None, target = None, label = None,
                 linkage_type = "UNDEFINED", isBiDirectional = False):
        """
        source:  the UUID key of the source entity, either as an instance of
            uuid.UUID or as a string or integer that can be used as input to
            the UUID class constructor.  The linkage created on the server
            will be part of this entity.  The argument is required.
        target:  the UUID key of the target entity, either as an instance of
            uuid.UUID or as a string or integer that can be used as input to
            the UUID class constructor.  The linkage created on the server
            will not be part of this entity, unless the link is bi-
            directional.  The argument is required.
        label:  a string label for the linkage.  This attribute isn't used by
            Coalesce, and may be set to any value required by an application.
        linkage_type:  the (string) type of linkage; the actual name of the
            key is "type", but this is a reserved word in Python, hence
            unusable as a keyword argument name, and therefore an alias is
            used here.  The value of this attribute must be one the keys in
            pyCoalesce.classes.LINKAGE_TYPES.
        isBiDirectional:  a boolean indicating whether or not the server
            should create a second linkage, from the target to the source.
            The unPythonic name of this argument is due to to need to match
            the attribute name of the Java GraphLink class.
        """

        # Make sure "source" and "target" are valid ID's, then convert them
        # (back) to strings and set the corresponding values.

        if source:

            if isinstance(source, UUID):
                source_obj = source

            else:
                try:
                    source_obj = UUID(source)
                except AttributeError:
                    source_obj = UUID(int = source)
                except ValueError:
                    source_obj = UUID(bytes = source)

            self["source"] = unicode(source_obj)

        else:
            raise TypeError("You must provide a source entity for the linkage.")

        if target:

            if isinstance(target, UUID):
                target_obj = target

            else:
                try:
                    target_obj = UUID(target)
                except AttributeError:
                    target_obj = UUID(int = target)
                except ValueError:
                    target_obj = UUID(bytes = target)

            self["target"] = unicode(target_obj)

        else:
            raise TypeError("You must provide a target entity for the linkage.")

        # If "label" has been provided and it isn't a string, try to coerce it
        # to one.

        if label:
            self["label"] = unicode(label)

        else:
            self["label"] = None

        # Check for a valid linkage type, and convert values (the original
        # Java names) from "LINKAGE_TYPES" into their corresponding keys.

        if linkage_type in LINKAGE_TYPES:
            self["type"] = linkage_type

        else:
            for key, value in LINKAGE_TYPES.iteritems():
                if linkage_type == value:
                    self["type"] = key
                    break
            if not "linkage_type" in self:
                raise ValueError('"' + unicode(linkage_type) + '" is not a ' +
                                 'valid linkage type.')

        if isinstance(bool, isBiDirectional):
            self["isBiDidrectional"] = isBiDirectional

        else:
            raise TypeError('Argument "isBiDirectional" must be of type bool.')


    @classmethod
    def from_dict(cls, input_dict):
        """
        Constructs an instance using a dict-like as input.

        :param input_dict:  a dict-like with keys matching the keys in
            cls.VALID_KEYS; "source" and "target" are required, while "label",
            "type", and "isBiDirectional" are optional.  Any extraneous keys
            will raise an exception.

        :return:  a new instance

        For a normal dict subclass, we'd probably just use the update method
        here, but it's not implemented for this class--and anyway, we need to
        check the input values, which is something the __init__method already
        handles.
        """

        # Copy input_dict, so that we don't screw it up.
        input_copy = copy(input_dict)

        # Get the mandatory arguments.
        source = input_copy["source"]
        input_copy.remove("source")
        target = input_copy["target"]
        input_copy.remove("source")

        # Get the (optional) kwargs, and check for any extraneous arguments.
        input_kwargs = {}
        for key, value in input_copy.iteritems():
            if key in cls.VALID_KEYS:
                input_kwargs[key] = value
            else:
                raise KeyError(cls.INVALID_KEY_ERROR_MSG)

        # Call the class constructor.
        new_linkage = cls(source, target, **input_kwargs)

        return new_linkage


    def __setitem__(self, key, value):

        if not key in self.VALID_KEYS:
            raise KeyError(self.INVALID_KEY_ERROR_MSG)

        super(CoalesceAPILinkage, self).__setitem__(key, value)


    def setdefault(self, key, value = None):

        if not key in self.VALID_KEYS:
            raise KeyError(self.INVALID_KEY_ERROR_MSG)

        super(CoalesceAPILinkage, self).setdefault(key, value)


    def update(self, *args, **kwargs):

        raise NotImplementedError('The update method is not implemented for ' +
                                  'class "' + unicode(type(self)) + '".')

    def to_XSD(self):
        """
        Returns a version of the linkage as an instance of
        pyCoalesce.classes.CoalesceLinkage.

        Note that this representation does _not_ include a counterpart of the
        "isBiDirectional" attribute, since the XSD that defines Coalesce
        entities includes no such attribute (which means no such attribute is
        ever stored on the server).  To generate the reverse linkage, call
        "reverse_to_XSD".
        """

        linkage_XSD = CoalesceLinkage(entity1key = self["source"],
                                      entity2key = self["target"],
                                      linktype = LINKAGE_TYPES[self["type"]],
                                      label = self["label"])

        return linkage_XSD


    def reverse_to_XSD(self):
        """
        For a bidrectional linkage, returns a version of the the reverse linkage
        as an instance of pyCoalesce.classes.CoalesceLinkage.  Calling this
        method on a unidirectional linkage will raise an exception.
        """

        if self.isBiDirectional:
            linkage_XSD = CoalesceLinkage(entity1key = self["target"],
                                          entity2key = self["source"],
                                          linktype = LINKAGE_TYPES[self["type"]],
                                          label = self["label"])
            return linkage_XSD

        else:
            raise ValueError("This method can only be called on a " +
                             "on a bidirectional link.")

