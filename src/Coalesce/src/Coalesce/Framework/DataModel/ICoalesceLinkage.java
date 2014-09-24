package Coalesce.Framework.DataModel;

import java.util.Locale;

import Coalesce.Common.Classification.Marking;

public interface ICoalesceLinkage {

    // Methods
    public String getName();

    public String getEntity1Key();

    public String getEntity1Name();

    public String getEntity1Source();

    public String getEntity1Version();

    public String getEntity2Key();

    public String getEntity2Name();

    public String getEntity2Source();

    public String getEntity2Version();

    public ELinkTypes getLinkType();

    public Marking getClassificationMarking();

    public String getModifiedBy();

    public Locale getInputLang();

    public void setName(String value);

    public void setEntity1Key(String value);

    public void setEntity1Name(String value);

    public void setEntity1Source(String value);

    public void setEntity1Version(String value);

    public void setEntity2Key(String value);

    public void setEntity2Name(String value);

    public void setEntity2Source(String value);

    public void setEntity2Version(String value);

    public void setLinkType(ELinkTypes value);

    public void setClassificationMarking(Marking value);

    public void setModifiedBy(String value);

    public void setInputLang(Locale value);
}
