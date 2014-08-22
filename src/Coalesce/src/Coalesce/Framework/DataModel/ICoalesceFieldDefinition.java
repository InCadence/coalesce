package Coalesce.Framework.DataModel;

public interface ICoalesceFieldDefinition {
    // Methods

	/*************************************************************************
	 * All instance variable/property declarations are static final 
	 * which means that they can not be changed.
	 * ******************************************************************** */
//    // Properties
//    String Name  = "";
//    String Label  = "";
//    String DataType  = "";
//    String DefaultValue  = "";
//    String DefaultClassificationMarking  = "";

    public String GetName();
    public String GetLabel();
    public String GetDataType();
    public String GetDefaultValue();
    public String GetDefaultClassificationMarking();

    public void SetName(String value);
    public void SetLabel(String value);
    public void SetDataType(String value);
    public void SetDefaultValue(String value);
    public void SetDefaultClassificationMarking(String value);
}
