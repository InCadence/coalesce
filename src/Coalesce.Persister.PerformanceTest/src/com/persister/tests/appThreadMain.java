package com.persister.tests;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
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

import Coalesce.Common.Exceptions.CoalesceException;
import Coalesce.Common.Exceptions.CoalesceInvalidFieldException;
import Coalesce.Framework.CoalesceFramework;
import Coalesce.Framework.DataModel.ECoalesceFieldDataTypes;
import Coalesce.Framework.DataModel.XsdEntity;
import Coalesce.Framework.DataModel.XsdFieldDefinition;
import Coalesce.Framework.DataModel.XsdLinkageSection;
import Coalesce.Framework.DataModel.XsdRecord;
import Coalesce.Framework.DataModel.XsdRecordset;
import Coalesce.Framework.DataModel.XsdSection;

import com.database.persister.PostGresSQLPersistor;
import com.database.persister.ServerConn;

public class appThreadMain {
	private final static Logger log = Logger.getLogger("TesterLog");
	private static int ITERATION_LIMIT = 10000;
	private static int CAPTURE_METRICS_INTERVAL = 100;
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
		// TODO Auto-generated method stub

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
	private static String getCurrentTime() {
		Calendar calStamp = Calendar.getInstance();
		java.util.Date nowCurrentDate = calStamp.getTime();
		java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(
				nowCurrentDate.getTime());
		return currentTimestamp.toString();
	}
	private static void createDOMTree() {
		Element rootEle = dom.createElement("TimeTrack");
		dom.appendChild(rootEle);
		for (TimeTrack b : timeLogger) {
			Element timeTrackElement = createTimeTrackElement(b);
			rootEle.appendChild(timeTrackElement);
		}
	}

	private static Element createTimeTrackElement(TimeTrack b) {

		Element timeElement = dom.createElement("TimeTrack");	
		timeElement.setAttribute("ENTITYID", b.getEntityID());
		timeElement.setAttribute("ITERATIONS", b.getIterationVal());
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
		} catch (CoalesceInvalidFieldException ex) {
			log.log(java.util.logging.Level.SEVERE, ex.toString());
			return null;
		}
	}
}
