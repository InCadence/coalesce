package com.incadencecorp.coalesce.framework.persistance.derby;

import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.common.helpers.CoalesceTableHelper;
import com.incadencecorp.coalesce.common.helpers.JodaDateTimeHelper;
import com.incadencecorp.coalesce.framework.CoalesceSettings;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldHistory;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceLinkage;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceLinkageSection;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceObject;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceSection;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;
import com.incadencecorp.coalesce.framework.persistance.CoalesceDataConnectorBase;
import com.incadencecorp.coalesce.framework.persistance.CoalesceParameter;
import com.incadencecorp.coalesce.framework.persistance.CoalescePersistorBase;
import com.incadencecorp.coalesce.framework.persistance.ElementMetaData;
import com.incadencecorp.coalesce.framework.persistance.EntityMetaData;
import com.incadencecorp.coalesce.framework.persistance.ObjectMetaData;
import com.incadencecorp.coalesce.framework.persistance.ServerConn;

public class DerbyPersistor extends CoalescePersistorBase {
	/*--------------------------------------------------------------------------
	Private Members
	--------------------------------------------------------------------------*/
	private String _schema;
	private DerbyDataConnector derbyDataConnector;

	/**
	 * Set the schema to use when making database calls.
	 *
	 * @param schema
	 */
	public void setSchema(String schema) {
		_schema = schema;
	}

	protected String getSchemaPrefix() {
		if (_schema != null) {
			return _schema + ".";
		} else {
			return "";
		}
	}

	protected String getSchema() {
		return _schema;
	}

	public void setDerbyDataConnector(DerbyDataConnector derbyDataCnonnector) {
		this.derbyDataConnector = derbyDataConnector;
	}

	public DerbyDataConnector getDerbyDataConnector() {
		return derbyDataConnector;
	}

	@Override
	protected CoalesceDataConnectorBase getDataConnector()
			throws CoalescePersistorException {
		if (derbyDataConnector == null) {
			// create a default
			ServerConn serverConnection = super.getConnectionSettings();
			if (serverConnection == null) {
				serverConnection = DerbyDataConnector.getServerConnection();
			}
			derbyDataConnector = new DerbyDataConnector(
					serverConnection.getDatabase(), null, "memory");
		}
		return derbyDataConnector;
	}

	@Override
	public List<String> getCoalesceEntityKeysForEntityId(String entityId,
			String entityIdType, String entityName, String entitySource)
			throws CoalescePersistorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EntityMetaData getCoalesceEntityIdAndTypeForKey(String key)
			throws CoalescePersistorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean flattenObject(boolean allowRemoval,
			CoalesceEntity... entities) throws CoalescePersistorException {
		boolean isSuccessful = true;
		CoalesceDataConnectorBase conn = null;

		if (derbyDataConnector == null) {
			conn = (DerbyDataConnector) this.getDataConnector();
		}

		// Create a Database Connection
		try {
			conn.openConnection(false);

			for (CoalesceEntity entity : entities) {
				// Persist (Recursively)
				isSuccessful &= updateCoalesceObject(entity, conn, allowRemoval);
			}

			conn.commit();
		} catch (SQLException e) {
			conn.rollback();

			throw new CoalescePersistorException("FlattenObject: "
					+ e.getMessage(), e);
		}

		return isSuccessful;
	}

	/**
	 * Deletes the Coalesce object & CoalesceObjectMap that matches the given
	 * parameters.
	 *
	 * @param coalesceObject
	 *            the Coalesce object to be deleted
	 * @param conn
	 *            is the PostGresDataConnector database connection
	 * @return True = Successful delete
	 * @throws SQLException
	 */
	protected boolean deleteObject(CoalesceObject coalesceObject,
			CoalesceDataConnectorBase conn) throws SQLException {
		String objectType = coalesceObject.getType();
		String objectKey = coalesceObject.getKey();
		String tableName = CoalesceTableHelper
				.getTableNameForObjectType(objectType);

		conn.executeUpdate("DELETE FROM " + getSchemaPrefix()
				+ "CoalesceObjectMap WHERE ObjectKey=?", new CoalesceParameter(
				objectKey, Types.OTHER));
		conn.executeUpdate("DELETE FROM " + getSchemaPrefix() + tableName
				+ " WHERE ObjectKey=?", new CoalesceParameter(objectKey,
				Types.OTHER));

		return true;
	}

