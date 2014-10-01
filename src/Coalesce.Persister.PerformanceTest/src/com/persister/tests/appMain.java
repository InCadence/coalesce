package com.persister.tests;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import coalesce.persister.postgres.PostGresSQLPersistor;

import Coalesce.Common.Exceptions.CoalesceException;
import Coalesce.Common.Exceptions.CoalesceInvalidFieldException;
import Coalesce.Common.Exceptions.CoalescePersistorException;
import Coalesce.Framework.CoalesceFramework;
import Coalesce.Framework.DataModel.ECoalesceFieldDataTypes;
import Coalesce.Framework.DataModel.XsdEntity;
import Coalesce.Framework.DataModel.XsdFieldDefinition;
import Coalesce.Framework.DataModel.XsdLinkageSection;
import Coalesce.Framework.DataModel.XsdRecord;
import Coalesce.Framework.DataModel.XsdRecordset;
import Coalesce.Framework.DataModel.XsdSection;


import Coalesce.Framework.Persistance.ServerConn;

public class appMain {

	private final static Logger log = Logger.getLogger("TesterLog");
	private static int ITERATION_LIMIT = 1000000;
	private static int CAPTURE_METRICS_INTERVAL = 1000;
	static ServerConn serCon;
	static PostGresSQLPersistor psPersister;
	private static CoalesceFramework _coalesceFramework;
	//private static String MODULE_NAME = "Coalesce.Persister.PerformanceTester";
	static int minorVal = 0;
	static int majorVal = 0;
	static int masterCounter = 0;
	static String startSaveTimeStamp = "";
	static String completeSaveTimeStamp = "";
	private static List<TimeTrack> timeLogger;
	static Document dom;

	public static void main(String[] args) {
		System.out.println("Performance Test Inserting " + ITERATION_LIMIT
				+ " XsdEntities Started **..** ");
		appMain._coalesceFramework = new CoalesceFramework();
		try {
			if (OpenConnection() == true) {
				appMain._coalesceFramework.Initialize(psPersister);
				timeLogger = new ArrayList<TimeTrack>();
				runVolume();
			}
		} catch (Exception ex) {
			log.log(java.util.logging.Level.SEVERE, ex.toString());
		}
	}

	public static boolean OpenConnection() throws SQLException {
		serCon = new ServerConn();
		serCon.setDatabase("CoalesceDatabase");
		serCon.setUser("root");
		serCon.setPassword("Passw0rd");
		psPersister = new PostGresSQLPersistor();
		psPersister.Initialize(serCon);
		return true;
	}

	private static void runVolume() {
		try {
			TimeTrack _timeTrack;
			String startTime = appMain.getCurrentTime();
			int _iteration_counter = 0;
			appMain.createDOMDocument();
			for (_iteration_counter = 0; _iteration_counter <= ITERATION_LIMIT; _iteration_counter++) {
				_timeTrack = new TimeTrack();
				
				XsdEntity _xsdEntity = new XsdEntity();
				String generateEntityVersionNumber = appMain
						.generateEntityVersionNumber(_iteration_counter);
				_xsdEntity = appMain.createEntity("1.0."
						.concat(generateEntityVersionNumber));
				if (_xsdEntity != null) {
					if (appMain.masterCounter % CAPTURE_METRICS_INTERVAL == 0) {
						saveEntity(_timeTrack, _xsdEntity);
						_timeTrack.setEntityID(_xsdEntity.getKey());
						timeLogger.add(_timeTrack);
						_timeTrack = null;
					} else
						appMain._coalesceFramework
								.SaveCoalesceEntity(_xsdEntity);
				} else
					break;
			}
			appMain.createDOMTree();
			String stopTime = appMain.getCurrentTime();
			outConsoleData(1,"STARTTIME: " + startTime);
			outConsoleData(2,"STOPTIME: " + stopTime);
			appMain.printToFile("d:/persistance.xml");
		} catch (Exception ex) {
			log.log(java.util.logging.Level.SEVERE, ex.toString());
		}
	}

	private static void saveAppTimeStamps(TimeTrack _timeTrack, String startTime,String stopTime){
		_timeTrack.setAppStartTime(startTime);
		_timeTrack.setStopTime(stopTime);
	}
	private static void saveEntity(TimeTrack _timeTrack, XsdEntity _xsdEntity)
			throws CoalescePersistorException {
		_timeTrack.setStartTime(getCurrentTime());	
		_timeTrack.setIterationVal(String.valueOf(masterCounter));
		_timeTrack.setIterationInterval(String.valueOf(CAPTURE_METRICS_INTERVAL));
		appMain._coalesceFramework.SaveCoalesceEntity(_xsdEntity);
		_timeTrack.setStopTime(getCurrentTime());
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

			//String _fieldKey = record.getFieldByName("CurrentStatus").getKey();
			return _entity;
		} catch (CoalesceInvalidFieldException ex) {
			log.log(java.util.logging.Level.SEVERE, ex.toString());
			return null;
		}
	}

	private static String generateEntityVersionNumber(int currentIterationNumber) {
		String entityVersion = "";
		masterCounter += 1;
		if (currentIterationNumber % 10000 == 0) {
			majorVal += 1;
			minorVal = 0;
			//outConsoleData(masterCounter, "INCREMENT: ");
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

	private static void createDOMDocument() {
		// get an instance of factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			dom = db.newDocument();

		} catch (ParserConfigurationException ex) {
			System.out
					.println("Error while trying to instantiate DocumentBuilder "
							+ ex.toString());
			log.log(java.util.logging.Level.SEVERE, ex.toString());
		}

	}

	private static void createDOMTree() {
		Element rootEle = dom.createElement("TimeTrack");
		dom.appendChild(rootEle);
		Iterator<TimeTrack> iterCNT  = timeLogger.iterator();
		while(iterCNT.hasNext()) {
			TimeTrack bVal = (TimeTrack)iterCNT.next();
			Element timeTrackElement = createTimeTrackElement(bVal);
			rootEle.appendChild(timeTrackElement);
		}
	}

	private static Element createTimeTrackElement(TimeTrack b) {

		Element timeElement = dom.createElement("TimeTrack");	
		timeElement.setAttribute("ENTITYID", b.getEntityID());
		timeElement.setAttribute("COMPLETE_ITERATIONS", b.getIterationVal());
		timeElement.setAttribute("CAPTURE_INTERVAL", b.getIterationInterval());
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

	@SuppressWarnings("deprecation")
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
			log.log(java.util.logging.Level.SEVERE, ex.toString());
		}
	}
	
}
