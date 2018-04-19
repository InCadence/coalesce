import { getRootKarafUrl } from './common'

var karafRootAddr = getRootKarafUrl();

export function loadTemplates()
{
  return fetch(`${karafRootAddr}/templates`, {
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

export function loadTemplate(key)
{
  return fetch(`${karafRootAddr}/templates/${key}`, {
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

export function loadTemplateByEntity(entity) {
  return loadTemplateByName(entity.name, entity.source, entity.version);
}

export function loadTemplateByName(name, source, version)
{
  return fetch(`${karafRootAddr}/templates/${name}/${source}/${version}`, {
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

export function registerTemplate(key)
{
  return fetch(`${karafRootAddr}/templates/${key}`, {
      method: "PUT",
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

export function saveTemplate(template)
{
  return fetch(`${karafRootAddr}/templates/${template.key}`, {
      method: "POST",
      body: JSON.stringify(template),
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

export function createNewEntity(key) {
  return fetch(`${karafRootAddr}/templates/${key}/new`, {
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
