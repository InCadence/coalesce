import * as React from "react";

import { Graph } from 'react-d3-graph';
import ReactTable from 'react-table'

import { DialogMessage } from 'coalesce-components/lib/components/dialogs'
import { getRootKarafUrl } from 'coalesce-components/lib/js/common'


import Paper from '@material-ui/core/Paper';
import Select from '@material-ui/core/Select';
import MenuItem from '@material-ui/core/MenuItem';
import IconButton from '@material-ui/core/IconButton';
import NavigationClose from '@material-ui/icons/Close';
import Input from '@material-ui/core/Input';
import TextField from '@material-ui/core/Input';
import FormHelperText from '@material-ui/core/FormHelperText';
import InputLabel from '@material-ui/core/InputLabel';
import FormControl from '@material-ui/core/FormControl';

import { validate } from './fast-xml-parser/validator'

export class GraphView extends React.Component {

  constructor(props) {
    super(props);

    props.config.staticGraph = true;
    this.state = {
      actions: this.props.actions || 'base',
      data: props.data,
      config: props.config,
      selectedNode: "",
      isValid: this.isValidGraph(props.data),
    };

    this.rootKarafUrl = getRootKarafUrl('core')
    this.addNodeURL = this.rootKarafUrl + '/blueprints/edit/' + this.props.title
    this.getNodeURL = this.rootKarafUrl + '/blueprints/get/' + this.props.title + '/'
    this.removeNodeURL = this.rootKarafUrl + '/blueprints/remove/' + this.props.title
    this.revertURL = this.rootKarafUrl + '/blueprints/undo/' + this.props.title

    this.guidRegex = /[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}/

    this.toggleStatic = this.toggleStatic.bind(this);
    this.handleSelectNode = this.handleSelectNode.bind(this);
    this.onClickNode = this.onClickNode.bind(this);
    this.onEditToggle = this.onEditToggle.bind(this)
    this.onEditCancel = this.onEditCancel.bind(this)
    this.onEditJson = this.onEditJson.bind(this)
    this.onAddToggle = this.onAddToggle.bind(this)
    this.onAddChange = this.onAddChange.bind(this)
    this.onAddCancel = this.onAddCancel.bind(this)
    this.onClose = this.onClose.bind(this)
    this.getNonGuid = this.getNonGuid.bind(this)
    this.attemptRevert = this.attemptRevert.bind(this)
    this.attemptRemoveNode = this.attemptRemoveNode.bind(this)
  }

  postNodeXml(nodeJson) {
    fetch(this.addNodeURL, {
      method: 'POST',
      headers: {
        Accept: 'application/json',
        'Content-Type': 'application/json',
      },
      body: nodeJson,
    })
    .then(() => this.props.reloadBlueprint())
    .catch((error) => {
      console.log(error);
    });
  }

  getNodeXml(id) {
    //fetches the xml
    return fetch(this.getNodeURL+id, {
      method: 'GET',

      headers: {
        Accept: 'application/json',
        'Content-Type': 'application/json',
      },
    })
    .then((response) => response.json())
    .then((responseJson) => {this.setState({originalXml:responseJson.xml[0], value: responseJson.xml[0]})} )
    .catch((error) => console.log(error));

  }

  removeOrphanNode(id) {
    //removes xml based on id
    return fetch(this.removeNodeURL, {
      method: 'POST',
      headers: {
        Accept: 'application/json',
        'Content-Type': 'application/json',
      },
      body: this.createXmlJson('', id)
    })
    .then(() => this.props.reloadBlueprint());
  }

  attemptRevert() {
    return fetch(this.revertURL).then(() => this.props.reloadBlueprint());
  }

  attemptRemoveNode(id) {
    if(this.isOrphan(id)) {
      //remove node nonGuid
      this.removeOrphanNode(id)
    }
    else {
      var error = `This node has links to other nodes, remove the node from ${this.props.title} and fix any appropriate references!`
      this.createError(error)
    }
  }

  getParent(id) {
    var links = this.state.data.links
    var parentId = null
    for(let i = 0; i < links.length; i++) {
      var link = links[i]
      if(link.target === id) {
        //if this id is being the target, return the parent's nonGuid
        parentId = link.source
        break;
      }
    }
    //return null if no parent exists, return parent id otherwise
    return parentId
  }

  getNonGuid(id) {
    const that = this
    if(id.search(this.guidRegex) ) { //search returns -1 if a match isnt found, if found returns the index
      return id
    }
    else {
      var parentId = this.getParent(id)
      if(parentId == null){
        return null
      }
      return that.getNonGuid(parentId);
    }
  }

