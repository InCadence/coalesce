import React, { Component } from 'react';

 class TemplateObject {
    constructor(parent, name) {

        this.parent = parent;

        this.name = name;

        this.children = [];
    }

    get parent() {
        return this.parent;
    }



    get name() {
        return this.name;
    }



    get children() {
        return this.children;
    }

}

export default TemplateObject;