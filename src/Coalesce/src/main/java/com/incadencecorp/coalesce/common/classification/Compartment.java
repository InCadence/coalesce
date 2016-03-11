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

import org.apache.commons.lang.StringUtils;

import com.incadencecorp.coalesce.common.helpers.StringHelper;

/**
 * Object that represents an ACCM nickname.
 * 
 * @author Derek
 *
 */
public class Compartment implements Comparable<Compartment> {

    /*
     * ----------------------------------------------------------------------
     * Private Members
     * ----------------------------------------------------------------------
     */

    private String _compartment;
    private List<String> _subcompartment;

    /*
     * ----------------------------------------------------------------------
     * Constructors
     * ----------------------------------------------------------------------
     */

    /**
     *
     * @param compartment
     * @param subs
     */
    public Compartment(String compartment, String... subs)
    {
        this(compartment);

        _subcompartment.addAll(Arrays.asList(subs));
    }

    /**
     * Parses the provided string for the nick name and caveats.
     * 
     * @param value
     */
    public Compartment(String value)
    {

        _subcompartment = new ArrayList<String>();

        String[] parts = value.trim().split(" ");

        if (parts.length > 0)
        {
            _compartment = parts[0].trim();

            for (int ii = 1; ii < parts.length; ii++)
            {
                if (!StringHelper.isNullOrEmpty(parts[ii]))
                {
                    _subcompartment.add(parts[ii].trim());
                }
            }

        }

    }

    /*
     * ----------------------------------------------------------------------
     * Public Methods
     * ----------------------------------------------------------------------
     */

    /**
     * Adds an additional caveat.
     * 
     * @param caveat
     */
    public void addSubCompartment(String caveat)
    {
        _subcompartment.add(caveat);
    }

    /**
     * 
     * @return a list of caveats sorted alphanumerically.
     */
    public String[] getSubCompartments()
    {

        // Sort the list
        Collections.sort(_subcompartment);

        return _subcompartment.toArray(new String[_subcompartment.size()]);
    }

    /**
     * 
     * @return the nickname.
     */
    public String getCompartmentName()
    {
        return _compartment;
    }

    /*
     * ----------------------------------------------------------------------
     * Overrides
     * ----------------------------------------------------------------------
     */

    @Override
    public String toString()
    {
        if (getSubCompartments().length != 0)
        {
            return _compartment + " " + StringUtils.join(getSubCompartments(), " ");
        }
        else
        {
            return _compartment;
        }

    }

    @Override
    public int compareTo(Compartment value)
    {
        return getCompartmentName().compareToIgnoreCase(value.getCompartmentName());
    }

}