	private boolean updateCoalesceObject(CoalesceObject coalesceObject,
			CoalesceDataConnectorBase conn, boolean allowRemoval)
			throws SQLException

	{
		boolean isSuccessful = false;

		if (coalesceObject.getFlatten()) {
			switch (coalesceObject.getStatus()) {
			case READONLY:
			case ACTIVE:
				// Persist Object
				isSuccessful = persistObject(coalesceObject, conn);
				break;

			case DELETED:
				if (allowRemoval) {
					// Delete Object
					isSuccessful = deleteObject(coalesceObject, conn);
				} else {
					// Mark Object as Deleted
					isSuccessful = persistObject(coalesceObject, conn);
				}

				break;

			default:
				isSuccessful = false;
			}

			// Successful?
			if (isSuccessful) {
				// Yes; Iterate Through Children
				for (CoalesceObject childObject : coalesceObject
						.getChildCoalesceObjects().values()) {
					updateCoalesceObject(childObject, conn, allowRemoval);
				}
			}
		}
		return isSuccessful;
	}

	/**
	 * Adds or Updates a Coalesce object that matches the given parameters.
	 *
	 * @param coalesceObject
	 *            the Coalesce object to be added or updated
	 * @param conn
	 *            is the PostGresDataConnector database connection
	 * @return isSuccessful = True = Successful add/update operation.
	 * @throws SQLException
	 */
	protected boolean persistObject(CoalesceObject coalesceObject,
			CoalesceDataConnectorBase conn) throws SQLException {
		boolean isSuccessful = true;

		switch (coalesceObject.getType()) {
		case "entity":

			// isSuccessful = checkLastModified(coalesceObject, conn);
			isSuccessful = persistEntityObject((CoalesceEntity) coalesceObject,
					conn);
			break;

		case "section":
			if (CoalesceSettings.getUseIndexing()) {
				isSuccessful = persistSectionObject(
						(CoalesceSection) coalesceObject, conn);
			}
			break;

		case "recordset":
			if (CoalesceSettings.getUseIndexing()) {
				isSuccessful = persistRecordsetObject(
						(CoalesceRecordset) coalesceObject, conn);
			}
			break;
		case "fielddefinition":
			// if (CoalesceSettings.getUseIndexing())
			// {
			// Removed Field Definition Persisting
			// isSuccessful =
			// PersistFieldDefinitionObject((CoalesceFieldDefinition)
			// coalesceObject, conn);
			// }
			break;

		case "record":
			if (CoalesceSettings.getUseIndexing()) {
				isSuccessful = persistRecordObject(
						(CoalesceRecord) coalesceObject, conn);
			}
			break;

		case "field":// Not testing the type to ascertain if it is BINARY now.
			if (CoalesceSettings.getUseIndexing()) {
				isSuccessful = persistFieldObject(
						(CoalesceField<?>) coalesceObject, conn);
			}
			break;

		case "fieldhistory":
			if (CoalesceSettings.getUseIndexing()) {
				isSuccessful = persistFieldHistoryObject(
						(CoalesceFieldHistory) coalesceObject, conn);
			}
			break;

		case "linkagesection":
			if (CoalesceSettings.getUseIndexing()) {
				isSuccessful = persistLinkageSectionObject(
						(CoalesceLinkageSection) coalesceObject, conn);
			}
			break;

		case "linkage":
			if (CoalesceSettings.getUseIndexing()) {
				isSuccessful = persistLinkageObject(
						(CoalesceLinkage) coalesceObject, conn);
			}
			break;

		default:
			isSuccessful = false;
		}

		if (isSuccessful && CoalesceSettings.getUseIndexing()) {
			// Persist Map Table Entry
			isSuccessful = persistMapTableEntry(coalesceObject, conn);
		}

		return isSuccessful;
	}

