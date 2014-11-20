package com.proto.irems.services;

//import java.util.Date;
//import java.text.DateFormat;
//
//import javax.jws.WebMethod;
//import javax.jws.WebParam;
//import javax.jws.WebResult;
import javax.jws.WebService;

import org.springframework.beans.BeansException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.incadencecorp.coalesce.framework.persistance.mysql.MySQLPersistor;
//import javax.jws.soap.SOAPBinding;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.context.support.SpringBeanAutowiringSupport;

@WebService(endpointInterface="com.proto.irems.services.ICoalesceDataService",
		portName="coalesceDataServicePort",serviceName="coalesceDataService")
//@BindingType(value="http://www.w3.org/2003/05/soap/bindings/HTTP/")
public class CoalesceImpl 
{
    //com.incadencecorp.coalesce.framework.persistance.mysql.MySQLPersistor
    MySQLPersistor mSQLP;
	
	/**
	 * @return the mSQLP
	 */
	public MySQLPersistor getmSQLP() {
		/*******************************************************************************************************************************
		 * 	This setter will allow me to handle stateless bean management.
		 * 	Need to use the ClassPathXmlApplicationContext
		 *******************************************************************************************************************************/
		ClassPathXmlApplicationContext appContext=null;
		try {
			if(this.mSQLP==null){
				appContext=new ClassPathXmlApplicationContext("classpath*:/WEB-INF/dataserviceServlet-servlet.xml");
				if(appContext.containsBean("mysqlPersistor")){
					MySQLPersistor mSQLPr=appContext.getBean("mysqlPersistor",MySQLPersistor.class);
					this.mSQLP=mSQLPr;
				}
			}
			return mSQLP;
		} catch (BeansException e) {
			e.printStackTrace();
		}
		finally{
			if(appContext!=null)
				appContext.close();
				
		}
		return null;
	}


	/**
	 * @param mSQLP the mSQLP to set
	 */
	public void setmSQLP(MySQLPersistor mSQLP) {
		this.mSQLP = mSQLP;
	}


	public boolean setEntity(String entityXML){
		return this.getmSQLP().setEntity(entityXML );
		
	}
	public String getEntity(String Key) {
		return getmSQLP().getEntityXml(Key);
	}


	public String[] getEntityKeys(String EntityId,
			String EntityIdType) {
		return getmSQLP().getEntityKeys(EntityId,EntityIdType);
	}


	public String getEntityByName(String Name
			,String EntityId,
			String EntityIdType) {
		return getmSQLP().getEntityXmlNameIdType(Name, EntityId,EntityIdType);

	}


	public String getEntityXML(String Key) {
		return getmSQLP().getEntityXml(Key);
	}


	public String[] getEntityXMLKeys(String EntityId, 
			String EntityIdType) {
		return getmSQLP().getEntityKeys(EntityId,EntityIdType);
	}


	public String getEntityXMLByName(String Name,
			String EntityId,
			String EntityIdType) {
		return getmSQLP().getEntityXmlNameIdType(Name, EntityId,EntityIdType);
	}


	public String getFieldValue( String FieldKey) {
		return getmSQLP().getFieldValue(FieldKey);
	}


	public String getXPath(String Key, String ObjectType, String EntityKey){
		return null;//getmSQLP().getXPath(Key, ObjectType, EntityKey,  XPath);
	}



}
