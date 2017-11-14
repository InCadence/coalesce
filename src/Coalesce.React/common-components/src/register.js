import React from 'react';
import { HashLoader } from 'react-spinners';
import {Prompt} from './prompt.js';
import {PromptTemplate} from './prompt-template.js';

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
