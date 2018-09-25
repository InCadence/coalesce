export const isJsons = ((array) => Array.isArray(array) && array.every(
 row => (typeof row === 'object' && !(row instanceof Array))
));

export const isArrays = ((array) => Array.isArray(array) && array.every(
 row => Array.isArray(row)
));

export const jsonsHeaders = ((array) => Array.from(
 array.map(json => Object.keys(json))
 .reduce((a, b) => new Set([...a, ...b]), [])
));

export const jsons2arrays = (jsons, headers) => {
  headers = headers || jsonsHeaders(jsons);

  // allow headers to have custom labels, defaulting to having the header data key be the label
  let headerLabels = headers;
  let headerKeys = headers;
  if (isJsons(headers)) {
    headerLabels = headers.map((header) => header.label);
    headerKeys = headers.map((header) => header.key);
  }

  const data = jsons.map((object) => headerKeys.map((header) => (header in object) ? object[header] : ''));
  return [headerLabels, ...data];
};


export const joiner = ((data, max, separator = ',') =>
 data.map((row, index) => row.map((element) => "\"" + elementOrEmpty(element, max) + "\"").join(separator)).join(`\n`)
);

export const arrays2csv = ((data, headers, max, separator) =>
 joiner(headers ? [headers, ...data] : data, max, separator)
);

export const jsons2csv = ((data, headers, max, separator) =>
 joiner(jsons2arrays(data, headers), max, separator)
);

export const toCSV = (data, headers, max, separator) => {
 if (isJsons(data)) return jsons2csv(data, headers, max, separator);
 if (isArrays(data)) return arrays2csv(data, headers, max, separator);
 throw new TypeError(`Data should be a "String", "Array of arrays" OR "Array of objects" `);
};

const elementOrEmpty = (element, max) => element || element === 0 ? truncate(escapeDoubleQuotes(element), max) : '';

const escapeDoubleQuotes = (value) => {

  if (value && typeof value === 'string' && value.includes('"')) {
    value = value.replace(/"/g, '""');
  }

  return value;
}

const truncate = (value, max) => {

  if (value && max && value.length > max) {
    value = value.substring(0, max - 1) + " ...";
  }

  return value;
}
