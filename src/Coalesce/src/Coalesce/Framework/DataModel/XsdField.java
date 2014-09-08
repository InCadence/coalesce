package Coalesce.Framework.DataModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.joda.time.DateTime;

import unity.core.runtime.CallResult;
import unity.core.runtime.CallResult.CallResults;
import Coalesce.Common.Helpers.FileHelper;
import Coalesce.Common.Helpers.GUIDHelper;
import Coalesce.Common.Helpers.JodaDateTimeHelper;
import Coalesce.Common.Helpers.StringHelper;
import Coalesce.Common.Helpers.XmlHelper;
import Coalesce.Framework.GeneratedJAXB.Classification;
import Coalesce.Framework.GeneratedJAXB.Entity.Section.Recordset.Record.Field;
import Coalesce.Framework.GeneratedJAXB.Entity.Section.Recordset.Record.Field.Fieldhistory;

/*-----------------------------------------------------------------------------'
 Copyright 2014 - InCadence Strategic Solutions Inc., All Rights Reserved

 Notwithstanding any contractor copyright notice, the Government has Unlimited
 Rights in this work as defined by DFARS 252.227-7013 and 252.227-7014.  Use
 of this work other than as specifically authorized by these DFARS Clauses may
 violate Government rights in this work.

 DFARS Clause reference: 252.227-7013 (a)(16) and 252.227-7014 (a)(16)
 Unlimited Rights. The Government has the right to use, modify, reproduce,
 perform, display, release or disclose this computer software and to have or
 authorize others to do so.

 Distribution Statement D. Distribution authorized to the Department of
 Defense and U.S. DoD contractors only in support of U.S. DoD efforts.
 -----------------------------------------------------------------------------*/

public class XsdField extends XsdFieldBase {

    // -----------------------------------------------------------------------//
    // protected Member Variables
    // -----------------------------------------------------------------------//

    private boolean _suspendHistory = false;
    protected Field _entityField;

    // -----------------------------------------------------------------------//
    // Factory and Initialization
    // -----------------------------------------------------------------------//

    public static XsdField Create(XsdRecord parent, XsdFieldDefinition fieldDefinition)
    {

        Field newEntityField = new Field();
        parent.GetEntityFields().add(newEntityField);

        XsdField newField = new XsdField();
        if (!newField.Initialize(parent, newEntityField)) return null;

        newField.SetSuspendHistory(true);

        newField.SetName(fieldDefinition.GetName());
        newField.SetDataType(fieldDefinition.GetDataType());
        newField.SetValue(fieldDefinition.GetDefaultValue());
        newField.SetClassificationMarking(fieldDefinition.GetDefaultClassificationMarking());
        newField.SetLabel(fieldDefinition.GetLabel());

        newField.SetSuspendHistory(false);

        newField.SetNoIndex(true);

        // Boolean Type? If so then default initial value to false.
        if (fieldDefinition.GetDataType() == ECoalesceFieldDataTypes.BooleanType)
        {
            newField.SetValue("false");
        }

        // Add to Parent's Child Collection
        if (!(parent._childDataObjects.containsKey(newField.GetKey())))
        {
            parent._childDataObjects.put(newField.GetKey(), newField);
        }

        return newField;

    }

    public boolean Initialize(XsdRecord parent, Field field)
    {

        // Set References
        _parent = parent;
        _entityField = field;

        super.Initialize();
        
        for (Fieldhistory entityFieldHistory : _entityField.getFieldhistory())
        {

            XsdFieldHistory fieldHistory = new XsdFieldHistory();
            fieldHistory.Initialize(this, entityFieldHistory);

            // Add to Child Collection
            _childDataObjects.put(fieldHistory.GetKey(), fieldHistory);
        }

        return true;

    }

    // -----------------------------------------------------------------------//
    // Public Properties
    // -----------------------------------------------------------------------//

    @Override
    protected String GetObjectKey()
    {
        return _entityField.getKey();
    }

    @Override
    public void SetObjectKey(String value)
    {
        _entityField.setKey(value);
    }

    @Override
    public String GetName()
    {
        return GetStringElement(_entityField.getName());
    }

    @Override
    public void SetName(String value)
    {
        _entityField.setName(value);
    }

    @Override
    public String getType()
    {
        return "field";
    }

    public String GetValue()
    {
        return _entityField.getValue();
    }

    public void SetValue(String value)
    {
        String oldValue = _entityField.getValue();

        SetChanged(oldValue, value);
        _entityField.setValue(value);
    }

    public ECoalesceFieldDataTypes GetDataType()
    {
        return ECoalesceFieldDataTypes.GetTypeForCoalesceType(_entityField.getDatatype());
    }

    public void SetDataType(ECoalesceFieldDataTypes value)
    {
        _entityField.setDatatype(value.getLabel());
    }

    public String GetLabel()
    {
        return GetStringElement(_entityField.getLabel());
    }

    public void SetLabel(String value)
    {
        _entityField.setLabel(value);
    }

