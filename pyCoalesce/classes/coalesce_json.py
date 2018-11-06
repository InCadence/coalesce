# -*- coding: utf-8 -*-
"""
This module provides a JSON-serializable version of Coalesce linkages that,
unlike the :class:`pyCoalesce.classes.coalesce_entity.CoalesceLinkage`
class, can be used with the JSON-only Coalesce linkage API.  Both this
class and :class:`~pyCoalesce.classes.coalesce_entity.CoalesceLinkage`
include methods to convert linkages to the other class.

The class can be imported directly from the :mod:`pyCoalesce.classes`
module.

@author: sorr

"""

from sys import stdout, stderr
from uuid import UUID
from copy import copy

from coalesce_entity import LINKAGE_TYPES, CoalesceLinkage


def _test_key(key):
    """
    Determines the format of an input UUID key, and, if necessary,
    transforms the input into a string

    :param key:  the key to be tested and transformed into a string

    :returns:  "key" as a string

    """

    key_error_msg = 'The argument "source" and "target" must be UUID keys, ' + \
                    'instances of the class "uuid.UUID", or any strings or ' + \
                    'integers that could serve as input for that class\'s ' + \
                    'class constructor.'

    if isinstance(key, basestring):

        try:
            UUID(key)

        except ValueError:
            try:
                key_obj = UUID(bytes = key)

            except ValueError:
                raise ValueError(key_error_msg)

        else:
            return key

    else:

        key_len = len(unicode(key))

        if key_len == 36:
            key_obj = key

        else:
            try:
                key_obj = UUID(int = key)
            except ValueError:
                raise ValueError(key_error_msg)

    key_str = unicode(key_obj)

    return key_str


