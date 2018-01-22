import React, { Component } from 'react';
import { Panel, ButtonGroup, DropdownButton, MenuItem } from 'react-bootstrap';

class TemplateMenu extends Component {
    constructor(props) {
        super(props);
    }

    render() {
        return (
          
                <ButtonGroup >
                    <DropdownButton title="File" bsStyle="default" noCaret id="dropdown-no-caret">
                        <MenuItem eventKey="1">Save Templates</MenuItem>
                        <MenuItem eventKey="2">Load Templates</MenuItem>
                        <MenuItem eventKey="3">Export Template</MenuItem>
                    </DropdownButton>
                    <DropdownButton title="Edit" bsStyle="default" noCaret id="dropdown-no-caret">
                        <MenuItem eventKey="4">Undo</MenuItem>
                        <MenuItem eventKey="5">Redo</MenuItem>
                        <MenuItem eventKey="6">Copy Template</MenuItem>
                    </DropdownButton>
                    <DropdownButton title="View" bsStyle="default" noCaret id="dropdown-no-caret">
                        <MenuItem eventKey="4">Outline</MenuItem>
                        <MenuItem eventKey="5">Editor</MenuItem>
                        <MenuItem eventKey="6">Properties</MenuItem>
                        <MenuItem eventKey="6">Record Pallet</MenuItem>
                        <MenuItem eventKey="6">Log</MenuItem>
                    </DropdownButton>
                    <DropdownButton title="Help" bsStyle="default" noCaret id="dropdown-no-caret">
                        <MenuItem eventKey="4">Documentation</MenuItem>
                    </DropdownButton>
                </ButtonGroup>
           
        );
    }
}

export default TemplateMenu;