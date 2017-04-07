package com.incadencecorp.coalesce.framework.persistance.derby;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceLinkage;
import com.incadencecorp.coalesce.framework.persistance.CoalesceDataConnectorBase;
import com.incadencecorp.coalesce.framework.persistance.ServerConn;

public class DerbyDataConnector extends CoalesceDataConnectorBase {
	private static String protocol = "jdbc";
	private static String databaseDriver = "derby";
	private static final String DIRECTORY = "directory";
	private static final String MEMORY = "memory";
	private static final String CLASSPATH = "classpath";
	private static final String JAR = "jar";
	private static final String CLIENT_DRIVER = "org.apache.derby.jdbc.ClientDriver";
	private static final String EMBEDDED_DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
	private static final Logger LOGGER = LoggerFactory.getLogger(DerbyDataConnector.class);

	private String _prefix = "";
	private boolean isEmbedded;
	private String subSubProtocol;

	public final static String DERBY_OBJECT_ALREADY_EXISTS_SQL_STATE = "X0Y32";
	
	/**
	 * Note: Derby stored procedures cannot handle long XML types. So, we will
	 * NOT use stored procedures in the derby persistor for the base tables.
	 * 
	 * I will keep this here temporarily as an example for template registration
	 * creation of stored procedures.
	 * 
	 * @param ivarobjectkey
	 * @param ivarname
	 * @param ivarsource
	 * @param ivarversion
	 * @param ivarentityid
	 * @param ivarentityidtype
	 * @param ivarentityxml
	 * @param ivardatecreated
	 * @param ivarlastmodified
	 * @throws SQLException
	 */
	public boolean coalesceEntity_InsertOrUpdate(String ivarobjectkey,
			String ivarname, String ivarsource, String ivarversion,
			String ivarentityid, String ivarentityidtype, String ivarentityxml,
			DateTime ivardatecreated, DateTime ivarlastmodified)
			throws SQLException {

		DateTimeFormatter fmt = DateTimeFormat
				.forPattern("yyyy-MM-dd HH:mm:ss");
		String dateCreated = fmt.print(ivardatecreated);
		String lastModified = fmt.print(ivarlastmodified);

		// get connection, insert or update
		Connection conn = this.getConnection();

		// prepare the query
		Statement stmt = conn.createStatement();
		ResultSet result = null;
		result = stmt
				.executeQuery("select name from coalesce.coalesceentity where objectkey='"
						+ ivarobjectkey + "'");
		if (!result.next()) {
			// insert
			Statement stmt2 = conn.createStatement();
			StringBuilder sql = new StringBuilder(
					"insert into coalesce.coalesceentity (objectkey, name, source, version, entityid,")
					.append("entityidtype, entityxml, datecreated, lastmodified) values ('")
					.append(ivarobjectkey).append("','").append(ivarname)
					.append("','").append(ivarsource).append("','")
					.append(ivarversion).append("','").append(ivarentityid)
					.append("','").append(ivarentityidtype).append("','")
					.append(ivarentityxml).append("','").append(dateCreated)
					.append("','").append(lastModified).append("')");
			stmt2.executeUpdate(sql.toString());
		} else {
			// update
			Statement stmt3 = conn.createStatement();
			StringBuilder sql = new StringBuilder(
					"update coalesce.coalescentity set name = ")
					.append(ivarname).append(", source = ").append(ivarsource)
					.append(", version = ").append(ivarversion)
					.append(", entityid = ").append(ivarentityid).append(",")
					.append("entityidtype = ").append(", entityxml=")
					.append(ivarentityxml).append(", datecreated = ")
					.append(ivardatecreated).append(", lastmodified=")
					.append(ivarlastmodified).append(" where objectkey=")
					.append(ivarobjectkey);
			stmt3.executeUpdate(sql.toString());
		}
		return true;
	}

