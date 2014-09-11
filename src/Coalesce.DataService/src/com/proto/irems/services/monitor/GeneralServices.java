package com.proto.irems.services.monitor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.stereotype.Repository;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.SAXException;

import unity.core.runtime.CallResult;
import unity.core.runtime.CallResult.CallResults;

import com.proto.irems.services.XMLRecord;

@Repository("iCoalesceDataServiceMonitorDAO")
public class GeneralServices implements IGeneralServicesDAO{
	
	private  boolean serviceStatus;

	/**
	 * @return the serviceEnabled
	 */
	public  boolean getServiceStatus() {
		return this.serviceStatus;
	}

	/**
	 * @param serviceStatus
	 *            the serviceEnabled to set
	 */
	public  void setServiceStatus(boolean _serviceStatus) {
		this.serviceStatus = _serviceStatus;
	}

	String DATASOURCE_CONTEXT;





	public GeneralServices() {

	}
	public GeneralServices(String dataSourcecontext){
		this.DATASOURCE_CONTEXT =dataSourcecontext;
	}

	public boolean setMonitorState(boolean enabled_state) {
		boolean rst = false;

		try {
			String value;
			if(enabled_state)
				value="1";
			else
				value="0";
			String sqlStmt = "UPDATE trse.CoalesceDataServiceMonitor SET MONITOR_START_STATE=?";

			ArrayList<String> params = new ArrayList<String>();
			params.add(value);
			rst = setSQLValue(sqlStmt, params);
			return rst;
		} catch (Exception ex) {
			CallResult.log(CallResults.FAILED_ERROR, ex, "setSQLValue");
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

	public boolean getServiceState() {
		try {
			String sqlStmt = "select MONITOR_START_STATE from trse.CoalesceDataServiceMonitor";
			String sVal=getSQLValue(sqlStmt);
			if(sVal.equals("1"))
				this.serviceStatus=true;
			else
				this.serviceStatus=false;
			return this.serviceStatus;
		} catch (Exception ex) {
			CallResult.log(CallResults.FAILED_ERROR, ex, "getServiceState");
			return this.serviceStatus;
		}
	}

	private String getSQLValue(String sqlStmt) {
		String rst = null;
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
			ResultSet srs = sql.executeQuery();
			ResultSetMetaData rsmd = srs.getMetaData();
			if (rsmd.getColumnCount() <= 1) {
				while (srs.next()) {
					rst = srs.getNString(1);
				}
			}
			return rst;
		} catch (SQLException ex) {
			CallResult.log(CallResults.FAILED_ERROR, ex, "getSQLValue");
			return rst;
		} catch (Exception ex) {
			CallResult.log(CallResults.FAILED_ERROR, ex, "getSQLValue");
			return rst;
		} finally {
			try {
				stx.close();
				conn.close();
			} catch (SQLException ex) {
				CallResult.log(CallResults.FAILED_ERROR, ex, "getSQLValue");
			}
		}
	}

	@Override
	public CoalesceDataServiceMonitor get() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void set(CoalesceDataServiceMonitor coalesceDataServiceMonitor) {
		// TODO Auto-generated method stub
		
	}
	
	public static XMLRecord getXMLDoc(String entityXML){
		org.w3c.dom.Document doc;
		org.w3c.dom.Node entityName=null;
		org.w3c.dom.Node entitySource=null;
		org.w3c.dom.Node entityVersion=null;
		org.w3c.dom.Node entityID=null;
		org.w3c.dom.Node entityIDType=null;
		org.w3c.dom.Node entityDateCreated=null;
		org.w3c.dom.Node entityLastModified=null;
		org.w3c.dom.Node entityXMLSource=null;
		org.w3c.dom.Node entityObjectKey=null;
		try (ByteArrayInputStream bais = new ByteArrayInputStream(entityXML.getBytes())) {
			XMLRecord x=new XMLRecord();
		    doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(bais);
		    NamedNodeMap atts = doc.getDocumentElement().getAttributes();
		    x.setObjectKey(atts.getNamedItem("key"));
		    x.setEntityName(atts.getNamedItem("name"));
		    x.setEntitySource(atts.getNamedItem("source"));
		    x.setEntityVersion(atts.getNamedItem("version"));
		    x.setEntityID(atts.getNamedItem("entityid"));
		    x.setEntityIDType(atts.getNamedItem("entityidtype"));
		    x.setEntityDateCreated(atts.getNamedItem("datecreated"));
		    x.setEntityLastModified(atts.getNamedItem("lastmodified"));	
		    return x;
		} catch (ParserConfigurationException | SAXException | IOException ex) {
			CallResult.log(CallResults.FAILED_ERROR, ex, "setEntity");
			return null;
		}
	}
	/*********************************************************************************************************
	 * 
	 * 	XML Serializer for data that is a string of XML data in the MySQL Database, this will use a memorystream
	 *  to serialize the XML into a single string.
	 * 
	 * @param xml
	 * @return
	 */
	public static String convertXMLfromMySQLtoString(byte[] xml){
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance(); 
			InputStream cleanedXMLString = new ByteArrayInputStream( xml );
			org.w3c.dom.Document doc = documentBuilderFactory.newDocumentBuilder().parse(cleanedXMLString); 
			StringWriter stw = new StringWriter(); 
			Transformer serializer = TransformerFactory.newInstance().newTransformer(); 
			serializer.transform(new DOMSource(doc), new StreamResult(stw)); 
			return stw.toString();
		
		} catch (Exception ex) {
			ex.printStackTrace();
		} 
		return null;
	}
}
