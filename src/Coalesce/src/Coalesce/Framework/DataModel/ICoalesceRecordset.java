package Coalesce.Framework.DataModel;

public interface ICoalesceRecordset {

    // Methods

    public String getName();

    public int getMinRecords();

    public int getMaxRecords();

    public void setName(String value);

    public void setMinRecords(int value);

    public void setMaxRecords(int value);
}
