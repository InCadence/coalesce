/**
 * ///-----------SECURITY CLASSIFICATION: UNCLASSIFIED------------------------
 * /// Copyright 2015 - Lockheed Martin Corporation, All Rights Reserved /// ///
 * Notwithstanding any contractor copyright notice, the government has ///
 * Unlimited Rights in this work as defined by DFARS 252.227-7013 and ///
 * 252.227-7014. Use of this work other than as specifically authorized by ///
 * these DFARS Clauses may violate government rights in this work. /// /// DFARS
 * Clause reference: 252.227-7013 (a)(16) and 252.227-7014 (a)(16) /// Unlimited
 * Rights. The Government has the right to use, modify, /// reproduce, perform,
 * display, release or disclose this computer software /// in whole or in part,
 * in any manner, and for any purpose whatsoever, /// and to have or authorize
 * others to do so. /// /// Distribution Statement D. Distribution authorized to
 * the Department of /// Defense and U.S. DoD contractors only in support of US
 * DoD efforts. /// Other requests shall be referred to the ACINT Modernization
 * Program /// Management under the Director of the Office of Naval
 * Intelligence. ///
 * -------------------------------UNCLASSIFIED---------------------------------
 */

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
public class SCIControl {

    /*
     * ----------------------------------------------------------------------
     * Private Members
     * ----------------------------------------------------------------------
     */

    private List<SCIElement> _sci;

    /*
     * ----------------------------------------------------------------------
     * Constructors
     * ----------------------------------------------------------------------
     */

    /**
     * Default Constructor
     */
    public SCIControl()
    {
        _sci = new ArrayList<SCIElement>();
    }

    /**
     * Parses the ACCM controls from a string.
     * 
     * @param value
     */
    public SCIControl(String value)
    {
        this();

        if (!StringHelper.isNullOrEmpty(value))
        {
            value = value.toUpperCase();

            for (String part : value.split("/"))
            {
                if (!StringHelper.isNullOrEmpty(part))
                {
                    _sci.add(new SCIElement(part));
                }
            }
        }

    }

    /**
     * @param sci
     */
    public SCIControl(SCIElement... sci)
    {
        this();
        _sci.addAll(Arrays.asList(sci));
    }

    /*
     * ----------------------------------------------------------------------
     * Public Methods
     * ----------------------------------------------------------------------
     */

    /**
     * @return a list of programs.
     */
    public SCIElement[] getElements()
    {

        Collections.sort(_sci);

        return _sci.toArray(new SCIElement[_sci.size()]);
    }

    /**
     * Adds a program.
     * 
     * @param value
     */
    public void addElement(SCIElement value)
    {
        _sci.add(value);
    }

    /**
     * @return <code>true</code> if controls have been set.
     */
    public boolean hasControls()
    {
        return _sci.size() > 0;
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

        for (SCIElement element : getElements())
        {

            if (!StringHelper.isNullOrEmpty(marking))
            {
                marking += "/";
            }

            marking += element.toString();
        }

        return marking;

    }

}
