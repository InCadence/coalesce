import * as React from "react";
import * as ReactDOM from "react-dom";
import Popup from 'react-popup';
import {registerLoader, registerErrorPrompt, registerPromptDropdown} from 'common-components/lib/register.js'
import {GraphView} from './graph.js'

// TODO Should reference menu.js from common but this is not working
import {Menu} from 'common-components/src/menu.js'
//import {Menu} from './menu.js'
import 'common-components/bootstrap/css/bootstrap.min.css'

import 'common-components/css/coalesce.css'
import 'common-components/css/popup.css'

var pjson = require('../package.json');
document.title = pjson.title;

var rootUrl;

if (window.location.port == 3000) {
  rootUrl  = 'http://' + window.location.hostname + ':8181';
} else {
  rootUrl  = '';
}

registerLoader(Popup);
registerErrorPrompt(Popup);
registerPromptDropdown(Popup);

function loadBlueprint(filename) {

  Popup.plugins().loader('Loading...');

  fetch(rootUrl + '/cxf/data/blueprints/' + filename)
    .then(res => res.json())
    .then(data => {

      var totals = {
        'SERVER': 0,
        'CONTROLLER_ENDPOINT': 0,
        'ENDPOINT': 0,
        'FRAMEWORK': 0,
        'PERSISTER': 0,
        'SETTINGS': 0,
        'ENTITY': 0,
        'OTHER': 0
      };


      data.nodes.forEach(function(node) {
        if (totals[node.nodeType] == null) {
          totals[node.nodeType] = 1;
        } else {
          totals[node.nodeType]++;
        }
      });

      var largest = 0;

      Object.keys(totals).forEach(function(key) {
        if (totals[key] > largest) {
          largest = totals[key];
        }
      });

      var colWidth = 200;
      var rowWidth = 100;

      var counts = {};
      var col = 1;

      Object.keys(totals).forEach(function(key) {

        counts[key] = {
          x: col++*colWidth,
          y: ((largest) / 2) - (totals[key] / 2) + 1
        }
      });

      ReactDOM.unmountComponentAtNode(document.getElementById('main'));

      data.nodes.forEach(function(node) {

        // Consolidate Node Types
        if (node.nodeType === 'CONTROLLER') {
            node.nodeType = "ENDPOINT";
        } else if (node.nodeType === 'CLIENT') {
          node.nodeType = "PERSISTER";
        }

        node.x=counts[node.nodeType].x;
        node.y=counts[node.nodeType].y++ * rowWidth;

        //http://colorbrewer2.org/#type=sequential&scheme=Oranges&n=6
        switch (node.nodeType) {
          case "SERVER":
            node.symbolType = 'square';
            node.color = '#084081';
            node.size = 800;
            node.strokeColor = '#FF9900';
            node.strokeWidth=1.5
            break;
          case "CONTROLLER_ENDPOINT":
            node.symbolType = 'wye';
            node.color = '#0868ac';
            node.size = 800;
            node.strokeColor = '#FF9900'
            break;
          case "ENDPOINT":
            node.symbolType = 'cross';
            node.color = '#2b8cbe';
            node.size = 800;
            node.strokeColor = '#FF9900'
            break;
          case "FRAMEWORK":
            node.symbolType = 'triangle';
            node.color = '#4eb3d3';
            node.size = 400;
            node.strokeColor = '#FF9900'
            break;
          case "PERSISTER":
            node.symbolType = 'star';
            node.color = '#7bccc4';
            node.size = 800;
            node.strokeColor = '#FF9900'
            break;
          case "SETTINGS":
            node.symbolType = 'circle';
            node.color = '#a8ddb5';
            node.size = 400;
            node.strokeColor = '#FF9900'
            break;
          case "ENTITY":
            node.symbolType = 'square';
            node.color = '#ccebc5';
            node.size = 100;
            node.strokeColor = '#FF9900'
            break;
          default:
            node.symbolType = 'square';
            node.color = '#ccebc5';
            node.size = 100;
            node.strokeColor = '#FF9900'
            break;
        }

      })


      Popup.close();

      ReactDOM.render(
      <GraphView data={data} title={filename} />,
        document.getElementById('main')
      );
  }).catch(function(error) {
    Popup.plugins().promptError("Loading Settings: " + error);
  });
}

// Default Component
ReactDOM.render(
    <Popup />,
    document.getElementById('popupContainer')
);

loadBlueprint("rest-blueprint.xml")

ReactDOM.render(
    <Menu logoSrc={pjson.icon} title={pjson.title} items={[
      {
        id: 'load',
        name: 'Load',
        img: "/images/svg/load.svg",
        title: 'Load Entity',
        onClick: () => {
          fetch(rootUrl + '/cxf/data/blueprints')
            .then(res => res.json())
            .then(data => {

              var options = [];

              data.forEach(function (filename) {
                options.push({
                  'key': filename,
                  'name': filename
                })
              })

              Popup.plugins().promptDropdown('Load', 'Select Blueprint', options[0].key, options, function (filename) {
                loadBlueprint(filename)
              });
          }).catch(function(error) {
            Popup.plugins().promptError("Loading Blueprint: " + error);
          });
        }
      }]}/>,
    document.getElementById('myNavbar')
);
