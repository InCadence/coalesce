package com.persister.tests;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;

//import javax.lang.model.element.Element;

import unity.common.CallResult;
import unity.common.CallResult.CallResults;
import Coalesce.Common.Exceptions.CoalesceException;
import Coalesce.Common.Exceptions.CoalesceInvalidFieldException;
import Coalesce.Common.Helpers.XmlHelper;
import Coalesce.Framework.CoalesceFramework;
import Coalesce.Framework.DataModel.ECoalesceFieldDataTypes;
import Coalesce.Framework.DataModel.XsdEntity;
import Coalesce.Framework.DataModel.XsdFieldDefinition;
import Coalesce.Framework.DataModel.XsdLinkageSection;
import Coalesce.Framework.DataModel.XsdRecord;
import Coalesce.Framework.DataModel.XsdRecordset;
import Coalesce.Framework.DataModel.XsdSection;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import com.database.persister.PostGresSQLPersistor;
import com.database.persister.ServerConn;

public class appMain {

	private final static Logger log = Logger.getLogger("TesterLog");
	private static int ITERATION_LIMIT = 100;
	static ServerConn serCon;
	static PostGresSQLPersistor psPersister;
	private static CoalesceFramework _coalesceFramework;
	private static String MODULE_NAME = "Coalesce.Persister.PerformanceTester";
	static int minorVal = 0;
	static int majorVal = 0;
	static int masterCounter = 0;
	static String startSaveTimeStamp = "";
	static String completeSaveTimeStamp = "";
	private static List<TimeTrack> timeLogger;
	static Document dom;

	public static void main(String[] args) {
		System.out.println("Started .. ");
		appMain._coalesceFramework = new CoalesceFramework();
		try {
			if (OpenConnection() == true) {
				appMain._coalesceFramework.Initialize(psPersister);
				timeLogger = new ArrayList<TimeTrack>();
				appMain.createDocument();
				appMain.createDOMTree();
				runVolume();
				appMain.printToFile("d:/persistance.xml");

			}
		} catch (Exception ex) {
			log.log(java.util.logging.Level.SEVERE, ex.toString());
		}
	}

	public static boolean OpenConnection() throws SQLException {
		serCon = new ServerConn();
		serCon.setURL("jdbc:postgresql://localhost/CoalesceDatabase");
		serCon.setUser("root");
		serCon.setPassword("Passw0rd");
		psPersister = new PostGresSQLPersistor();
		psPersister.Initialize(serCon);
		return true;
	}

	private static void runVolume() {
		try {
			String val = ".0.0";
			TimeTrack _timeTrack = new TimeTrack();
			String startTime = appMain.getCurrentTime();
			int _iteration_counter = 0;

			for (_iteration_counter = 0; _iteration_counter <= ITERATION_LIMIT; _iteration_counter++) {
				XsdEntity _xsdEntity = new XsdEntity();
				String generateEntityVersionNumber = appMain
						.generateEntityVersionNumber(_iteration_counter);
				_xsdEntity = appMain.createEntity("1.0."
						.concat(generateEntityVersionNumber));

				if (_xsdEntity != null) {
					_timeTrack.setStartTime(getCurrentTime());
					appMain._coalesceFramework.SaveCoalesceEntity(_xsdEntity);
					_timeTrack.setStopTime(getCurrentTime());
					_timeTrack.setEntityID(_xsdEntity.getKey());
					timeLogger.add(_timeTrack);
				} else
					break;
			}		
			appMain.printToFile("persistance.xml");
			String stopTime = appMain.getCurrentTime();
			System.out.println("STARTTIME: " + startTime);
			System.out.println("STOPTIME: " + stopTime);
		} catch (Exception ex) {
			log.log(java.util.logging.Level.SEVERE, ex.toString());
		}
	}

	private static String getCurrentTime() {
		Calendar calStamp = Calendar.getInstance();
		java.util.Date nowCurrentDate = calStamp.getTime();
		java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(
				nowCurrentDate.getTime());
		return currentTimestamp.toString();
	}