  isOrphan(id) {
    //returns true if the node with given id is an orphan
    var isOrphan = this.getParent(id) === null
    return isOrphan
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
      actions: nextProps.actions || 'base',
      editable: true,
    });

    if(nextProps.actions === 'reverting') {
      this.attemptRevert()
    }
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
    //for the dropdown selector
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
    var nodeSelected = null
    data.nodes.forEach(function (node) {
      if (node.id === nodeId) {
        nodeSelected = node
      }
    })
    var editable = true
    var nonGuid = this.getNonGuid(nodeId)
    if(nonGuid) {
      that.getNodeXml(nonGuid) //gets and sets the value to xml
    }
    else {
      editable = false;
    }

    that.setState({
      selected: nodeSelected,
      editable: editable
    });

    return null
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

  onClose() {
    const {actions, selected, originalXml, value} = this.state
    var xmlString = ''
    var xmlWithoutNewLines = ''
    var jsonString = ''
    var closeDialog = false
    var nonGuid = ''

    if (value != null && actions === 'editing') {

      if (value !== originalXml) { //if the value changed
        xmlString = this.state.value
        xmlWithoutNewLines = xmlString
        //xmlWithoutNewLines = xmlString.replace(/(\r\n|\n|\r|\t)/gm,"");
        nonGuid = this.getNonGuid(selected.id)

        if(validate(xmlString) === true) {
          jsonString = this.createXmlJson(xmlWithoutNewLines, nonGuid)
          this.postNodeXml(jsonString)
          closeDialog = true
        }
        else if(xmlString.trim() === "") {
          this.attemptRemoveNode(nonGuid)
        }
        else {
          this.createError('Invalid XML');
          closeDialog = false;
        }
      }
      else { //if the value didn't change
        closeDialog = true;
      }
    }
    else if (this.state.actions === 'adding') {
      xmlString = this.state.value
      xmlWithoutNewLines = xmlString;
      //xmlWithoutNewLines = xmlString.replace(/(\r\n|\n|\r|\t)/gm,"");
      if(value == null || xmlString.trim() === "") {
        closeDialog = true
      }
      else if(validate(xmlString) === true) { //returns true if valid
        jsonString = this.createXmlJson(xmlWithoutNewLines, '')
        this.setState({actions: 'base'})
        this.postNodeXml(jsonString)
        closeDialog = true
      }
      else {
        this.createError('Invalid XML');
        closeDialog = false
      }
    }

    if(closeDialog || actions === 'base') {
      this.setState({
        data: this.state.data,
        value: null,
        filtered: null,
        selected: null,
        actions: 'base',
        errorMsg: null,
        editable: false,
      })
    }
  }

  onEditToggle(selected) {
    this.setState({
      actions: 'editing',
    })
  }

  onEditJson(event) {
    var newValue = event.target.value
    var state = {value: newValue}
    if(this.state.errorMsg){
      state.errorMsg = null
    }
    this.setState(state)
  }

  onEditCancel() {
    var state = {
      value: this.state.originalXml,
      actions: 'base',
    }

    if(this.state.errorMsg){
      state.errorMsg = null
    }

    this.setState(state)
  }

  onAddToggle() {

    this.setState({
      actions: 'adding',
      value: null,
      errorMsg: null,
    })
  }

  onAddChange(event) {
    var newValue = event.target.value
    var state = {value: newValue}
    if(this.state.errorMsg){
      state.errorMsg = null
    }
    this.setState(state)
  }

  onAddCancel() {
    this.onEditCancel();
  }

  createError(error) {
    this.setState({errorMsg: error})
  }

  createXmlJson(xml, id) {

    var jsonObject= {}
    jsonObject.xml = [xml];
    jsonObject.oldId = id
    var jsonString = JSON.stringify(jsonObject)
    return jsonString
  }

  isValidGraph(data) {
    var isValid = true
    for(var i = 0; i < data.links.length; i++) {
      var link = data.links[i]
      if (!data.nodes.find(function (n) {
        return n.id === link.source;
      })) {
        isValid = false
      }
      if (!data.nodes.find(function (n) {
        return n.id === link.target;
      })) {
        isValid = false;
      }
    };
    return isValid
  }

  render() {

    const {data, selected, config, actions} = this.state;
    var editable = this.state.editable

    if(editable == null) {
      editable = true
    }
    var base = null
    var editing = null
    var adding = null

    var details = [];
    var filtered = {}

    if(this.props.title === 'core-blueprint.xml') {
      editable = false;
    }
    if (selected != null) {

      if (selected.label === 'BlueprintControllerJaxRS') {
        editable = false
      }

      Object.keys(selected).forEach((e) => {
        if (e !== 'x' && e !== 'y' && e !== 'symbolType' && e !== 'strokeColor' && e !== 'strokeWidth' && e !== 'size' && e !== 'color') {
          details.push({key: e, value: selected[e]});
        }
        else {
          filtered[e] = selected[e]
        }
      });
    }


    base = actions === 'base'
    editing = actions === 'editing'
    adding = actions === 'adding'

    var onOther = undefined; 
    var onOtherText = undefined;
    var onSecondary = console.log
    var onSecondaryText = undefined
    var onPrimary = console.log
    var onPrimaryText = undefined
    var confirmation = false; 

    if (!this.state.isValid) {
      onSecondary = (() => this.setState({isValid: true}))
      onPrimary = this.attemptRevert

      onPrimaryText = "REVERT"

      confirmation = true;
    }
    else if (base) {
      if (editable) {
      onOther =  (() => this.onEditToggle(selected))
      }
      onSecondary = this.onClose
      onPrimary = this.onClose

      onOtherText = "Edit"
      onPrimaryText = "Close"
    }
    else if (editing) {
      onOther = (() => this.attemptRemoveNode(this.getNonGuid(selected.id)))
      onSecondary = this.onEditCancel
      onPrimary = this.onClose

      onSecondaryText = "Cancel"
      onOtherText = "Delete Node"
      onPrimaryText = "Save"

      confirmation = true;
    }
    else if (adding) {
      onSecondary = this.onAddCancel
      onPrimary = this.onClose

      onPrimaryText = "Add"
      confirmation = true;
    }
/*
<Checkbox
  label="Static"
  checked={config.staticGraph}
  onCheck={this.toggleStatic}
/>
*/
    if(this.state.isValid) {
      return (
        <Paper style={{padding: '5px', margin: '10px'}}>
          <table>
            <tbody>
              <tr width="100%">
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
            opened={selected != null || adding || editing}
            //actions={this.state.actions}
            //editable={editable}
            maxWidth="xl"
            message={
              //if the user is not editing or its not editable
              //checking against boolean values because !editing is true (same as !null if not defined yet)
              (base && selected != null &&
                <ReactTable
                  width="100%"
                  resizable={true}
                  data={details}
                  columns={[
                    {
                      minWidth: 300,
                      Header: "Key",
                      id: "key",
                      accessor: "key"
                    },{
                      minWidth: 300,
                      Header: "Value",
                      id: "value",
                      accessor: "value"
                    }
                  ]}
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

              ||

            //if the user is editing or its editable
              (editing && editable &&
                <div>
                <TextField
                  label="Insert XML"
                  id="nodeedittextfield"
                  multiline={true}
                  fullWidth={true}
                  rows="20"
                  onChange={this.onEditJson}
                  value={this.state.value || ''}
                />
                <FormHelperText error={true} id="name-error-text">{this.state.errorMsg}</FormHelperText>
                </div>
              )

              ||

              (adding &&
                <div>
                <TextField
                  label="Insert XML"
                  id="nodeaddtextfield"
                  multiline={true}
                  fullWidth={true}
                  rows="20"
                  onChange={this.onAddChange}
                  value={this.state.value || ''}
                />
                <FormHelperText error={true} id="name-error-text">{this.state.errorMsg}</FormHelperText>
                </div>
              )
            }

            confirmation={confirmation}
            onOther={onOther}
            onOtherText={onOtherText}
            onClose={onSecondary}
            onCloseText={onSecondaryText}
            onClick={onPrimary}
            onClickText={onPrimaryText}
            //onClose={this.onClose}
          />
        </Paper>
      )

    } else {
      return (
        <Paper style={{padding: '5px', margin: '10px'}}>
          <DialogMessage
            title="Details"
            opened={!this.state.isValid}
            confirmation={confirmation}
            onClose={onSecondary}
            onCloseText={onSecondaryText}
            onClick={onPrimary}
            onClickText={onPrimaryText}
            >
              The file {this.props.title} is broken and cannot be loaded.
                You can try and use the revert button, but if it is not able
                to revert, someone must manually fix the file on the system.
            </DialogMessage>
        </Paper>
      )
    }
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
