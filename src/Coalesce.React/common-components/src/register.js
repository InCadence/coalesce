import React from 'react';
import { HashLoader } from 'react-spinners';
import {Prompt} from './prompt.js';
import {PromptTemplate} from './prompt-template.js';
import {PromptDropdown} from './prompt-dropdown.js';

export function registerLoader(popup) {

  popup.registerPlugin('loader', function (msg) {
      this.create({
          content:
            <center className='sweet-loading'>
              <HashLoader
                color={'#FF9900'}
                loading={true}
              />
              <label>{msg}</label>
            </center>,
            closeOnOutsideClick: false
      });
  });

}

export function registerPromptDropdown(popup) {
  popup.registerPlugin('promptDropdown', function (buttontext, title, defaultValue, data, callback) {
  /** Prompt plugin */
    let promptValue = null;
    let promptChange = function (value) {``
        promptValue = value;
    };

    this.create({
        title: title,
        content: <PromptDropdown onChange={promptChange} value={defaultValue} data={data}/>,
        buttons: {
            left: ['cancel'],
            right: [{
                text: buttontext,
                className: 'success',
                action: function () {
                    callback(promptValue);
                    popup.close();
                }

            }]
        }
    });
  });
}

export function registerPrompt(popup) {
  /** Prompt plugin */
  popup.registerPlugin('prompt', function (buttontext, title, defaultValue, placeholder, callback) {
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
                      popup.close();
                  }
              }]
          }
      });
  });
}

export function registerTemplatePrompt(popup, url, data) {
  popup.registerPlugin('promptTemplate', function (buttontext, defaultValue, callback) {
  /** Prompt plugin */
    let promptValue = null;
    let promptChange = function (value) {``
        promptValue = value;
    };

    this.create({
        title: "Select Template",
        content: <PromptTemplate onChange={promptChange} value={defaultValue} url={url} data={data}/>,
        buttons: {
            left: ['cancel'],
            right: [{
                text: buttontext,
                className: 'success',
                action: function () {
                    callback(promptValue);
                    popup.close();
                }

            }]
        }
    });
  });
}

export function registerErrorPrompt(popup) {
  popup.registerPlugin('promptError', function (error) {
    popup.close();
    popup.create({
        title: 'Error',
        content: error,
        className: 'alert',
        buttons: {
            right: ['ok']
        }
    }, true);
  });
}
