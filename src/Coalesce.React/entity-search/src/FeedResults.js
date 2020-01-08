import React from 'react';
import ExpansionPanel from '@material-ui/core/ExpansionPanel';
import ExpansionPanelSummary from '@material-ui/core/ExpansionPanelSummary';
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';
import Typography from '@material-ui/core/Typography';
import List from '@material-ui/core/List';
import Parser from 'html-react-parser';
import { ListItemExpandable } from 'coalesce-components/lib/components';

export class FeedResults extends React.Component {

  constructor(props) {
    super(props);
  }

  componentWillReceiveProps(nextProps) {

    if (nextProps.data.key !== this.props.data.key) {
      this.setState(() => {return {
          data: nextProps.data
        }
      })
    }
  }

  render() {

    return (
      <ExpansionPanel  defaultExpanded>
         <ExpansionPanelSummary style={{padding: '5px', height: '32px'}} expandIcon={<ExpandMoreIcon />}>
           <Typography variant="headline">
             Query Results
           </Typography>
         </ExpansionPanelSummary>
         <List>
           { this.props.data.hits.map(this.renderListItem, this.props.onClick) }
         </List>
     </ExpansionPanel>
     )

  }

  renderListItem = (hit) => {

      var content = hit.values[0];
      var title = hit.values[1];
      var secondary = "";

      if (hit.values.length > 2) {
        secondary = hit.values.slice(1).join(' | ');
      }

      if (title && title != null) {
        title = Parser(title);
      }

      if (content && content != null) {
        content = Parser(content);
      }

      return (
        <ListItemExpandable
          key={hit.entityKey}
          expanded
          primary={title}
          secondary={secondary}
          details={content}
          onClick={() => this.props.onClick(hit)}
        />
      )
  }
}

FeedResults.defaultProps = {
  data: [],
  properties: [],
  editMode: true
}
