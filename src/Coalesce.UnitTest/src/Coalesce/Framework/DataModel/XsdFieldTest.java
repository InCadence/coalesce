package Coalesce.Framework.DataModel;

import static org.junit.Assert.*;

import java.util.UUID;

import org.joda.time.DateTime;
import org.junit.Test;

import Coalesce.Common.Classification.Marking;
import Coalesce.Common.Classification.MarkingValueTest;
import Coalesce.Common.Helpers.JodaDateTimeHelper;
import Coalesce.Common.UnitTest.CoalesceTypeInstances;

public class XsdFieldTest {

    private static final Marking _topSecretClassificationMarking = new Marking("//JOINT TOP SECRET AND USA//FOUO-LES//SBU/ACCM-BOB");

    /*
     * @BeforeClass public static void setUpBeforeClass() throws Exception { }
     * 
     * @AfterClass public static void tearDownAfterClass() throws Exception { }
     * 
     * @Before public void setUp() throws Exception { }
     * 
     * @After public void tearDown() throws Exception { }
     */

    @Test
    public void GetKeyTest()
    {

        XsdField field = GetTestMissionNameField();

        assertEquals(CoalesceTypeInstances.TESTMISSIONNAMEKEY, field.GetKey());

    }

    @Test
    public void SetKeyTest()
    {

        XsdEntity mission = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdField field = GetTestMissionNameField(mission);

        UUID newGuid = UUID.randomUUID();
        field.SetKey(newGuid.toString());

        XsdField savedField = GetSavedTestMissionField(mission);

        assertEquals(savedField.GetKey().toUpperCase(), newGuid.toString().toUpperCase());

    }

    @Test
    public void GetNameTest()
    {

        XsdField field = GetTestMissionNameField();

        assertEquals(CoalesceTypeInstances.TESTMISSIONNAMENAME, field.GetName());

    }

    @Test
    public void SetNameTest()
    {

        XsdEntity mission = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdField field = GetTestMissionNameField(mission);

        field.SetName("Testingname");

        String serializedMission = mission.ToXml();
        XsdEntity savedMission = XsdEntity.Create(serializedMission);

        XsdField savedField = (XsdField) savedMission.GetDataObjectForNamePath(CoalesceTypeInstances.TESTMISSIONNAMEPATH.replace(CoalesceTypeInstances.TESTMISSIONNAMENAME,
                                                                                                                               "Testingname"));

        assertEquals("Testingname", savedField.GetName());

    }

    @Test
    public void GetValueTest()
    {
        
        XsdField field = GetTestMissionNameField();
        
        assertEquals(CoalesceTypeInstances.TESTMISSIONNAMEVALUE, field.GetValue());
        
    }
    
    @Test
    public void SetValueTest()
    {
      
        XsdEntity mission = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdField field = GetTestMissionNameField(mission);

        field.SetValue("Testingvalue");

        XsdField savedField = GetSavedTestMissionField(mission);

        assertEquals("Testingvalue", savedField.GetValue());

    }
    
    @Test
    public void GetDataType()
    {
        XsdEntity mission = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);
        
        XsdField stringField = GetTestMissionFieldByName(mission, CoalesceTypeInstances.TESTMISSIONNAMEPATH);
        assertEquals("string", stringField.GetDataType());
        
        XsdField dateField = GetTestMissionFieldByName(mission, CoalesceTypeInstances.TESTMISSIONSTARTTIMEPATH);
        assertEquals("datetime", dateField.GetDataType());
        
