import * as React from "react";

import { DialogMessage, DialogOptions } from 'common-components/lib/components/dialogs'
import { Menu } from 'common-components/lib/components'
import { getRootKarafUrl } from 'common-components/lib/js/common'

import { MuiThemeProvider } from '@material-ui/core/styles'

import { GraphView } from './graph'


export class App extends React.Component {

  constructor(props) {
    super(props);

    this.state = {
      data: null
    };

    this.setState = this.setState.bind(this)
  }

  componentDidMount() {
    this.loadBlueprint("core-blueprint.xml");
  }

  render() {

    const { data, selected, error, theme, blueprints } = this.state;
    const that = this;
    return (
      <MuiThemeProvider theme={this.props.theme}>
        <Menu logoSrc={this.props.icon} title={`${this.props.title} / ${selected}`} items={[
          {
            id: 'load',
            name: 'Load',
            img: "/images/svg/load.svg",
            title: 'Load Entity',
            onClick: () => {
              getBlueprintOptions().then(data => {

                  data.push("Network Diagram");

                  this.setState({
                    blueprints: data,
                    data: null
                  })

              }).catch(function(error) {
                that.setState({error: `Loading Blueprint: ${error}`});
              });
            }
          },
          {
            id: 'add',
            name: 'Add',
            img: "/images/svg/add.svg",
            title: 'Add Entity',
            onClick: () => this.setState({actions: 'adding'})
          }
          ]}/>
          { data != null &&
            <GraphView actions={this.state.actions} data={data} title={selected} theme={theme} />
          }
          <DialogMessage
            title="Error"
            opened={error != null}
            message={error}
            onClose={() => {this.setState({error: null})}}
          />
          {blueprints != null &&
            <DialogOptions
              title="Selection"
              open={data == null && this.state.error == null}
              onClose={() => {this.loadBlueprint(selected)}}
              options={blueprints.map((blueprint) => {
                return {
                  key: blueprint,
                  name: blueprint,
                  onClick: () => {blueprint === 'Network Diagram' ? this.loadNetwork() : this.loadBlueprint(blueprint)}
                }
              })}

            >
            </DialogOptions>
          }
        </MuiThemeProvider>
    )
  }

  loadNetwork() {
    getNetwork().then((value) => {

      this.setState({
        selected: 'Network Diagram',
        data: this.formatData(value)
      })

    }).catch((err) => {
      this.setState({error: `Loading Network Diagram: ${err}`})
    })
  }

  loadBlueprint(blueprint) {

    getBlueprint(blueprint).then((value) => {
      this.setState({
        selected: blueprint,
        data: this.formatData(value)
      });

    }).catch((err) => {
      this.setState({error: `Loading ${blueprint}: ${err}`})
    })

  }

  formatData(data) {
    var totals = {
      'SERVER': 0,
      'CONTROLLER_ENDPOINT': 0,
      'ENDPOINT': 0,
      'FRAMEWORK': 0,
      'PERSISTER': 0,
      'SETTINGS': 0,
      'ENTITY': 0,
      'OTHER': 0,
      'node': 0,
      'role': 0
    };


    data.nodes.forEach(function(node) {
      // Consolidate Node Types
      if (node.type === 'CONTROLLER') {
          node.type = "ENDPOINT";
      } else if (node.type === 'CLIENT') {
        node.type = "PERSISTER";
      }

      if (totals[node.type] == null) {
        totals[node.type] = 1;
      } else {
        totals[node.type]++;
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

    //implement tree
    Object.keys(totals).forEach(function(key) {
      if (totals[key] > 0) {
        counts[key] = {
          x: col++*colWidth,
          y: ((largest) / 2) - (totals[key] / 2) + 1
        }
      }
    });

    data.nodes.forEach(function(node) {
      node.x=counts[node.type].x;
      node.y=counts[node.type].y++ * rowWidth;

      //http://colorbrewer2.org/#type=sequential&scheme=Oranges&n=6
      switch (node.type) {
        case "SERVER":
        case "node":
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
        case "role":
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
    return data;
  }
}

function getBlueprintOptions () {

  var karafRootAddr = getRootKarafUrl();
  return fetch(`${karafRootAddr}/blueprints`)
    .then(res => {
      if (!res.ok)
      {
        throw Error(res.statusText);
      }
      return res.json();
    }).catch(function(error) {
      throw Error(error);
    });
}

function getBlueprint(filename) {

  var karafRootAddr = getRootKarafUrl();
  console.log(karafRootAddr);
  return fetch(`${karafRootAddr}/blueprints/${filename}`)
    .then(res => {
      if (!res.ok)
      {
        throw Error(res.statusText);
      }
      return res.json();
    }).catch(function(error) {
      throw Error(error);
    });
}

function getNetwork() {

  var karafRootAddr = getRootKarafUrl();

  return fetch(`${karafRootAddr}/network`)
    .then(res => {
      if (!res.ok)
      {
        throw Error(res.statusText);
      }
      return res.json();
    }).catch(function(error) {
      throw Error(error);
    });
}
