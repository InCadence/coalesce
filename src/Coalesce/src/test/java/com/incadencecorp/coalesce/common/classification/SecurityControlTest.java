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

package com.incadencecorp.coalesce.common.classification;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Unit test for security controls.
 * 
 * @author n78554
 */
public class SecurityControlTest {

    /**
     * Ensures the parsing of an ACCM control is done correctly.
     */
    @Test
    public void parseACCMTest()
    {

        ACCMControl control = new ACCMControl("ACCM-XXX AAA/ZZZ BBB CCC/YYY");

        assertEquals("XXX", control.getNicknames()[0].getNickname());
        assertEquals("YYY", control.getNicknames()[1].getNickname());
        assertEquals("ZZZ", control.getNicknames()[2].getNickname());

        assertEquals("AAA", control.getNicknames()[0].getCaveat()[0]);

        assertEquals("BBB", control.getNicknames()[2].getCaveat()[0]);
        assertEquals("CCC", control.getNicknames()[2].getCaveat()[1]);

    }

    /**
     * Ensures the toString() formats the ACCM control including the correct
     * sorting.
     */
    @Test
    public void createACCMTest()
    {

        ACCMControl control = new ACCMControl(new ACCMNickname("ACCM-ZZZ 222 111"));

        control.addNickname(new ACCMNickname("AAA", "333"));
        control.addNickname(new ACCMNickname("BBB"));
        
        assertEquals("ACCM-AAA 333/BBB/ZZZ 111 222", control.toString());
        assertTrue(control.hasControls());

    }
    
    /**
     * Ensures the parsing of an ACCM control is done correctly.
     */
    @Test
    public void parseACCMBadTest()
    {

        ACCMControl control = new ACCMControl("ACCM-ZZZ/ /XXX");

        assertEquals(2, control.getNicknames().length);
        assertEquals("XXX", control.getNicknames()[0].getNickname());
        assertEquals("ZZZ", control.getNicknames()[1].getNickname());
        
        control = new ACCMControl("");
        
        assertFalse(control.hasControls());

    }

    /**
     * Ensures the parsing of an SAP control is done correctly.
     */
    @Test
    public void parseSAPTest()
    {

        SAPControl control = new SAPControl("SAR-BP-A12 CDE 125-121");

        assertEquals(1, control.getPrograms().length);
        assertEquals("BP", control.getPrograms()[0].getProgramName());
        assertEquals(2, control.getPrograms()[0].getCompartments().length);
        assertEquals("121", control.getPrograms()[0].getCompartments()[0].getCompartmentName());
        assertEquals("A12", control.getPrograms()[0].getCompartments()[1].getCompartmentName());

        assertEquals(0, control.getPrograms()[0].getCompartments()[0].getSubCompartments().length);
        assertEquals(2, control.getPrograms()[0].getCompartments()[1].getSubCompartments().length);

        assertEquals("125", control.getPrograms()[0].getCompartments()[1].getSubCompartments()[0]);
        assertEquals("CDE", control.getPrograms()[0].getCompartments()[1].getSubCompartments()[1]);

    }
    
    /**
     * Ensures the parsing of an SAP control is done correctly.
     */
    @Test
    public void parseSAPFullTest()
    {

        SAPControl control = new SAPControl("SPECIAL ACCESS REQUIRED-BP");

        assertEquals(1, control.getPrograms().length);
        assertEquals("BP", control.getPrograms()[0].getProgramName());

    }
    
    /**
     * Ensures the parsing of an SAP control is done correctly.
     */
    @Test
    public void parseSAPBadTest()
    {

        SAPControl control = new SAPControl("SAR-BP/ /BC");

        assertEquals(2, control.getPrograms().length);
        assertEquals("BC", control.getPrograms()[0].getProgramName());
        assertEquals("BP", control.getPrograms()[1].getProgramName());

        control = new SAPControl("");
        
        assertFalse(control.hasControls());
        
        control = new SAPControl("SAR-BP--X");

        assertEquals(1, control.getPrograms().length);
        assertEquals(1, control.getPrograms()[0].getCompartments().length);
        
        assertEquals("BP", control.getPrograms()[0].getProgramName());
        assertEquals("X", control.getPrograms()[0].getCompartments()[0].getCompartmentName());

        control = new SAPControl("SAR-");

        assertEquals(0, control.getPrograms().length);
        
    }

    /**
     * Ensures the toString() formats the SAP control including the correct
     * sorting.
     */
    @Test
    public void createSAPTest()
    {

        SAPControl control = new SAPControl(new SAPProgram("AA", new Compartment("ZZZ"), new Compartment("YYY")));

        control.addProgram(new SAPProgram("BP", new Compartment("A12", "CDE", "125"), new Compartment("121")));

        assertEquals("SAR-AA-YYY-ZZZ/BP-121-A12 125 CDE", control.toString());
        assertEquals("SPECIAL ACCESS REQUIRED-AA-YYY-ZZZ/BP-121-A12 125 CDE", control.toString(false));
        assertTrue(control.hasControls());

    }

    /**
     * Ensures the parsing of an SCI control is done correctly.
     */
    @Test
    public void parseSCITest()
    {

        SCIControl control = new SCIControl("SI-G ABCD WXYZ-175 JJJ");

        assertEquals("SI", control.getElements()[0].getControlSystem());

        assertEquals("175", control.getElements()[0].getCompartments()[0].getCompartmentName());
        assertEquals("G", control.getElements()[0].getCompartments()[1].getCompartmentName());

        assertEquals("JJJ", control.getElements()[0].getCompartments()[0].getSubCompartments()[0]);

        assertEquals("ABCD", control.getElements()[0].getCompartments()[1].getSubCompartments()[0]);
        assertEquals("WXYZ", control.getElements()[0].getCompartments()[1].getSubCompartments()[1]);

    }
    
    /**
     * Ensures the parsing of an SCI control is done correctly.
     */
    @Test
    public void parseSCIBadTest()
    {

        SCIControl control = new SCIControl("SI-G/ /XXX");

        assertEquals(2, control.getElements().length);
        assertEquals("SI", control.getElements()[0].getControlSystem());
        assertEquals("G", control.getElements()[0].getCompartments()[0].getCompartmentName());

        assertEquals("XXX", control.getElements()[1].getControlSystem());
        
        control = new SCIControl("");
        
        assertFalse(control.hasControls());

        control = new SCIControl("SI-G--X");

        assertEquals(1, control.getElements().length);
        assertEquals(2, control.getElements()[0].getCompartments().length);
        
        assertEquals("G", control.getElements()[0].getCompartments()[0].getCompartmentName());
        assertEquals("X", control.getElements()[0].getCompartments()[1].getCompartmentName());

        control = new SCIControl("SI-");

        assertEquals(1, control.getElements().length);
        assertEquals(0, control.getElements()[0].getCompartments().length);
        
    }

    /**
     * Ensures the toString() formats the SCI control including the correct
     * sorting.
     */
    @Test
    public void createSCITest()
    {

        SCIControl control = new SCIControl(new SCIElement("AA", new Compartment("BB")));

        control.addElement(new SCIElement("SI", new Compartment("G", "WXYZ", "ABCD"), new Compartment("175", "JJJ")));

        assertEquals("AA-BB/SI-175 JJJ-G ABCD WXYZ", control.toString());
        assertTrue(control.hasControls());

    }

}
