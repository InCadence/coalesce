package Coalesce.Framework.DataModel;

import static org.junit.Assert.*;

import org.junit.Test;

import Coalesce.Common.Classification.Marking;
import Coalesce.Common.Classification.MarkingValueTest;
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
    public void GetKeyExistingTest() {
        
    }
    
    @Test
    public void GetClassificationMarkingDefaultTest()
    {

        XsdEntity mission = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdDataObject fdo = mission.GetDataObjectForNamePath(CoalesceTypeInstances.TESTMISSIONNAMEPATH);

        assertTrue(fdo instanceof XsdField);

        XsdField field = (XsdField) fdo;

        MarkingValueTest.assertMarkingValue(new Marking().GetClassification(),
                                            new Marking(field.GetClassificationMarking()).GetClassification());

    }

    @Test
    public void GetClassificationMarkingAfterSetAndSerializedTest()
    {
        XsdEntity mission = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdField field = (XsdField) mission.GetDataObjectForNamePath(CoalesceTypeInstances.TESTMISSIONNAMEPATH);

        field.SetClassificationMarking(_topSecretClassificationMarking);

        String serializedMission = mission.ToXml();

        XsdEntity desMission = XsdEntity.Create(serializedMission.toString());

        field = (XsdField) desMission.GetDataObjectForNamePath(CoalesceTypeInstances.TESTMISSIONNAMEPATH);

        MarkingValueTest.assertMarkingValue(_topSecretClassificationMarking.GetClassification(),
                                            new Marking(field.GetClassificationMarking()).GetClassification());

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

        XsdEntity mission = XsdEntity.Create(CoalesceTypeInstances.TESTMISSION);

        XsdField field = (XsdField) mission.GetDataObjectForNamePath("TREXMission/Mission Information Section/Mission Information Recordset/Mission Information Recordset Record/MissionName");

        String previousHistoryKey = field.GetKey();
        
        field.SetClassificationMarking(_topSecretClassificationMarking);

        assertEquals(previousHistoryKey, field.GetPreviousHistoryKey());
        
    }

    @Test
    public void GetValuewithMarking()
    {

    }
}
