import { getRootKarafUrl } from './common'

var karafRootAddr = getRootKarafUrl();

export function searchComplex(query)
{
  return fetch(`${karafRootAddr}/search/complex`, {
      method: "POST",
      body: JSON.stringify(query),
      headers: new Headers({
        'content-type': 'application/json; charset=utf-8'
      }),
    }).then(res => {
      if (!res.ok)
      {
        throw Error(res.statusText);
      }
      return res.json();
    }).catch(function(error) {
      throw Error(error);
    });
}

export const OPERATORS = {
  'STRING_TYPE': ['EqualTo', 'NotEqualTo', 'Like', 'NullCheck'],
  'STRING_LIST_TYPE': ['EqualTo', 'NotEqualTo', 'Like', 'NullCheck'],
  'GUID_TYPE': ['EqualTo', 'NotEqualTo', 'Like', 'NullCheck'],
  'GUID_LIST_TYPE': ['EqualTo', 'NotEqualTo', 'Like', 'NullCheck'],
  'DATE_TIME_TYPE': ['After', 'Before', 'During', 'NullCheck'],
  'URI_TYPE': ['EqualTo', 'NotEqualTo', 'Like', 'NullCheck'],
  'BINARY_TYPE': [],
  'FILE_TYPE': [],
  'BOOLEAN_TYPE': ['EqualTo', 'NotEqualTo', 'NullCheck'],
  'BOOLEAN_LIST_TYPE': ['EqualTo', 'NotEqualTo', 'NullCheck'],
  'INTEGER_TYPE': ['EqualTo', 'NotEqualTo', 'Between', 'GreaterThan', 'GreaterThanOrEqualTo', 'LessThan', 'LessThanOrEqualTo', 'NullCheck'],
  'INTEGER_LIST_TYPE': ['EqualTo', 'NotEqualTo', 'Between', 'GreaterThan', 'GreaterThanOrEqualTo', 'LessThan', 'LessThanOrEqualTo', 'NullCheck'],
  'DOUBLE_TYPE': ['EqualTo', 'NotEqualTo', 'Between', 'GreaterThan', 'GreaterThanOrEqualTo', 'LessThan', 'LessThanOrEqualTo', 'NullCheck'],
  'DOUBLE_LIST_TYPE': ['EqualTo', 'NotEqualTo', 'Between', 'GreaterThan', 'GreaterThanOrEqualTo', 'LessThan', 'LessThanOrEqualTo', 'NullCheck'],
  'FLOAT_TYPE': ['EqualTo', 'NotEqualTo', 'Between', 'GreaterThan', 'GreaterThanOrEqualTo', 'LessThan', 'LessThanOrEqualTo', 'NullCheck'],
  'FLOAT_LIST_TYPE': ['EqualTo', 'NotEqualTo', 'Between', 'GreaterThan', 'GreaterThanOrEqualTo', 'LessThan', 'LessThanOrEqualTo', 'NullCheck'],
  'LONG_TYPE': ['EqualTo', 'NotEqualTo', 'Between', 'GreaterThan', 'GreaterThanOrEqualTo', 'LessThan', 'LessThanOrEqualTo', 'NullCheck'],
  'LONG_LIST_TYPE': ['EqualTo', 'NotEqualTo', 'Between', 'GreaterThan', 'GreaterThanOrEqualTo', 'LessThan', 'LessThanOrEqualTo', 'NullCheck'],
  'GEOCOORDINATE_TYPE': ['BBOX', 'NullCheck'],
  'GEOCOORDINATE_LIST_TYPE': ['BBOX', 'NullCheck'],
  'LINE_STRING_TYPE': ['BBOX', 'NullCheck'],
  'POLYGON_TYPE': ['BBOX', 'NullCheck'],
  'CIRCLE_TYPE': ['BBOX', 'NullCheck'],
  'ENUMERATION_TYPE': ['EqualTo', 'NotEqualTo', 'NullCheck'],
  'ENUMERATION_LIST_TYPE': ['EqualTo', 'NotEqualTo', 'NullCheck'],
}
