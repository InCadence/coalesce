package Coalesce.Framework.DataModel;

public interface ICoalesceField {
	// Methods

	/*************************************************************************
	 * All instance variable/property declarations are static final 
	 * which means that they can not be changed.
	 * ******************************************************************** */
//    // Properties
//    String Name  = "";
//    String Value  = "";
//    String Label  = "";
//    String DataType  = "";
//    String ClassificationMarking  = "";
//    String ModifiedBy  = "";
//    String ModifiedByIP  = "";
//    String InputLang  = "";
//    String MimeType  = "";
//    String Filename  = "";
//    String Extension  = "";
//    String Hash  = "";
//    Integer Size  = 0;
//    String PreviousHistoryKey  = "";

    public String GetName();
    public String GetValue();
    public String GetLabel();
    public String GetDataType();
    public String GetClassificationMarking();
    public String GetModifiedBy();
    public String GetModifiedByIP();
    public String GetInputLang();
    public String GetMimeType();
    public String GetFilename();
    public String GetExtension();
    public String GetHash();
    public Integer GetSize();
    public String GetPreviousHistoryKey();

    public void SetName(String value);
    public void SetValue(String value);
    public void SetLabel(String value);
    public void SetDataType(String value);
    public void SetClassificationMarking(String value);
    public void SetModifiedBy(String value);
    public void SetModifiedByIP(String value);
    public void SetInputLang(String value);
    public void SetMimeType(String value);
    public void SetFilename(String value);
    public void SetExtension(String value);
    public void SetHash(String value);
    public void SetSize(Integer value);
    public void SetPreviousHistoryKey(String value);
}