    public int GetSize()
    {
        try
        {
            return Integer.parseInt(_entityField.getSize());
        }
        catch (NumberFormatException e)
        {
            return 0;
        }
    }

    public void SetSize(Integer value)
    {
        _entityField.setSize(value.toString());
    }

    public String GetModifiedBy()
    {
        return GetStringElement(_entityField.getModifiedby());
    }

    public void SetModifiedBy(String value)
    {
        _entityField.setModifiedby(value);
    }

    public String GetModifiedByIP()
    {
        return GetStringElement(_entityField.getModifiedbyip());
    }

    public void SetModifiedByIP(String value)
    {
        _entityField.setModifiedbyip(value);
    }

    public String GetClassificationMarking()
    {
        return _entityField.getClassificationmarking();
    }

    public void SetClassificationMarking(String value)
    {
        String oldValue = _entityField.getClassificationmarking();

        SetChanged(oldValue, value);
        _entityField.setClassificationmarking(value);
    }

    public String GetPreviousHistoryKey()
    {
        String prevHistKey = _entityField.getPrevioushistorykey();
        if (StringHelper.IsNullOrEmpty(prevHistKey))
        {
            return "00000000-0000-0000-0000-000000000000";
        }
        else
        {
            return prevHistKey;
        }
    }

    public void SetPreviousHistoryKey(String value)
    {
        _entityField.setPrevioushistorykey(value);
    }

    public String GetFilename()
    {
        return GetStringElement(_entityField.getFilename());
    }

    public void SetFilename(String value)
    {
        String oldFilename = _entityField.getFilename();

        SetChanged(oldFilename, value);
        _entityField.setFilename(value);
    }

    public String GetExtension()
    {
        return GetStringElement(_entityField.getExtension());
    }

    public void SetExtension(String value)
    {
        String oldExtension = _entityField.getExtension();

        SetChanged(oldExtension, value);
        _entityField.setExtension(value.replace(".", ""));
    }

    public String GetMimeType()
    {
        return GetStringElement(_entityField.getMimetype());
    }

    public void SetMimeType(String value)
    {
        _entityField.setMimetype(value);
    }

    public String GetHash()
    {
        return GetStringElement(_entityField.getHash());
    }

    public void SetHash(String value)
    {
        String oldHash = _entityField.getHash();

        SetChanged(oldHash, value);
        _entityField.setHash(value);
    }

    public boolean GetSuspendHistory()
    {
        return this._suspendHistory;
    }

    public void SetSuspendHistory(boolean value)
    {
        this._suspendHistory = value;
    }

    public ArrayList<XsdFieldHistory> GetHistory()
    {

        ArrayList<XsdFieldHistory> historyList = new ArrayList<XsdFieldHistory>();

        // Return history items in the same order they are in the Entity
        for (Fieldhistory fh : _entityField.getFieldhistory())
        {
            XsdDataObject fdo = _childDataObjects.get(fh.getKey());

            if (fdo != null && fdo instanceof XsdFieldHistory)
            {
                historyList.add((XsdFieldHistory) _childDataObjects.get(fh.getKey()));
            }
        }

        return historyList;
    }

    public XsdFieldHistory GetHistoryRecord(String historyKey)
    {
        XsdFieldHistory historyRecord = (XsdFieldHistory) _childDataObjects.get(historyKey);

        return historyRecord;

    }

    /*
     * public String GetInputLang(){ return _entityField.getInputlang(); } public void SetInputLang(String value){
     * _entityField.setInputlang(value); }
     */

    @Override
    public DateTime GetDateCreated()
    {

        // SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").parse(_entityField.getDatecreated());
        return _entityField.getDatecreated();
    }

    @Override
    public void SetDateCreated(DateTime value)
    {
        // SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").format(value));
        _entityField.setDatecreated(value);
    }

    @Override
    public DateTime GetLastModified()
    {
        // SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").parse(_entityField.getLastmodified());
        return _entityField.getLastmodified();
    }

    @Override
    protected void SetObjectLastModified(DateTime value)
    {
        // SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").format(value));
        _entityField.setLastmodified(value);
    }

    public String ToXml()
    {
        return XmlHelper.Serialize(_entityField);
    }

    public String GetCoalesceFullFilename()
    {

        if (GetDataType() != ECoalesceFieldDataTypes.FileType)
        {
            return "";
        }

        String baseFilename = FileHelper.GetBaseFilenameWithFullDirectoryPathForKey(GetKey());

        return baseFilename + "." + GetExtension();

    }

    public String GetCoalesceFullThumbnailFilename()
    {

        if (GetDataType() != ECoalesceFieldDataTypes.FileType)
        {
            return "";
        }

        String baseFilename = FileHelper.GetBaseFilenameWithFullDirectoryPathForKey(GetKey());

        return baseFilename + "_thumb.jpg";

    }

    public String GetCoalesceFilenameWithLastModifiedTag()
    {
        try
        {
            String fullPath = GetCoalesceFullFilename();
            if (StringHelper.IsNullOrEmpty(fullPath)) return "";

            File theFile = new File(fullPath);
            long lastModifiedTicks = theFile.lastModified();

            return theFile.getName() + "?" + lastModifiedTicks;

        }
        catch (Exception ex)
        {
            return this.GetCoalesceFilename();
        }
    }

