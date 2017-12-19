/*-----------------------------------------------------------------------------'
 Copyright 2017 - InCadence Strategic Solutions Inc., All Rights Reserved

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
 * Object that represents a SAP control.
 * 
 * @author Derek
 */
public class SAPControl {

    /*
     * ----------------------------------------------------------------------
     * Private Members
     * ----------------------------------------------------------------------
     */

    private List<SAPProgram> _programs;

    /*
     * ----------------------------------------------------------------------
     * Constructors
     * ----------------------------------------------------------------------
     */

    /**
     * Default Constructor
     */
    public SAPControl()
    {
        _programs = new ArrayList<SAPProgram>();
    }

    /**
     * Parses the ACCM controls from a string.
     * 
     * @param value
     */
    public SAPControl(String value)
    {
        this();

        if (!StringHelper.isNullOrEmpty(value))
        {
            value = value.toUpperCase();

            // Trim off SAR
            if (value.startsWith("SAR-"))
            {
                value = value.replace("SAR-", "");
            }

            if (value.startsWith("SPECIAL ACCESS REQUIRED-"))
            {
                value = value.replace("SPECIAL ACCESS REQUIRED-", "");
            }

            for (String part : value.split("/"))
            {
                if (!StringHelper.isNullOrEmpty(part))
                {
                    _programs.add(new SAPProgram(part));
                }
            }
        }

    }

    /**
     * @param programs
     */
    public SAPControl(SAPProgram... programs)
    {
        this();

        _programs.addAll(Arrays.asList(programs));
    }

    /*
     * ----------------------------------------------------------------------
     * Public Methods
     * ----------------------------------------------------------------------
     */

    /**
     * @return a list of programs.
     */
    public SAPProgram[] getPrograms()
    {

        Collections.sort(_programs);

        return _programs.toArray(new SAPProgram[_programs.size()]);
    }

    /**
     * Adds a program.
     * 
     * @param program
     */
    public void addProgram(SAPProgram program)
    {
        _programs.add(program);
    }

    /**
     * @return <code>true</code> if controls have been set.
     */
    public boolean hasControls()
    {
        return _programs.size() > 0;
    }

    /*
     * ----------------------------------------------------------------------
     * Override
     * ----------------------------------------------------------------------
     */

    @Override
    public String toString()
    {
        return toString(true);
    }

    /**
     * 
     * @param asPortion
     * @return the marking serialized to a string.
     */
    public String toString(boolean asPortion)
    {

        String marking = "";

        for (SAPProgram program : getPrograms())
        {

            if (!StringHelper.isNullOrEmpty(marking))
            {
                marking += "/";
            }

            marking += program.toString();
        }

        if (asPortion)
        {
            return "SAR-" + marking;
        }
        else
        {
            return "SPECIAL ACCESS REQUIRED-" + marking;
        }

    }

}
