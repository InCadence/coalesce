package Coalesce.Framework.DataModel;

import org.joda.time.DateTime;

import unity.core.runtime.CallResult;
import unity.core.runtime.CallResult.CallResults;
import Coalesce.Common.Helpers.XmlHelper;
import Coalesce.Framework.DataModel.Entity.Linkagesection.Linkage;

public class XsdLinkageSection extends XsdDataObject {

	private static String MODULE = "Coalesce.Framework.DataModel.CoalesceLinkageSection";

	private Entity.Linkagesection _entityLinkageSection;

	// -----------------------------------------------------------------------//
	// Factory and Initialization
	// -----------------------------------------------------------------------//

	public static CallResult Create(XsdEntity parent, XsdLinkageSection newLinkageSection)
	{
		try {
			boolean noIndex = false;

			return Create(parent, newLinkageSection, noIndex);

		} catch (Exception ex) {
			return new CallResult(CallResults.FAILED_ERROR, ex, XsdLinkageSection.MODULE);
		}
	}

	public static CallResult Create(XsdEntity parent, XsdLinkageSection newLinkageSection, Boolean noIndex)
	{
		try {
			CallResult rst;

			newLinkageSection = new XsdLinkageSection();
			rst = newLinkageSection.Initialize(parent);
			if (!rst.getIsSuccess()) return rst;

			rst = newLinkageSection.InitializeEntity();

			newLinkageSection.SetName("Linkages");

			newLinkageSection.SetNoIndex(noIndex);

			// Add to parent's child collection
			if (parent._childDataObjects.containsKey(newLinkageSection.GetKey())) {
				parent._childDataObjects.put(newLinkageSection.GetKey(), newLinkageSection);
			}

			return CallResult.successCallResult;

		} catch (Exception ex) {
			return new CallResult(CallResults.FAILED_ERROR, ex, XsdLinkageSection.MODULE);
		}
	}

	public CallResult Initialize(XsdEntity parent)
	{
		try {
			CallResult rst;

			// Set References
			_parent = parent;
			_entityLinkageSection = parent.GetEntityLinkageSection();

			for (Linkage childLinkage : _entityLinkageSection.linkage) {

				XsdLinkage newLinkage = new XsdLinkage();
				rst = newLinkage.Initialize(this, childLinkage);
				if (!rst.getIsSuccess()) continue;

				if (!_childDataObjects.containsKey(newLinkage.GetKey())) {
					_childDataObjects.put(newLinkage.GetKey(), newLinkage);
				}
			}

			return CallResult.successCallResult;

		} catch (Exception ex) {
			return new CallResult(CallResults.FAILED_ERROR, ex, this);
		}
	}

	// -----------------------------------------------------------------------//
	// Public Methods
	// -----------------------------------------------------------------------//

	protected String GetObjectKey()
	{
		return _entityLinkageSection.getKey();
	}

	public void SetKey(String value)
	{
		_entityLinkageSection.setKey(value);
	}

	public String GetName()
	{
		return _entityLinkageSection.getName();
	}

	public void SetName(String value)
	{
		_entityLinkageSection.setName(value);
	}

	public CallResult CreateLinkage(XsdLinkage newLinkage)
	{
		try {
			CallResult rst;

			// Create new Linkage
			rst = XsdLinkage.Create(this, newLinkage);
			if (!rst.getIsSuccess()) return rst;

			return CallResult.successCallResult;

		} catch (Exception ex) {
			return new CallResult(CallResults.FAILED_ERROR, ex, this);
		}
	}

	public CallResult ToXml(StringBuilder xml)
	{
		try {
			CallResult rst;

			rst = XmlHelper.Serialize(_entityLinkageSection, xml);

			return rst;

		} catch (Exception ex) {
			return new CallResult(CallResults.FAILED_ERROR, ex, this);
		}
	}

	public DateTime GetDateCreated()
	{
		try {

			// return new
			// SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").parse(_entityLinkageSection.getDatecreated());
			return _entityLinkageSection.getDatecreated();

		} catch (Exception ex) {
			CallResult.log(CallResults.FAILED_ERROR, ex, this);
			return null;
		}
	}

	public CallResult SetDateCreated(DateTime value)
	{
		try {
			// _entityLinkageSection.setDatecreated(new
			// SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").format(value));
			_entityLinkageSection.setDatecreated(value);

			return CallResult.successCallResult;

		} catch (Exception ex) {
			return new CallResult(CallResults.FAILED_ERROR, ex, this);
		}
	}

	public DateTime GetLastModified()
	{
		try {

			// return new
			// SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").parse(_entityLinkageSection.getLastmodified());
			return _entityLinkageSection.getLastmodified();

		} catch (Exception ex) {
			CallResult.log(CallResults.FAILED_ERROR, ex, this);
			return null;
		}
	}

	protected CallResult SetObjectLastModified(DateTime value)
	{
		try {
			// _entityLinkageSection.setLastmodified(new
			// SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").format(value));
			_entityLinkageSection.setLastmodified(value);

			return CallResult.successCallResult;

		} catch (Exception ex) {
			return new CallResult(CallResults.FAILED_ERROR, ex, this);
		}
	}

	// -----------------------------------------------------------------------//
	// Protected Methods
	// -----------------------------------------------------------------------//

	protected CallResult GetObjectStatus(String status)
	{
		try {
			status = _entityLinkageSection.getStatus();

			return CallResult.successCallResult;

		} catch (Exception ex) {
			return new CallResult(CallResults.FAILED_ERROR, ex, this);
		}
	}

	protected CallResult SetObjectStatus(String status)
	{
		try {
			_entityLinkageSection.setStatus(status);

			return CallResult.successCallResult;

		} catch (Exception ex) {
			return new CallResult(CallResults.FAILED_ERROR, ex, this);
		}
	}

	protected CallResult GetObjectNoIndex(String value)
	{
		try {
			value = _entityLinkageSection.getNoindex();

			return CallResult.successCallResult;

		} catch (Exception ex) {
			return new CallResult(CallResults.FAILED_ERROR, ex, this);
		}
	}

	protected CallResult SetObjectNoIndex(String value)
	{
		try {
			_entityLinkageSection.setNoindex(value);

			return CallResult.successCallResult;

		} catch (Exception ex) {
			return new CallResult(CallResults.FAILED_ERROR, ex, this);
		}
	}

	protected Entity.Linkagesection GetEntityLinkageSection()
	{
		return _entityLinkageSection;
	}
}