	/**
	 * Adds or updates map table entry for a given element.
	 *
	 * @param coalesceObject
	 *            the Coalesce object to be added or updated
	 * @param conn
	 *            is the SQLServerDataConnector database connection
	 * @return True if successfully added/updated.
	 * @throws SQLException
	 */
	protected boolean persistMapTableEntry(CoalesceObject coalesceObject,
			CoalesceDataConnectorBase conn) throws SQLException {
		return true;
		// String parentKey;
		// String parentType;
		//
		// if (coalesceObject.getParent() != null)
		// {
		// parentKey = coalesceObject.getParent().getKey();
		// parentType = coalesceObject.getParent().getType();
		// }
		// else
		// {
		// parentKey = "00000000-0000-0000-0000-000000000000";
		// parentType = "";
		// }
		//
		// return conn.executeProcedure("CoalesceObjectMap_Insert",
		// new CoalesceParameter(parentKey, Types.OTHER),
		// new CoalesceParameter(parentType),
		// new CoalesceParameter(coalesceObject.getKey(), Types.OTHER),
		// new CoalesceParameter(coalesceObject.getType()));
	}

	/**
	 * Adds or Updates a Coalesce entity that matches the given parameters.
	 *
	 * @param entity
	 *            the XsdEntity to be added or updated
	 * @param conn
	 *            is the PostGresDataConnector database connection
	 * @return True = No Update required.
	 * @throws SQLException
	 */
	protected boolean persistEntityObject(CoalesceEntity entity,
			CoalesceDataConnectorBase conn) throws SQLException {
		// Return true if no update is required.
		if (!checkLastModified(entity, conn)) {
			return true;
		}

		return ((DerbyDataConnector)conn).coalesceEntity_InsertOrUpdate(
				entity.getKey(),
				entity.getName(), 
				entity.getSource(),
				entity.getVersion(),
				entity.getEntityId(),
				entity.getEntityIdType(),
				entity.toXml(), 
				entity.getDateCreated(),
				entity.getLastModified());
	}

	/**
	 * Adds or Updates a Coalesce section that matches the given parameters.
	 *
	 * @param section
	 *            the XsdSection to be added or updated
	 * @param conn
	 *            is the PostGresDataConnector database connection
	 * @return True = No Update required.
	 * @throws SQLException
	 */
	protected boolean persistSectionObject(CoalesceSection section,
			CoalesceDataConnectorBase conn) throws SQLException {
		// Return true if no update is required.
		return true;
	}

	/**
	 * Adds or Updates a Coalesce recordset that matches the given parameters.
	 *
	 * @param recordset
	 *            the XsdRecordset to be added or updated
	 * @param conn
	 *            is the PostGresDataConnector database connection
	 * @return True = No Update required.
	 * @throws SQLException
	 */
	protected boolean persistRecordsetObject(CoalesceRecordset recordset,
			CoalesceDataConnectorBase conn) throws SQLException {
		// Return true if no update is required.
		return true;
	}

	/**
	 * Adds or Updates a Coalesce field definition that matches the given
	 * parameters.
	 *
	 * @param fieldDefinition
	 *            the XsdFieldDefinition to be added or updated
	 * @param conn
	 *            is the PostGresDataConnector database connection
	 * @return True = No Update required.
	 * @throws SQLException
	 */
	protected boolean persistFieldDefinitionObject(
			CoalesceFieldDefinition fieldDefinition,
			CoalesceDataConnectorBase conn) throws SQLException {
		return true;
	}

	/**
	 * Adds or Updates a Coalesce record that matches the given parameters.
	 *
	 * @param record
	 *            the XsdRecord to be added or updated
	 * @param conn
	 *            is the PostGresDataConnector database connection
	 * @return True = No Update required.
	 * @throws SQLException
	 */
	protected boolean persistRecordObject(CoalesceRecord record,
			CoalesceDataConnectorBase conn) throws SQLException {
		// Return true if no update is required.
		return true;
	}

	/**
	 * Adds or Updates a Coalesce field that matches the given parameters.
	 *
	 * @param field
	 *            the XsdField to be added or updated
	 * @param conn
	 *            is the PostGresDataConnector database connection
	 * @return True = No Update required.
	 * @throws SQLException
	 */
	protected boolean persistFieldObject(CoalesceField<?> field,
			CoalesceDataConnectorBase conn) throws SQLException {
		// Return true if no update is required.
		return true;
	}

	/**
	 * Adds or Updates a Coalesce field history that matches the given
	 * parameters.
	 *
	 * @param fieldHistory
	 *            the XsdFieldHistory to be added or updated
	 * @param conn
	 *            is the PostGresDataConnector database connection
	 * @return True = No Update required.
	 * @throws SQLException
	 */
	protected boolean persistFieldHistoryObject(
			CoalesceFieldHistory fieldHistory, CoalesceDataConnectorBase conn)
			throws SQLException {
		// Return true if no update is required.
		return true;
	}

