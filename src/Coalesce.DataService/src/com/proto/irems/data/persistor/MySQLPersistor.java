package com.proto.irems.data.persistor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.springframework.stereotype.Repository;

import unity.common.CallResult;
import unity.common.CallResult.CallResults;
import Coalesce.Framework.Persistance.CoalesceTable;

import com.proto.irems.services.XMLRecord;
import com.proto.irems.services.monitor.GeneralServices;

@Repository("iMySQLPersistor")
public class MySQLPersistor implements IMySQLPersistor 
{
	GeneralServices wsStatus;
	/**
	 * @return the wsStatus
	 */
	public GeneralServices getWsStatus() {
		return wsStatus;
	}

	/**
	 * @param wsStatus the wsStatus to set
	 */
	public void setWsStatus(GeneralServices wsStatus) {
		this.wsStatus = wsStatus;
	}




	String DATASOURCE_CONTEXT=null;

	/**
	 * @return the dATASOURCE_CONTEXT
	 */
	public String getDATASOURCE_CONTEXT() {
		return DATASOURCE_CONTEXT;
	}

	/**
	 * @param datasourceContext the dATASOURCE_CONTEXT to set
	 */
	public void setDATASOURCE_CONTEXT(String datasourceContext) {
		DATASOURCE_CONTEXT = datasourceContext;
	}

	public MySQLPersistor() {
		/******************** Lets see what the current start state is *************************************************/
		//wsStatus.setServiceEnabled(wsStatus.getMonitorState());
		//getWsStatus().getMonitorState();
	}

	/* (non-Javadoc)
	 * @see com.proto.irems.data.persistor.IMySQLPersistor#getEntityXml(java.lang.String)
	 */
	@Override
	public String getEntityXml(String Key) {
		String crst=null;
		getWsStatus().getServiceState();
		try {
			if (getWsStatus().getServiceStatus()) {
				String sqlStmt = "SELECT EntityXml from CoalesceEntity WHERE ObjectKey=?";
				ArrayList<String> params = new ArrayList<>();
				params.add(Key.trim());		
				crst=getXmlSQL(sqlStmt, params);
			}
			return crst;
		} catch (Exception ex) {
			CallResult.log(CallResults.FAILED_ERROR, ex, "getEntityXml");
			return crst;
		}
	}
	
	public Boolean isDupEntity(String Key) {
		Boolean crst=false;
		getWsStatus().getServiceState();
		try {
			if (getWsStatus().getServiceStatus()) {
				String sqlStmt="select count(*) from trse.coalesceentity where ObjectKey=?";
				ArrayList<String> params = new ArrayList<>();
				params.add(Key.trim());		
				int iCntr = 0;
				iCntr=getSQL(sqlStmt, params);
				if(iCntr>=1)
					return crst;
				else
				{
					crst=true;
					return crst;
				}
					
			}
			return crst;
		} catch (Exception ex) {
			CallResult.log(CallResults.FAILED_ERROR, ex, "getEntityXml");
			return crst;
		}
	}

