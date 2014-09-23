package Coalesce.Framework.Geography;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.JDOMException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import Coalesce.Common.Exceptions.CoalesceCryptoException;
import Coalesce.Common.Exceptions.CoalesceDataFormatException;
import Coalesce.Common.Helpers.DocumentProperties;

import com.drew.imaging.ImageProcessingException;

/*-----------------------------------------------------------------------------'
 Copyright 2014 - InCadence Strategic Solutions Inc., All Rights Reserved

 Notwithstanding any contractor copyright notice, the Government has Unlimited
 Rights in this work as defined by DFARS 252.227-7013 and 252.227-7014.  Use
 of this work other than as specifically authorized by these DFARS Clauses may
 violate Government rights in this work.

 DFARS Clause reference: 252.227-7013 (a)(16) and 252.227-7014 (a)(16)
 Unlimited Rights. The Government has the right to use, modify, reproduce,
 perform, display, release or disclose this computer software and to have or
 authorize others to do so.

 Distribution Statement D. Distribution authorized to the Department of
 Defense and U.S. DoD contractors only in support of U.S. DoD efforts.
 -----------------------------------------------------------------------------*/

public class GeolocationTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private static final String PARSE_ERROR_MESSAGE = "fromXml must be in the form of '[POINT (double double)|MULTIPOINT ((double double) ...)] '";

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
    public void geolocationToStringTest() throws ImageProcessingException, CoalesceCryptoException, IOException,
            JDOMException
    {

        DocumentProperties docProps = new DocumentProperties();
        docProps.initialize("src\\resources\\desert.jpg");

        Geolocation fromFile = new Geolocation(docProps.getLatitude(), docProps.getLongitude());
        assertEquals("POINT (8.67243350003624 49.39875240003339)", fromFile.toString());

        Geolocation allZero = new Geolocation(0, 0);
        assertEquals("POINT (0 0)", allZero.toString());

        Geolocation maxNegative = new Geolocation(-90, -90);
        assertEquals("POINT (-90 -90)", maxNegative.toString());

        Geolocation maxPositive = new Geolocation(90, 90);
        assertEquals("POINT (90 90)", maxPositive.toString());

        Geolocation equatorMaxPositive = new Geolocation(0, 90);
        assertEquals("POINT (90 0)", equatorMaxPositive.toString());

        Geolocation equatorMaxNegative = new Geolocation(0, -90);
        assertEquals("POINT (-90 0)", equatorMaxNegative.toString());

        Geolocation zeroNorthPole = new Geolocation(90, 0);
        assertEquals("POINT (0 90)", zeroNorthPole.toString());

        Geolocation zeroSouthPole = new Geolocation(-90, 0);
        assertEquals("POINT (0 -90)", zeroSouthPole.toString());

        Geolocation pentagon = new Geolocation(38.87116000, -77.05613800);
        assertEquals("POINT (-77.056138 38.87116)", pentagon.toString());

    }

    @Test
    public void geolocationLatitudeToLargeTest()
    {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Latitude values must be between -90 and 90 degrees.");

        @SuppressWarnings("unused")
        Geolocation location = new Geolocation(90.00000000000001, 0);

    }

    @Test
    public void geolocationLatitudeToSmallTest()
    {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Latitude values must be between -90 and 90 degrees.");

        @SuppressWarnings("unused")
        Geolocation location = new Geolocation(-90.00000000000001, 0);

    }

    @Test
    public void geolocationLongitudeToLargeTest()
    {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Longitude values must be between -90 and 90 degrees.");

        @SuppressWarnings("unused")
        Geolocation location = new Geolocation(0, 90.00000000000001);

    }

    @Test
    public void geolocationLongitudeToSmallTest()
    {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Longitude values must be between -90 and 90 degrees.");

        @SuppressWarnings("unused")
        Geolocation location = new Geolocation(0, -90.00000000000001);

    }

    @Test
    public void geolocationBothTooLargeTest()
    {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Latitude values must be between -90 and 90 degrees.");

        @SuppressWarnings("unused")
        Geolocation location = new Geolocation(90.00000000000001, 90.00000000000001);

    }

    @Test
    public void geolocationBothTooSmallTest()
    {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Latitude values must be between -90 and 90 degrees.");

        @SuppressWarnings("unused")
        Geolocation location = new Geolocation(-90.00000000000001, -90.00000000000001);

    }

    @Test
    public void parseGeolocationTest() throws CoalesceDataFormatException
    {

        Geolocation fromFile = (Geolocation) Geolocation.parseGeolocation("POINT (8.67243350003624 49.39875240003339)");
        assertEquals(new Geolocation(49.39875240003339, 8.67243350003624), fromFile);

        Geolocation allZero = (Geolocation) Geolocation.parseGeolocation("POINT (0 0)");
        assertEquals(new Geolocation(0, 0), allZero);

        Geolocation maxNegative = (Geolocation) Geolocation.parseGeolocation("POINT (-90 -90)");
        assertEquals(new Geolocation(-90, -90), maxNegative);

        Geolocation maxPositive = (Geolocation) Geolocation.parseGeolocation("POINT (90 90)");
        assertEquals(new Geolocation(90, 90), maxPositive);

        Geolocation equatorMaxPositive = (Geolocation) Geolocation.parseGeolocation("POINT (90 0)");
        assertEquals(new Geolocation(0, 90), equatorMaxPositive);

        Geolocation equatorMaxNegative = (Geolocation) Geolocation.parseGeolocation("POINT (-90 0)");
        assertEquals(new Geolocation(0, -90), equatorMaxNegative);

        Geolocation zeroNorthPole = (Geolocation) Geolocation.parseGeolocation("POINT (0 90)");
        assertEquals(new Geolocation(90, 0), zeroNorthPole);

        Geolocation zeroSouthPole = (Geolocation) Geolocation.parseGeolocation("POINT (0 -90)");
        assertEquals(new Geolocation(-90, 0), zeroSouthPole);

        Geolocation pentagon = (Geolocation) Geolocation.parseGeolocation("POINT (-77.056138 38.87116)");
        assertEquals(new Geolocation(38.87116000, -77.05613800), pentagon);

    }

    @Test
    public void parseGeolocationLatitudeToLargeTest() throws CoalesceDataFormatException
    {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Latitude values must be between -90 and 90 degrees.");

        @SuppressWarnings("unused")
        Geolocation location = (Geolocation) Geolocation.parseGeolocation("POINT (0 90.00000000000001)");

    }

    @Test
    public void parseGeolocationLatitudeToSmallTest() throws CoalesceDataFormatException
    {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Latitude values must be between -90 and 90 degrees.");

        @SuppressWarnings("unused")
        Geolocation location = (Geolocation) Geolocation.parseGeolocation("POINT (0 -90.00000000000001)");

    }

    @Test
    public void parseGeolocationLongitudeToLargeTest() throws CoalesceDataFormatException
    {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Longitude values must be between -90 and 90 degrees.");

        @SuppressWarnings("unused")
        Geolocation location = (Geolocation) Geolocation.parseGeolocation("POINT (90.00000000000001 0)");

    }

    @Test
    public void parseGeolocationLongitudeToSmallTest() throws CoalesceDataFormatException
    {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Longitude values must be between -90 and 90 degrees.");

        @SuppressWarnings("unused")
        Geolocation location = (Geolocation) Geolocation.parseGeolocation("POINT (-90.00000000000001 0)");

    }

    @Test
    public void parseGeolocationBothTooLargeTest() throws CoalesceDataFormatException
    {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Latitude values must be between -90 and 90 degrees.");

        @SuppressWarnings("unused")
        Geolocation location = (Geolocation) Geolocation.parseGeolocation("POINT (90.00000000000001 90.00000000000001)");

    }

    @Test
    public void parseGeolocationBothTooSmallTest() throws CoalesceDataFormatException
    {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Latitude values must be between -90 and 90 degrees.");

        @SuppressWarnings("unused")
        Geolocation location = (Geolocation) Geolocation.parseGeolocation("POINT (-90.00000000000001 -90.00000000000001)");

    }

    @Test
    public void parseGeolocationMissingLeftParenTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(PARSE_ERROR_MESSAGE);

        @SuppressWarnings("unused")
        Geolocation location = (Geolocation) Geolocation.parseGeolocation("POINT 0 0)");

    }

    @Test
    public void parseGeolocationMissingRightParenTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(PARSE_ERROR_MESSAGE);

        @SuppressWarnings("unused")
        Geolocation location = (Geolocation) Geolocation.parseGeolocation("POINT (0 0");

    }

    @Test
    public void parseGeolocationMissingBothParenTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(PARSE_ERROR_MESSAGE);

        @SuppressWarnings("unused")
        Geolocation location = (Geolocation) Geolocation.parseGeolocation("POINT 0 0");

    }

    @Test
    public void parseGeolocationMissingSpaceTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(PARSE_ERROR_MESSAGE);

        @SuppressWarnings("unused")
        Geolocation location = (Geolocation) Geolocation.parseGeolocation("POINT(0 0)");

    }

    @Test
    public void parseGeolocationMissingPOINTTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(PARSE_ERROR_MESSAGE);

        @SuppressWarnings("unused")
        Geolocation location = (Geolocation) Geolocation.parseGeolocation("(0 0)");

    }

    @Test
    public void parseGeolocationLatitudeNotNumberTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(PARSE_ERROR_MESSAGE);

        @SuppressWarnings("unused")
        Geolocation location = (Geolocation) Geolocation.parseGeolocation("POINT (X 0)");

    }

    @Test
    public void parseGeolocatioLongitudeNotNumberTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(PARSE_ERROR_MESSAGE);

        @SuppressWarnings("unused")
        Geolocation location = (Geolocation) Geolocation.parseGeolocation("POINT (0 Y)");

    }

    @Test
    public void parseGeolocationBothNotNumberTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(PARSE_ERROR_MESSAGE);

        @SuppressWarnings("unused")
        Geolocation location = (Geolocation) Geolocation.parseGeolocation("POINT (X Y)");

    }

    @Test
    public void parseGeolocationMissingValueTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(PARSE_ERROR_MESSAGE);

        @SuppressWarnings("unused")
        Geolocation location = (Geolocation) Geolocation.parseGeolocation("POINT (0)");

    }

    @Test
    public void parseGeolocationNullTest() throws CoalesceDataFormatException
    {
        assertNull(Geolocation.parseGeolocation(null));
    }

    @Test
    public void parseGeolocationEmptyTest() throws CoalesceDataFormatException
    {
        assertNull(Geolocation.parseGeolocation(""));
    }

    @Test
    public void parseGeolocationWhiteSpaceTest() throws CoalesceDataFormatException
    {
        thrown.expect(CoalesceDataFormatException.class);
        thrown.expectMessage(PARSE_ERROR_MESSAGE);

        @SuppressWarnings("unused")
        Geolocation location = (Geolocation) Geolocation.parseGeolocation("  ");

    }

    @Test
    public void parseGeolocationMultipointTest() throws CoalesceDataFormatException
    {
        @SuppressWarnings("unchecked")
        List<Geolocation> locations = (List<Geolocation>) Geolocation.parseGeolocation("MULTIPOINT ((-70.6280916 34.6873833), (-77.056138 38.87116))");

        List<Geolocation> expected = new ArrayList<Geolocation>();
        expected.add((Geolocation) Geolocation.parseGeolocation("POINT (-70.6280916 34.6873833)"));
        expected.add((Geolocation) Geolocation.parseGeolocation("POINT (-77.056138 38.87116)"));

        assertEquals(expected, locations);

    }

    @Test
    public void parseGeolocationMultipointSingleTest() throws CoalesceDataFormatException
    {
        @SuppressWarnings("unchecked")
        List<Geolocation> locations = (List<Geolocation>) Geolocation.parseGeolocation("MULTIPOINT ((-70.6280916 34.6873833))");

        List<Geolocation> expected = new ArrayList<Geolocation>();
        expected.add((Geolocation) Geolocation.parseGeolocation("POINT (-70.6280916 34.6873833)"));

        assertEquals(expected, locations);

    }

    // TODO: Error conditions for Multipoint

}