        XsdField integerField = GetTestMissionFieldByName(mission, CoalesceTypeInstances.TESTMISSIONBASE64PATH);
        assertEquals("integer", integerField.GetDataType());
        
    }
    
    @Test
    public void SetDateType()
    {
        XsdEntity mission = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);
        
        XsdField stringField = GetTestMissionFieldByName(mission, CoalesceTypeInstances.TESTMISSIONNAMEPATH);
        stringField.SetDataType("datetime");
        
        XsdField dateField = GetTestMissionFieldByName(mission, CoalesceTypeInstances.TESTMISSIONSTARTTIMEPATH);
        dateField.SetDataType("integer");
        
        XsdField integerField = GetTestMissionFieldByName(mission, CoalesceTypeInstances.TESTMISSIONBASE64PATH);
        integerField.SetDataType("string");
        
        String serializedMission = mission.ToXml();
        XsdEntity savedMission = XsdEntity.Create(serializedMission);
        
        XsdField savedStringField = GetTestMissionFieldByName(savedMission, CoalesceTypeInstances.TESTMISSIONNAMEPATH);
        assertEquals("datetime", savedStringField.GetDataType());
        
        XsdField savedDateField = GetTestMissionFieldByName(savedMission, CoalesceTypeInstances.TESTMISSIONSTARTTIMEPATH);
        assertEquals("integer", savedDateField.GetDataType());
        
        XsdField savedIntegerField = GetTestMissionFieldByName(savedMission, CoalesceTypeInstances.TESTMISSIONBASE64PATH);
        assertEquals("string", savedIntegerField.GetDataType());
        
    }
    
    @Test
    public void GetLabelTest()
    {
        
        XsdField field = GetTestMissionFieldByName(CoalesceTypeInstances.TESTMISSIONACTIONNUMBERPATH);
        
        assertEquals(CoalesceTypeInstances.TESTMISSIONACTIONNUMBERLABEL, field.GetLabel());
        
    }
    
    @Test
    public void GetLabelDoesNotExistTest()
    {
        
        XsdField field = GetTestMissionFieldByName(CoalesceTypeInstances.TESTMISSIONNAMEPATH);
        
        assertEquals("", field.GetLabel());
        
    }
    
    @Test
    public void SetLabelTest()
    {
      
        XsdEntity mission = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdField field = GetTestMissionNameField(mission);

        field.SetLabel("Testinglabel");

        XsdField savedField = GetSavedTestMissionField(mission);

        assertEquals("Testinglabel", savedField.GetLabel());

    }
    
    @Test
    public void SetLabelNullTest()
    {
      
        XsdEntity mission = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdField field = GetTestMissionNameField(mission);

        field.SetLabel(null);

        XsdField savedField = GetSavedTestMissionField(mission);

        assertEquals("", savedField.GetLabel());

    }
    
    @Test
    public void GetSizeDoesNotExistTest()
    {
        XsdField field = GetTestMissionNameField();
        
        assertEquals(0, field.GetSize());
        
    }
    
    @Test
    public void SetSizeTest()
    {
        XsdEntity mission = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);
        
        XsdField field = GetTestMissionNameField(mission);
        field.SetSize(128);
        
        String serializedMission = mission.ToXml();
        XsdEntity savedMission = XsdEntity.Create(serializedMission);
        
        XsdField savedField = GetTestMissionNameField(savedMission);
        assertEquals(128, savedField.GetSize());
                
    }
    
    @Test
    public void GetModifiedByDoesNotExistTest()
    {
        
        XsdField field = GetTestMissionNameField();
        
        assertEquals("", field.GetModifiedBy());
        
    }
    
    @Test
    public void SetModifiedByTest()
    {
      
        XsdEntity mission = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdField field = GetTestMissionNameField(mission);

        field.SetModifiedBy("TestingModifiedBy");

        XsdField savedField = GetSavedTestMissionField(mission);

        assertEquals("TestingModifiedBy", savedField.GetModifiedBy());

    }
    
    @Test
    public void GetModifiedByIpDoesNotExistTest()
    {
        
        XsdField field = GetTestMissionNameField();
        
        assertEquals("", field.GetModifiedByIP());
        
    }
    
    @Test
    public void SetModifiedByIpTest()
    {
      
        XsdEntity mission = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdField field = GetTestMissionNameField(mission);

        field.SetModifiedByIP("192.168.2.2");

        XsdField savedField = GetSavedTestMissionField(mission);

        assertEquals("192.168.2.2", savedField.GetModifiedByIP());

    }
        @Test
    public void GetClassificationMarkingDefaultTest()
    {

        XsdField field = GetTestMissionNameField();

        MarkingValueTest.assertMarkingValue(new Marking().GetClassification(),
                                            new Marking(field.GetClassificationMarking()).GetClassification());

    }

    @Test
    public void GetClassificationMarkingAfterSetAndSerializedTest()
    {
        XsdEntity mission = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdField field = GetTestMissionNameField(mission);

        field.SetClassificationMarking(_topSecretClassificationMarking);

        XsdField savedField = GetSavedTestMissionField(mission);

        MarkingValueTest.assertMarkingValue(_topSecretClassificationMarking.GetClassification(),
                                            new Marking(savedField.GetClassificationMarking()).GetClassification());

    }

    @Test
    public void SetClassificationMarkingTopSecretTest()
    {

        XsdEntity mission = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdField field = (XsdField) mission.GetDataObjectForNamePath(CoalesceTypeInstances.TESTMISSIONNAMEPATH);

        field.SetClassificationMarking(_topSecretClassificationMarking);

        MarkingValueTest.assertMarkingValue(_topSecretClassificationMarking.GetClassification(),
                                            new Marking(field.GetClassificationMarking()).GetClassification());

    }

    @Test
    public void GetPreviousHistoryKeyNoneTest()
    {

        XsdEntity mission = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdField field = (XsdField) mission.GetDataObjectForNamePath(CoalesceTypeInstances.TESTMISSIONNAMEPATH);

        assertEquals("00000000-0000-0000-0000-000000000000", field.GetPreviousHistoryKey());

    }

    @Test
    public void GetPreviousHistoryKeyClassificationMarkingChangeTest()
    {

        XsdField field = GetTestMissionNameField();

        String previousHistoryKey = field.GetKey();

        field.SetClassificationMarking(_topSecretClassificationMarking);

        assertEquals(previousHistoryKey, field.GetPreviousHistoryKey());

    }

    @Test
    public void GetFilenameDoesNotExistTest()
    {
        
        XsdField field = GetTestMissionNameField();
        
        assertEquals("", field.GetFilename());
        
    }
    
    @Test
    public void SetFilenameTest()
    {
      
        XsdEntity mission = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdField field = GetTestMissionNameField(mission);

        field.SetFilename("c:/Program Files/java/jre7/bin");

        XsdField savedField = GetSavedTestMissionField(mission);

        assertEquals("c:/Program Files/java/jre7/bin", savedField.GetFilename());

    }
    
    @Test
    public void GetExtensionDoesNotExistTest()
    {
        
        XsdField field = GetTestMissionNameField();
        
        assertEquals("", field.GetExtension());
        
    }
    
    @Test
    public void SetExtensionTest()
    {
      
        XsdEntity mission = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdField field = GetTestMissionNameField(mission);

        field.SetExtension(".jpeg");

        XsdField savedField = GetSavedTestMissionField(mission);

        assertEquals("jpeg", savedField.GetExtension());

    }
    
    @Test
    public void GetMimeTypeDoesNotExistTest()
    {
        
        XsdField field = GetTestMissionNameField();
        
        assertEquals("", field.GetMimeType());
        
    }
    
    @Test
    public void SetMimeTypeTest()
    {
      
        XsdEntity mission = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdField field = GetTestMissionNameField(mission);

        field.SetMimeType("application/pdf");

        XsdField savedField = GetSavedTestMissionField(mission);

        assertEquals("application/pdf", savedField.GetMimeType());

    }
    
    @Test
    public void GetHashDoesNotExistTest()
    {
        
        XsdField field = GetTestMissionNameField();
        
        assertEquals("", field.GetHash());
        
    }
    
    @Test
    public void SetHashTest()
    {
      
        XsdEntity mission = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdField field = GetTestMissionNameField(mission);

        field.SetHash("8743b52063cd84097a65d1633f5c74f5");

        XsdField savedField = GetSavedTestMissionField(mission);

        assertEquals("8743b52063cd84097a65d1633f5c74f5", savedField.GetHash());

    }
    
    @Test
    public void GetDateCreatedExistTest()
    {
        
        XsdField field = GetTestMissionNameField();
        
        assertEquals(CoalesceTypeInstances.TESTMISSIONNAMECREATED, field.GetDateCreated());
        
    }
    
    @Test
    public void SetDateCreatedTest()
    {
      
        XsdEntity mission = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdField field = GetTestMissionNameField(mission);

        DateTime now = JodaDateTimeHelper.NowInUtc();
        
        field.SetDateCreated(now);

        XsdField savedField = GetSavedTestMissionField(mission);

        assertEquals(now, savedField.GetDateCreated());

    }
    
    @Test
    public void GetLastModifiedExistTest()
    {
        
        XsdField field = GetTestMissionNameField();
        
        assertEquals(CoalesceTypeInstances.TESTMISSIONNAMEMODIFIED, field.GetLastModified());
        
    }
    
    @Test
    public void SetLastModifiedTest()
    {
      
        XsdEntity mission = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdField field = GetTestMissionNameField(mission);

        DateTime now = JodaDateTimeHelper.NowInUtc();
        
        field.SetLastModified(now);

        XsdField savedField = GetSavedTestMissionField(mission);

        assertEquals(now, savedField.GetLastModified());

    }
    
    @Test
    public void ToXmlTest() {
        
        XsdField field = GetTestMissionNameField();
        
        String fieldXml = field.ToXml();
        
        assertEquals(CoalesceTypeInstances.TESTMISSIONNAMEXML, fieldXml.replace("\n", "").replace("\r", ""));
        
    }
    
    @Test
    public void GetValuewithMarking()
    {

    }

    // -----------------------------------------------------------------------//
    // Private Methods
    // -----------------------------------------------------------------------//

    private XsdField GetTestMissionNameField(String entityXml)
    {

        XsdEntity entity = XsdEntity.Create(entityXml);

        return GetTestMissionNameField(entity);

    }

    private XsdField GetTestMissionNameField(XsdEntity entity)
    {

        return GetTestMissionFieldByName(entity, CoalesceTypeInstances.TESTMISSIONNAMEPATH);

    }

    private XsdField GetTestMissionNameField()
    {

        return GetTestMissionNameField(CoalesceTypeInstances.TESTMISSION);

    }

    private XsdField GetTestMissionFieldByName(String fieldPath)
    {
        
        XsdEntity mission = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        return GetTestMissionFieldByName(mission, fieldPath);

    }
    
    private XsdField GetTestMissionFieldByName(XsdEntity entity, String fieldPath)
    {
        
        XsdDataObject fdo = entity.GetDataObjectForNamePath(fieldPath);

        assertTrue(fdo instanceof XsdField);

        return (XsdField) fdo;

    }
    
    private XsdField GetSavedTestMissionField(XsdEntity entity)
    {

        String serializedMission = entity.ToXml();

        return GetTestMissionNameField(serializedMission);

    }

}
