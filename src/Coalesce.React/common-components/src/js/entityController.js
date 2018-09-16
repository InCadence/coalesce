import { getRootKarafUrl } from './common'

var karafRootAddr = getRootKarafUrl();

export function createEntity(entity)
{
  return saveEntity(entity, true);
}

export function updateEntity(entity)
{
  return saveEntity(entity, false);
}

export function saveEntity(entity, isNew) {
  var url = `${karafRootAddr}/entity/`;
  var method = 'PUT';
  if(isNew) {
    method = 'POST';
    url += 'new'
  }
  else {
    url += entity.key;
  }
  return fetch(url, {
      method: method,
      body: JSON.stringify(entity),
      headers: new Headers({
        'content-type': 'application/json; charset=utf-8'
      }),
    }).then(res => {
      if (method === 'POST' && !res.ok)
      {
        throw Error(res.statusText);
      }
      else if (method === 'PUT' && !res.status === 204) {
        throw Error(res.statusText);
      }
      return res;
    }).catch(function(error) {
      throw Error(error);
    });
}

export function loadEntity(key) {
  return fetch(`${karafRootAddr}/entity/${key}`, {
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

export function deleteEntity(key) {
  return fetch(`${karafRootAddr}/entity/${key}`, {
      method: "DELETE",
    }).then(res => {
      if (!res.ok)
      {
        throw Error(res.statusText);
      }
    }).catch(function(error) {
      throw Error(error);
    });
}

export function deleteEntities(keys) {
  return fetch(`${karafRootAddr}/entity`, {
      method: "DELETE",
      body: JSON.stringify(keys),
      headers: new Headers({
        'content-type': 'application/json; charset=utf-8'
      }),
    }).then(res => {
      if (!res.ok)
      {
        throw Error(res.statusText);
      }
    }).catch(function(error) {
      throw Error(error);
    });
}