	/**
	 * Adds or Updates a Coalesce linkage section that matches the given
	 * parameters.
	 *
	 * @param linkageSection
	 *            the XsdLinkageSection to be added or updated
	 * @param conn
	 *            is the PostGresDataConnector database connection
	 * @return True = No Update required.
	 * @throws SQLException
	 */
	protected boolean persistLinkageSectionObject(
			CoalesceLinkageSection linkageSection,
			CoalesceDataConnectorBase conn) throws SQLException {
		// Return true if no update is required.
		return true;
	}

	/**
	 * Adds or Updates a Coalesce linkage that matches the given parameters.
	 *
	 * @param linkage
	 *            the XsdLinkage to be added or updated
	 * @param conn
	 *            is the PostGresDataConnector database connection
	 * @return True = No Update required.
	 * @throws SQLException
	 */
	protected boolean persistLinkageObject(CoalesceLinkage linkage,
			CoalesceDataConnectorBase conn) throws SQLException {
		// Return true if no update is required.
		if (!checkLastModified(linkage, conn)) {
			return true;
		}

		// Yes; Call Procedure
		return ((DerbyDataConnector)conn).coalesceLinkage_InsertOrUpdate(linkage);
	}

	/**
	 * Returns the EntityMetaData for the Coalesce entity that matches the given
	 * parameters.
	 *
	 * @param key
	 *            primary key of the Coalesce entity
	 * @param conn
	 *            is the PostGresDataConnector database connection
	 * @return metaData the EntityMetaData for the Coalesce entity.
	 * @throws SQLException
	 */
	protected EntityMetaData getCoalesceEntityIdAndTypeForKey(String key,
			CoalesceDataConnectorBase conn) throws SQLException {
		EntityMetaData metaData = null;

		// Execute Query
		ResultSet results = conn.executeQuery(
				"SELECT EntityId,EntityIdType,ObjectKey FROM "
						+ getSchemaPrefix()
						+ "CoalesceEntity WHERE ObjectKey=?",
				new CoalesceParameter(key, Types.OTHER));
		// Get Results
		while (results.next()) {
			metaData = new EntityMetaData(results.getString("EntityId"),
					results.getString("EntityIdType"),
					results.getString("ObjectKey"));
		}

		return metaData;
	}

	/**
	 * Returns the comparison for the Coalesce object last modified date versus
	 * the same objects value in the database.
	 *
	 * @param coalesceObject
	 *            the Coalesce object to have it's last modified date checked.
	 * @param conn
	 *            is the PostGresDataConnector database connection
	 * @return False = Out of Date
	 * @throws SQLException
	 */
	protected boolean checkLastModified(CoalesceObject coalesceObject,
			CoalesceDataConnectorBase conn) throws SQLException {
		boolean isOutOfDate = true;

		// Get LastModified from the Database
		DateTime lastModified = this.getCoalesceObjectLastModified(
				coalesceObject.getKey(), coalesceObject.getType(), conn);

		// DB Has Valid Time?
		if (lastModified != null) {
			// Remove NanoSeconds (100 ns / Tick and 1,000,000 ns / ms = 10,000
			// Ticks / ms)
			long objectTicks = coalesceObject.getLastModified().getMillis();
			long SQLRecordTicks = lastModified.getMillis();

			// TODO: Round Ticks for SQL (Not sure if this is required for .NET)
			// ObjectTicks = this.RoundTicksForSQL(ObjectTicks);

			if (objectTicks == SQLRecordTicks) {
				// They're equal; No Update Required
				isOutOfDate = false;
			}
		}

		return isOutOfDate;
	}

	@Override
	protected boolean flattenCore(boolean allowRemoval,
			CoalesceEntity... entities) throws CoalescePersistorException {
		boolean isSuccessful = false;

		CoalesceDataConnectorBase conn = null;

		// Create a Database Connection
		try {
			conn = new DerbyDataConnector(getConnectionSettings(),
					getSchemaPrefix(), "memory");
			conn.openConnection(false);

			for (CoalesceEntity entity : entities) {
				if (persistEntityObject(entity, conn)) {

					isSuccessful = updateFileContent(entity, conn);

				}
			}

			conn.getConnection().commit();
		} catch (Exception e) {
			conn.rollback();

			throw new CoalescePersistorException("FlattenObject: "
					+ e.getMessage(), e);
		} finally {
			conn.close();
		}

		return isSuccessful;
	}

