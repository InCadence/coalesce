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
  return fetch(`${karafRootAddr}/entity/${entity.key}`, {
      method: ((isNew) ? "PUT" : "POST"),
      body: JSON.stringify(entity),
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