	/* (non-Javadoc)
	 * @see com.proto.irems.data.persistor.IMySQLPersistor#getEntityXmlNameIdType(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public String getEntityXmlNameIdType(String Name, String EntityId,
			String EntityIdType) {
		String crst=null;
		getWsStatus().getServiceState();
		try {
			if (getWsStatus().getServiceStatus()) {
				String sqlStmt = "SELECT EntityXml from CoalesceEntity WHERE Name=? AND EntityId=?";
				sqlStmt += " AND EntityIdType=?";
				ArrayList<String> params = new ArrayList<>();
				params.add(Name.trim());
				params.add(EntityId.trim());
				params.add(EntityIdType.trim());
				crst=getXmlSQL(sqlStmt, params);
			}
			return crst;
		} catch (Exception ex) {
			CallResult.log(CallResults.FAILED_ERROR, ex, "getEntityXmlNameIdType");
			return crst;
		}
	}

	private String getXmlSQL(String sqlStmt, ArrayList<String> sqlParams) {
		String crst = null;
		Context ctx = null;
		Connection conn = null;
		Statement stx = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup(DATASOURCE_CONTEXT);
			conn = ds.getConnection();
			stx = conn.createStatement();
			java.sql.PreparedStatement sql;

			sql = conn.prepareStatement(sqlStmt);
			Object[] pRams = sqlParams.toArray();

			for (int iSetter = 0; iSetter < sqlParams.size(); iSetter++) {
				sql.setString(iSetter + 1, pRams[iSetter].toString());
			}

			ResultSet srs = sql.executeQuery();
			ResultSetMetaData rsmd = srs.getMetaData();
			if (rsmd.getColumnCount() <= 1) {
				while (srs.next()) {
					crst = srs.getString("EntityXml");
				}
				if(crst!=null){
					//String results = org.apache.commons.lang.StringEscapeUtils.escapeJava(rst);
					byte[] results=crst.getBytes();
					String rsts=GeneralServices.convertXMLfromMySQLtoString(results);
					return rsts;
				}
			}
			return crst;
		} catch (SQLException ex) {
			CallResult.log(CallResults.FAILED_ERROR, ex, "getXmlSQL");
			return crst;
		} catch (Exception ex) {
			CallResult.log(CallResults.FAILED_ERROR, ex, "getXmlSQL");
			return crst;
		} finally {
			try {
				stx.close();
				conn.close();
			} catch (SQLException ex) {
				CallResult.log(CallResults.FAILED_ERROR, ex, "getXmlSQL");
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.proto.irems.data.persistor.IMySQLPersistor#getEntityKeys(java.lang.String, java.lang.String)
	 */
	@Override
	public String[] getEntityKeys(String EntityId, String EntityIdType) {
		String[] crst=null;
		getWsStatus().getServiceState();
		try {
			if (getWsStatus().getServiceStatus()) {
				String sqlStmt = "SELECT ObjectKey from CoalesceEntity WHERE EntityId=?";
				sqlStmt += " AND EntityIdType=?";
				ArrayList<String> params = new ArrayList<>();
				params.add(EntityId.trim());
				params.add(EntityIdType.trim());
				crst=getEntityKeysFromSQL(sqlStmt, params);		
			}
			return crst;
		} catch (Exception ex) {
			CallResult.log(CallResults.FAILED_ERROR, ex, "getEntityKeys");
			return  crst;
		}
	}

