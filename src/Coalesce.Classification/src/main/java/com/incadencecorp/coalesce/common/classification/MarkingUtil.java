/*-----------------------------------------------------------------------------'
 Copyright 2016 - InCadence Strategic Solutions Inc., All Rights Reserved

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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

/**
 * This utility class handles converting security CAPCO markings into
 * enumeration list.
 * 
 * @author n78554
 */
public class MarkingUtil {

    /**
     * Defines the separating character when combining parts to create a
     * enumerated list.
     */
    public static final String SEPERATOR = "-";

    public static SAPProgram[] fromSAPSet(Set<String> values)
    {
        Map<String, SAPProgram> controls = new HashMap<String, SAPProgram>();

        for (String value : values)
        {
            String[] parts = StringUtils.split(value, SEPERATOR);

            if (parts.length > 0)
            {
                SAPProgram currentControl;

                if (controls.containsKey(parts[0]))
                {
                    currentControl = controls.get(parts[0]);
                }
                else
                {
                    currentControl = new SAPProgram(parts[0]);
                }

                if (parts.length > 1)
                {
                    Compartment currentCompartment = null;

                    for (Compartment compartment : currentControl.getCompartments())
                    {
                        if (compartment.getCompartmentName().equalsIgnoreCase(parts[1]))
                        {
                            currentCompartment = compartment;
                            break;
                        }
                    }

                    if (currentCompartment == null)
                    {
                        currentCompartment = new Compartment(parts[1]);
                        currentControl.addCompartment(currentCompartment);
                    }

                    if (parts.length > 2)
                    {

                        boolean found = false;

                        for (String subCompartment : currentCompartment.getSubCompartments())
                        {
                            if (subCompartment.equalsIgnoreCase(parts[2]))
                            {
                                found = true;
                                break;
                            }
                        }

                        if (!found)
                        {
                            currentCompartment.addSubCompartment(parts[2]);
                        }

                    }

                }

                controls.put(currentControl.getProgramName(), currentControl);
            }

        }

        return controls.values().toArray(new SAPProgram[controls.size()]);
    }

    public static String[] toArray(SAPProgram[] programs)
    {
        return toSet(programs).toArray(new String[programs.length]);
    }

    public static Set<String> toSet(SAPProgram[] programs)
    {

        Set<String> groups = new HashSet<String>();

        for (SAPProgram program : programs)
        {

            for (Compartment compartment : program.getCompartments())
            {

                for (String sub : compartment.getSubCompartments())
                {
                    groups.add(program.getProgramName() + SEPERATOR + compartment.getCompartmentName() + SEPERATOR + sub);
                }

                groups.add(program.getProgramName() + SEPERATOR + compartment.getCompartmentName());
            }

            groups.add(program.getProgramName());
        }

        return groups;
    }

    public static SCIElement[] fromSCISet(Set<String> values)
    {
        Map<String, SCIElement> controls = new HashMap<String, SCIElement>();

        for (String value : values)
        {
            String[] parts = StringUtils.split(value, SEPERATOR);

            if (parts.length > 0)
            {
                SCIElement currentControl;

                if (controls.containsKey(parts[0]))
                {
                    currentControl = controls.get(parts[0]);
                }
                else
                {
                    currentControl = new SCIElement(parts[0]);
                }

                if (parts.length > 1)
                {
                    Compartment currentCompartment = null;

                    for (Compartment compartment : currentControl.getCompartments())
                    {
                        if (compartment.getCompartmentName().equalsIgnoreCase(parts[1]))
                        {
                            currentCompartment = compartment;
                            break;
                        }
                    }

                    if (currentCompartment == null)
                    {
                        currentCompartment = new Compartment(parts[1]);
                        currentControl.addCompartment(currentCompartment);
                    }

                    if (parts.length > 2)
                    {

                        boolean found = false;

                        for (String subCompartment : currentCompartment.getSubCompartments())
                        {
                            if (subCompartment.equalsIgnoreCase(parts[2]))
                            {
                                found = true;
                                break;
                            }
                        }

                        if (!found)
                        {
                            currentCompartment.addSubCompartment(parts[2]);
                        }

                    }

                }

                controls.put(currentControl.getControlSystem(), currentControl);
            }

        }

        return controls.values().toArray(new SCIElement[controls.size()]);
    }

    public static String[] toArray(SCIElement[] elements)
    {
        return toSet(elements).toArray(new String[elements.length]);
    }

    public static Set<String> toSet(SCIElement[] elements)
    {

        Set<String> groups = new HashSet<String>();

        for (SCIElement element : elements)
        {

            for (Compartment compartment : element.getCompartments())
            {

                for (String sub : compartment.getSubCompartments())
                {
                    groups.add(element.getControlSystem() + SEPERATOR + compartment.getCompartmentName() + SEPERATOR + sub);
                }

                groups.add(element.getControlSystem() + SEPERATOR + compartment.getCompartmentName());
            }

            groups.add(element.getControlSystem());
        }

        return groups;
    }

    public static ACCMNickname[] fromACCMSet(Set<String> values)
    {
        ACCMNickname[] results = new ACCMNickname[values.size()];

        int ii = 0;

        for (String value : values)
        {
            results[ii++] = new ACCMNickname(value);
        }

        return results;
    }

    public static String[] toArray(ACCMNickname[] nicknames)
    {
        return toSet(nicknames).toArray(new String[nicknames.length]);
    }

    public static Set<String> toSet(ACCMNickname[] nicknames)
    {
        Set<String> groups = new HashSet<String>();

        for (ACCMNickname nickname : nicknames)
        {
            groups.add(nickname.getNickname());
        }

        return groups;
    }

}
