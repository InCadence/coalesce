import React, { Component } from 'react';
import {Modal, FormControl, Button} from 'react-bootstrap';

class NewModal extends Component {

    constructor(props) {
        super(props);
        this.open = this.open.bind(this);
        this.handlePropsChange = this.handlePropsChange.bind(this);
        this.handleNewToggle = this.handleNewToggle.bind(this);
    }

    handlePropsChange(e) {
        this.props.onPropsChange(e.target.value);
    };

    handleNewToggle(){
        this.props.onNewToggle();
    }

    render() {

        return (
            <Modal show={this.props.showModal} onHide={this.handleNewToggle}>
                <Modal.Header closeButton >
                    <Modal.Title>Edit {this.props.type} Properties</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <FormControl type="text" value={this.props.name} onChange={this.handlePropsChange}/>
                </Modal.Body>
                <Modal.Footer>
                    <Button onClick={this.handleNewToggle}>Close</Button>
                </Modal.Footer>
            </Modal>
        );
    }
}

export default NewModal;