package Coalesce.Framework.DataModel;

public interface ICoalesceRecordset {

    // Methods

    /**
     * Return the value of the RecordSet's Name attribute
     * 
     * @return
     */
    public String getName();

    /**
     * Return the value corresponding to the minimum number of records the RecordSet must contain
     * 
     * @return
     */
    public int getMinRecords();

    /**
     * Return the value corresponding to the maximum number of records the RecordSet must contain
     * 
     * @return
     */
    public int getMaxRecords();

    /**
     * Sets the value of the RecordSet's Name attribute
     * 
     * @param value
     */
    public void setName(String value);

    /**
     * Sets the value corresponding to the minimum number of records the RecordSet must contain
     * 
     * @param value
     */
    public void setMinRecords(int value);

    /**
     * Sets the value corresponding to the maximum number of records the RecordSet must contain
     * 
     * @param value
     */
    public void setMaxRecords(int value);
}
