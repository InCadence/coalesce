package Coalesce.Framework.DataModel;

import Coalesce.Common.Classification.Marking;

public interface ICoalesceFieldDefinition {

    // Methods
    /**
     * Return the value of the FieldDefinitions's Name attribute
     * 
     * @return
     */
    public String getName();

    /**
     * Return the value of the FieldDefinitions's Label attribute
     * 
     * @return
     */
    public String getLabel();

    /**
     * Return the value of the FieldDefinitions's DataType attribute
     * 
     * @return
     */
    public ECoalesceFieldDataTypes getDataType();

    /**
     * Return the value of the FieldDefinitions's DefaultValue attribute
     * 
     * @return
     */
    public String getDefaultValue();

    /**
     * Return a Marking class value of the FieldDefinitions's ClassificationMarking attribute
     * 
     * @return
     */
    public Marking getDefaultClassificationMarking();

    /**
     * Sets the value of the FieldDefinitions's Name attribute
     * 
     * @param value
     */
    public void setName(String value);

    /**
     * Sets the value of the FieldDefinitions's Label attribute
     * 
     * @param value
     */
    public void setLabel(String value);

    /**
     * Sets the value of the FieldDefinitions's DataType attribute
     * 
     * @param value
     */
    public void setDataType(ECoalesceFieldDataTypes value);

    /**
     * Sets the value of the FieldDefinitions's DefaultValue attribute
     * 
     * @param value
     */
    public void setDefaultValue(String value);

    /**
     * Sets the FieldDefinitions's DefaultClassificationMarking attribute based on the Marking class value parameter
     * 
     * @param value
     */
    public void setDefaultClassificationMarking(Marking value);
}
