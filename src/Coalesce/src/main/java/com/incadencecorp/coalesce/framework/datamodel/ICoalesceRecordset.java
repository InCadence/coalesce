package com.incadencecorp.coalesce.framework.datamodel;

public interface ICoalesceRecordset {

    // Methods

    /**
     * Return the value of the RecordSet's Name attribute
     * 
     * @return String of the RecordSet's name attribute
     */
    public String getName();

    /**
     * Return the value corresponding to the minimum number of records the RecordSet must contain
     * 
     * @return integer of the RecordSet's min records attribute
     */
    public int getMinRecords();

    /**
     * Return the value corresponding to the maximum number of records the RecordSet must contain
     * 
     * @return integer of the RecordSet's max records attribute
     */
    public int getMaxRecords();

    /**
     * Sets the value of the RecordSet's Name attribute
     * 
     * @param value String to be the RecordSet's name attribute
     */
    public void setName(String value);

    /**
     * Sets the value corresponding to the minimum number of records the RecordSet must contain
     * 
     * @param value integer to be the RecordSet's min records attribute
     */
    public void setMinRecords(int value);

    /**
     * Sets the value corresponding to the maximum number of records the RecordSet must contain
     * 
     * @param value integer to be the RecordSet's max records attribute
     */
    public void setMaxRecords(int value);
}
