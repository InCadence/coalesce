import React from 'react';

/** The prompt content component */
export class PromptDropdown extends React.Component {
    constructor(props) {
        super(props);

        this.props.onChange(props.data[0].key);

        this.state = {
            data: props.data,
            value: props.data[0].key
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

        const {data} = this.state;

        var options = []

        if (data != null) {
          data.forEach(function(option) {
            options.push(<option key={option.key} value={option.key}>{option.name}</option>);
          });
        }

        return (
            <select className="form-control" value={this.state.value} onChange={this.onChange}>
              {options}
            </select>
        )
    }
}

export class MultiSelect extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            data: props.data,
            value: props.data[0].key
        };

    }

    componentDidUpdate(prevProps, prevState) {
        if (prevState.value !== this.state.value) {
            this.props.onChange(this.state.value);
        }
    }

    onChange(e) {
        let value = e.target.value;

        this.setState({value: value});
    }

    render() {

        const {data} = this.state;

        var options = []

        if (data != null) {
          data.forEach(function(option) {
            options.push(<option key={option.key} value={option.key}>{option.name}</option>);
          });
        }

        return (
            <select className="form-control" onSelect={this.onChange.bind(this)} multiple>
              {options}
            </select>
        )
    }
}
