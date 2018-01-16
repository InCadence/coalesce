import React, { Component } from 'react';
import {Modal, FormControl, Button} from 'react-bootstrap';

class TestModal extends Component {

    constructor(props) {
        super(props);
  
        this.handlePropsChange = this.handlePropsChange.bind(this);
        this.handleToggle = this.handleToggle.bind(this);
        this.state = {
            text:"start",
        }
    }

    handlePropsChange(e) {
        this.setState({  
            text: e.target.value,
          });
    };

    handleToggle(){
        this.props.onToggle();
    }

    render() {

        return (
            <Modal show={this.props.showModal} onHide={this.handleToggle}>
                <Modal.Header closeButton >
                    <Modal.Title>Test Properties</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <FormControl type="text" value={this.state.text} onChange={this.handlePropsChange}/>
                </Modal.Body>
                <Modal.Footer>
                    <Button onClick={this.handleToggle}>Close</Button>
                </Modal.Footer>
            </Modal>
        );
    }
}

export default TestModal;