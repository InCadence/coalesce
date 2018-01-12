import React, { Component } from 'react';
import {Modal, FormControl, Button} from 'react-bootstrap';

class EditModal extends Component {

    constructor(props) {
        super(props);
        this.handlePropsChange = this.handlePropsChange.bind(this);
        this.handleEditToggle = this.handleEditToggle.bind(this);
    }

    handlePropsChange(e) {
        this.props.onPropsChange(e.target.value);
    };

    handleEditToggle(){
        this.props.onEditToggle();
    }

    render() {

        return (
            <Modal show={this.props.showModal} onHide={this.handleEditToggle}>
                <Modal.Header closeButton >
                    <Modal.Title>Edit {this.props.type} Properties</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <FormControl autoFocus type="text" value={this.props.name} onChange={this.handlePropsChange}/>
                    <textarea value="heloooooooooooooooo"/>
                </Modal.Body>
                <Modal.Footer>
                    <Button onClick={this.handleEditToggle}>Close</Button>
                </Modal.Footer>
            </Modal>
        );
    }
}

export default EditModal;