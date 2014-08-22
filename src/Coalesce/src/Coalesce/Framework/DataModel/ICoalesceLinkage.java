package Coalesce.Framework.DataModel;

public interface ICoalesceLinkage {
    // Methods

	/*************************************************************************
	 * All instance variable/property declarations are static final 
	 * which means that they can not be changed.
	 * ******************************************************************** */
//    // Properties
//    String Name = "";
//    String Entity1Key = "";
//    String Entity1Name = "";
//    String Entity1Source = "";
//    String Entity1Version = "";
//    String Entity2Key = "";
//    String Entity2Name = "";
//    String Entity2Source = "";
//    String Entity2Version = "";
//    String LinkType = "";
//    String ClassificationMarking = "";
//    String ModifiedBy = "";
//    String InputLang = "";

    public String GetName();
    public String GetEntity1Key();
    public String GetEntity1Name();
    public String GetEntity1Source();
    public String GetEntity1Version();
    public String GetEntity2Key();
    public String GetEntity2Name();
    public String GetEntity2Source();
    public String GetEntity2Version();
    public String GetLinkType();
    public String GetClassificationMarking();
    public String GetModifiedBy();
    public String GetInputLang();

    public void SetName(String value);
    public void SetEntity1Key(String value);
    public void SetEntity1Name(String value);
    public void SetEntity1Source(String value);
    public void SetEntity1Version(String value);
    public void SetEntity2Key(String value);
    public void SetEntity2Name(String value);
    public void SetEntity2Source(String value);
    public void SetEntity2Version(String value);
    public void SetLinkType(String value);
    public void SetClassificationMarking(String value);
    public void SetModifiedBy(String value);
    public void SetInputLang(String value);
}