	@Override
	public DateTime getCoalesceObjectLastModified(String key, String objectType)
			throws CoalescePersistorException {
		try (CoalesceDataConnectorBase conn = this.getDataConnector()) {
			return this.getCoalesceObjectLastModified(key, objectType, conn);
		} catch (SQLException e) {
			throw new CoalescePersistorException(
					"GetCoalesceObjectLastModified", e);
		} catch (Exception e) {
			throw new CoalescePersistorException(
					"GetCoalesceObjectLastModified", e);
		}
	}

	@Override
	public byte[] getBinaryArray(String key) throws CoalescePersistorException {
		try (CoalesceDataConnectorBase conn = this.getDataConnector()) {

			byte[] binaryArray = null;

			ResultSet results = conn.executeQuery("SELECT BinaryObject FROM "
					+ getSchemaPrefix()
					+ "CoalesceFieldBinaryData WHERE ObjectKey=?",
					new CoalesceParameter(key, Types.OTHER));

			// Get Results
			if (results != null && results.first()) {
				Blob dataVal = results.getBlob("BinaryObject");
				binaryArray = dataVal.getBytes(1, (int) dataVal.length());
			}

			return binaryArray;
		} catch (SQLException e) {
			throw new CoalescePersistorException("GetBinaryArray", e);
		} catch (Exception e) {
			throw new CoalescePersistorException("GetBinaryArray", e);
		}
	}

	@Override
	public void saveTemplate(CoalesceDataConnectorBase conn,
			CoalesceEntityTemplate... templates)
			throws CoalescePersistorException {
		try {
			for (CoalesceEntityTemplate template : templates) {
				// Always persist template
				conn.executeProcedure("CoalesceEntityTemplate_InsertOrUpdate",
						new CoalesceParameter(UUID.randomUUID().toString(),
								Types.OTHER),
						new CoalesceParameter(template.getName()),
						new CoalesceParameter(template.getSource()),
						new CoalesceParameter(template.getVersion()),
						new CoalesceParameter(template.toXml()),
						new CoalesceParameter(JodaDateTimeHelper.nowInUtc()
								.toString(), Types.OTHER),
						new CoalesceParameter(JodaDateTimeHelper.nowInUtc()
								.toString(), Types.OTHER));
			}
		} catch (Exception e) {
			throw new CoalescePersistorException("PersistEntityTemplate", e);
		}

	}

	@Override
	public ElementMetaData getXPath(String key, String objectType)
			throws CoalescePersistorException {
		try (CoalesceDataConnectorBase conn = this.getDataConnector()) {
			return getXPathRecursive(key, objectType, "", conn);
		} catch (SQLException e) {
			throw new CoalescePersistorException("GetXPath", e);
		} catch (Exception e) {
			throw new CoalescePersistorException("GetXPath", e);
		}
	}

	@Override
	public String getFieldValue(String fieldKey)
			throws CoalescePersistorException {
		try (CoalesceDataConnectorBase conn = this.getDataConnector()) {
			String value = null;

			ResultSet results = conn.executeQuery("SELECT value FROM "
					+ getSchemaPrefix() + "CoalesceField WHERE ObjectKey =?",
					new CoalesceParameter(fieldKey, Types.OTHER));

			while (results.next()) {
				value = results.getString("value");
			}

			return value;
		} catch (SQLException e) {
			throw new CoalescePersistorException("GetFieldValue", e);
		} catch (Exception e) {
			throw new CoalescePersistorException("GetFieldValue", e);
		}
	}

	@Override
	public String[] getEntityXml(String... keys)
			throws CoalescePersistorException {
		try (CoalesceDataConnectorBase conn = this.getDataConnector()) {
			List<String> xmlList = new ArrayList<String>();
			List<CoalesceParameter> parameters = new ArrayList<CoalesceParameter>();

			StringBuilder sb = new StringBuilder("");

			for (String key : keys) {
				if (sb.length() > 0) {
					sb.append(",");
				}

				sb.append("?");
				parameters.add(new CoalesceParameter(key, Types.OTHER));
			}

			String SQL = String
					.format("SELECT EntityXml FROM %sCoalesceEntity WHERE ObjectKey IN (%s)",
							getSchemaPrefix(), sb.toString());

			ResultSet results = conn.executeQuery(SQL, parameters
					.toArray(new CoalesceParameter[parameters.size()]));

			while (results.next()) {
				xmlList.add(results.getString("EntityXml"));
			}

			return xmlList.toArray(new String[xmlList.size()]);
		} catch (SQLException e) {
			throw new CoalescePersistorException("GetEntityXml", e);
		} catch (Exception e) {
			throw new CoalescePersistorException("GetEntityXml", e);
		}
	}

