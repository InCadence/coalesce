package Coalesce.Framework.DataModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.commons.lang.NullArgumentException;
import org.joda.time.DateTime;

import Coalesce.Common.Helpers.JodaDateTimeHelper;
import Coalesce.Common.Helpers.StringHelper;
import Coalesce.Common.Helpers.XmlHelper;
import Coalesce.Framework.GeneratedJAXB.Entity.Section;
import Coalesce.Framework.GeneratedJAXB.Entity.Section.Recordset;

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

public class XsdSection extends XsdDataObject {

    private Section _entitySection;

    // -----------------------------------------------------------------------//
    // Factory and Initialization
    // -----------------------------------------------------------------------//

    // TODO: Nested sections are not currently part of the Entity object
    /*
     * public CallResult Create(CoalesceSection Parent, CoalesceSection NewSection, String Name) { try{ return Create(Parent,
     * NewSection, Name, false); }catch(Exception ex){ // return Failed Error return new CallResult(CallResults.FAILED_ERROR,
     * ex, XsdSection.MODULE); } }
     * 
     * public CallResult Create(CoalesceSection Parent, CoalesceSection NewSection, String Name, boolean NoIndex) { try{
     * CallResult rst; Node NewNode = null;
     * 
     * // Create CoalesceSection Node NewSection = new CoalesceSection();
     * 
     * //TODO:Node creation (remove null assignment above) // Create the DataObjectNode //.DOCUMENT_NODE //NewNode =
     * Parent.GetDataObjectDocument().CreateNode(Node.ELEMENT_NODE, "section", ""); //NewNode =
     * Parent.GetDataObjectDocument().CreateNode(XmlNodeType.Element, "section", "");
     * Parent.GetDataObjectNode().appendChild(NewNode);
     * 
     * // Initialize the CoalesceField Object rst = NewSection.Initialize(Parent, NewNode); if ( !(rst.getIsSuccess()))
     * return rst;
     * 
     * Date UTCDate = new Date(); DateTimeHelper dth = new DateTimeHelper(); dth.ConvertDateToGMT(UTCDate);
     * 
     * // Set Default Values //TODO: GUIDHelper //rst = GUIDHelper.GetGuidString(Guid.NewGuid, NewSection.Key)
     * NewSection.SetKey(java.util.UUID.randomUUID().toString()); NewSection.SetName(Name);
     * NewSection.SetDateCreated(UTCDate); NewSection.SetLastModified(UTCDate); if (NoIndex) NewSection.NoIndex = true;
     * 
     * // Add to Parent's Child Collection if ( !(Parent.GetChildDataObjects().containsKey(NewSection.GetKey())) ) {
     * Parent.GetChildDataObjects().put(NewSection.GetKey(), NewSection); }
     * 
     * // return Success return CallResult.successCallResult;
     * 
     * }catch(Exception ex){ // return Failed Error return new CallResult(CallResults.FAILED_ERROR, ex, XsdSection.MODULE); }
     * }
     */

    public static XsdSection create(XsdEntity parent, String name)
    {
        return XsdSection.create(parent, name, false);
    }

    public static XsdSection create(XsdEntity parent, String name, boolean noIndex)
    {

        if (parent == null) throw new NullArgumentException("parent");
        if (name == null) throw new NullArgumentException("name");
        if (StringHelper.IsNullOrEmpty(name.trim())) throw new IllegalArgumentException("name cannot be empty");

        // Check that a section with the same name doesn't already exist
        for (XsdSection section : parent.getSections().values())
        {
            if (section.getName().equalsIgnoreCase(name))
            {

                section.setNoIndex(noIndex);
                return section;
            }
        }

        Section newEntitySection = new Section();
        parent.getEntitySections().add(newEntitySection);

        XsdSection newSection = new XsdSection();
        if (!newSection.initialize(parent, newEntitySection)) return null;

        newSection.setName(name);

        newSection.setNoIndex(noIndex);

        // Add to parent's child collection
        if (!parent._childDataObjects.containsKey(newSection.getKey()))
        {
            parent._childDataObjects.put(newSection.getKey(), newSection);
        }

        return newSection;
    }

    // TODO: Need to get Entity with nested sections
    /*
     * public CallResult Initialize(CoalesceSection Parent, Node DataObjectNode) { try{ // Since the Section can be
     * initialized with an Entity as a parent or // another Section as a parent, we have a private method that takes in // an
     * ICoalesceDataObject, and these public methods expose the strongly // typed interface to outside callers. return
     * this.Initialize((ICoalesceDataObject)Parent, DataObjectNode);
     * 
     * }catch(Exception ex){ // return Failed Error return new CallResult(CallResults.FAILED_ERROR, ex, this); } }
     */

    public boolean initialize(XsdEntity parent, Section section)
    {

        if (parent == null) throw new NullArgumentException("parent");
        if (section == null) throw new NullArgumentException("section");

        // Set References
        _parent = parent;
        _entitySection = section;

        super.initialize();

        for (Recordset childRecordSet : _entitySection.getRecordset())
        {

            XsdRecordset newRecordSet = new XsdRecordset();
            if (!newRecordSet.initialize(this, childRecordSet)) continue;

            if (!_childDataObjects.containsKey(newRecordSet.getKey()))
            {
                _childDataObjects.put(newRecordSet.getKey(), newRecordSet);
            }
        }

        // TODO: Need to add another loop child Sections if they are added

        return true;
    }

