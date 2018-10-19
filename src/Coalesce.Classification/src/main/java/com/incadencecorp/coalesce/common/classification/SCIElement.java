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
 * Object that represents SCI elemnt.
 * 
 * @author Derek
 */
public class SCIElement implements Comparable<SCIElement> {

    /*
     * ----------------------------------------------------------------------
     * Private Members
     * ----------------------------------------------------------------------
     */

    private String _controlSystem;
    private List<Compartment> _compartments;

    /*
     * ----------------------------------------------------------------------
     * Constructors
     * ----------------------------------------------------------------------
     */

    /**
     * Default Constructor
     */
    private SCIElement()
    {
        _compartments = new ArrayList<>();
    }

    /**
     * Parses the ACCM controls from a string.
     * 
     * @param value
     */
    public SCIElement(String value)
    {
        this();

        value = value.toUpperCase();

        String[] parts = value.split("-");

        if (parts.length > 0)
        {
            _controlSystem = parts[0];
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
     * @param controlSystem
     * @param compartments
     */
    public SCIElement(String controlSystem, Compartment... compartments)
    {
        _controlSystem = controlSystem;
        _compartments = Arrays.asList(compartments);
    }

    /*
     * ----------------------------------------------------------------------
     * Public Methods
     * ----------------------------------------------------------------------
     */

    /**
     * @return the control system.
     */
    public String getControlSystem()
    {
        return _controlSystem;
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
     * @param compartment
     */
    public void addCompartment(Compartment compartment)
    {
        _compartments.add(compartment);
    }

    /*
     * ----------------------------------------------------------------------
     * Overrides
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

        return _controlSystem + marking;
    }

    @Override
    public int compareTo(SCIElement o)
    {
        return getControlSystem().compareToIgnoreCase(o.getControlSystem());
    }

}
