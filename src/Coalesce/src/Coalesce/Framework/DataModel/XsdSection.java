package Coalesce.Framework.DataModel;

import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.joda.time.DateTime;

import unity.core.runtime.CallResult;
import unity.core.runtime.CallResult.CallResults;
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

    private static String MODULE = "Coalesce.Framework.DataModel.CoalesceSection";

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

    public static XsdSection Create(XsdEntity parent, String name)
    {
        return XsdSection.Create(parent, name, false);
    }

    public static XsdSection Create(XsdEntity parent, String name, boolean noIndex)
    {

        Section newEntitySection = new Section();
        parent.GetEntitySections().add(newEntitySection);

        XsdSection newSection = new XsdSection();
        if (!newSection.Initialize(parent, newEntitySection)) return null;

        newSection.SetName(name);

        newSection.SetNoIndex(noIndex);

        // Add to parent's child collection
        if (!parent._childDataObjects.containsKey(newSection.GetKey()))
        {
            parent._childDataObjects.put(newSection.GetKey(), newSection);
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
    public boolean Initialize(XsdEntity parent, Section section)
    {

        // Set References
        _parent = parent;
        _entitySection = section;

        for (Recordset childRecordSet : _entitySection.getRecordset())
        {

            XsdRecordset newRecordSet = new XsdRecordset();
            if (!newRecordSet.Initialize(this, childRecordSet)) continue;

            if (!_childDataObjects.containsKey(newRecordSet.GetKey()))
            {
                _childDataObjects.put(newRecordSet.GetKey(), newRecordSet);
            }
        }

        // TODO: Need to add another loop child Sections if they are added

        return super.Initialize();
    }

    // -----------------------------------------------------------------------//
    // public Methods
    // -----------------------------------------------------------------------//

    @Override
    protected String GetObjectKey()
    {
        return _entitySection.getKey();
    }

    @Override
    protected void SetObjectKey(String value)
    {
        _entitySection.setKey(value);
    }

    @Override
    public String GetName()
    {
        return _entitySection.getName();
    }

    @Override
    public void SetName(String value)
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

    public XsdRecordset CreateRecordset(String name)
    {
        return XsdRecordset.Create(this, name);
    }

    @Override
    public String ToXml()
    {
        return XmlHelper.Serialize(_entitySection);
    }

    @Override
    public DateTime GetDateCreated()
    {
        // return new SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").parse(_entitySection.getDatecreated());
        return _entitySection.getDatecreated();
    }

    @Override
    public void SetDateCreated(DateTime value)
    {
        // _entitySection.setDatecreated(new SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").format(value));
        _entitySection.setDatecreated(value);
    }

    @Override
    public DateTime GetLastModified()
    {
        // return new SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").parse(_entitySection.getLastmodified());
        return _entitySection.getLastmodified();
    }

    @Override
    protected void SetObjectLastModified(DateTime value)
    {
        // _entitySection.setLastmodified(new SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").format(value));
        _entitySection.setLastmodified(value);
    }

    // -----------------------------------------------------------------------//
    // Protected Methods
    // -----------------------------------------------------------------------//

    @Override
    protected String GetObjectStatus()
    {
        return _entitySection.getStatus();
    }

    @Override
    protected void SetObjectStatus(String status)
    {
        _entitySection.setStatus(status);
    }

    /*
    @Override
    protected boolean GetNoIndex()
    {
        return Boolean.parseBoolean(_entitySection.getNoindex());
    }

    @Override
    protected void SetObjectNoIndex(boolean value)
    {
        _entitySection.setNoindex(Boolean.toString(value));
    }
    */

    protected List<Recordset> GetEntityRecordSets()
    {
        return _entitySection.getRecordset();
    }

    @Override
    protected Map<QName, String> getAttributes()
    {
        return this._entitySection.getOtherAttributes();
    }
}