    public String GetCoalesceThumbnailFilenameWithLastModifiedTag()
    {
        try
        {
            String fullThumbPath = GetCoalesceFullThumbnailFilename();
            if (StringHelper.IsNullOrEmpty(fullThumbPath)) return "";

            File theFile = new File(fullThumbPath);
            long lastModifiedTicks = theFile.lastModified();

            return theFile.getName() + "?" + lastModifiedTicks;

        }
        catch (Exception ex)
        {
            return this.GetCoalesceThumbnailFilename();
        }
    }

    public String GetCoalesceFilename()
    {

        if (GetDataType() == ECoalesceFieldDataTypes.FileType)
        {

            String baseFilename = GetKey();
            baseFilename = GUIDHelper.RemoveBrackets(baseFilename);

            return baseFilename + "." + GetExtension();

        }
        else
        {
            return "";
        }
    }

    public String GetCoalesceThumbnailFilename()
    {

        if (GetDataType() == ECoalesceFieldDataTypes.FileType)
        {

            String baseFilename = this.GetKey();
            baseFilename = GUIDHelper.RemoveBrackets(baseFilename);

            return baseFilename + "_thumb.jpg";

        }
        else
        {
            return "";
        }
    }

    // -----------------------------------------------------------------------//
    // protected Methods
    // -----------------------------------------------------------------------//

    @Override
    protected String GetObjectStatus()
    {
        return _entityField.getStatus();
    }

    @Override
    protected void SetObjectStatus(String status)
    {
        _entityField.setStatus(status);

    }

    // -----------------------------------------------------------------------//
    // protected Methods
    // -----------------------------------------------------------------------//

    protected CallResult SetChanged(Object oldValue, Object newValue)
    {
        try
        {

            // Does the new value differ from the existing?
            if ((oldValue == null && newValue != null) || !oldValue.equals(newValue))
            {

                // Yes; should we create a FieldHistory entry to reflect the
                // change?
                // We create FieldHistory entry if History is not Suspended; OR
                // if DataType is binary; OR if DateCreated=LastModified and
                // Value is unset
                if (!this.GetSuspendHistory())
                {

                    switch (GetDataType()) {

                    case BinaryType:
                    case FileType:
                        // Don't Create History Entry for these types
                        break;
                    default:

                        // Does LastModified = DateCreated?
                        if (GetLastModified().compareTo(GetDateCreated()) != 0)
                        {

                            // No; Create History Entry
                            XsdFieldHistory fieldHistory = XsdFieldHistory.Create(this);
                            if (fieldHistory == null) return CallResult.failedCallResult;

                            SetPreviousHistoryKey(fieldHistory.GetKey());
                        }

                    }

                }

                // Set LastModified
                DateTime utcNow = JodaDateTimeHelper.NowInUtc();
                if (utcNow != null) SetLastModified(utcNow);

            }

            return CallResult.successCallResult;

        }
        catch (Exception ex)
        {
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    public CallResult Change(String value, String marking, String user, String ip)
    {
        try
        {

            // Does the new value differ from the existing?
            if (!(GetValue().equals(value) && GetClassificationMarking().equals(marking)))
            {

                // Yes; should we create a FieldHistory entry to reflect the
                // change?
                // We create FieldHistory entry if History is not Suspended; OR
                // if DataType is binary; OR if DateCreated=LastModified and
                // Value is unset
                if (!GetSuspendHistory())
                {

                    // Does LastModified = DateCreated?
                    if (GetLastModified().compareTo(GetDateCreated()) != 0)
                    {
                        // No; Create History Entry
                        // TODO: Something just feels wrong about declaring one
                        // CoalesceFieldHistory to create another.
                        XsdFieldHistory fieldHistory = XsdFieldHistory.Create(this);
                        if (fieldHistory == null) return CallResult.failedCallResult;

                        SetPreviousHistoryKey(fieldHistory.GetKey());
                    }
                }

                // Change Values
                if (GetDataType() == ECoalesceFieldDataTypes.DateTimeType && !StringHelper.IsNullOrEmpty(value))
                {

                    DateTime valueDate = JodaDateTimeHelper.FromXmlDateTimeUTC(value);

                    SetTypedValue(valueDate);

                }
                else
                {
                    SetValue(value);
                }

                SetClassificationMarking(marking);
                SetModifiedBy(user);
                SetModifiedByIP(ip);

                // Set LastModified
                DateTime utcNow = JodaDateTimeHelper.NowInUtc();
                if (utcNow != null) SetLastModified(utcNow);

            }

            return CallResult.successCallResult;

        }
        catch (Exception ex)
        {
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    protected List<Fieldhistory> GetEntityFieldHistories() throws Exception
    {
        return _entityField.getFieldhistory();
    }

    @Override
    protected Map<QName, String> getAttributes()
    {
        return this._entityField.getOtherAttributes();
    }

}
