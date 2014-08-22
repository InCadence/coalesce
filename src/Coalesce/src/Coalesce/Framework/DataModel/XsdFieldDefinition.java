package Coalesce.Framework.DataModel;

import org.joda.time.DateTime;

import unity.core.runtime.CallResult;
import unity.core.runtime.CallResult.CallResults;
import Coalesce.Common.Helpers.XmlHelper;
import Coalesce.Framework.DataModel.Entity.Section.Recordset.Fielddefinition;

public class XsdFieldDefinition extends XsdDataObject {

	// -----------------------------------------------------------------------//
	// protected Member Variables
	// -----------------------------------------------------------------------//

	private static String MODULE = "Coalesce.Framework.DataModel.XsdFieldDefinition";

	private Fielddefinition _entityFieldDefinition;

	// -----------------------------------------------------------------------//
	// Factory and Initialization
	// -----------------------------------------------------------------------//

	public static CallResult Create(XsdRecordset parent,
	                                XsdFieldDefinition newFieldDefinition,
	                                String name,
	                                String dataType,
	                                String label,
	                                String defaultClassificationMarking,
	                                String defaultValue)
	{
		try {
			CallResult rst;

			rst = Create(parent,
			             newFieldDefinition,
			             name,
			             dataType,
			             label,
			             defaultClassificationMarking,
			             defaultValue,
			             false);

			return rst;

		} catch (Exception ex) {
			return new CallResult(CallResults.FAILED_ERROR, ex, XsdFieldDefinition.MODULE);
		}
	}

	public static CallResult Create(XsdRecordset parent,
	                                XsdFieldDefinition newFieldDefinition,
	                                String name,
	                                String dataType,
	                                String label,
	                                String defaultClassificationMarking,
	                                String defaultValue,
	                                boolean noIndex)
	{
		try {
			CallResult rst;

			rst = Create(parent, newFieldDefinition, name, dataType);
			if (!rst.getIsSuccess()) return rst;

			// Set Additional Properties
			newFieldDefinition.SetLabel(label);
			newFieldDefinition.SetDefaultClassificationMarking(defaultClassificationMarking);
			newFieldDefinition.SetDefaultValue(defaultValue);
			newFieldDefinition.SetNoIndex(noIndex);

			return CallResult.successCallResult;

		} catch (Exception ex) {
			return new CallResult(CallResults.FAILED_ERROR, ex, XsdFieldDefinition.MODULE);
		}
	}

	public static CallResult Create(XsdRecordset parent,
	                                XsdFieldDefinition newFieldDefinition,
	                                String name,
	                                String dataType)
	{
		try {
			CallResult rst;

			rst = Create(parent, newFieldDefinition, name, dataType, false);

			return rst;

		} catch (Exception ex) {
			return new CallResult(CallResults.FAILED_ERROR, ex, XsdFieldDefinition.MODULE);
		}
	}

	public static CallResult Create(XsdRecordset parent,
	                                XsdFieldDefinition newFieldDefinition,
	                                String name,
	                                String dataType,
	                                boolean noIndex)
	{
		try {
			CallResult rst;

			rst = Create(parent,
			             newFieldDefinition,
			             name,
			             ECoalesceFieldDataTypes.GetELinkTypeForLabel(dataType),
			             noIndex);

			return rst;

		} catch (Exception ex) {
			return new CallResult(CallResults.FAILED_ERROR, ex, XsdFieldDefinition.MODULE);
		}
	}

	public static CallResult Create(XsdRecordset parent,
	                                XsdFieldDefinition newFieldDefinition,
	                                String name,
	                                ECoalesceFieldDataTypes dataType)
	{
		try {
			CallResult rst;

			rst = Create(parent, newFieldDefinition, name, dataType, false);

			return rst;

		} catch (Exception ex) {
			return new CallResult(CallResults.FAILED_ERROR, ex, XsdFieldDefinition.MODULE);
		}
	}

	public static CallResult Create(XsdRecordset parent,
	                                XsdFieldDefinition newFieldDefinition,
	                                String name,
	                                ECoalesceFieldDataTypes dataType,
	                                boolean noIndex)
	{
		try {
			CallResult rst;

			Fielddefinition newEntityFieldDefinition = new Fielddefinition();
			parent.GetEntityFieldDefinitions().add(newEntityFieldDefinition);

			rst = newFieldDefinition.Initialize(parent, newEntityFieldDefinition);
			if (!rst.getIsSuccess()) return rst;

			newFieldDefinition.SetName(name);
			newFieldDefinition.SetDefaultClassificationMarking("U");
			newFieldDefinition.SetDefaultValue("");
			newFieldDefinition.SetNoIndex(noIndex);

			switch (dataType) {
			case StringType:
				newFieldDefinition.SetDataType("string");
				break;
			case DateTimeType:
				newFieldDefinition.SetDataType("datetime");
				break;
			case UriType:
				newFieldDefinition.SetDataType("uri");
				break;
			case BinaryType:
				newFieldDefinition.SetDataType("binary");
				break;
			case BooleanType:
				newFieldDefinition.SetDataType("boolean");
				break;
			case IntegerType:
				newFieldDefinition.SetDataType("integer");
				break;
			case GuidType:
				newFieldDefinition.SetDataType("guid");
				break;
			case GeocoordinateType:
				newFieldDefinition.SetDataType("geocoordinate");
				break;
			case GeocoordinateListType:
				newFieldDefinition.SetDataType("geocoordinatelist");
				break;
			case FileType:
				newFieldDefinition.SetDataType("file");
				break;
			}

			return CallResult.successCallResult;

		} catch (Exception ex) {
			return new CallResult(CallResults.FAILED_ERROR, ex, XsdFieldDefinition.MODULE);
		}
	}

