export function getRootKarafUrl(path) {
  var karafRootAddr;

  if (path == null) {
    path = 'data';
  }

  if (window.location.port == 3000) {
    karafRootAddr  = `http://${window.location.hostname}:8181/cxf/${path}`;
  } else {
    karafRootAddr  = `/cxf/${path}`;
  }

  return karafRootAddr;
}
