

export const TemplateObjectTypes = {
    TEMPLATE: 1,
    SECTION: 2,
    RECORDSET: 3,
    FIELDDEF: 4,
};

export class TemplateObject {
    constructor(id,parent, name) {

        this.id = id;

        this.parent = parent;

        this.name = name;

        this.children = [];
    }
}

export class Template extends TemplateObject {

    constructor(id, parent, name) {

        super(id, parent, name);

        this.type = TemplateObjectTypes.TEMPLATE;

    }

}

export class TemplateSection extends TemplateObject {
    constructor(id, parent, name) {

        super(id, parent, name);

        this.type = TemplateObjectTypes.SECTION;

    }
}

export class TemplateRecordSet extends TemplateObject {
    constructor(id, parent, name) {

        super(id, parent, name);

        this.type = TemplateObjectTypes.RECORDSET;

    }
}

export class TemplateFieldDef extends TemplateObject {

    constructor(id, parent, name) {

        super(id, parent, name);

        this.type = TemplateObjectTypes.FIELDDEF;

    }
}