class CoalesceAPILinkage(dict):
    """
    A JSON-serializable version of a Coalesce linkage, intended for use
    with the linkage CRUD endpoints of the RESTful API, which allow the
    creation, retreival, and deletion of links independently of their
    corresponding entities.  This capability makes it possible to submit
    links in bulk jobs, and also allows the submission of bi-directional
    links (which Coalesce instantiates by creating links in each of the
    corresponding entities).

    While this class is used to create Coalesce linkages, its format is
    different from the representation of those linkages in the databases
    themselves, and in the Java and RESTful entity API's:  this class's
    keys match the attribues in the Java GraphLink class, which correspond
    to a subset of the full set of keys/attributes in the entity model, but
    which use different names (thus, though this class is
    JSON-serializable, it doesn't produce a JSON object that looks the same
    as a nested linkage object in a JSON representation of an entity).

    Although this is a subclass of dict (in order to make it
    JSON-serializable), only six keys can be set, each corresponding to one
    of the attributes included in the GraphLink class.  The __setitem__ and
    setdefault methods enforce this restriction, and the update method has
    been disabled.

    Keys:

    * "source":  the UUID key of the source entity, as either an instance
      of :class:`uuid.UUID` or a string or integer that can be used as
      input to the :class:`UUID <uuid.UUID>` class constructor.  The
      linkage created on the server will be part of this entity.

    * "target":  the UUID key of the target entity, as either an instance
      of :class:`uuid.UUID` or a string or integer that can be used as
      input to the :class:`UUID <uuid.UUID>` class constructor.  The
      linkage created on the server will not be part of this entity, unless
      the link is bi-directional.

    * "label":  a string label for the linkage.  This attribute isn't used
      by Coalesce, and may be set to any value required by an application.

    * "type":  the (string) type of linkage.  The value of this attribute
      must be one the keys in
      :const:`pyCoalesce.classes.coalesce_entity.LINKAGE_TYPES`.

    * "isBiDirectional":  a boolean indicating whether or not the server
      should create a second linkage, from the target to the source.  The
      unPythonic name of this key is due to the need to match the attribute
      name of the Java GraphLink class.

    * "status":  the current status (active or deleted) of the linkage.
      This key is set by the server, and therefore can't be specified in
      the class constructor, and the constructor sets its value to
      ``None``.  However, the server will accept accept changes to
      "status", and its value can be set directly.  The key can be passed
      through the
      :meth:`~pyCoalesce.classes.coalesce_JSON.CoalesceAPILinkage.from_dict`
      method, allowing a server-set value to be retained for links received
      from the RESTful API.

    Class Constant:

    """

    # Define the list of valid keys.

    VALID_KEYS = ("source", "target", "label", "type", "biDirectional")
    """
    All instances have these keys (though "label" may have a value of
    ``None``), and may not have any others.

    """

    SERVER_KEYS = ("status",)
    """
    This key may be set only by the server, and therefore isn't included
    in the :class:`~pyCoalesce.classes.coalesce_JSON.CoalesceAPILinkage`
    constructor.

    """

    _INVALID_KEY_ERROR_MSG = "Only the following keys may be set:\n" + \
                            unicode(VALID_KEYS + SERVER_KEYS)


    def __init__(self, source = None, target = None, label = None,
                 linkage_type = "UNDEFINED", biDirectional = False):
        """
        :param source:  the UUID key of the source entity, as either an
            instance of :class:`uuid.UUID` or a string or integer that can
            be used as input to the :class:`UUID <uuid.UUID>` class
            constructor.  The linkage created on the server will be part of
            this entity.  This argument is required.

        :param target:  the UUID key of the target entity, as either an
            instance of :class:`uuid.UUID` or a string or integer that can
            be used as input to the :class:`UUID <uuid.UUID>` class
            constructor. The linkage created on the server will not be part
            of this entity, unless the link is bi- directional.  This
            argument is
             required.

        :param label:  a string label for the linkage.  This attribute
            isn't used by Coalesce, and may be set to any value required by
            an application.

        :param linkage_type:  the (string) type of linkage; the actual name
            of the key is "type", but this is a reserved word in Python,
            hence unusable as a keyword argument name, and therefore an
            alias is used here.  The value of this attribute must be one of
            the keys in
            :const:`pyCoalesce.classes.coalesce_entity.LINKAGE_TYPES`.

        :param biDirectional:  a boolean indicating whether or not the
            server should create a second linkage, from the target to the
            source.  The unPythonic name of this argument is due to to need
            to match the attribute name of the Java GraphLink class.

        """

        # Make sure "source" and "target" are valid ID's, then convert them
        # (back) to strings and set the corresponding values.

        if source:
            self["source"] = _test_key(source)

        else:
            raise TypeError("You must provide a source entity for the linkage.")

        if target:
            self["target"] = _test_key(target)

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
            if not "type" in self:
                raise ValueError('"' + unicode(linkage_type) + '" is not a ' +
                                 'valid linkage type.')

        if isinstance(biDirectional, bool):
            self["biDirectional"] = biDirectional

        else:
            raise TypeError('Argument "biDirectional" must be of type bool.')

        self["status"] = None


    @classmethod
    def from_dict(cls, input_dict):
        """
        Constructs an instance using a dict-like as input.

        :param input_dict:  a dict-like with keys matching the keys in
            :attr:'cls.VALID_KEYS'; "source" and "target" are required,
            while "label", "type", and "isBiDirectional" are optional.  Any
            extraneous keys will raise an exception.

        :returns:  a new instance

        For a normal dict subclass, we'd probably just use the update
        method here, but it's not implemented for this class--and anyway,
        we need to check the input values, which is something the
        :meth:`__init__` method already handles.

        """

        # Copy input_dict, so that we don't uninentionally alter it.
        input_copy = copy(input_dict)

        # Get the mandatory arguments.
        source = input_copy.pop("source")
        target = input_copy.pop("target")

        # Get the (optional) kwargs, and check for any extraneous
        # arguments.   If "type" is present, we need to substitute
        # "linkage_type", which is the equivalent used by the constructor.

        input_kwargs = {}
        direct_set_keys = {}

        for key, value in input_copy.iteritems():

            if key in cls.VALID_KEYS:
                if key == "type":
                    input_kwargs["linkage_type"] = value
                else:
                    input_kwargs[key] = value

            elif key in cls.SERVER_KEYS:
                direct_set_keys[key] = value

            else:
                raise KeyError(cls._INVALID_KEY_ERROR_MSG)

        # Call the class constructor.
        new_linkage = cls(source, target, **input_kwargs)

        # Set any server keys:
        for key, value in direct_set_keys.iteritems():
            setattr(new_linkage, key, value)

        return new_linkage


    def __setitem__(self, key, value):
        """
        This :class:`dict` method has been overridden to add a check for
        extraneous keys.

        """

        if not (key in self.VALID_KEYS or key in self.SERVER_KEYS):
            raise KeyError(self._INVALID_KEY_ERROR_MSG)

        super(CoalesceAPILinkage, self).__setitem__(key, value)


    def setdefault(self, key, value = None):
        """
        This :class:`dict` method has been overriden to add a check for
        extraneous keys.

        """

        if not (key in self.VALID_KEYS or key in self.SERVER_KEYS):
            raise KeyError(self._INVALID_KEY_ERROR_MSG)

        super(CoalesceAPILinkage, self).setdefault(key, value)


    def update(self, *args, **kwargs):
        """
        This :class:`dict` method has been disabled to prevent the addition
        of extraneous keys.

        """

        raise NotImplementedError('The update method is not implemented for ' +
                                  'class "' + unicode(type(self)) + '".')

    def to_XSD(self):
        """
        Returns a version of the linkage as an instance of
        :class:`pyCoalesce.classes.coalesce_entity.CoalesceLinkage`.

        Note that this representation does _not_ include a counterpart of
        the "isBiDirectional" attribute, since the XSD that defines
        Coalesce entities includes no such attribute (which means no such
        attribute is ever stored on the server).  To generate the reverse
        linkage, call "reverse_to_XSD".

        :returns:  the linkage as an instance of class
            :class:`~pyCoalesce.classes.coalesce_entity.CoalesceLinkage`

        """

        linkage_XSD = CoalesceLinkage(entity1key = self["source"],
                                      entity2key = self["target"],
                                      linktype = LINKAGE_TYPES[self["type"]],
                                      label = self["label"])

        linkage_XSD.status = self["status"]

        return linkage_XSD


    def reverse_to_XSD(self):
        """
        For a bidrectional linkage, returns a version of the the reverse
        linkage as an instance of
        :class:`pyCoalesce.classes.coalesce_entity.CoalesceLinkage`.
        Calling this method on a unidirectional linkage will raise an
        exception.

        :returns:  the reverse linkage as an instance of class
            :class:`~pyCoalesce.classes.coalesce_entity.CoalesceLinkage`

        """

        if not self["biDirectional"]:
            raise ValueError("This method can only be called on a " +
                         "on a bidirectional link.")

        linkage_XSD = CoalesceLinkage(entity1key = self["target"],
                                      entity2key = self["source"],
                                      linktype = LINKAGE_TYPES[self["type"]],
                                      label = self["label"])

        return linkage_XSD

