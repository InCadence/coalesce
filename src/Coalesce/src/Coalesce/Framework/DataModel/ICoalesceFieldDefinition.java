package Coalesce.Framework.DataModel;

import Coalesce.Common.Classification.Marking;

public interface ICoalesceFieldDefinition {

    // Methods
    public String getName();

    public String getLabel();

    public ECoalesceFieldDataTypes getDataType();

    public String getDefaultValue();

    public Marking getDefaultClassificationMarking();

    public void setName(String value);

    public void setLabel(String value);

    public void setDataType(ECoalesceFieldDataTypes value);

    public void setDefaultValue(String value);

    public void setDefaultClassificationMarking(Marking value);
}
