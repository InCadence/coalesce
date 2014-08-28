package Coalesce.Framework.DataModel;

import org.joda.time.DateTime;

import unity.core.runtime.CallResult;
import unity.core.runtime.CallResult.CallResults;
import Coalesce.Common.Helpers.XmlHelper;
import Coalesce.Framework.GeneratedJAXB.Entity.Section.Recordset.Fielddefinition;

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

public class XsdFieldDefinition extends XsdDataObject {

	// -----------------------------------------------------------------------//
	// protected Member Variables
	// -----------------------------------------------------------------------//

	private static String MODULE = "Coalesce.Framework.DataModel.XsdFieldDefinition";

	private Fielddefinition _entityFieldDefinition;

	// -----------------------------------------------------------------------//
	// Factory and Initialization
	// -----------------------------------------------------------------------//

	public static XsdFieldDefinition Create(XsdRecordset parent, String name, String dataType, String label,
			String defaultClassificationMarking, String defaultValue) {
		return XsdFieldDefinition.Create(parent, name, dataType, label, defaultClassificationMarking, defaultValue, false);
	}

	public static XsdFieldDefinition Create(XsdRecordset parent, String name, String dataType, String label,
			String defaultClassificationMarking, String defaultValue, boolean noIndex) {

			XsdFieldDefinition fieldDefinition = Create(parent, name, dataType);

			// Set Additional Properties
			fieldDefinition.SetLabel(label);
			fieldDefinition.SetDefaultClassificationMarking(defaultClassificationMarking);
			fieldDefinition.SetDefaultValue(defaultValue);
			fieldDefinition.SetNoIndex(noIndex);
			
			return fieldDefinition;

	}

	public static XsdFieldDefinition Create(XsdRecordset parent, String name, String dataType) {
			return Create(parent, name, dataType, false);
	}

	public static XsdFieldDefinition Create(XsdRecordset parent, String name, String dataType, boolean noIndex) {
			return Create(parent, name, ECoalesceFieldDataTypes.GetELinkTypeForLabel(dataType), noIndex);
	}

	public static XsdFieldDefinition Create(XsdRecordset parent, String name, ECoalesceFieldDataTypes dataType) {
			return Create(parent, name, dataType, false);
	}

	public static XsdFieldDefinition Create(XsdRecordset parent, String name, ECoalesceFieldDataTypes dataType,
			boolean noIndex) {

			Fielddefinition newEntityFieldDefinition = new Fielddefinition();
			parent.GetEntityFieldDefinitions().add(newEntityFieldDefinition);

			XsdFieldDefinition fieldDefinition = new XsdFieldDefinition();
			
			if (!fieldDefinition.Initialize(parent, newEntityFieldDefinition))
				return null;

			fieldDefinition.SetName(name);
			fieldDefinition.SetDefaultClassificationMarking("U");
			fieldDefinition.SetDefaultValue("");
			fieldDefinition.SetNoIndex(noIndex);
			fieldDefinition.SetDataType(dataType);

			return fieldDefinition;
	}

	public boolean Initialize(XsdRecordset parent, Fielddefinition fieldDefinition) {

		// Set References
		_parent = parent;
		_entityFieldDefinition = fieldDefinition;

		// Add to Parent Collections
		if (GetStatus() == ECoalesceDataObjectStatus.ACTIVE) {
			parent._childDataObjects.put(this.GetKey(), this);
			parent.GetFieldDefinitions().add(this);
		}

		return super.Initialize();
	}

	// -----------------------------------------------------------------------//
	// public Properties
	// -----------------------------------------------------------------------//

	protected String GetObjectKey() {
		return _entityFieldDefinition.getKey();
	}

	public void SetKey(String value) {
		_entityFieldDefinition.setKey(value);
	}

	public String GetName() {
		return _entityFieldDefinition.getName();
	}

	public void SetName(String value) {
		_entityFieldDefinition.setName(value);
	}

	public String GetLabel() {
		return _entityFieldDefinition.getLabel();
	}

	public void SetLabel(String value) {
		_entityFieldDefinition.setLabel(value);
	}

	public String GetDataType() {
		return _entityFieldDefinition.getDatatype();
	}

	public void SetDataType(ECoalesceFieldDataTypes value) {
		_entityFieldDefinition.setDatatype(value.getLabel());
	}

	public String GetDefaultClassificationMarking() {
		return _entityFieldDefinition.getDefaultclassificationmarking();
	}

	public void SetDefaultClassificationMarking(String value) {
		_entityFieldDefinition.setDefaultclassificationmarking(value);
	}

