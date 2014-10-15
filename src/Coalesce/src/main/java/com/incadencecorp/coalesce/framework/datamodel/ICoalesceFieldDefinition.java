package com.incadencecorp.coalesce.framework.datamodel;

import com.incadencecorp.coalesce.common.classification.Marking;

/**
 * Interface for accessing field definitions of Coalesce Entities.
 */
public interface ICoalesceFieldDefinition {

    // Methods
    /**
     * Return the value of the FieldDefinitions's Name attribute.
     * 
     * @return String of the FieldDefinition's name attribute.
     */
    String getName();

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
    Marking getDefaultClassificationMarking();

    /**
     * Return the value of the FieldDefinition's DisableHistory attribute.
     * 
     * @return Boolean of the FieldDefinition's disable history attribute.
     */
    boolean isDisableHistory();

    /**
     * 
     * @param value String to be the FieldDefinition's name attribute.
     */
    void setName(String value);

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
     * Sets the FieldDefinitions's DefaultClassificationMarking attribute based on the Marking class value parameter.
     * 
     * @param value Marking class to be the FieldDefinition's default classification marking attribute.
     */
    void setDefaultClassificationMarking(Marking value);

    /**
     * Sets the value of the FieldDefinitions's DisableHistory attribute.
     * 
     * @param disable Boolean to be the FieldDefinition's disable history attribute.
     */
    void setDisableHistory(boolean disable);

}
