import React from 'react';
import Popup from 'react-popup';

/** The prompt content component */
export default class Prompt extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            value: this.props.defaultValue
        };

        this.onChange = (e) => this._onChange(e);
    }

    componentDidUpdate(prevProps, prevState) {
        if (prevState.value !== this.state.value) {
            this.props.onChange(this.state.value);
        }
    }

    _onChange(e) {
        let value = e.target.value;

        this.setState({value: value});
    }

    render() {
        return <input type="text" placeholder={this.props.placeholder} className="mm-popup__input" value={this.state.value} onChange={this.onChange} />;
    }
}

/** Prompt plugin */
Popup.registerPlugin('prompt', function (buttontext, title, defaultValue, placeholder, callback) {
    let promptValue = null;
    let promptChange = function (value) {
        promptValue = value;
    };

    this.create({
        title: title,
        content: <Prompt onChange={promptChange} placeholder={placeholder} value={defaultValue} />,
        buttons: {
            left: ['cancel'],
            right: [{
                text: buttontext,
                className: 'success',
                action: function () {
                    callback(promptValue);
                    Popup.close();
                }
            }]
        }
    });
});
