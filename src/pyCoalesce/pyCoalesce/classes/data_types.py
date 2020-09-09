# -*- coding: utf-8 -*-
"""
@author: Scott Orr

This module defines several Coalesce data types, providing a class method
for instantiating each from a Coalesce XML string.  These classes can be
imported directly from the :mod:`pyCoalesce.classes` module.

"""

from ast import literal_eval
from collections import abc
import csv
from urllib.parse import urlsplit

from shapely.geometry import Point
from shapely.errors import DimensionError


class CoalesceData(abc.ABC):
    """
    This is an abstract base class for Coalesce data types.  It specifies
    a commmon interface for all such types:  a class method to instantiate
    an object from the XML :attr:`~pyCoalesce.classes.entity.fieldSub.value`
    of a :attr:`Caolesce field <pyCoalesce.classes.coalesce_entity.fieldSub>`,
    namely :meth:`~pyCoalesce.classes.data_types.CoalesceData.from_Coalesce`.


    """

    @abc.abstractmethod
    @classmethod
    def from_Coalesce(cls, value):
        return


@CoalesceData.register
class GeoCoordinates(Point):
    """
    Provides a Coalesce implementation for 2D and 3D points, with
    methods to convert to and from the WKT (well-known text) format used by
    Coalesce.  Input to the default constructor can take one of two
    forms:

    * 2 or 3 numerical inputs in the order of latitude (degrees),
      longitude (degrees), and, optionally, elevation (meters).  The
      constructor will also accept these values as keyword arguments, with
      "lat", "long", and "elev" as the keywords.  Non-keyword and keyword
      arguments cannot be mixed.
    * A list-like or array-lke iterable of 2 (latitude, longitude) or 3
      (latitude, longitude, elevation) numerical items.

    The coordinates can be read (but not set) indvidually as properties:
    :attr:`~pyCoalesce.classes.data_types.GeoCoordinates.lat`,
    :attr:`~pyCoalesce.classes.data_types.GeoCoordinates.long`, and
    :attr:`~pyCoalesce.classes.data_types.GeoCoordinates.elev`.

    Internally, the class stores these coordinates in the form inherited
    from :class:`~shapely.geometry.Point`, readable as the propertites
    :attr:`~pyCoalesce.classes.data_types.GeoCoordinates.x`,
    :attr:`~pyCoalesce.classes.data_types.GeoCoordinates.y`, and
    :attr:`~pyCoalesce.classes.data_types.GeoCoordinates.z`.  It's
    important to note that because x corresponds to
    :attr:`~pyCoalesce.classes.data_types.GeoCoordinates.long` and y
    corresponds to :attr:`~pyCoalesce.classes.data_types.GeoCoordinates.lat`,
    the order of the coordinates is inverted in this class from the
    parent Point class, with in the constructor and the
    :meth:`~pyCoalesce.classes.data_types.GeoCoordinates.__str__`
    following the traditional order for geographic coordinates of  latitude
    first, rather than the traditional Cartesian order, with x  first, as
    followed by Point (and, for that matter, the Coalesce Java interface).
    However, when the an object of this class is fed to the Shapely
    func:`wkt.dumps <shapely.wkt.dumps>`  function, the resulting WKT
    string has x (longitude) first, which is the correct format for
    Coalesce XML.

    """

    def __init__(self, *args, **kwargs):
        """
        Checks input for numerical values in the proper ranges.

        """

        # If we received non-keyword input...
        if args:

            # Check for mixed keyword/non-keyword input.
            if kwargs:
                raise ValueError("Keyword and non-keyword arguments cannot " +
                                 "be used together.")

            # If the input was list-like or array-like, pull that list
            # out.
            if len(args) == 1:
                coords = args[0]

            # If we have an individual argument for each coordinate, since
            # the superclass (Point) constructor will take a list-like, we
            # can simple use "args" as that input.
            else:
                coords = args

            # Swap lat and long in the list order.
            lat = coords[0]
            long = coords[1]
            coords[0] = long
            coords[1] = lat

        # If we received keyword input...
        else:
            if "elev" in kwargs:
                coords = [kwargs["long"], kwargs["lat"], kwargs["elev"]]
            else:
                coords = [kwargs["long"], kwargs["lat"]]

        # Pass the coordinates to the superclass (Point) constructor, which
        # will make sure we have the correct number (2-3) and type
        # (numerical) of coordinates.
        super().__init__(coords)

        # Make sure that the values for lat and long are valid.  We do this
        # after calling the parent constructor so that we don't have to
        # worry here about a type error.
        if lat < -90 or lat > 90:
            raise ValueError("Latitude must be a number between -90 and 90, " +
                             "inclusive.")
        if long < -180 or long > 180:
            raise ValueError("Longitude must be a number between -180 and " + \
                             "180, inclusive.")


    @property
    def lat(self):
        return self.y


    @property
    def long(self):
        return self.x


    @property
    def elev(self):
        try:
            elev = self.z
        except DimensionError:
            raise DimensionError("These coordinates do not include elevation.")
        else:
            return elev


    def __repr__(self):

        # Create a string with latitude and longitude.
        repr_string = "Latitude: " + str(self.lat) + ", Longitude: " + \
                      str(self.long)

        # If there's an elevation, add that.
        if len(self.coords[0]) > 2:
            repr_string += ", Elevation: " + str(self.elev) + "m"

        return repr_string


    def __str__(self):
        return self.__repr__()


    @classmethod
    def from_WKT(cls, WKT_string):
        """
        Creates a :class:`~pyCoalesce.classes.data_types.GeoCoordinate`
        object from a string in WKT (well-known text) format.

        :param text:  a string of 2-4 coordinates in WKT frns:  an instance of
            :class:`~pyCoalesce.classes.data_types.GeoCoordinate`

        """

        coords_as_text = csv.reader(CSV, delimiter = ',', quotechar = '"')
        coords = [literal_eval(coord) for coord in coords_as_text]

        return cls.from_coords(coords)


    def to_Coalesce(self):
        """
        Creates a representation of the
            :class:`~pyCoalesce.classes.data_types.GeoCoordinate` object in
            Coalesce CSV format.

        :returns:  a string of 2-4 coordinates in Coalesce CSV format

        """

        # Convert coordinates to strings, and get rid of any `None` values.
        coords_as_text = [str(coord) for coord in self if coord]

        # Create the CSV.  Note that, because we're dealing only with
        # integers and floats here, there's no need to escape anything
        # with quotes.
        CSV = ','.join(coords_as_text)

        return CSV


@CoalesceData.register
class GeoCircle(tuple):
    pass


@CoalesceData.register
class URL(str):
    """
    Adds a check to the vanilla string constructor to make sure the value
    is a valid and supported URL of a scheme appropriate for a RESTful
    server (that is, HTTP or HTTPS).

    """

    VALID_SCHEMES = ("http", "https")


    def __new__(cls, value):
        """
        :param value:  a URL as an ASCII or Unicode string

        """

        scheme = urlsplit(value).scheme
        if scheme == "":
            raise ValueError("The provided address is not a URL.")
        elif scheme.lower() not in cls.VALID_SCHEMES:
            raise ValueError('"' + scheme + '" is not a valid URL scheme.')

        return super().__new__(cls, value)


    @classmethod
    def from_Coalesce(cls, value):
        """
        This method is provided solely to satisfy the requirement to
        implement the abstract method--it's simply an alias for the
        normal constructor.

        """

        return cls(value)