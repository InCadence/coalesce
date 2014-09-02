package Coalesce.Framework.DataModel;

import java.util.Map;

import javax.xml.namespace.QName;

import org.joda.time.DateTime;

import unity.core.runtime.CallResult;
import unity.core.runtime.CallResult.CallResults;
import Coalesce.Common.Helpers.XmlHelper;
import Coalesce.Framework.GeneratedJAXB.Entity.Linkagesection;
import Coalesce.Framework.GeneratedJAXB.Entity.Linkagesection.Linkage;

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

public class XsdLinkageSection extends XsdDataObject {

	private Linkagesection _entityLinkageSection;

	// -----------------------------------------------------------------------//
	// Factory and Initialization
	// -----------------------------------------------------------------------//

	public static XsdLinkageSection Create(XsdEntity parent) {

		return XsdLinkageSection.Create(parent, false);

	}

	public static XsdLinkageSection Create(XsdEntity parent, Boolean noIndex) {

		XsdLinkageSection linkageSection = new XsdLinkageSection();
		if (!linkageSection.Initialize(parent))
			return null;

		linkageSection.SetName("Linkages");
		linkageSection.SetNoIndex(noIndex);

		// Add to parent's child collection
		if (!parent._childDataObjects.containsKey(linkageSection.GetKey())) {
			parent._childDataObjects.put(linkageSection.GetKey(), linkageSection);
		}

		return linkageSection;

	}

	public boolean Initialize(XsdEntity parent) {

		// Set References
		_parent = parent;
		_entityLinkageSection = parent.GetEntityLinkageSection();

		if (_entityLinkageSection != null) {

			for (Linkage childLinkage : _entityLinkageSection.getLinkage()) {

				XsdLinkage newLinkage = new XsdLinkage();
				if (!newLinkage.Initialize(this, childLinkage))
					continue;

				if (!_childDataObjects.containsKey(newLinkage.GetKey())) {
					_childDataObjects.put(newLinkage.GetKey(), newLinkage);
				}
			}

			return super.Initialize();
		} else {
			return false;
		}

	}

	// -----------------------------------------------------------------------//
	// Public Methods
	// -----------------------------------------------------------------------//

	@Override
    protected String GetObjectKey() {
		return _entityLinkageSection.getKey();
	}

	@Override
    public void SetObjectKey(String value) {
		_entityLinkageSection.setKey(value);
	}

	@Override
    public String GetName() {
		return _entityLinkageSection.getName();
	}

	@Override
    public void SetName(String value) {
		_entityLinkageSection.setName(value);
	}

    @Override
    public String getType()
    {
        return "linkagesection";
    }
    
	public XsdLinkage CreateLinkage() {
		return XsdLinkage.Create(this);
	}

	public String ToXml() {
		return XmlHelper.Serialize(_entityLinkageSection);
	}

	public DateTime GetDateCreated() {
		try {

			// return new
			// SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").parse(_entityLinkageSection.getDatecreated());
			return _entityLinkageSection.getDatecreated();

		} catch (Exception ex) {
			CallResult.log(CallResults.FAILED_ERROR, ex, this);
			return null;
		}
	}

	@Override
    public void SetDateCreated(DateTime value) {
			// SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").format(value));
			_entityLinkageSection.setDatecreated(value);
	}

	@Override
    public DateTime GetLastModified() {
			// SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").parse(_entityLinkageSection.getLastmodified());
			return _entityLinkageSection.getLastmodified();
	}

	@Override
    protected void SetObjectLastModified(DateTime value) {
			// SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").format(value));
			_entityLinkageSection.setLastmodified(value);
	}

	// -----------------------------------------------------------------------//
	// Protected Methods
	// -----------------------------------------------------------------------//

	@Override
    protected String GetObjectStatus() {
			return _entityLinkageSection.getStatus();
	}

	@Override
    protected void SetObjectStatus(String status) {
			_entityLinkageSection.setStatus(status);
	}

	protected Linkagesection GetEntityLinkageSection() {
		return _entityLinkageSection;
	}
	
    @Override
    protected Map<QName, String> getAttributes()
    {
        return this._entityLinkageSection.getOtherAttributes();
    }
}
