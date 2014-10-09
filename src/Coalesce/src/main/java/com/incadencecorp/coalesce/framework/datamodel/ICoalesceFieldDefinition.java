package com.incadencecorp.coalesce.framework.datamodel;

import com.incadencecorp.coalesce.common.classification.Marking;

public interface ICoalesceFieldDefinition {

    // Methods
    /**
     * Return the value of the FieldDefinitions's Name attribute
     * 
     * @return String of the FieldDefinition's name attribute
     */
    public String getName();

    /**
     * Return the value of the FieldDefinition's Label attribute
     * 
     * @return String of the FieldDefinition's label attribute
     */
    public String getLabel();

    /**
     * Return the value of the FieldDefinition's DataType attribute
     * 
     * @return ECoalesceFieldDataTypes of the FieldDefinition's value attribute
     */
    public ECoalesceFieldDataTypes getDataType();

    /**
     * Return the value of the FieldDefinitions's DefaultValue attribute
     * 
     * @return String of the FieldDefinition's value attribute
     */
    public String getDefaultValue();

    /**
     * Return a Marking class value of the FieldDefinitions's ClassificationMarking attribute
     * 
     * @return Marking class of the FieldDefinition's default classification marking attribute
     */
    public Marking getDefaultClassificationMarking();

    /**
     * Return the value of the FieldDefinition's DisableHistory attribute
     * 
     * @return Boolean of the FieldDefinition's disable history attribute
     */
    public boolean getDisableHistory();
    
    /**
     * Sets the value of the FieldDefinitions's Name attribute
     * 
     * @param value String to be the FieldDefinition's name attribute
     */
    public void setName(String value);

    /**
     * Sets the value of the FieldDefinitions's Label attribute
     * 
     * @param value String to be the FieldDefinition's label attribute
     */
    public void setLabel(String value);

    /**
     * Sets the value of the FieldDefinitions's DataType attribute
     * 
     * @param value ECoalesceFieldDataTypes to be the FieldDefinition's datatype attribute
     */
    public void setDataType(ECoalesceFieldDataTypes value);

    /**
     * Sets the value of the FieldDefinitions's DefaultValue attribute
     * 
     * @param value String to be the FieldDefinition's value attribute
     */
    public void setDefaultValue(String value);

    /**
     * Sets the FieldDefinitions's DefaultClassificationMarking attribute based on the Marking class value parameter
     * 
     * @param value Marking class to be the FieldDefinition's default classification marking attribute
     */
    public void setDefaultClassificationMarking(Marking value);
    
    /**
     * Sets the value of the FieldDefinitions's DisableHistory attribute
     *  
     * @param value Boolean to be the FieldDefinition's disable history attribute
     */
    public void setDisableHistory(boolean value);
    
}
