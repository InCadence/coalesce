package Coalesce.Framework.DataModel;

import java.util.Locale;

import Coalesce.Common.Classification.Marking;

public interface ICoalesceField {

    // Methods
    public String getName();

    public String getValue();

    public String getLabel();

    public ECoalesceFieldDataTypes getDataType();

    public Marking getClassificationMarking();

    public String getModifiedBy();

    public String getModifiedByIP();

    public Locale getInputLang();

    public String getMimeType();

    public String getFilename();

    public String getExtension();

    public String getHash();

    public int getSize();

    public String getPreviousHistoryKey();

    public void setName(String value);

    public void setValue(String value);

    public void setLabel(String value);

    public void setDataType(ECoalesceFieldDataTypes value);

    public void setClassificationMarking(Marking value);

    public void setModifiedBy(String value);

    public void setModifiedByIP(String value);

    public void setInputLang(Locale value);

    public void setMimeType(String value);

    public void setFilename(String value);

    public void setExtension(String value);

    public void setHash(String value);

    public void setSize(int value);

    public void setPreviousHistoryKey(String value);
}
