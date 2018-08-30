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

export function loadTemplateAsXML(key)
{
  return fetch(`${karafRootAddr}/templates/${key}.xml`, {
      method: "GET",
      headers: new Headers({
        'content-type': 'application/xml; charset=utf-8'
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
  var templateKey = template.key
  // If there's been a change to name/source/version, save this as a new template.
  if (!templateKey) {
    templateKey = "new"
  }
  return fetch(`${karafRootAddr}/templates/${templateKey}`, {
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
      return res.text();
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
