export function getRootKarafUrl(path) {

  if (path == null) {
    path = 'data';
  }

  return `/cxf/${path}`;
}
