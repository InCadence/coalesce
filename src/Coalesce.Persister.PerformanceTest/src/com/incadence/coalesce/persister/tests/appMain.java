package com.incadence.coalesce.persister.tests;

import java.io.File;
import java.io.FileInputStream;
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

import org.apache.commons.io.IOUtils;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.common.helpers.EntityLinkHelper;
import com.incadencecorp.coalesce.framework.CoalesceFramework;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceLinkageSection;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceSection;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;
import com.incadencecorp.coalesce.framework.datamodel.ELinkTypes;
import com.incadencecorp.coalesce.framework.objects.MissionEntity;
import com.incadencecorp.coalesce.framework.persistance.ServerConn;
import com.incadencecorp.coalesce.framework.persistance.mysql.MySQLPersistor;
import com.incadencecorp.coalesce.framework.persistance.postgres.PostGresSQLPersistor;
import com.incadencecorp.coalesce.framework.objects.EActionStatuses;

public class appMain {

	static int _ITERATION_LIMIT = 1000000;
	static int _CAPTURE_METRICS_INTERVAL = 1000;

	static Logger _log = Logger.getLogger("TesterLog");
	static ServerConn _serCon;
	static FileInputStream _inputStream = null;
	static PostGresSQLPersistor _psPersister;
	static CoalesceFramework _coalesceFramework;
	static int _minorVal = 0;
	static int _majorVal = 0;
	static int _masterCounter = 0;
	static String _startSaveTimeStamp = "";
	static String _completeSaveTimeStamp = "";
	static List<TimeTrack> _timeLogger;
	static String _entityXml = "";
	static Document dom;
	// Set to True to use the File XML for Persistance
	// Set to False for the CoalesceTypeInstances.TEST_MISSION
	static boolean _isModeFile = false;

	public static void main(String[] args) {
		System.out.println("Performance Test Inserting " + _ITERATION_LIMIT
				+ " XsdEntities Started **..** ");
		String userDir = System.getProperty("user.dir");
		appMain._coalesceFramework = new CoalesceFramework();
		try {
			if (OpenConnection() == true) {
				appMain._coalesceFramework.initialize(_psPersister);
				_timeLogger = new ArrayList<TimeTrack>();
				runVolume();
				if (_inputStream != null)
					_inputStream.close();
			}
		} catch (Exception ex) {
			_log.log(java.util.logging.Level.SEVERE, ex.toString());
		}
	}

	public static boolean OpenConnection() throws SQLException {
		_serCon = new ServerConn();
		_serCon.setDatabase("CoalesceDatabase");
		_serCon.setUser("root");
		_serCon.setPassword("Passw0rd");
		_psPersister = new PostGresSQLPersistor();
		_psPersister.Initialize(_serCon);
		return true;
	}

	private static void runVolume() {
		try {
			TimeTrack _timeTrack;
			String startTime = appMain.getCurrentTime();
			int _iteration_counter = 0;
			appMain.createDOMDocument();
			for (_iteration_counter = 0; _iteration_counter <= _ITERATION_LIMIT; _iteration_counter++) {
				_timeTrack = new TimeTrack();

				CoalesceEntity _xsdEntity = new CoalesceEntity();
				String generateEntityVersionNumber = appMain
						.generateEntityVersionNumber(_iteration_counter);
				_xsdEntity = appMain.createEntity("1.0."
						.concat(generateEntityVersionNumber));
				if (_xsdEntity != null) {
					if (appMain._masterCounter % _CAPTURE_METRICS_INTERVAL == 0) {
						saveEntity(_timeTrack, _xsdEntity);
						_timeTrack.setEntityID(_xsdEntity.getKey());
						_timeLogger.add(_timeTrack);
						_timeTrack = null;
					} else
						appMain._coalesceFramework
								.saveCoalesceEntity(_xsdEntity);
				} else
					break;
			}
			appMain.createDOMTree();
			String stopTime = appMain.getCurrentTime();
			outConsoleData(1, "STARTTIME: " + startTime);
			outConsoleData(2, "STOPTIME: " + stopTime);
			String userDir = System.getProperty("user.dir");
			appMain.printToFile(userDir + "/persistance.xml");
		} catch (Exception ex) {
			_log.log(java.util.logging.Level.SEVERE, ex.toString());
		}
	}

