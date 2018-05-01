# -*- coding: utf-8 -*-
"""
@author: sorr
"""

from pyparsing import Literal, Optional, SkipTo, CaselessLiteral, Or, Group, \
    commaSeparatedList, nestedExpr, ParseException, CharsNotIn, ZeroOrMore, \
    MatchFirst

from pyCoalesce import package_logger


# Set up logging.
logger = package_logger.getChild(__name__)

# The following characters, while legal for Coalesce names and values,
# may trip up this parser--however, they're not likely to be used in
# practice:
#    ():=<>!~&|,


# Set constants.  These could conceivably be loaded from a configuration
# file.
DEFAULT_RECORDSET = [u"CoalesceEntity"]
OPERATORS = \
    {u"=" : {"op" : u"PropertyIsEqualTo", "num_values" : 1},
     u"equals" : {"op" : u"PropertyIsEqualTo", "num_values" : 1},
     u"is" : {"op" : u"PropertyIsEqualTo", "num_values" : 1},
     u">" : {"op" : u"PropertyIsGreaterThan", "num_values" : 1},
     u"greater than" : {u"op" : u"PropertyIsGreaterThan", "num_values" : 1},
     u">=" : {"op" : u"PropertyIsGreaterThanOrEqualTo", "num_values" : 1},
     u"greater than or equal to" :
         {"op" : u"PropertyIsGreaterThanOrEqualTo", "num_values" : 1},
     u"<" : {"op" : u"PropertyIsLessThan", "num_values" : 1},
     u"less than" : {"op" : u"PropertyIsLessThan", "num_values" : 1},
     u"<=" : {"op" : u"PropertyIsLessThanOrEqualTo", "num_values" : 1},
     u"less than or equal to" :
         {"op" : u"PropertyIsLessThanOrEqualTo", "num_values" : 1},
     u"!=" : {"op" : u"PropertyIsNotEqualTo", "num_values" : 1},
     u"not equal to" : {"op" : u"PropertyIsNotEqualTo", "num_values" : 1},
     u"is not" : {"op" : u"PropertyIsNotEqualTo", "num_values" : 1},
     u"~" : {"op" : u"PropertyIsLike", "num_values": 1},
     u"like" : {"op" : u"PropertyIsLike", "num_values" : 1},
     u"><" : {"op" : u"PropertyIsBetween", "num_values" : 2},
     u"between" : {"op" : u"PropertyIsBetween", "num_values" : 2},
     u"during" : {"op" : "During", "num_values" : 2}}
GROUP_OPERATORS = {u"+" : u"AND", u"and" : u"AND", u"|" : u"OR", u"or" : u"OR"}


