package Coalesce.Framework.DataModel;

import org.junit.Test;

import Coalesce.Framework.GeneratedJAXB.Entity.Section;
import Coalesce.Framework.GeneratedJAXB.Entity.Section.Recordset;
import Coalesce.Framework.GeneratedJAXB.Entity.Section.Recordset.Record;
import Coalesce.Framework.GeneratedJAXB.Entity.Section.Recordset.Record.Field;
import Coalesce.Framework.GeneratedJAXB.Entity.Section.Recordset.Record.Field.Fieldhistory;

public class XsdFieldHistoryTest {

    /*
     * @BeforeClass public static void setUpBeforeClass() throws Exception { }
     * 
     * @AfterClass public static void tearDownAfterClass() throws Exception { }
     * 
     * @Before public void setUp() throws Exception { }
     * 
     * @After public void tearDown() throws Exception { }
     */

    @Test(expected = ClassCastException.class)
    public void ConstructorXsdFieldBaseXsdFieldHistoryTest()
    {

        XsdFieldHistory fh1 = new XsdFieldHistory();
        fh1.Initialize(null, new Fieldhistory());

        @SuppressWarnings("unused")
        XsdFieldHistory fh2 = XsdFieldHistory.Create((XsdFieldBase) fh1);

    }

    @Test
    public void ConstructorXsdFieldBaseXsdFieldTest()
    {

        XsdField field = new XsdField();
        field.Initialize(null, new Field());

        @SuppressWarnings("unused")
        XsdFieldHistory fh = XsdFieldHistory.Create((XsdFieldBase) field);

    }

    @Test(expected = ClassCastException.class)
    public void ConstructorXsdFieldHistoryTest()
    {

        XsdFieldHistory fh1 = new XsdFieldHistory();
        fh1.Initialize(null, new Fieldhistory());

        @SuppressWarnings("unused")
        XsdFieldHistory fh2 = XsdFieldHistory.Create(fh1);

    }

    @Test
    public void ConstructorXsdFieldTest()
    {
        XsdEntity entity = new XsdEntity();
        entity.Initialize();
        
        XsdSection section = new XsdSection();
        section.Initialize(entity, new Section());
        
        XsdRecordset rs = new XsdRecordset();
        rs.Initialize(section, new Recordset());
        
        XsdRecord record = new XsdRecord();
        record.Initialize(rs, new Record());

        XsdField field = new XsdField();
        field.Initialize(record, new Field());

        @SuppressWarnings("unused")
        XsdFieldHistory fh = XsdFieldHistory.Create(field);

    }


    @Test
    public void test()
    {
        // TODO: Complete testing
    }

}
