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
