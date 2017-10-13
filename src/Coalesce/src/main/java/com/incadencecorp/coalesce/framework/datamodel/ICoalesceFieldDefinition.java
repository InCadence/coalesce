package com.incadencecorp.coalesce.framework.datamodel;

/**
 * Interface for accessing field definitions of Coalesce Entities.
 */
public interface ICoalesceFieldDefinition extends ICoalesceObject {

    /**
     * Return the value of the FieldDefinition's Label attribute.
     * 
     * @return String of the FieldDefinition's label attribute.
     */
    String getLabel();

    /**
     * Return the value of the FieldDefinition's DataType attribute.
     * 
     * @return ECoalesceFieldDataTypes of the FieldDefinition's value attribute.
     */
    ECoalesceFieldDataTypes getDataType();

    /**
     * Return the value of the FieldDefinitions's DefaultValue attribute.
     * 
     * @return String of the FieldDefinition's value attribute.
     */
    String getDefaultValue();

    /**
     * Return a Marking class value of the FieldDefinitions's ClassificationMarking attribute.
     * 
     * @return Marking class of the FieldDefinition's default classification marking attribute.
     */
    String getDefaultClassificationMarkingAsString();

    /**
     * Return the value of the FieldDefinition's DisableHistory attribute.
     * 
     * @return Boolean of the FieldDefinition's disable history attribute.
     */
    boolean isDisableHistory();

    /**
     * Sets the value of the FieldDefinitions's Label attribute.
     * 
     * @param value String to be the FieldDefinition's label attribute.
     */
    void setLabel(String value);

    /**
     * Sets the value of the FieldDefinitions's DataType attribute.
     * 
     * @param value ECoalesceFieldDataTypes to be the FieldDefinition's datatype attribute.
     */
    void setDataType(ECoalesceFieldDataTypes value);

    /**
     * Sets the value of the FieldDefinitions's DefaultValue attribute.
     * 
     * @param value String to be the FieldDefinition's value attribute.
     */
    void setDefaultValue(String value);

    /**
     * Sets the Fielddefinition's DefaultClassificationMarking.
     * 
     * @param value String, the default classification marking for the field
     *            type.
     */
    void setDefaultClassificationMarkingAsString(String value);

    /**
     * Sets the value of the FieldDefinitions's DisableHistory attribute.
     * 
     * @param disable Boolean to be the FieldDefinition's disable history attribute.
     */
    void setDisableHistory(boolean disable);

}
