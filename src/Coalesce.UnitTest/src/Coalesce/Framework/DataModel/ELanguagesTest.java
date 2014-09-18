/**
 * 
 */
package Coalesce.Framework.DataModel;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/*
 * @BeforeClass public static void setUpBeforeClass() throws Exception { }
 * 
 * @AfterClass public static void tearDownAfterClass() throws Exception { }
 * 
 * @Before public void setUp() throws Exception { }
 * 
 * @After public void tearDown() throws Exception { }
 */

public class ELanguagesTest {

    @Test
    public void GetValueTest()
    {

        // String a = "HasParticipant";
        // ELinkTypes value = ELinkTypes.HasParticipant;

        // assertEquals(a, value.getLabel());

        for (int i = 0; i < 1020; i++)
        {

            if (i == 0)
            {
                assertEquals(i, ELanguages.English);
            }
            
            else if (i == 1)
            {
                assertEquals(i, ELanguages.French);
            }
            
            else if (i == 2)
            {
                assertEquals(i, ELanguages.Arabic);
            }
            
            else if (i == 3)
            {
                assertEquals(i, ELanguages.Spanish);
            }
            
            else if (i == 4)
            {
                assertEquals(i, ELanguages.Chinese);
            }
            
            else if (i == 5)
            {
                assertEquals(i, ELanguages.Russian);
            }
            
            else if (i == 6)
            {
                assertEquals(i, ELanguages.German);
            }
            
            else if (i == 7)
            {
                assertEquals(i, ELanguages.Korean);
            }
            
            else if (i == 8)
            {
                assertEquals(i, ELanguages.Japanese);
            }
            
            else if (i == 9)
            {
                assertEquals(i, ELanguages.Greek);
            }
            
            else if (i == 10)
            {
                assertEquals(i, ELanguages.Italian);
            }
            
            else if (i == 11)
            {
                assertEquals(i, ELanguages.Portuguese);
            }
            
            else if (i == 12)
            {
                assertEquals(i, ELanguages.Swedish);
            }
            
            else if (i == 13)
            {
                assertEquals(i, ELanguages.Romanian);
            }
            
            else if (i == 14)
            {
                assertEquals(i, ELanguages.Norwegian);
            }
            
            else if (i == 15)
            {
                assertEquals(i, ELanguages.Lithuanian);
            }
            
            else if (i == 16)
            {
                assertEquals(i, ELanguages.Hungarian);
            }
            
            else if (i == 17)
            {
                assertEquals(i, ELanguages.Polish);
            }
            
            else if (i == 18)
            {
                assertEquals(i, ELanguages.Dutch);
            }
            
            else if (i == 19)
            {
                assertEquals(i, ELanguages.Danish);
            }
            
            else if (i == 20)
            {
                assertEquals(i, ELanguages.Pashtu);
            }
            
            else if (i == 21)
            {
                assertEquals(i, ELanguages.HaitianCreole);
            }
            
            else if (i == 22)
            {
                assertEquals(i, ELanguages.Dari);
            }
            
            else if (i == 1001)
            {
                assertEquals(i, ELanguages.Custom1);
            }
            
            else if (i == 1002)
            {
                assertEquals(i, ELanguages.Custom2);
            }
            
            else if (i == 1003)
            {
                assertEquals(i, ELanguages.Custom3);
            }
            
            else if (i == 1004)
            {
                assertEquals(i, ELanguages.Custom4);
            }
            
            else if (i == 1005)
            {
                assertEquals(i, ELanguages.Custom5);
            }
            
            else if (i == 1006)
            {
                assertEquals(i, ELanguages.Custom6);
            }
            
            else if (i == 1007)
            {
                assertEquals(i, ELanguages.Custom7);
            }
            
            else if (i == 1008)
            {
                assertEquals(i, ELanguages.Custom8);
            }
        }

    }

}
