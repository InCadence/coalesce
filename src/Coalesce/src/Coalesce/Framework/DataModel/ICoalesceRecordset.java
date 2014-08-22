package Coalesce.Framework.DataModel;

public interface ICoalesceRecordset {
    // Methods

	/*************************************************************************
	 * All instance variable/property declarations are static final 
	 * which means that they can not be changed.
	 * ******************************************************************** */
//    // Properties
//    String Name = "";
//    Integer MinRecords = 0;
//    Integer MaxRecords = 0;

    public String GetName();
    public int GetMinRecords();
    public int GetMaxRecords();

    public void SetName(String value);
    public void SetMinRecords(int value);
    public void SetMaxRecords(int value);
}
