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
        int language = 0;
        for (int i = 0; i < 1020; i++)
        {

            switch (language) {
            case 1:
                assertEquals(language, ELanguages.English.getValue());
                break;

            case 2:
                assertEquals(language, ELanguages.French.getValue());
                break;

            case 3:
                assertEquals(language, ELanguages.Arabic.getValue());
                break;

            case 4:
                assertEquals(language, ELanguages.Spanish.getValue());
                break;

            case 5:
                assertEquals(language, ELanguages.Chinese.getValue());
                break;

            case 6:
                assertEquals(language, ELanguages.Russian.getValue());
                break;

            case 7:
                assertEquals(language, ELanguages.German.getValue());
                break;

            case 8:
                assertEquals(language, ELanguages.Korean.getValue());
                break;

            case 9:
                assertEquals(language, ELanguages.Japanese.getValue());
                break;

            case 10:
                assertEquals(language, ELanguages.Greek.getValue());
                break;

            case 11:
                assertEquals(language, ELanguages.Italian.getValue());
                break;

            case 12:
                assertEquals(language, ELanguages.Portuguese.getValue());
                break;

            case 13:
                assertEquals(language, ELanguages.Swedish.getValue());
                break;

            case 14:
                assertEquals(language, ELanguages.Romanian.getValue());
                break;

            case 15:
                assertEquals(language, ELanguages.Norwegian.getValue());
                break;

            case 16:
                assertEquals(language, ELanguages.Lithuanian.getValue());
                break;

            case 17:
                assertEquals(language, ELanguages.Hungarian.getValue());
                break;

            case 18:
                assertEquals(language, ELanguages.Polish.getValue());
                break;

            case 19:
                assertEquals(language, ELanguages.Dutch.getValue());
                break;

            case 20:
                assertEquals(language, ELanguages.Danish.getValue());
                break;

            case 21:
                assertEquals(language, ELanguages.Pashtu.getValue());
                break;

            case 22:
                assertEquals(language, ELanguages.HaitianCreole.getValue());
                break;

            case 23:
                assertEquals(language, ELanguages.Dari.getValue());
                break;

            case 24:
                assertEquals(language, ELanguages.Custom1.getValue());
                break;

            case 25:
                assertEquals(language, ELanguages.Custom2.getValue());
                break;

            case 26:
                assertEquals(language, ELanguages.Custom3.getValue());
                break;

            case 27:
                assertEquals(language, ELanguages.Custom4.getValue());
                break;

            case 28:
                assertEquals(language, ELanguages.Custom5.getValue());
                break;

            case 29:
                assertEquals(language, ELanguages.Custom6.getValue());
                break;

            case 30:
                assertEquals(language, ELanguages.Custom7.getValue());
                break;

            case 31:
                assertEquals(language, ELanguages.Custom8.getValue());
                break;
            }

            // ToDo
            // No Unknown value set in the ELanguages.java file.
            // Therefore cannot test for unknown values at this time
        }

    }
}