class SearchParser(object):
    """
    Provides an intuitive grammar for Coalesce searches.

    :ivar _query_parser:  a pyparsing object that performs the initial parsing
        of a search query.
    """

    def __init__(self):

        # Define the basic search criteria parser.  Vagaries of the way
        # pyparsing works mean that "recordset" and "optional_colon" can't
        # be logically linked without fouling up the parser results.  Thus,
        # while both elements are optional, it's possible to supply a colon
        # without a preceeding recordset without triggering an error.  In
        # such a case, the colon is simply ignored.
        colon = Literal(u":").suppress()
        recordset = Optional(SkipTo(colon), default = DEFAULT_RECORDSET). \
                    setResultsName(u"recordset")
        optional_colon = Optional(Literal(u":").suppress())
        op_types = []
        for op_key, op_definition in OPERATORS.iteritems():
            op_type = CaselessLiteral(op_key).setResultsName(op_key)
            op_types.append(op_type)
        operator = Or(op_types).setResultsName(u"operator")
        field_and_op = SkipTo(operator, include = True). \
                       setResultsName(u"field")
        value = commaSeparatedList.setResultsName(u"value")
        criteria_parser = (recordset + optional_colon + field_and_op + value). \
                          setResultsName("criteria", listAllMatches = True)

        # Define a parser for the group operators.
        group_op_types = []
        for group_op_key, group_op_definition in GROUP_OPERATORS.iteritems():
            group_op_type = \
                CaselessLiteral(group_op_key).setResultsName(group_op_key)
            group_op_types.append(group_op_type)
        group_operator_parser = \
            Or(group_op_types).setResultsName("group_operator",
              listAllMatches = True)

        # Define a parser for groups of criteria linked by operators.

        # First, define a function to parse the contents of a group that
        # aren't sub-groups.

        def extract_group_contents(loc, tokens):

            # Because of structure of "group_parser", each level of
            # results is nested in an extra, unnecessary list level.
            results = tokens[0]

            # If the parsed string has only a single token, it should be
            # be a criteria set.  Otherwise the first element in the tokens
            # list should be a nested enclosure that contains a criteria
            # set (and will have been parsed when that enclosure was
            # parsed).
            num_tokens = len(results)
            if num_tokens == 1 and isinstance(results[0], basestring):
                try:
                    results = criteria_parser.parseString(results[0])
                except ParseException:
                    raise ParseException("Expected a set of search criteria " +
                                         "as the contents of the enclosure " +
                                         "beginning at character position " +
                                         str(loc) + ".")
                tokens[0] = results
                return tokens

            # After that, tokens should alternate between group operators
            # and criteria sets (with the latter in their own nested
            # enclosures).
            for i in xrange(1, num_tokens, 2):
                try:
                    results[i] = group_operator_parser.parseString(results[i])
                except ParseException:
                    raise ParseException("Expected a group operator " +
                                         "as token number " + str(i) + "in " +
                                         "the enclosure beginning at " +
                                         "character position " + str(loc) + ".")

            # Check to make sure the input didn't end with a group operator.
            # (After the checks that have been performed above, if the
            # number of tokens is even, the last token must have been a
            # a group operator).
            even = num_tokens % 2 == 0
            if even:
                raise ParseException("Expected a set of search criteria at " +
                                     "the end of the enclosure begininng at " +
                                     "character position " + str(loc))

            tokens[0] = results
            return tokens

        # Now, define the group parser itself, incorporating the secondary
        # parsing in "extract_group_contents" through "setParseAction".
        # Note that a "group" enclosure must be at one of two levels:
        # either an inner-level set of criteria, or (only in groups with
        # multiple sets of criteria) an outer-level list alternating
        # between sets of criteria and group operators.
        group_parser = nestedExpr(content = CharsNotIn("()")). \
                       setParseAction(extract_group_contents). \
                       setResultsName("group", listAllMatches = True)

        # Finally, define parsers for nested sets of groups, and for the
        # entire query.  The seemingly convoluted syntax allows a complex
        # query to be written without parentheses around the entire
        # expression.
        group_set_parser = nestedExpr(content = group_parser +
                           ZeroOrMore(group_operator_parser + group_parser)). \
                           setResultsName("group_set", listAllMatches = True)
        self._query_parser = MatchFirst([Or([group_parser, group_set_parser]) +
                             ZeroOrMore(group_operator_parser +
                             Or([group_parser, group_set_parser])),
                             group_set_parser, group_parser, criteria_parser])


    def parse(self, query):
        """
        Transforms a text query into a Python dict corresponding to the
        JSON input expected by the Coalesce search API.

        :param query:  a text string (ASCII or Unicode)

        :return:  a dict corresponding to Coalesce search API input
        """

        def construct_query_dict(results):
            """
            Recurcisely construct a query object in the Python analog of
            the JSON syntax required by the Coalesce search API.

            :param results: a pyparsing ParseResults object.

            :return: the search-query dict, or a subset of it.
            """

            query_dict = {u"group" : {}}

            for result in results:

                # If this result is a set of criteria, or a group operator,
                # transform it into the lowest-level element of the return
                # dict.  Otherwise, process the result recursively, and
                # assemble the dict elements into larger blocks.

                if "criteria" in result:
                    criteria_set = result.asDict()
                    if not u"criteria" in query_dict["group"]:
                        query_dict["group"][u"criteria"] = [criteria_set]
                    else:
                        query_dict["group"][u"criteria"].append(criteria_set)

                # Which branch of the following test works depends on
                # whether the operator in question is wrapped in a (single-
                # item) list--and that depends on exactly which parser the
                # result comes from.
                elif "group_operator" in result or result in GROUP_OPERATORS:
                    operator = GROUP_OPERATORS[result]

                    if not u"operator" in query_dict["group"]:
                        query_dict["group"][u"operator"] = operator

                    else:

                        # If this operator is different from the previous
                        # group operator, we need to create a new group.
                        if operator != query_dict["group"][u"operator"]:


                else:
                    sub_query_dict = construct_query_dict(result)

            return query_dict



        # Parse the query.
        parsed = self._query_parser.parseString(query)