	@Override
	public String getEntityXml(String entityId, String entityIdType)
			throws CoalescePersistorException {
		try (CoalesceDataConnectorBase conn = this.getDataConnector()) {
			String value = null;

			ResultSet results = conn.executeQuery("SELECT EntityXml FROM "
					+ getSchemaPrefix()
					+ "CoalesceEntity WHERE EntityId=? AND EntityIdType=?",
					new CoalesceParameter(entityId), new CoalesceParameter(
							entityIdType));

			while (results.next()) {
				value = results.getString("EntityXml");
			}

			return value;
		} catch (SQLException e) {
			throw new CoalescePersistorException("GetEntityXml", e);
		} catch (Exception e) {
			throw new CoalescePersistorException("GetEntityXml", e);
		}
	}

	@Override
	public String getEntityXml(String name, String entityId, String entityIdType)
			throws CoalescePersistorException {
		try (CoalesceDataConnectorBase conn = this.getDataConnector()) {
			String value = null;

			ResultSet results = conn
					.executeQuery(
							"SELECT EntityXml FROM "
									+ getSchemaPrefix()
									+ "CoalesceEntity WHERE Name=? AND EntityId=? AND EntityIdType=?",
							new CoalesceParameter(name), new CoalesceParameter(
									entityId), new CoalesceParameter(
									entityIdType));

			while (results.next()) {
				value = results.getString("EntityXml");
			}

			return value;
		} catch (SQLException e) {
			throw new CoalescePersistorException("GetEntityXml", e);
		} catch (Exception e) {
			throw new CoalescePersistorException("GetEntityXml", e);
		}
	}

	private DateTime getCoalesceObjectLastModified(String key,
			String objectType, CoalesceDataConnectorBase conn)
			throws SQLException {
		DateTime lastModified = null;

		// Determine the Table Name
		String tableName = CoalesceTableHelper
				.getTableNameForObjectType(objectType);
		String dateValue = null;

		ResultSet results = conn.executeQuery("SELECT LastModified FROM "
				+ getSchemaPrefix() + tableName + " WHERE ObjectKey=?",
				new CoalesceParameter(key.trim(), Types.VARCHAR));
		ResultSetMetaData resultsmd = results.getMetaData();

		// JODA Function DateTimeFormat will adjust for the Server timezone when
		// converting the time.
		if (resultsmd.getColumnCount() <= 1) {
			while (results.next()) {
				dateValue = results.getString("LastModified");
				if (dateValue != null) {
					lastModified = JodaDateTimeHelper
							.getPostGresDateTim(dateValue);
				}
			}
		}
		return lastModified;

	}

	/**
	 * Sets the active Coalesce field objects matching the parameters given.
	 *
	 * @param coalesceObject
	 *            the Coalesce field object.
	 * @param conn
	 *            is the PostGresDataConnector database connection
	 * @throws SQLException
	 *             ,Exception,CoalescePersistorException
	 */
	protected boolean updateFileContent(CoalesceObject coalesceObject,
			CoalesceDataConnectorBase conn) throws SQLException {
		boolean isSuccessful = false;

		if (!coalesceObject.isMarkedDeleted()) {
			if (coalesceObject.getType().toLowerCase() == "field") {
				if (((CoalesceField<?>) coalesceObject).getDataType() == ECoalesceFieldDataTypes.FILE_TYPE) {
					isSuccessful = persistFieldObject(
							(CoalesceField<?>) coalesceObject, conn);
				}
			}

			for (Map.Entry<String, CoalesceObject> s : coalesceObject
					.getChildCoalesceObjects().entrySet()) {
				isSuccessful = updateFileContent(s.getValue(), conn);
			}
		}
		return isSuccessful;
	}