	private static void saveEntity(TimeTrack _timeTrack,
			CoalesceEntity _xsdEntity) throws CoalescePersistorException {
		_timeTrack.setStartTime(getCurrentTime());
		_timeTrack.setStartMSTime(getCurrentTime(false));
		_timeTrack.setIterationVal(String.valueOf(_masterCounter));
		_timeTrack.setIterationInterval(String
				.valueOf(_CAPTURE_METRICS_INTERVAL));
		appMain._coalesceFramework.saveCoalesceEntity(_xsdEntity);
		_timeTrack.setStopTime(getCurrentTime());
		_timeTrack.setStopMSTime(getCurrentTime(false));
	}

	private static String getCurrentTime(boolean isNano) {
		String currentTime;
		if (isNano)
			currentTime = String.valueOf(System.nanoTime());
		else
			currentTime = String.valueOf(System.currentTimeMillis());
		return currentTime;
	}

	private static String getCurrentTime() {
		Calendar calStamp = Calendar.getInstance();
		java.util.Date nowCurrentDate = calStamp.getTime();
		java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(
				nowCurrentDate.getTime());
		return currentTimestamp.toString();
	}

	private static CoalesceEntity createEntity(String entityVersion)
			throws CoalesceException {
		// Create Test Entity
		CoalesceEntity _entity = new CoalesceEntity();

		if (appMain._isModeFile == true) {
			_entity = appMain.createEntityFromXMLFile(entityVersion);
			_entity.setVersion(entityVersion);
		} else if (appMain._isModeFile == false) {
			_entity=appMain.createMissionEntity(_entity, entityVersion);
		}

		return _entity;
	}

	private static CoalesceEntity createMissionEntity(CoalesceEntity _entity,
			String entityVersion) throws CoalesceException {
		CoalesceSection section;
		CoalesceRecordset recordSet;
		@SuppressWarnings("unused")
		CoalesceRecord _liveStatusRecord;
		_entity.initialize(
				"Mission Entity - Performance Testing",
				"Coalesce_Mission",
				entityVersion,
				"",
				"",
				"TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionName,TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/IncidentTitle");
		section = CoalesceSection.create(_entity, "Live Status Section");
		recordSet = CoalesceRecordset.create(section, "Live Status Recordset");
		CoalesceFieldDefinition.create(recordSet, "CurrentStatus",ECoalesceFieldDataTypes.StringType);
		_liveStatusRecord = recordSet.addNew();
		
		section = CoalesceSection.create(_entity, MissionEntity.Name
				+ " Information Section");
		recordSet = CoalesceRecordset.create(section, MissionEntity.Name
				+ " Information Recordset");

		CoalesceFieldDefinition.create(recordSet, "ActionNumber",ECoalesceFieldDataTypes.StringType, "Action Number", "U", "0");
		CoalesceFieldDefinition.create(recordSet, "IncidentNumber",	ECoalesceFieldDataTypes.StringType);
		CoalesceFieldDefinition.create(recordSet, "IncidentTitle",ECoalesceFieldDataTypes.StringType);
		CoalesceFieldDefinition.create(recordSet, "IncidentDescription",ECoalesceFieldDataTypes.StringType);
		CoalesceFieldDefinition.create(recordSet, "IncidentDateTime",ECoalesceFieldDataTypes.DateTimeType);
		CoalesceFieldDefinition.create(recordSet, "MissionName",ECoalesceFieldDataTypes.StringType);
		CoalesceFieldDefinition.create(recordSet, "MissionType",ECoalesceFieldDataTypes.StringType);
		CoalesceFieldDefinition.create(recordSet, "MissionDescription",	ECoalesceFieldDataTypes.StringType);
		CoalesceFieldDefinition.create(recordSet, "MissionIndicatorColor",ECoalesceFieldDataTypes.StringType);
		CoalesceFieldDefinition.create(recordSet, "MissionIndicatorShape",ECoalesceFieldDataTypes.StringType);
		CoalesceFieldDefinition.create(recordSet, "MissionIndicatorNumber",	ECoalesceFieldDataTypes.StringType);
		CoalesceFieldDefinition.create(recordSet,"MissionIndicatorNumberBASE10",ECoalesceFieldDataTypes.IntegerType);
		CoalesceFieldDefinition.create(recordSet, "MissionStartDateTime",ECoalesceFieldDataTypes.DateTimeType);
		CoalesceFieldDefinition.create(recordSet, "MissionEndDateTime",	ECoalesceFieldDataTypes.DateTimeType);
		CoalesceFieldDefinition.create(recordSet, "MissionGeoLocation",	ECoalesceFieldDataTypes.GeocoordinateType);
		CoalesceFieldDefinition.create(recordSet, "MissionAddress",	ECoalesceFieldDataTypes.StringType);

		// _informationRecord = recordSet.addNew();
		_liveStatusRecord = recordSet.addNew();
		return _entity;
	}

