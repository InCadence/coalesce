import { getRootKarafUrl } from './common'

var karafRootAddr = getRootKarafUrl();

export function createEntity(entity)
{
  return fetch(`${karafRootAddr}/entity/`, {
      method: "POST",
      body: JSON.stringify(entity),
      headers: new Headers({
        'content-type': 'application/json; charset=utf-8'
      }),
    }).then(res => {
      if (!res.ok)
      {
        throw Error(res.statusText);
      }
      // Return key generated by server
      return res.text();
    }).catch(function(error) {
      throw Error(error);
    });
}

export function updateEntity(entity)
{
  return fetch(`${karafRootAddr}/entity/${entity.key}`, {
      method: "PUT",
      body: JSON.stringify(entity),
      headers: new Headers({
        'content-type': 'application/json; charset=utf-8'
      }),
    }).then(res => {
      if (!res.ok)
      {
        throw Error(res.statusText);
      }
      return entity.key
    }).catch(function(error) {
      throw Error(error);
    });
}

export function saveEntity(entity, isNew) {
  if (isNew) {
    return createEntity(entity);
  } else {
    return updateEntity(entity);
  }
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