	private String[] getEntityKeysFromSQL(String sqlStmt,
		ArrayList<String> sqlParams) {
		String[] crst = null;
		Context ctx = null;
		Connection conn = null;
		Statement stx = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup(DATASOURCE_CONTEXT);
			conn = ds.getConnection();
			stx = conn.createStatement();
			java.sql.PreparedStatement sql;
			sql = conn.prepareStatement(sqlStmt);
			Object[] pRams = sqlParams.toArray();
			for (int iSetter = 0; iSetter < sqlParams.size(); iSetter++) {
				sql.setString(iSetter + 1, pRams[iSetter].toString());
			}
			ResultSet srs = sql.executeQuery();
			ResultSetMetaData rsmd = srs.getMetaData();
			int rowCount = 0;
			int rstCntr = 0;
			if (rsmd.getColumnCount() <= 1) {
				if (srs.last()) {
					rowCount = srs.getRow();
					crst = new String[rowCount];
					srs.beforeFirst();
				}
				while (srs.next()) {
					crst[rstCntr]=srs.getString("ObjectKey");
					rstCntr++;
				}
			}
			return crst;
		} catch (SQLException ex) {
			CallResult.log(CallResults.FAILED_ERROR, ex, "getEntityKeysFromSQL");
			return  crst;
		} catch (Exception ex) {
			CallResult.log(CallResults.FAILED_ERROR, ex, "getEntityKeysFromSQL");
			return  crst;
		} finally {
			try {
				stx.close();
				conn.close();
			} catch (SQLException ex) {
				CallResult.log(CallResults.FAILED_ERROR, ex, "getEntityKeysFromSQL");
			}
		}
	}
	//region
	/* (non-Javadoc)
	 * @see com.proto.irems.data.persistor.IMySQLPersistor#getFieldValue(java.lang.String)
	 */
	//@Override
	public String getFieldValue(String FieldKey) {
		String crst = null;
		getWsStatus().getServiceState();
		try {
			if (getWsStatus().getServiceStatus()) {
				String sqlStmt = "SELECT value FROM CoalesceField WHERE ObjectKey =?";
				ArrayList<String> params = new ArrayList<>();
				params.add(FieldKey.trim());
				/* NOT USING FIELDKEY LIKE IN THE DOTNET CODE, TALK TO DEREK */
				// params.add(FieldKey.trim());
				crst=getSQLValue(sqlStmt, params);
			}
			return crst;
		} catch (Exception ex) {
			CallResult.log(CallResults.FAILED_ERROR, ex, "getFieldValue");
			return crst;
		}
	}
	//endregion
	private String getSQLValue(String sqlStmt, ArrayList<String> sqlParams) {
		String crst = null;
		Context ctx = null;
		Connection conn = null;
		Statement stx = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup(DATASOURCE_CONTEXT);
			conn = ds.getConnection();
			stx = conn.createStatement();
			java.sql.PreparedStatement sql;
			sql = conn.prepareStatement(sqlStmt);
			Object[] pRams = sqlParams.toArray();

			for (int iSetter = 0; iSetter < sqlParams.size(); iSetter++) {
				sql.setString(iSetter + 1, pRams[iSetter].toString());
			}

			ResultSet srs = sql.executeQuery();
			ResultSetMetaData rsmd = srs.getMetaData();
			if (rsmd.getColumnCount() <= 1) {
				while (srs.next()) {
					crst = srs.getString("Value");
				}
			}
			return crst;
		} catch (SQLException ex) {
			CallResult.log(CallResults.FAILED_ERROR, ex, "getSQLValue");
			return crst;
		} catch (Exception ex) {
			CallResult.log(CallResults.FAILED_ERROR, ex, "getSQLValue");
			return crst;
		} finally {
			try {
				stx.close();
				conn.close();
			} catch (SQLException ex) {
				CallResult.log(CallResults.FAILED_ERROR, ex, "getSQLValue");
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.proto.irems.data.persistor.IMySQLPersistor#setFieldValue(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean setFieldValue(String key, String value) {
		boolean rst = false;
		getWsStatus().getServiceState();
		try {
			if (getWsStatus().getServiceStatus()) {
				String sqlStmt = "UPDATE coalescefield SET VALUE=? WHERE OBJECTKEY=?";

				ArrayList<String> params = new ArrayList<String>();
				params.add(value);
				params.add(key);
				rst = setSQLValue(sqlStmt, params);
			}
			return rst;
		} catch (Exception ex) {
			CallResult.log(CallResults.FAILED_ERROR, ex, "setFieldValue");
			return rst;
		}
	}

	private boolean setSQLValue(String sqlStmt, ArrayList<String> sqlParams) {
		boolean rst = false;
		Context ctx = null;
		Connection conn = null;
		Statement stx = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup(DATASOURCE_CONTEXT);
			conn = ds.getConnection();
			stx = conn.createStatement();
			java.sql.PreparedStatement sql = conn.prepareStatement(sqlStmt);
			sql.setString(1, sqlParams.get(0).toString());
			sql.setString(2, sqlParams.get(1).toString());
			sql.executeUpdate();
			rst = true;
			return rst;
		} catch (SQLException ex) {
			CallResult.log(CallResults.FAILED_ERROR, ex, "setSQLValue");
			return rst;
		} catch (Exception ex) {
			CallResult.log(CallResults.FAILED_ERROR, ex, "setSQLValue");
			return rst;
		} finally {
			try {
				stx.close();
				conn.close();
			} catch (SQLException ex) {
				CallResult.log(CallResults.FAILED_ERROR, ex, "setSQLValue");
			}
		}
	}

	private String getXPSQLValue(String sqlStmt, ArrayList<String> sqlParams,
			String XPath, boolean isEntityTable) {
		Context ctx = null;
		Connection conn = null;
		Statement stx = null;
		String rst=null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup(DATASOURCE_CONTEXT);
			conn = ds.getConnection();
			stx = conn.createStatement();
			java.sql.PreparedStatement sql = conn.prepareStatement(sqlStmt);
			sql = conn.prepareStatement(sqlStmt);
//			Object[] pRams = sqlParams.toArray();
//
//			for (int iSetter = 0; iSetter < sqlParams.size(); iSetter++) {
//				// sql.setString(iSetter+1, pRams[iSetter].toString());
//			}

			ResultSet srs = sql.executeQuery();
			ResultSetMetaData rsmd = srs.getMetaData();
			if (rsmd.getColumnCount() <= 1) {
				String p=null;
				while (srs.next()) {
					if (isEntityTable) {
						p=srs.getString("name");
					} else {
						p=srs.getString("name");
					}
				}
				rst=p;
			}
			return rst;
		} catch (SQLException ex) {
			CallResult.log(CallResults.FAILED_ERROR, ex, "getXPSQLValue");
			return rst;
		} catch (Exception ex) {
			CallResult.log(CallResults.FAILED_ERROR, ex, "getXPSQLValue");
			return rst;
		} finally {
			try {
				stx.close();
				conn.close();
			} catch (SQLException ex) {
				CallResult.log(CallResults.FAILED_ERROR, ex, "getXPSQLValue");
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.proto.irems.data.persistor.IMySQLPersistor#getXPath(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public String getXPath(String FieldKey, String ObjectKey, String EntityKey,
			String XPath) {

		String crst=null;
		boolean isEntityTable = false;
		getWsStatus().getServiceState();
		try {
			if (getWsStatus().getServiceStatus()) {
				String sqlStmt = "";
				String tableName = CoalesceTable
						.gettableNameForObjectType(ObjectKey.trim()
								.toLowerCase());
				if (tableName.toLowerCase().compareTo(
						"CoalesceEntity".toLowerCase()) == 0)
					isEntityTable = true;
				if (isEntityTable)
					sqlStmt = "SELECT name FROM ".concat(tableName).concat(
							" WHERE ObjectKey =?");
				else
					sqlStmt = "SELECT name, ParentKey, ParentType FROM "
							.concat(tableName).concat(" WHERE ObjectKey =?");
				ArrayList<String> params = new ArrayList<>();
				params.add(ObjectKey.trim());
				crst= getXPSQLValue(sqlStmt, params, XPath, isEntityTable);
			}
			return crst;
		} catch (Exception ex) {
			CallResult.log(CallResults.FAILED_ERROR, ex, "getXPSQLValue");
			return crst;
		}
	}
	//	The CoalesceEntity_InsertOrUpdate - cannot see the procedure source code but the name tells me what it does.
	//	All this logic should be at the DB layer.
	@Override
	public boolean setEntity(String entityXML) {
		boolean crst=false;
		XMLRecord p=new XMLRecord();
		p=GeneralServices.getXMLDoc(entityXML);
		try {
			//	Check to see if the entity is in the db, if so perform an update.
			if(isDupEntity(p.getObjectKey().toString())==false){
				if (getWsStatus().getServiceStatus()) {
					String sqlStmt = "UPDATE coalesceentity SET ENTITYXML=? WHERE OBJECTKEY=?";
					ArrayList<String> params = new ArrayList<String>();
					params.add(entityXML);
					params.add(p.getObjectKey().toString()); 
					crst=setEntitySQL(sqlStmt, params);
				}
			}else{
				//	add the record to the database
				if (getWsStatus().getServiceStatus()) {
//					Entity ent=new Entity();
//					ent=(Entity)XmlHelper.Deserialize(entityXML,ent);
					String sqlStmt = "insert into coalesceentity (ObjectKey,Name,Source,Version,EntityId,EntityIdType,EntityXml,Datecreated,LastModified) ";
					//sqlStmt+="VALUES (?,?,?,?,?,?,STR_TO_DATE(?,'%Y-%m-%dT%H:%i:%sZ')),STR_TO_DATE(?,'%Y-%m-%dT%H:%i:%sZ')),?)";
					sqlStmt+="VALUES (?,?,?,?,?,?,?,?,?)";
					ArrayList<String> params = new ArrayList<String>();
					params.add(p.getObjectKey().toString());
					params.add(p.getEntityName().toString());
					params.add(p.getEntitySource().toString());
					params.add(p.getEntityVersion().toString());
					params.add(p.getEntityID().toString());
					params.add(p.getEntityIDType().toString());
					params.add(entityXML);
					//Date utcDate = DateTimeHelper.FromXmlDateTimeUTC(entityDateCreated.getNodeValue());
					//String toXmlDate = DateTimeHelper.ToXmlDateTimeUTC(utcDate);
					String dtCreatreated=p.getEntityDateCreated().toString().replace("T", " ").replace("Z", "");
//					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//					java.util.Date dateStr = formatter.parse(newXmlDate);
					params.add(dtCreatreated);
					//Date utcDate2 = DateTimeHelper.FromXmlDateTimeUTC(entityLastModified.getNodeValue());
					//String toXmlDate2 = DateTimeHelper.ToXmlDateTimeUTC(utcDate);
//					String newXmlDate2=toXmlDate2.replace("T", " ");
//					SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//					java.util.Date dateStr2 = formatter2.parse(newXmlDate);
					String dtModified=p.getEntityLastModified().toString().replace("T", " ").replace("Z", "");
					params.add(dtModified);
					
					 
					crst=setEntitySQL(sqlStmt, params);
				}
			}
			return crst;
		} catch (Exception ex) {
			CallResult.log(CallResults.FAILED_ERROR, ex, "setEntity");
			return crst;
		}
	}
	
	private boolean setEntitySQL(String sqlStmt,  ArrayList<String> sqlParams){
		boolean crst=false;
		Context ctx = null;
		Connection conn = null;
		Statement stx = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup(DATASOURCE_CONTEXT);
			conn = ds.getConnection();
			stx = conn.createStatement();
			java.sql.PreparedStatement sql = conn.prepareStatement(sqlStmt);
			sql.setString(1, sqlParams.get(0));
			sql.setString(2, sqlParams.get(1)); 
			sql.setString(3, sqlParams.get(2));
			sql.setString(4, sqlParams.get(3)); 
			sql.setString(5, sqlParams.get(4));
			sql.setString(6, sqlParams.get(5)); 
			sql.setString(7, sqlParams.get(6)); 
			sql.setString(8, sqlParams.get(7));
			sql.setString(9, sqlParams.get(8));
			sql.executeUpdate();
			crst=true;
			return crst;
		} catch (SQLException ex) {
			CallResult.log(CallResults.FAILED_ERROR, ex, "setEntitySQL");
			return crst;
		} catch (Exception ex) {
			CallResult.log(CallResults.FAILED_ERROR, ex, "setEntitySQL");
			return crst;
		} finally {
			try {
				stx.close();
				conn.close();
			} catch (SQLException ex) {
				CallResult.log(CallResults.FAILED_ERROR, ex, "getXPSQLValue");
			}
		}
	}
	
	private int getSQL(String sqlStmt, ArrayList<String> sqlParams) {
		int crst = 0;
		Context ctx = null;
		Connection conn = null;
		Statement stx = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup(DATASOURCE_CONTEXT);
			conn = ds.getConnection();
			stx = conn.createStatement();
			java.sql.PreparedStatement sql;

			sql = conn.prepareStatement(sqlStmt);
			Object[] pRams = sqlParams.toArray();

			for (int iSetter = 0; iSetter < sqlParams.size(); iSetter++) {
				sql.setString(iSetter + 1, pRams[iSetter].toString());
			}

			ResultSet srs = sql.executeQuery();
			ResultSetMetaData rsmd = srs.getMetaData();
			if (rsmd.getColumnCount() <= 1) {
				while (srs.next()) {
					crst = srs.getInt("count(*)");
				}
			}
			return crst;
		} catch (SQLException ex) {
			CallResult.log(CallResults.FAILED_ERROR, ex, "getXmlSQL");
			return crst;
		} catch (Exception ex) {
			CallResult.log(CallResults.FAILED_ERROR, ex, "getXmlSQL");
			return crst;
		} finally {
			try {
				stx.close();
				conn.close();
			} catch (SQLException ex) {
				CallResult.log(CallResults.FAILED_ERROR, ex, "getXmlSQL");
			}
		}
	}
}