	private ElementMetaData getXPathRecursive(String key, String objectType,
			String xPath, CoalesceDataConnectorBase conn) throws SQLException {

		boolean isEntityTable = false;
		ElementMetaData meteData = null;

		String sql = "";

		// Get Table Name
		String tableName = CoalesceTableHelper
				.getTableNameForObjectType(objectType);

		// Check to see if its the Entity Table
		if (tableName.equals("CoalesceEntity")) {
			isEntityTable = true;
		}

		if (isEntityTable) {
			sql = "SELECT name FROM ".concat(getSchemaPrefix())
					.concat(tableName).concat(" WHERE ObjectKey=?");
		} else {
			sql = "SELECT name, ParentKey, ParentType FROM "
					.concat(getSchemaPrefix()).concat(tableName)
					.concat(" WHERE ObjectKey=?");
		}

		ResultSet results = conn.executeQuery(sql,
				new CoalesceParameter(key.trim(), Types.OTHER));

		// Valid Results?
		while (results.next()) {

			String name = results.getString("name");

			if (isEntityTable) {
				xPath = name + "/" + xPath;

				// Set Meta Data
				meteData = new ElementMetaData(key, xPath);
			} else {
				String parentKey = results.getString("ParentKey");
				String parentType = results.getString("ParentType");

				if (xPath == null || xPath == "") {
					meteData = getXPathRecursive(parentKey, parentType, name,
							conn);
				} else {
					meteData = getXPathRecursive(parentKey, parentType, name
							+ "/" + xPath, conn);
				}
			}

		}

		return meteData;

	}

	@Override
	public String getEntityTemplateKey(String name, String source,
			String version) throws CoalescePersistorException {
		try (CoalesceDataConnectorBase conn = this.getDataConnector()) {
			String value = null;

			ResultSet results = conn
					.executeQuery(
							"SELECT TemplateKey FROM "
									+ getSchemaPrefix()
									+ "CoalesceEntityTemplate WHERE Name=? and Source=? and Version=?",
							new CoalesceParameter(name), new CoalesceParameter(
									source), new CoalesceParameter(version));

			while (results.next()) {
				value = results.getString("TemplateKey");
			}

			return value;
		} catch (SQLException e) {
			throw new CoalescePersistorException("GetEntityTemplateKey", e);
		} catch (Exception e) {
			throw new CoalescePersistorException("GetEntityTemplateKey", e);
		}
	}

	@Override
	public List<ObjectMetaData> getEntityTemplateMetadata()
			throws CoalescePersistorException {
		try (CoalesceDataConnectorBase conn = this.getDataConnector()) {
			return conn.getTemplateMetaData("SELECT * FROM "
					+ getSchemaPrefix() + "CoalesceEntityTemplate");
		} catch (Exception ex) {
			throw new CoalescePersistorException("getEntityTemplateMetadata",
					ex);
		}
	}

	@Override
	public String getEntityTemplateXml(String key)
			throws CoalescePersistorException {
		try (CoalesceDataConnectorBase conn = this.getDataConnector()) {
			String value = null;

			ResultSet results = conn.executeQuery("SELECT TemplateXml FROM "
					+ getSchemaPrefix()
					+ "CoalesceEntityTemplate WHERE TemplateKey=?",
					new CoalesceParameter(key, Types.OTHER));

			while (results.next()) {
				value = results.getString("TemplateXml");
			}

			return value;
		} catch (SQLException e) {
			throw new CoalescePersistorException("GetEntityTemplateXml", e);
		} catch (Exception e) {
			throw new CoalescePersistorException("GetEntityTemplateXml", e);
		}
	}

	@Override
	public String getEntityTemplateXml(String name, String source,
			String version) throws CoalescePersistorException {
		try (CoalesceDataConnectorBase conn = this.getDataConnector()) {
			String value = null;

			ResultSet results = conn
					.executeQuery(
							"SELECT TemplateXml FROM "
									+ getSchemaPrefix()
									+ "CoalesceEntityTemplate WHERE Name=? and Source=? and Version=?",
							new CoalesceParameter(name), new CoalesceParameter(
									source), new CoalesceParameter(version));

			while (results.next()) {
				value = results.getString("TemplateXml");
			}

			return value;
		} catch (SQLException e) {
			throw new CoalescePersistorException("GetEntityTemplateXml", e);
		} catch (Exception e) {
			throw new CoalescePersistorException("GetEntityTemplateXml", e);
		}
	}

}
