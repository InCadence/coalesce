package com.persister.tests;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
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
import coalesce.persister.postgres.PostGresSQLPersistor;
import coalesce.persister.postgres.PostGresDataConnector;

public class appThreadMain {
	final static Logger log = Logger.getLogger("TesterLog");
	static ServerConn serCon;
	static PostGresSQLPersistor psPersister;

	public static void main(String[] args) {
		int ITERATION_LIMIT = 10;
		appRunner._coalesceFramework = new CoalesceFramework();
		
		try {
			if (appThreadMain.OpenConnection() == true) {
				appRunner._coalesceFramework.Initialize(appThreadMain.psPersister);
				// timeLogger = new ArrayList<TimeTrack>();
				Thread vol1 = new Thread(new appRunner(ITERATION_LIMIT, 1));
				vol1.setName("Thread #1");
				vol1.start();
				Thread vol2 = new Thread(new appRunner(ITERATION_LIMIT, 1));
				vol2.setName("Thread #2");
				vol2.start();
				Thread vol3=new Thread(new appRunner(ITERATION_LIMIT,1));
				vol3.setName("Thread #3");
				vol3.start();
				Thread vol4=new Thread(new appRunner(ITERATION_LIMIT,1));
				vol4.setName("Thread #4");
				vol4.start();
				Thread vol5=new Thread(new appRunner(ITERATION_LIMIT,1));
				vol5.setName("Thread #5");
				vol5.start();
				Thread vol6=new Thread(new appRunner(ITERATION_LIMIT,1));
				vol6.setName("Thread #6");
				vol6.start();
				Thread vol7=new Thread(new appRunner(ITERATION_LIMIT,1));
				vol7.setName("Thread #7");
				vol7.start();
				Thread vol8=new Thread(new appRunner(ITERATION_LIMIT,1));
				vol8.setName("Thread #8");
				vol8.start();
				Thread vol9 = new Thread(new appRunner(ITERATION_LIMIT, 1));
				vol9.setName("Thread #9");
				vol9.start();
				Thread vol10 = new Thread(new appRunner(ITERATION_LIMIT, 1));
				vol10.setName("Thread #10");
				vol10.start();
				Thread vol11=new Thread(new appRunner(ITERATION_LIMIT,1));
				vol11.setName("Thread #11");
				vol11.start();
				Thread vol12=new Thread(new appRunner(ITERATION_LIMIT,1));
				vol12.setName("Thread #12");
				vol12.start();
				Thread vol13=new Thread(new appRunner(ITERATION_LIMIT,1));
				vol13.setName("Thread #13");
				vol13.start();
				Thread vol14=new Thread(new appRunner(ITERATION_LIMIT,1));
				vol14.setName("Thread #14");
				vol14.start();
				Thread vol15=new Thread(new appRunner(ITERATION_LIMIT,1));
				vol15.setName("Thread #15");
				vol15.start();
				Thread vol16=new Thread(new appRunner(ITERATION_LIMIT,1));
				vol16.setName("Thread #16");
				vol16.start();
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
}

class appRunner implements Runnable {
	private Object mutexXMLLogger = new Object();
	static int ITERATION_LIMIT = 10;
	static int CAPTURE_METRICS_INTERVAL = 1;
	static ServerConn serCon;

	static CoalesceFramework _coalesceFramework;
	private static String MODULE_NAME = "Coalesce.Persister.PerformanceTester";
	private static String _threadID;


	public static String getThreadID() {
		return _threadID;
	}

	public static void setThreadID(String string) {
		appRunner._threadID = string;
	}

	static int minorVal = 0;
	static int majorVal = 0;
	static int masterCounter = 0;
	static String startSaveTimeStamp = "";
	static String completeSaveTimeStamp = "";
	private static List<TimeTrack> timeLogger;
	static Document dom;

	public appRunner(int iteration_lim) {
		appRunner.ITERATION_LIMIT = iteration_lim;
	}

	public appRunner(int iteration_lim, int cap_interval) {
		appRunner.ITERATION_LIMIT = iteration_lim;
		appRunner.CAPTURE_METRICS_INTERVAL = cap_interval;
	}

	@Override
	public void run() {
		try {
			timeLogger = new ArrayList<TimeTrack>();
			this.setThreadID(String.valueOf(Thread.currentThread().toString()));
			outConsoleData(Thread.currentThread().getId(),"************* STARTING THREAD # " + Thread.currentThread().getName() + " of " + Thread.currentThread().activeCount() + " *************",false);
			TimeTrack _timeTrack;
			String startTime = appRunner.getCurrentTime();
			int _iteration_counter = 0;
			//appRunner.createDOMDocument();
			for (_iteration_counter = 0; _iteration_counter <= ITERATION_LIMIT; _iteration_counter++) {
				_timeTrack = new TimeTrack();

				XsdEntity _xsdEntity = new XsdEntity();
				String generateEntityVersionNumber = appRunner
						.generateEntityVersionNumber(_iteration_counter);
				_xsdEntity = appRunner.createEntity("1.0."
						.concat(generateEntityVersionNumber));
				if (_xsdEntity != null) {
					if (appRunner.masterCounter % CAPTURE_METRICS_INTERVAL == 0) {
						saveEntity(_timeTrack, _xsdEntity);
						_timeTrack.setEntityID(_xsdEntity.getKey());
						timeLogger.add(_timeTrack);
						_timeTrack = null;
					} else
						appRunner._coalesceFramework
								.SaveCoalesceEntity(_xsdEntity);
				} else
					break;
			}
			synchronized (mutexXMLLogger) {
				//appRunner.createDOMTree();
				String stopTime = appRunner.getCurrentTime();
				outConsoleData(Thread.currentThread().getId(),"STARTTIME: "
						+ startTime);
				outConsoleData(Thread.currentThread().getId(), "STOPTIME: " + stopTime);

				//appRunner.printToFile("datafile_" + Thread.currentThread().toString()						+ "_persistance.xml");
			}
		} catch (Exception ex) {
			appThreadMain.log
					.log(java.util.logging.Level.SEVERE, ex.toString());
		}
	}
	private static void outConsoleData(int cntValue, String msg) {
		System.out.println(msg + getCurrentTime() + "\t" + cntValue);
	}
	private static void outConsoleData(Thread cntValue, String msg) {
		System.out.println(msg + getCurrentTime() + "\t" + cntValue);
	}
	private void outConsoleData(long id, String msg) {
		System.out.println(msg + getCurrentTime() + "\t" + id);
		
	}
	private void outConsoleData(long id, String msg,boolean flagShowTime) {
		if(flagShowTime)
			System.out.println(msg + getCurrentTime() + "\t" + id);
		else
			System.out.println(msg + "\t" + id);
		
	}

	private static void saveAppTimeStamps(TimeTrack _timeTrack,
			String startTime, String stopTime) {
		_timeTrack.setAppStartTime(startTime);
		_timeTrack.setStopTime(stopTime);
	}

	private static void saveEntity(TimeTrack _timeTrack, XsdEntity _xsdEntity)
			throws CoalescePersistorException {
		_timeTrack.setStartTime(getCurrentTime());
		_timeTrack.setIterationVal(String.valueOf(masterCounter));
		_timeTrack.setIterationInterval(String
				.valueOf(CAPTURE_METRICS_INTERVAL));
		if (_timeTrack.getThread() == "" | _timeTrack.getThread() == null)
			_timeTrack.setThread(String.valueOf(Thread.currentThread()));

		appRunner._coalesceFramework.SaveCoalesceEntity(_xsdEntity);
		_timeTrack.setStopTime(getCurrentTime());
	}

	private static String generateEntityVersionNumber(int currentIterationNumber) {
		String entityVersion = "";
		masterCounter += 1;
		if (currentIterationNumber % 10000 == 0) {
			majorVal += 1;
			minorVal = 0;
			// outConsoleData(masterCounter, "INCREMENT: ");
			entityVersion = String.valueOf(majorVal) + "."
					+ String.valueOf(minorVal);
		} else {
			minorVal += 1;
			entityVersion = String.valueOf(majorVal) + "."
					+ String.valueOf(minorVal);
		}
		return entityVersion;
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
			appThreadMain.log
					.log(java.util.logging.Level.SEVERE, ex.toString());
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
		timeElement.setAttribute("THREAD", b.getThread());
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
			appThreadMain.log
					.log(java.util.logging.Level.SEVERE, ex.toString());
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
			appThreadMain.log
					.log(java.util.logging.Level.SEVERE, ex.toString());
			return null;
		}
	}

}