	public boolean coalesceLinkage_InsertOrUpdate(CoalesceLinkage linkage)
			throws SQLException {
		// get connection, insert or update
		Connection conn = this.getConnection();

		String sql = null;

		// prepare the query
		PreparedStatement pstmt = conn.prepareStatement(sql);
		ResultSet result = null;

		Statement stmt = conn.createStatement();
		result = stmt
				.executeQuery("select name from coalesce.coalescelinkage where objectkey='"
						+ linkage.getKey() + "'");
		if (!result.next()) {
			// insert
			StringBuilder sql2 = new StringBuilder(
					"insert into coalesce.coalescelinkage (ObjectKey,")
					.append("Name,Entity1Key,Entity1Name,Entity1Source,Entity1Version,LinkType,")
					.append("LinkLabel,LinkStatus,Entity2Key,Entity2Name,Entity2Source,Entity2Version,")
					.append("ClassificationMarking,ModifiedBy,InputLanguage,ParentKey,ParentType,DateCreated,")
					.append("LastModified) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			PreparedStatement stmt2 = conn.prepareStatement(sql2.toString());
			stmt2.setString(1, linkage.getKey());
			stmt2.setString(2, linkage.getName());
			stmt2.setString(3, linkage.getEntity1Key());
			stmt2.setString(4, linkage.getEntity1Name());
			stmt2.setString(5, linkage.getEntity1Source());
			stmt2.setString(6, linkage.getEntity1Version());
			stmt2.setString(7, linkage.getLinkType().getLabel());
			stmt2.setString(8, linkage.getLabel());
			stmt2.setString(9, linkage.getStatus().toString());
			stmt2.setString(10, linkage.getEntity2Key());
			stmt2.setString(11, linkage.getEntity2Name());
			stmt2.setString(12, linkage.getEntity2Source());
			stmt2.setString(13, linkage.getEntity2Version());
			stmt2.setString(14, linkage.getClassificationMarking().toString());
			stmt2.setString(15, linkage.getModifiedBy());
			stmt2.setString(16, linkage.getParent().getKey());
			stmt2.setString(17, linkage.getParent().getType());
			stmt2.setString(18, linkage.getDateCreated().toString());
			stmt2.setString(19, linkage.getLastModified().toString());
			stmt2.executeUpdate(sql.toString());
		} else {
			// update
			StringBuilder sql3 = new StringBuilder(
					"update coalesce.coalescelinkage set Name=?,")
					.append("Entity1Key=?,Entity1Name=?,Entity1Source=?,Entity1Version=?,LinkType=?,")
					.append("LinkLabel=?,LinkStatus=?,Entity2Key=?,Entity2Name=?,Entity2Source=?,Entity2Version=?,")
					.append("ClassificationMarking=?,ModifiedBy=?,InputLanguage=?,ParentKey=?,ParentType=?,DateCreated=?,")
					.append("LastModified=? where ObjectKey=?");

			PreparedStatement stmt3 = conn.prepareStatement(sql3.toString());
			stmt3.setString(1, linkage.getName());
			stmt3.setString(2, linkage.getEntity1Key());
			stmt3.setString(3, linkage.getEntity1Name());
			stmt3.setString(4, linkage.getEntity1Source());
			stmt3.setString(5, linkage.getEntity1Version());
			stmt3.setString(6, linkage.getLinkType().getLabel());
			stmt3.setString(7, linkage.getLabel());
			stmt3.setString(8, linkage.getStatus().toString());
			stmt3.setString(9, linkage.getEntity2Key());
			stmt3.setString(10, linkage.getEntity2Name());
			stmt3.setString(11, linkage.getEntity2Source());
			stmt3.setString(12, linkage.getEntity2Version());
			stmt3.setString(13, linkage.getClassificationMarking().toString());
			stmt3.setString(14, linkage.getModifiedBy());
			stmt3.setString(15, linkage.getParent().getKey());
			stmt3.setString(16, linkage.getParent().getType());
			stmt3.setString(17, linkage.getDateCreated().toString());
			stmt3.setString(18, linkage.getLastModified().toString());
			stmt3.setString(19, linkage.getKey());

			stmt3.executeUpdate(sql.toString());
		}

		return true;
	}

	public static ServerConn getServerConnection() {
		ServerConn serCon = new ServerConn();
		// InCadence Settings
		serCon.setServerName("127.0.0.1");
		serCon.setDatabase("CoalesceDatabase");
		serCon.setUser("CoalesceUser");
		serCon.setPassword("Passw0rd");
		return serCon;
	}

	private static boolean tableAlreadyExists(SQLException sqlException) {
		boolean exists;
		if (sqlException.getSQLState().equals(
				DERBY_OBJECT_ALREADY_EXISTS_SQL_STATE)) {
			exists = true;
		} else {
			exists = false;
		}
		return exists;
	}

	/**
	 * This constructor is for the Derby Embedded Option
	 * 
	 * @param prefix
	 * @param subSubProtocol
	 *            one of "directory", "memory", "classpath", "jar"
	 * @throws CoalescePersistorException
	 */
	public DerbyDataConnector(String databaseName, String procedurePrefix,
			String subSubProtocol) throws CoalescePersistorException {
		try {
			ServerConn serverConnection = new ServerConn();
			serverConnection.setDatabase(databaseName);
			setSettings(serverConnection);
			_prefix = procedurePrefix == null ? "" : procedurePrefix;
			isEmbedded = true;
			setSubSubProtocol(subSubProtocol);

			Class.forName(EMBEDDED_DRIVER);

			// note: if the protocol is memory, the coalesce database needs to
			// be created
			if (subSubProtocol.equals(MEMORY)) {
				this.createTables();
			}
		} catch (ClassNotFoundException | SQLException e) {
			throw new CoalescePersistorException("CoalesceDataConnector", e);
		}
	}

	public DerbyDataConnector(ServerConn settings, String procedurePrefix,
			String subSubProtocol) throws CoalescePersistorException {
		try {
			setSettings(settings);
			_prefix = procedurePrefix == null ? "" : procedurePrefix;
			isEmbedded = false;
			setSubSubProtocol(subSubProtocol);

			Class.forName(CLIENT_DRIVER);

			// note: if the protocol is memory, the coalesce database needs to
			// be created
			if (subSubProtocol.equals(MEMORY)) {
				// need to create the tables
				this.createTables();
			}
		} catch (ClassNotFoundException | SQLException e) {
			throw new CoalescePersistorException("CoalesceDataConnector", e);
		}

	}

	private void setSubSubProtocol(String subSubProtocol)
			throws CoalescePersistorException {
		switch (subSubProtocol) {
		case DIRECTORY:
		case MEMORY:
		case CLASSPATH:
		case JAR: {
			this.subSubProtocol = subSubProtocol;
			break;
		}
		default:
			throw new CoalescePersistorException(
					"ERROR: Unknown SubSubProtocol!");
		}
	}

	@Override
	public Connection getDBConnection() throws SQLException {
		if (_conn == null || (_conn != null && _conn.isClosed())) {
			StringBuilder urlBuilder = new StringBuilder(protocol).append(":")
					.append(databaseDriver).append(":");
			urlBuilder.append(subSubProtocol).append(":");

			if (isEmbedded) {
				urlBuilder.append(getSettings().getDatabase());
			} else {
				urlBuilder.append("//")
						.append(getSettings().getServerNameWithPort())
						.append("/").append(getSettings().getDatabase());
			}

			// if a memory connection, assume you have to create the database
			if (subSubProtocol.equals(MEMORY)) {
				urlBuilder.append(";create=true");
			}

			_conn = DriverManager.getConnection(urlBuilder.toString(),
					getSettings().getProperties());
		}
		return _conn;
	}

	@Override
	protected String getProcedurePrefix() {
		return "call " + _prefix;
	}

	private void createTables() throws SQLException {

		// need to create the database and tables
		Connection conn = this.getDBConnection();
		try {
			Statement stmt = null;
			stmt = conn.createStatement();
			stmt.execute("CREATE TABLE coalesce.CoalesceEntity"
					+ "( ObjectKey VARCHAR(256) NOT NULL,"
					+ " Name VARCHAR(256)," + " Source VARCHAR(256), "
					+ " Version VARCHAR(256)," + " EntityId VARCHAR(256),"
					+ " EntityIdType VARCHAR(256),"
					+ " EntityXml LONG VARCHAR," + " DateCreated timestamp,"
					+ " LastModified timestamp," + " title VARCHAR(256),"
					+ " deleted boolean DEFAULT false,"
					+ " securitylow bigint DEFAULT 0,"
					+ " securityhigh bigint DEFAULT 0,"
					+ " scope VARCHAR(256)," + " creator VARCHAR(256),"
					+ " type VARCHAR(256),"
					+ " CONSTRAINT CoalesceEntity_pkey PRIMARY KEY (ObjectKey)"
					+ ")");

			LOGGER.debug("CoalesceEntity Table created");
			stmt.close();
		} catch (SQLException e) {
			if (!DerbyDataConnector.tableAlreadyExists(e)) {
				throw e;
			}
		}

		try {
			Statement stmt = conn.createStatement();
			stmt.execute("CREATE TABLE coalesce.CoalesceEntityTemplate "
					+ "( "
					+ " TemplateKey VARCHAR(128) NOT NULL, "
					+ " Name VARCHAR(128), "
					+ " Source VARCHAR(256), "
					+ " Version VARCHAR(128), "
					+ " TemplateXml LONG VARCHAR, "
					+ " DateCreated timestamp, "
					+ " LastModified timestamp, "
					+ "  CONSTRAINT CoalesceEntityTemplate_pkey PRIMARY KEY (TemplateKey) "
					+ ")");
			LOGGER.debug("Coalesce Entity Template Table created");
			stmt.close();
		} catch (SQLException e) {
			if (!DerbyDataConnector.tableAlreadyExists(e)) {
				throw e;
			}
		}
		try {
			Statement stmt = conn.createStatement();
			stmt.execute("CREATE TABLE coalesce.CoalesceLinkage "
					+ "( "
					+ " ObjectKey VARCHAR(128) NOT NULL,"
					+ " Name VARCHAR(256),"
					+ " Entity1Key VARCHAR(128), "
					+ " Entity1Name VARCHAR(256),"
					+ " Entity1Source VARCHAR(128),"
					+ " Entity1Version VARCHAR(64),"
					+ " LinkType VARCHAR(128),"
					+ " LinkLabel VARCHAR(256),"
					+ " LinkStatus VARCHAR(128),"
					+ " Entity2Key VARCHAR(128),"
					+ " Entity2Name VARCHAR(128),"
					+ " Entity2Source VARCHAR(128),"
					+ " Entity2Version VARCHAR(64),"
					+ " ClassificationMarking VARCHAR(128),"
					+ " ModifiedBy VARCHAR(128),"
					+ " InputLanguage VARCHAR(128),"
					+ " ParentKey VARCHAR(128),"
					+ " ParentType VARCHAR(64),"
					+ " DateCreated timestamp,"
					+ " LastModified timestamp,"
					+ " CONSTRAINT CoalesceLinkage_pkey PRIMARY KEY (ObjectKey)"
					+ ")");
			LOGGER.debug("Coalesce Linkage Table created");
			stmt.close();

		} catch (SQLException e) {
			if (!DerbyDataConnector.tableAlreadyExists(e)) {
				throw e;
			}
		}
	}

}
