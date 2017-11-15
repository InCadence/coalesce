import React from 'react';

/** The prompt content component */
export class PromptTemplate extends React.Component {
    constructor(props) {
        super(props);

        var defaultValue;

        if (this.props.defaultValue != null) {
          defaultValue = this.props.defaultValue;
        } else if (this.props.data != null) {
          defaultValue = this.props.data[0].key;
        }

        this.state = {
            data: this.props.data,
            value: defaultValue
        };

        this.props.onChange(this.state.value);
    }

    componentDidMount() {
      if (this.state.data == null) {
          fetch(this.props.url + '/cxf/data/templates')
            .then(res => res.json())
            .then(data => {
              this.setState({
                data: data,
                value: data[0].key
              });
            })
      }
    }

    componentDidUpdate(prevProps, prevState) {
        if (prevState.value !== this.state.value) {
            this.props.onChange(this.state.value);
        }
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
            <select className="form-control" value={this.state.value} onChange={(e) => this.setState({ value: e.target.value })}>
              {options}
            </select>
        )
    }
}