	public CallResult Initialize(XsdRecordset parent, Fielddefinition fieldDefinition)
	{
		try {
			CallResult rst;

			// Set References
			_parent = parent;

			_entityFieldDefinition = fieldDefinition;

			rst = InitializeEntity();

			// Add to Parent Collections
			if (GetStatus() == ECoalesceDataObjectStatus.ACTIVE) {
				parent._childDataObjects.put(this.GetKey(), this);
				parent.GetFieldDefinitions().add(this);
			}
			 
			return rst;

		} catch (Exception ex) {
			return new CallResult(CallResults.FAILED_ERROR, ex, this);
		}
	}

	// -----------------------------------------------------------------------//
	// public Properties
	// -----------------------------------------------------------------------//

	protected String GetObjectKey()
	{
		return _entityFieldDefinition.getKey();
	}

	public void SetKey(String value)
	{
		_entityFieldDefinition.setKey(value);
	}

	public String GetName()
	{
		return _entityFieldDefinition.getName();
	}

	public void SetName(String value)
	{
		_entityFieldDefinition.setName(value);
	}

	public String GetLabel()
	{
		return _entityFieldDefinition.getLabel();
	}

	public void SetLabel(String value)
	{
		_entityFieldDefinition.setLabel(value);
	}

	public String GetDataType()
	{
		return _entityFieldDefinition.getDatatype();
	}

	public void SetDataType(String value)
	{
		_entityFieldDefinition.setDatatype(value);
	}

	public String GetDefaultClassificationMarking()
	{
		return _entityFieldDefinition.getDefaultclassificationmarking();
	}

	public void SetDefaultClassificationMarking(String value)
	{
		_entityFieldDefinition.setDefaultclassificationmarking(value);
	}

	public String GetDefaultValue()
	{
		return _entityFieldDefinition.getDefaultvalue();
	}

	public void SetDefaultValue(String value)
	{
		_entityFieldDefinition.setDefaultvalue(value);
	}

	public DateTime GetDateCreated()
	{
		try {

			//return new SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").parse(_entityFieldDefinition.getDatecreated());
			return _entityFieldDefinition.getDatecreated();

		} catch (Exception ex) {
			CallResult.log(CallResults.FAILED_ERROR, ex, this);
			return null;
		}
	}

	public CallResult SetDateCreated(DateTime value)
	{
		try {
			//_entityFieldDefinition.setDatecreated(new SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").format(value));
			_entityFieldDefinition.setDatecreated(value);

			return CallResult.successCallResult;

		} catch (Exception ex) {
			return new CallResult(CallResults.FAILED_ERROR, ex, this);
		}
	}

	public DateTime GetLastModified()
	{
		try {

			//return new SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").parse(_entityFieldDefinition.getLastmodified());
			return _entityFieldDefinition.getLastmodified();

		} catch (Exception ex) {
			CallResult.log(CallResults.FAILED_ERROR, ex, this);
			return null;
		}
	}

	protected CallResult SetObjectLastModified(DateTime value)
	{
		try {
			//_entityFieldDefinition.setLastmodified(new SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").format(value));
			_entityFieldDefinition.setLastmodified(value);

			return CallResult.successCallResult;

		} catch (Exception ex) {
			return new CallResult(CallResults.FAILED_ERROR, ex, this);
		}
	}

    public static ECoalesceFieldDataTypes GetCoalesceFieldDataTypeForCoalesceType(String CoalesceType) {
        try{
            switch (CoalesceType.toUpperCase()){

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
        }catch(Exception ex){
            return ECoalesceFieldDataTypes.StringType;
        }
    }

	public static ECoalesceFieldDataTypes GetCoalesceFieldDataTypeForSQLType(String SQLType)
	{
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

	public CallResult ToXml(StringBuilder xml)
	{
		try {
			CallResult rst;

			rst = XmlHelper.Serialize(_entityFieldDefinition, xml);

			return rst;

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
			status = _entityFieldDefinition.getStatus();

			return CallResult.successCallResult;

		} catch (Exception ex) {
			return new CallResult(CallResults.FAILED_ERROR, ex, this);
		}
	}

	protected CallResult SetObjectStatus(String status)
	{
		try {
			_entityFieldDefinition.setStatus(status);

			return CallResult.successCallResult;

		} catch (Exception ex) {
			return new CallResult(CallResults.FAILED_ERROR, ex, this);
		}
	}

}