	public String GetDefaultValue() {
		return _entityFieldDefinition.getDefaultvalue();
	}

	public void SetDefaultValue(String value) {
		_entityFieldDefinition.setDefaultvalue(value);
	}

	public DateTime GetDateCreated() {
		try {

			// return new SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").parse(_entityFieldDefinition.getDatecreated());
			return _entityFieldDefinition.getDatecreated();

		} catch (Exception ex) {
			CallResult.log(CallResults.FAILED_ERROR, ex, this);
			return null;
		}
	}

	public CallResult SetDateCreated(DateTime value) {
		try {
			// _entityFieldDefinition.setDatecreated(new SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").format(value));
			_entityFieldDefinition.setDatecreated(value);

			return CallResult.successCallResult;

		} catch (Exception ex) {
			return new CallResult(CallResults.FAILED_ERROR, ex, this);
		}
	}

	public DateTime GetLastModified() {
		try {

			// return new SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").parse(_entityFieldDefinition.getLastmodified());
			return _entityFieldDefinition.getLastmodified();

		} catch (Exception ex) {
			CallResult.log(CallResults.FAILED_ERROR, ex, this);
			return null;
		}
	}

	protected CallResult SetObjectLastModified(DateTime value) {
		try {
			// _entityFieldDefinition.setLastmodified(new SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").format(value));
			_entityFieldDefinition.setLastmodified(value);

			return CallResult.successCallResult;

		} catch (Exception ex) {
			return new CallResult(CallResults.FAILED_ERROR, ex, this);
		}
	}

	public static ECoalesceFieldDataTypes GetCoalesceFieldDataTypeForCoalesceType(String CoalesceType) {
		try {
			switch (CoalesceType.toUpperCase()) {

			case "BINARY":
				return ECoalesceFieldDataTypes.BinaryType;

			case "BOOLEAN":
				return ECoalesceFieldDataTypes.BooleanType;

			case "DATETIME":
				return ECoalesceFieldDataTypes.DateTimeType;

			case "GEOCOORDINATE":
				return ECoalesceFieldDataTypes.GeocoordinateType;

			case "GEOCOORDINATELIST":
				return ECoalesceFieldDataTypes.GeocoordinateListType;

			case "GUID":
				return ECoalesceFieldDataTypes.GuidType;

			case "INTEGER":
				return ECoalesceFieldDataTypes.IntegerType;

			case "URI":
				return ECoalesceFieldDataTypes.UriType;

			case "FILE":
				return ECoalesceFieldDataTypes.FileType;

			default:
				return ECoalesceFieldDataTypes.StringType;

			}
		} catch (Exception ex) {
			return ECoalesceFieldDataTypes.StringType;
		}
	}

	public static ECoalesceFieldDataTypes GetCoalesceFieldDataTypeForSQLType(String SQLType) {
		try {
			switch (SQLType.toUpperCase()) {
			case "ADVARWCHAR":
			case "ADLONGVARWCHAR":
				return ECoalesceFieldDataTypes.StringType;
			case "ADDBTIMESTAMP":
				return ECoalesceFieldDataTypes.DateTimeType;
			case "ADBOOLEAN":
				return ECoalesceFieldDataTypes.BooleanType;
			case "ADGUID":
				return ECoalesceFieldDataTypes.GuidType;
			case "ADSMALLINT":
			case "ADINTEGER":
				return ECoalesceFieldDataTypes.IntegerType;
			case "ADLONGVARBINARY":
				return ECoalesceFieldDataTypes.BinaryType;
			default:
				return ECoalesceFieldDataTypes.StringType;
			}

		} catch (Exception ex) {
			CallResult.log(CallResults.FAILED_ERROR, ex, XsdFieldDefinition.MODULE);

			return ECoalesceFieldDataTypes.StringType;
		}
	}

	// -----------------------------------------------------------------------//
	// Public Methods
	// -----------------------------------------------------------------------//

	public String ToXml() {
		return XmlHelper.Serialize(_entityFieldDefinition);
	}

	// -----------------------------------------------------------------------//
	// Protected Methods
	// -----------------------------------------------------------------------//

	protected CallResult GetObjectStatus(String status) {
		try {
			status = _entityFieldDefinition.getStatus();

			return CallResult.successCallResult;

		} catch (Exception ex) {
			return new CallResult(CallResults.FAILED_ERROR, ex, this);
		}
	}

	protected CallResult SetObjectStatus(String status) {
		try {
			_entityFieldDefinition.setStatus(status);

			return CallResult.successCallResult;

		} catch (Exception ex) {
			return new CallResult(CallResults.FAILED_ERROR, ex, this);
		}
	}

}
