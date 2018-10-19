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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.incadencecorp.coalesce.common.classification.helpers.StringHelper;

/**
 * Object that represents SAP controls.
 * 
 * @author Derek
 */
public class SAPProgram implements Comparable<SAPProgram> {

    /*
     * ----------------------------------------------------------------------
     * Private Members
     * ----------------------------------------------------------------------
     */

    private String _program;
    private List<Compartment> _compartments;

    /*
     * ----------------------------------------------------------------------
     * Constructors
     * ----------------------------------------------------------------------
     */

    /**
     * Default Constructor
     */
    private SAPProgram()
    {
        _compartments = new ArrayList<>();
    }

    /**
     * Parses controls from a string.
     * 
     * @param value
     */
    public SAPProgram(String value)
    {
        this();

        value = value.toUpperCase();

        String[] parts = value.split("-");

        if (parts.length > 0)
        {
            _program = parts[0].trim();
        }

        for (int ii = 1; ii < parts.length; ii++)
        {
            if (!StringHelper.isNullOrEmpty(parts[ii]))
            {
                _compartments.add(new Compartment(parts[ii]));
            }
        }

    }

    /**
     * @param program
     * @param compartments
     */
    public SAPProgram(String program, Compartment... compartments)
    {
        _program = program;
        _compartments = Arrays.asList(compartments);
    }

    /*
     * ----------------------------------------------------------------------
     * Public Methods
     * ----------------------------------------------------------------------
     */

    /**
     * @return the program's name.
     */
    public String getProgramName()
    {
        return _program;
    }

    /**
     * @return a list of compartments sorted alphanumerically.
     */
    public Compartment[] getCompartments()
    {

        Collections.sort(_compartments);

        return _compartments.toArray(new Compartment[_compartments.size()]);

    }

    /**
     * Adds a compartment.
     * 
     * @param nickname
     */
    public void addCompartment(Compartment nickname)
    {
        _compartments.add(nickname);
    }

    /*
     * ----------------------------------------------------------------------
     * Override
     * ----------------------------------------------------------------------
     */

    @Override
    public String toString()
    {
        String marking = "";

        for (Compartment nickname : getCompartments())
        {
            marking += "-" + nickname.toString();
        }

        return _program + marking;
    }

    @Override
    public int compareTo(SAPProgram value)
    {
        return getProgramName().compareToIgnoreCase(value.getProgramName());
    }

}
