import React from 'react';
import { EntityView } from './entity.js'
import { DialogMessage, DialogOptions, DialogPrompt, DialogLoader } from 'common-components/lib/components/dialogs'
import { Menu } from 'common-components/lib/components'

import { loadTemplate, loadTemplates, createNewEntity, loadTemplateByEntity } from 'common-components/lib/js/templateController'
import { saveEntity, loadEntity } from 'common-components/lib/js/entityController'

import Paper from '@material-ui/core/Paper';

export class App extends React.Component {

  constructor(props) {
    super(props);

    if (this.props.entitykey != null) {
      this.renderEntity(this.props.entitykey);
    } else if (this.props.templatekey != null) {
      this.renderNewEntity(this.props.templatekey)
    }

    this.state = {
      entity: null,
      template: null,
      isNew: false,
      prompt: false,
      promptTemplate: false,
      error: null
    }

    this.renderEntity = this.renderEntity.bind(this);
    this.renderNewEntity = this.renderNewEntity.bind(this);
  }

  componentDidMount() {
    var that = this;
    loadTemplates().then((value) => {
      that.setState({
        templates: value
      })
    }).catch((err) => {
      that.setState({
        error: "Loading Templates: " + err
      });
    })

  }

  handleSaveEntity() {
    const that = this;

    if (this.state.entity != null) {

      this.setState({loading: "Saving..."});

      saveEntity(this.state.entity, this.state.isNew).then((value) => {
        that.setState({
          isNew: false,
          loading: null
        })

      }).catch((err) => {
        that.setState({
          error: "Saving: " + err,
          loading: null
        });
      });
    }

  }

  renderEntity(key) {
    const that = this;

    this.setState({
      prompt: false,
      loading: "Loading Entity..."
    });

    loadEntity(key).then((entity) => {

        loadTemplateByEntity(entity).then((template) => {

          that.setState({
            entity: entity,
            template: template,
            isNew: false,
            loading: null
          })

        }).catch((err) => {
          that.setState({
            error: `Failed to load template (${entity.name},${entity.source},${entity.version})`,
            loading: null
          });
        })

      }).catch(function(error) {
        that.setState({
          error: `Failed to load entity (${key})`,
          loading: null
        });
      });
  }

  renderNewEntity(key) {

    var that = this;

    this.setState({
      loading: "Creating Entity..."
    });

    loadTemplate(key).then((template) => {
      createNewEntity(key).then((entity) => {

        that.setState({
          entity: entity,
          template: template,
          isNew: true,
          loading: null
        })

      }).catch((err) => {
        that.setState({
          error: 'Failed to create new entity (' + key + ')',
          loading: null
        });
      })
    }).catch((err) => {
      that.setState({
        error: 'Failed to load template (' + key + ')',
        loading: null
      });
    });
  }

  render() {

    const { entity, template, isNew } = this.state;
    const that = this;

    const menuItems = [
      {
        id: 'new',
        name: 'New',
        img: '/images/svg/new.svg',
        title: 'Create New Entity',
        onClick: () => {
          this.setState({
            promptTemplate: true
          })
        }
      },{
        id: 'load',
        name: 'Load',
        img: '/images/svg/load.svg',
        title: 'Load Entity',
        onClick: () => {
          this.setState({prompt: true});
        }
      }
    ]

    if (entity) {
      menuItems.push({
          id: 'save',
          name: 'Save',
          img: '/images/svg/save.svg',
          title: 'Save Entity',
          onClick: () => {that.handleSaveEntity();}
      })
    }

    return (
      <div>
        <Menu logoSrc={this.props.icon} title={this.props.title} items={menuItems}/>
        <Paper style={{padding: '5px', margin: '10px'}}>
          <EntityView data={entity} template={template} isNew={isNew} />
          <DialogPrompt
            title="Enter Entity Key"
            value=''
            opened={this.state.prompt}
            onClose={() => {this.setState({prompt: false})}}
            onSubmit={this.renderEntity}
          />
          {this.state.error &&
            <DialogMessage
              title="Error"
              opened={true}
              message={this.state.error}
              onClose={() => {this.setState({error: null})}}
            />
          }
          {this.state.templates && this.state.promptTemplate &&
            <DialogOptions
              title="Select Template"
              open={true}
              onClose={() => {this.setState({promptTemplate: false})}}
              options={this.state.templates.map((item) => {
                return {
                  key: item.key,
                  name: item.name,
                  onClick: () => {
                    this.setState({promptTemplate: false});
                    this.renderNewEntity(item.key);
                  }
                }
              })}

            />
          }
          {this.state.loading &&
            <DialogLoader opened={true} title={this.state.loading} />
          }
        </Paper>
      </div>
    )
  }
};

export default App
