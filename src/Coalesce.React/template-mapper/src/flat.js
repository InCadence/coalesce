export function flatten(type, input) {

  var XML = 1;
  var JSON = 2;
  if(type == XML) {
    var x = 1;
    var parser = new DOMParser();
    var xmlDoc = parser.parseFromString(input,"text/xml");
    var root = xmlDoc.documentElement;

    var final = {};
    flatXml(final, xmlDoc, root)
    return final;
  }

}

function flatXml(final, xml, node) {
  var children = node.childNodes

  if(children.length === 0) {
    return null;
  }
  for(var i = 0; i < children.length; i++) {
    var child = children[i]
    if(child.nodeName === "#text" && child.nodeValue != " ") {
      var x = getXPathForElement(node, xml) + "/text()";
      final[x] = child.nodeValue
    }
    else {
      flatXml(final, xml, child);
    }
  }
}

function getXPathForElement(el, xml) {
	var xpath = '';
	var pos, tempitem2;

	while(el !== xml.documentElement) {
		pos = 0;
		tempitem2 = el;
		while(tempitem2) {
			if (tempitem2.nodeType === 1 && tempitem2.nodeName === el.nodeName) { // If it is ELEMENT_NODE of the same name
				pos += 1;
			}
			tempitem2 = tempitem2.previousSibling;
		}

    xpath = "*[name()='"+el.nodeName+"' and namespace-uri()='"+(el.namespaceURI===null?'':el.namespaceURI)+"']["+pos+']'+'/'+xpath;

		el = el.parentNode;
	}
	xpath = '/*'+"[name()='"+xml.documentElement.nodeName+"' and namespace-uri()='"+(el.namespaceURI===null?'':el.namespaceURI)+"']"+'/'+xpath;
	xpath = xpath.replace(/\/$/, '');
	return xpath;
}
