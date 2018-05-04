import { getRootKarafUrl } from './common'

var karafRootAddr = getRootKarafUrl();

export function getEnumerations() {
  return fetch(`${karafRootAddr}/enumerations`, {
      method: "GET",
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

export function getEnumeration(key) {
  return fetch(`${karafRootAddr}/enumerations/${key}`, {
      method: "GET",
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

export function getEnumerationValues(key) {
  return fetch(`${karafRootAddr}/enumerations/${key}/values`, {
      method: "GET",
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

export function getEnumerationAssociatedValues(key, valueKey) {
  return fetch(`${karafRootAddr}/enumerations/${key}/values/${valueKey}/associatedValues`, {
      method: "GET",
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
