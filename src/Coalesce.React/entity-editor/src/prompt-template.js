import React from 'react';
import Popup from 'react-popup';

var karafRootAddr = 'http://' + window.location.hostname + ':8181';

/** The prompt content component */
export default class PromptTemplate extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            value: this.props.defaultValue
        };

        this.onChange = (e) => this._onChange(e);
    }

    componentDidMount() {
      if (this.state.data == null) {
          fetch(karafRootAddr + '/cxf/data/templates')
            .then(res => res.json())
            .then(data => {
              this.setState({data: data})
              this.setState({value: data[0].key});
            })
      }
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
        const {data} = this.state;

        var options = []

        if (data != null) {
          data.forEach(function(option) {
            options.push(<option value={option.key}>{option.name}</option>);
          });
        }

        return (
            <select id="templates" className="form-control" value={this.state.value} onChange={this.onChange}>
              {options}
            </select>
        )
    }
}

/** Prompt plugin */
Popup.registerPlugin('promptTemplate', function (buttontext, defaultValue, callback) {
    let promptValue = null;
    let promptChange = function (value) {
        promptValue = value;
    };

    this.create({
        title: "Select Template",
        content: <PromptTemplate onChange={promptChange} value={defaultValue} />,
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
