import { getRootKarafUrl } from './common'

var karafRootAddr = getRootKarafUrl();

export function loadJSON(name)
{
  return fetch(`${karafRootAddr}/property/${name}.json`, {
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

export function saveJSON(name, json)
{
  return fetch(`${karafRootAddr}/property/${name}.json`, {
      method: "PUT",
      body: JSON.stringify(json),
      headers: new Headers({
        'content-type': 'application/json; charset=utf-8'
      }),
    }).then(res => {
      if (!res.ok)
      {
        throw Error(res.statusText);
      }
      return res;
    }).catch(function(error) {
      throw Error(error);
    });
}


export function loadProperty(name)
{
  return fetch(`${karafRootAddr}/property/${name}`, {
      method: "GET",
      headers: new Headers({
        'content-type': 'application/json; charset=utf-8'
      }),
    }).then(res => {
      if (!res.ok)
      {
        throw Error(res.statusText);
      }
      return res.text();
    }).catch(function(error) {
      throw Error(error);
    });
}

// Properties is an array of strings
export function loadProperties(properties)
{
  return fetch(`${karafRootAddr}/property/`, {
      method: "POST",
      body: JSON.stringify(properties),
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

// Properties is a Map<String, String>
export function saveProperties(properties)
{
  return fetch(`${karafRootAddr}/property/`, {
      method: "PUT",
      body: JSON.stringify(properties),
      headers: new Headers({
        'content-type': 'application/json; charset=utf-8'
      }),
    }).then(res => {
      if (!res.ok)
      {
        throw Error(res.statusText);
      }
      return res;
    }).catch(function(error) {
      throw Error(error);
    });
}
