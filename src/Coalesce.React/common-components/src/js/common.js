export function getRootKarafUrl(path) {
  var karafRootAddr;

  if (path == null) {
    path = 'core';
  }

  if (window.location.port == 3000) {
    karafRootAddr  = `http://${window.location.hostname}:8181/cxf/${path}`;
  } else {
    karafRootAddr  = `/cxf/${path}`;
  }

  return karafRootAddr;
}

export function saveFile(blob, filename) {
  if (window.navigator.msSaveOrOpenBlob) {
    window.navigator.msSaveOrOpenBlob(blob, filename);
  } else {
    const a = document.createElement('a');
    document.body.appendChild(a);
    const url = window.URL.createObjectURL(blob);
    a.href = url;
    a.download = filename;
    a.click();
    setTimeout(() => {
      window.URL.revokeObjectURL(url);
      document.body.removeChild(a);
    }, 0)
  }
}