	private static CoalesceEntity createEntityFromXMLFile(String entityVersion)
			throws CoalesceException {
		CoalesceSection section = null;
		CoalesceRecordset recordSet = null;
		CoalesceRecord record = null;
		try {
			// Create Test Entity
			CoalesceEntity _entity = new CoalesceEntity();

			String suserDir = System.getProperty("user.dir");
			suserDir += "/EntityPerfFile.xml";
			if (_inputStream == null)
				_inputStream = new FileInputStream(suserDir);
			if (_entityXml == "") {
				_entityXml = IOUtils.toString(_inputStream);
			}
			try {
				CoalesceEntity entity = new CoalesceEntity();
				entity.initialize(_entityXml);
				CoalesceEntityTemplate template = CoalesceEntityTemplate
						.create(entity);
				_entity = template.createNewEntity();
				// ************************************//
				CoalesceLinkageSection.create(_entity, true);

				section = CoalesceSection.create(_entity,
						CoalesceTypeInstances.TEST_MISSION_INFO_SECTION_PATH,
						true);
				recordSet = CoalesceRecordset.create(section,
						CoalesceTypeInstances.TEST_MISSION_RECORDSET);
				CoalesceFieldDefinition.create(recordSet,
						"Performance TestFieldDef",
						ECoalesceFieldDataTypes.StringType);

				record = recordSet.addNew();
				record.setFieldValue(
						"Performance TestFieldDef",
						"abcdefghijklmnopqrstuvwxyzABCDEFGHIZKLMNOPQRSTUVWXYZ1234567890!@#$%^&*()_+-=;'[],./?><:{}");

				// _fieldKey =
				// record.getFieldByName("Performance TestFieldDef").getKey();

			} finally {
				// inputStream.close();
			}

			return _entity;
		} catch (Exception ex) {
			_log.log(java.util.logging.Level.SEVERE, ex.toString());
			return null;
		}
	}

	private static String generateEntityVersionNumber(int currentIterationNumber) {
		String entityVersion = "";
		_masterCounter += 1;
		if (currentIterationNumber % 10000 == 0) {
			_majorVal += 1;
			_minorVal = 0;
			// outConsoleData(masterCounter, "INCREMENT: ");
			entityVersion = String.valueOf(_majorVal) + "."
					+ String.valueOf(_minorVal);
		} else {
			_minorVal += 1;
			entityVersion = String.valueOf(_majorVal) + "."
					+ String.valueOf(_minorVal);
		}
		return entityVersion;
	}

	private static void outConsoleData(int cntValue, String msg) {
		System.out.println(msg + "\t" + cntValue);
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
			_log.log(java.util.logging.Level.SEVERE, ex.toString());
		}

	}

	private static void createDOMTree() {
		Element rootEle = dom.createElement("TimeTrack");
		dom.appendChild(rootEle);
		Iterator<TimeTrack> iterCNT = _timeLogger.iterator();
		while (iterCNT.hasNext()) {
			TimeTrack bVal = (TimeTrack) iterCNT.next();
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

		Element startEleMS = dom.createElement("INSERT_START_MS");
		Text startMSText = dom.createTextNode(b.getStartMSTime());
		startEleMS.appendChild(startMSText);
		timeElement.appendChild(startEleMS);

		Element stopEleMS = dom.createElement("INSERT_COMPLETE_MS");
		Text stopMSText = dom.createTextNode(b.getStopMSTime());
		stopEleMS.appendChild(stopMSText);
		timeElement.appendChild(stopEleMS);
		return timeElement;

	}

	@SuppressWarnings("deprecation")
	private static void printToFile(String fileName) {
		try {
			// print
			OutputFormat format = new OutputFormat(dom);
			format.setIndenting(true);
			XMLSerializer serializer = new XMLSerializer(new FileOutputStream(
					new File(fileName)), format);
			serializer.serialize(dom);

		} catch (IOException ex) {
			_log.log(java.util.logging.Level.SEVERE, ex.toString());
		}
	}

}