	private static XsdEntity createEntity(String entityVersion)
			throws CoalesceException {
		try {
			// Create Test Entity
			XsdEntity _entity = new XsdEntity();

			XsdSection section = null;
			XsdRecordset recordSet = null;
			XsdRecord record = null;

			// Create Entity
			_entity = XsdEntity.create("Volume Push Test Entity", "Unit Test",
					entityVersion, "", "", "");

			XsdLinkageSection.create(_entity, true);

			section = XsdSection.create(_entity, "Live Status Section", true);
			recordSet = XsdRecordset.create(section, "Live Status Recordset");
			XsdFieldDefinition.create(recordSet, "CurrentStatus",
					ECoalesceFieldDataTypes.StringType);

			record = recordSet.addNew();
			record.setFieldValue("CurrentStatus", "Test Status");

			String _fieldKey = record.getFieldByName("CurrentStatus").getKey();
			return _entity;
		} catch (CoalesceInvalidFieldException e) {
			CallResult.log(CallResults.FAILED_ERROR, e, MODULE_NAME);
			return null;
		}
	}

	private static String generateEntityVersionNumber(int currentIterationNumber) {
		String entityVersion = "";
		masterCounter += 1;
		if (currentIterationNumber % 10000 == 0) {
			majorVal += 1;
			minorVal = 0;
			outConsoleData(masterCounter, "INCREMENT: ");
			entityVersion = String.valueOf(majorVal) + "."
					+ String.valueOf(minorVal);
		} else {
			minorVal += 1;
			entityVersion = String.valueOf(majorVal) + "."
					+ String.valueOf(minorVal);
		}
		return entityVersion;
	}

	private static void outConsoleData(int cntValue, String msg) {
		System.out.println(msg + getCurrentTime() + "\t" + cntValue);
	}

	private static void createDocument() {

		// get an instance of factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			// get an instance of builder
			DocumentBuilder db = dbf.newDocumentBuilder();

			// create an instance of DOM
			dom = db.newDocument();

		} catch (ParserConfigurationException ex) {
			// dump it
			System.out
					.println("Error while trying to instantiate DocumentBuilder "
							+ ex.toString());
			CallResult.log(CallResults.FAILED_ERROR, ex, MODULE_NAME);
		}

	}

	private static void createDOMTree() {
		Element rootEle = dom.createElement("TimeTrack");
		dom.appendChild(rootEle);
		Iterator it = timeLogger.iterator();
		while (it.hasNext()) {
			TimeTrack b = (TimeTrack) it.next();
			Element timeTrackElement = createBookElement(b);
			rootEle.appendChild(timeTrackElement);
		}

	}

	private static Element createBookElement(TimeTrack b) {

		Element timeElement = dom.createElement("TimeTrack");
		timeElement.setAttribute("ENTITYID", b.getEntityID());
		Element entityIDElement=dom.createElement("ENTITYID");
		Text entID=dom.createTextNode(b.getEntityID());
		entityIDElement.appendChild(entID);
		timeElement.appendChild(entityIDElement);
		// create start time element and start time text node and attach it to
		// timeElement -Start
		Element startEle = dom.createElement("STARTTIME");
		Text startText = dom.createTextNode(b.getStartTime());
		startEle.appendChild(startText);
		timeElement.appendChild(startEle);

		// create stop time element and stop time text node and attach it to
		// timeElement -Stop
		Element stopEle = dom.createElement("STOPTIME");
		Text stopText = dom.createTextNode(b.getStopTime());
		stopEle.appendChild(stopText);
		timeElement.appendChild(stopEle);

		return timeElement;

	}

	private static void printToFile(String fileName) {
		try {
			// print
			@SuppressWarnings("deprecation")
			OutputFormat format = new OutputFormat(dom);
			format.setIndenting(true);
			@SuppressWarnings("deprecation")
			XMLSerializer serializer = new XMLSerializer(new FileOutputStream(
					new File(fileName)), format);
			serializer.serialize(dom);

		} catch (IOException ex) {
			CallResult.log(CallResults.FAILED_ERROR, ex, MODULE_NAME);
		}
	}
}
