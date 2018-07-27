import * as React from "react";
import { Graph } from 'react-d3-graph';
import Paper from '@material-ui/core/Paper';
import { DialogMessage } from 'common-components/lib/components/dialogs'
import ReactTable from 'react-table'
//import Checkbox from 'material-ui/Checkbox';
import Select from '@material-ui/core/Select';
import MenuItem from '@material-ui/core/MenuItem';
import IconButton from '@material-ui/core/IconButton';
import NavigationClose from '@material-ui/icons/Close';
import Input from '@material-ui/core/Input';
import InputLabel from '@material-ui/core/InputLabel';
import FormControl from '@material-ui/core/FormControl';

export class GraphView extends React.Component {

  constructor(props) {
    super(props);

    props.config.staticGraph = true;

    this.state = {
      data: props.data,
      config: props.config,
      selectedNode: ""
    };

    this.toggleStatic = this.toggleStatic.bind(this);
    this.handleSelectNode = this.handleSelectNode.bind(this);
    this.onClickNode = this.onClickNode.bind(this);
  }

  handleResize(that) {
    const {config} = this.state;

    config.width = window.innerWidth-30;
    config.height = window.innerHeight-222;

    this.setState({
      config: config
    })
  }

  componentWillReceiveProps(nextProps) {
    this.setState({
      data: nextProps.data,
    });
  }

  componentDidMount() {
    this.handleResize(this);
    window.addEventListener("resize", this.handleResize.bind(this));
  }

  componentWillUnmount() {
    window.removeEventListener("resize", this.handleResize.bind(this));
  }

  toggleStatic() {
    const { config, data } = this.state;

    config.staticGraph = !config.staticGraph;

    data.mode = "static";
    data.nodes[0].label = "hello2"

    this.setState({
      config: config,
      data: data
    });
  }

  handleSelectNode(event) {

    const that = this;
    const { data } = this.props;
    const { config } = this.state;

    if (event.target.value != null)
    {
      config.staticGraph = false;

      var subData = {nodes: [], links: []}

      subData.nodes.push(that.getNode(data.nodes, event.target.value));

      // Find Referenced Node's Edges
      data.links.forEach(function (link) {
        if (link.source === event.target.value) {
          subData.nodes.push(that.getNode(data.nodes, link.target));
          subData.links.push(link);
        }
        if (link.target === event.target.value) {
          subData.nodes.push(that.getNode(data.nodes, link.source));
          subData.links.push(link);
        }
      })

      this.setState({
        data: subData,
        config: config,
        selectedNode: event.target.value
      });
    } else {
      this.setState({
        data: data,
        selectedNode: ""
      });
    }
  }

  getNode(nodes, id) {
    for (var ii=0; ii<nodes.length; ii++) {
      if (nodes[ii].id === id)
      {
        return nodes[ii];
      }
    }

    return null;
  }

  // Graph event callbacks
  onClickNode = function(nodeId) {

    const {data} = this.state;
    const that = this;

    data.nodes.forEach(function (node) {
      if (node.id === nodeId) {
        that.setState({selected: node});
      }
    })

  };

  onMouseOverNode = function(nodeId) {
      // window.alert('Mouse over node', nodeId);
  };

  onMouseOutNode = function(nodeId) {
      // window.alert('Mouse out node', nodeId);
  };

  onClickLink = function(source, target) {
       //window.alert(`Clicked link between ${source} and ${target}`);
  };

  render() {

    const {data, selected, config} = this.state;

    var details = [];

    if (selected != null) {
      Object.keys(selected).forEach((e) => {
          if (e !== 'x' && e !== 'y' && e !== 'symbolType' && e !== 'strokeColor' && e !== 'strokeWidth' && e !== 'size' && e !== 'color') {
            details.push({key: e, value: selected[e]});
          }
        }
      );
    }
/*
<Checkbox
  label="Static"
  checked={config.staticGraph}
  onCheck={this.toggleStatic}
/>
*/

    return (
        <Paper style={{padding: '5px', margin: '10px'}}>
          <table>
            <tbody>
            <tr>
              <td width="100%">
                <FormControl style={{width: "100%"}}>
                  <InputLabel htmlFor="node-selection-helper">Node Selection</InputLabel>
                  <Select
                      input={<Input name="nodeSelection" id="node-selection-helper" />}
                      style={{width: "100%"}}
                      value={this.state.selectedNode}
                      onChange={this.handleSelectNode}
                    >
                      {
                        this.props.data.nodes.map((item) => {
                          return (<MenuItem value={item.id} key={item.id}>{item.label != null ? item.label : item.id}</MenuItem>);
                        })
                      }
                  </Select>
                </FormControl>
              </td>
              <td width="30px">
                <IconButton tooltip="SVG Icon" onClick={this.handleSelectNode}>
                  <NavigationClose />
                </IconButton>
              </td>
            </tr>
            </tbody>
            </table>
            <Graph
               id='graph-id' // id is mandatory, if no id is defined rd3g will throw an error
               data={data}
               config={config}
               onClickNode={this.onClickNode}
               onClickLink={this.onClickLink}
               onMouseOverNode={this.onMouseOverNode}
               onMouseOutNode={this.onMouseOutNode} />
             <DialogMessage
               title="Details"
               opened={selected != null}
               message={
                 (
                   <ReactTable
                      data={details}
                      columns={[
                          {
                            Header: "Key",
                            id: "key",
                            accessor: "key"
                          },{
                            Header: "Value",
                            id: "value",
                            accessor: "value"
                          }
                        ]
                      }
                      defaultSorted={[
                        {
                          id: "key",
                          desc: false
                        }
                      ]}
                      showPageSizeOptions={false}
                      defaultPageSize={10}
                      className="-striped -highlight"
                    />
                  )
                }
               onClose={() => {this.setState({selected: null})}}
             />
         </Paper>
    )
  }

}

GraphView.defaultProps = {
  config: {
      staticGraph: true,
      highlightBehavior: true,
      automaticRearrangeAfterDropNode: true,
      node: {
          color: '#3d3d3c',
          size: 1000,
          highlightStrokeColor: '#FF9900',
          highlightOpacity: 0.8,
          highlightFontSize: 20,
          highlightFontWeight: 'bold',
          labelProperty: 'label',
          fontSize: 12,

      },
      link: {
          highlightColor: '#FF9900',
          color: '#000000'
      }
  }
}