    // -----------------------------------------------------------------------//
    // public Methods
    // -----------------------------------------------------------------------//

    @Override
    protected String getObjectKey()
    {
        return _entitySection.getKey();
    }

    @Override
    protected void setObjectKey(String value)
    {
        _entitySection.setKey(value);
    }

    @Override
    public String getName()
    {
        return _entitySection.getName();
    }

    @Override
    public void setName(String value)
    {
        _entitySection.setName(value);
    }

    @Override
    public String getType()
    {
        return "section";
    }

    // TODO: Need nested sections
    /*
     * public CallResult CreateSection(XsdSection newSection, String name) { try{ CallResult rst;
     * 
     * // Create new Section rst = XsdSection.Create(this, newSection, name); if (!rst.getIsSuccess()) return rst;
     * 
     * return CallResult.successCallResult;
     * 
     * }catch(Exception ex){ return new CallResult(CallResults.FAILED_ERROR, ex, this); } }
     */

    public XsdRecordset createRecordset(String name)
    {
        return XsdRecordset.create(this, name);
    }

    public XsdRecordset getRecordset(String NamePath)
    {
        XsdDataObject dataObject = getDataObjectForNamePath(NamePath);

        if (dataObject != null && dataObject instanceof XsdRecordset)
        {
            return (XsdRecordset) dataObject;
        }

        return null;
    }

    public Map<String, XsdRecordset> getRecordsets()
    {

        Map<String, XsdRecordset> recordSets = new HashMap<String, XsdRecordset>();

        for (XsdDataObject child : _childDataObjects.values())
        {
            if (child instanceof XsdRecordset)
            {
                recordSets.put(child.getKey(), (XsdRecordset) child);
            }
        }

        return recordSets;

    }

    @Override
    public boolean getNoIndex()
    {
        return Boolean.parseBoolean(_entitySection.getNoindex());
    }

    @Override
    public void setNoIndex(boolean value)
    {
        _entitySection.setNoindex(Boolean.toString(value));
    }

    @Override
    public String toXml()
    {
        return XmlHelper.Serialize(_entitySection);
    }

    @Override
    public DateTime getDateCreated()
    {
        // return new SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").parse(_entitySection.getDatecreated());
        return _entitySection.getDatecreated();
    }

    @Override
    public void setDateCreated(DateTime value)
    {
        // _entitySection.setDatecreated(new SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").format(value));
        _entitySection.setDatecreated(value);
    }

    @Override
    public DateTime getLastModified()
    {
        // return new SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").parse(_entitySection.getLastmodified());
        return _entitySection.getLastmodified();
    }

    @Override
    protected void setObjectLastModified(DateTime value)
    {
        // _entitySection.setLastmodified(new SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").format(value));
        _entitySection.setLastmodified(value);
    }

    // -----------------------------------------------------------------------//
    // Protected Methods
    // -----------------------------------------------------------------------//

    @Override
    protected String getObjectStatus()
    {
        return _entitySection.getStatus();
    }

    @Override
    protected void setObjectStatus(ECoalesceDataObjectStatus status)
    {
        _entitySection.setStatus(status.getLabel());
    }

    /*
     * @Override protected boolean GetNoIndex() { return Boolean.parseBoolean(_entitySection.getNoindex()); }
     * 
     * @Override protected void SetObjectNoIndex(boolean value) { _entitySection.setNoindex(Boolean.toString(value)); }
     */

    protected List<Recordset> getEntityRecordSets()
    {
        return _entitySection.getRecordset();
    }

    @Override
    protected Map<QName, String> getOtherAttributes()
    {
        return this._entitySection.getOtherAttributes();
    }

    @Override
    public boolean setAttribute(String name, String value)
    {
        switch (name) {
        case "key":
            _entitySection.setKey(value);
            return true;
        case "datecreated":
            _entitySection.setDatecreated(JodaDateTimeHelper.FromXmlDateTimeUTC(value));
            return true;
        case "lastmodified":
            _entitySection.setLastmodified(JodaDateTimeHelper.FromXmlDateTimeUTC(value));
            return true;
        case "name":
            _entitySection.setName(value);
            return true;
        case "noindex":
            _entitySection.setNoindex(value);
            return true;
        case "status":
            _entitySection.setStatus(value);
            return true;
        default:
            this.setOtherAttribute(name, value);
            return true;
        }
    }

    @Override
    protected Map<QName, String> getAttributes()
    {
        Map<QName, String> map = new HashMap<QName, String>();
        map.put(new QName("key"), _entitySection.getKey());
        map.put(new QName("datecreated"), JodaDateTimeHelper.ToXmlDateTimeUTC(_entitySection.getDatecreated()));
        map.put(new QName("lastmodified"), JodaDateTimeHelper.ToXmlDateTimeUTC(_entitySection.getLastmodified()));
        map.put(new QName("name"), _entitySection.getName());
        map.put(new QName("noindex"), _entitySection.getNoindex());
        map.put(new QName("status"), _entitySection.getStatus());
        return map;
    }
}
