package Coalesce.Framework.DataModel;

import static org.junit.Assert.fail;

import org.junit.Test;

public class ECoalesceDataObjectStatusTest {

    @Test
    public void ToValueActiveTest()
    {
       String a = "Active";
       ECoalesceDataObjectStatus value = ECoalesceDataObjectStatus.ACTive;
              
       assertEquals(a, value.ToValue());
    }

}
