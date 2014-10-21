package com.incadencecorp.coalesce.common.classification;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.incadencecorp.coalesce.common.helpers.StringHelper;

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

public class Marking implements Serializable, Comparable<Marking> {

    private static final long serialVersionUID = 956620060017206311L;

    public static final String UNCLASSIFIED = "UNCLASSIFIED";
    public static final String RESTRICTED = "RESTRICTED";
    public static final String CONFIDENTIAL = "CONFIDENTIAL";
    public static final String SECRET = "SECRET";
    public static final String TOPSECRET = "TOP SECRET";

    private boolean _isNATO;
    private List<ISO3166Country> _selectedCountries;
    private MarkingValue _classification;
    private boolean _isFOUO;
    private boolean _isLES;
    private boolean _isORCON;
    private boolean _isIMCON;
    private boolean _isRELIDO;
    private boolean _isPROPIN;
    private boolean _isFISA;
    private boolean _isNOFORN;
    private boolean _isDSEN;
    private List<ISO3166Country> _releaseToCountries;
    private List<ISO3166Country> _displayOnlyCountries;
    private boolean _isLIMDIS;
    private boolean _isEXDIS;
    private boolean _isSBU;
    private boolean _isSBUNF;
    private List<String> _nicknames;

    private static List<MarkingValue> _classificationList;

    private static List<MarkingValue> getClassifications()
    {
        if (_classificationList == null)
        {
            _classificationList = FieldValues.getListOfClassifications(null, true);
        }

        return _classificationList;
    }

    @Override
    public boolean equals(Object other)
    {
        if (!(other instanceof Marking)) return false;

        return toPortionString().equals(((Marking) other).toPortionString());
    }

    @Override
    public int compareTo(Marking other)
    {

        String title = getClassification().getTitle();
        String otherTitle = other.getClassification().getTitle();

        if (title.contains("TOP SECRET"))
        {

            if (otherTitle.contains("TOP SECRET"))
            {
                return 0;
            }

        }
        else if (title.contains("SECRET"))
        {

            if (otherTitle.contains("TOP SECRET"))
            {
                return 1;
            }
            else if (otherTitle.contains("SECRET"))
            {
                return 0;
            }

        }
        else if (title.contains("CONFIDENTIAL"))
        {

            if (otherTitle.contains("SECRET"))
            { // handles ts and s
                return 1;
            }
            else if (otherTitle.contains("CONFIDENTIAL"))
            {
                return 0;
            }

        }
        else if (title.contains("RESTRICTED"))
        {

            if (otherTitle.contains("SECRET") || otherTitle.contains("CONFIDENTIAL"))
            {
                return 1;
            }
            else if (otherTitle.contains("RESTRICTED"))
            {
                return 0;
            }

        }
        else if (title.contains("UNCLASSIFIED"))
        {

            if (otherTitle.contains("SECRET") || otherTitle.contains("CONFIDENTIAL") || otherTitle.contains("RESTRICTED"))
            {
                return 1;
            }
            else if (otherTitle.contains("UNCLASSIFIED"))
            {
                return 0;
            }
        }

        return -1;

    }

    /**
     * Class constructor. Creates a Marking class with a default value of Unclassified.
     */
    public Marking()
    {
        this("UNCLASSIFIED");
    }

    /**
     * Class constructor. Creates a Marking class with a classification set to the contents of the String parameter
     * @param markingString
     *     allowed object is
     *     {@link String }
     */
    public Marking(String markingString)
    {

        int index = 0;
        boolean isPortionMarking = false;

        if (markingString == null)
        {
            markingString = "";
        }

        // CLASSIFICATION//CONTROLSYSTEM1/CONTROLSYSTEM2-COMPARTMENT1 SUBCOMPARTMENT1 SUBCOMPARTMENT2-COMPARTMENT2//
        // SPECIAL ACCESS REQUIRED-PROGRAM NICKNAME/CODEWORD//" & _
        // RD-SG 1//FGI DEU//FOUO//LIMDIS

        markingString = markingString.trim();

        if (markingString.startsWith("(") && markingString.endsWith(")"))
        {
            isPortionMarking = true;
            markingString = StringHelper.trimParentheses(markingString);
        }

        // If it is blank or invalid make it Unclass
        if (isPortionMarking)
        {
            if (markingString.length() < 1)
            {
                markingString = "U";
            }
        }
        else
        {
            if (markingString.length() < 6)
            {
                markingString = "UNCLASSIFIED";
            }
        }

        // If it does not have // assume it is the USA class level only
        if (!markingString.contains("//"))
        {

            if (isPortionMarking)
            {
                _classification = FieldValues.getMarkingValueByPortion(markingString, getClassifications());
            }
            else
            {
                _classification = FieldValues.getMarkingValueByTitle(markingString, getClassifications());
            }

            getSelectedCountries().add(ISO3166Country.getUSA());
            return;
        }

        // Parse out the marking
        markingString = markingString.replace("//", "|");
        String[] parts = markingString.split("\\|");

        if (parts.length == 0) return; // Invalid Classification marking

        // Reset values first
        _isNATO = false;
        getSelectedCountries().clear();
        _classification = null;

        // CLASSIFICATION//
        if (!parts[index].equals(""))
        {

            if (isPortionMarking)
            {
                _classification = FieldValues.getMarkingValueByPortion(parts[0], getClassifications());
            }
            else
            {
                _classification = FieldValues.getMarkingValueByTitle(parts[0], getClassifications());
            }

            getSelectedCountries().add(ISO3166Country.getUSA());

            index += 1;

            // //CLASSIFICATION
        }
        else if (parts.length > 1)
        {

            index += 1;

            if (parts[index].startsWith("JOINT"))
            {

                List<String> joint = Arrays.asList(parts[index].split(" "));
                String marking = "";

                // The count must be at least 4 unless TS which would make it 5 eg. '//JOINT TOP SECRET USA DEU' or
                // '//JOINT TS USA DEU' for a portion
                if (joint.size() < (joint.contains("TOP") ? 5 : 4)) return; // Invalid Joint Classification marking

                marking = joint.contains("TOP") ? "JOINT TOP SECRET" : "JOINT " + joint.get(1);

                if (isPortionMarking)
                {
                    _classification = FieldValues.getMarkingValueByPortion(marking, getClassifications());
                }
                else
                {
                    _classification = FieldValues.getMarkingValueByTitle(marking, getClassifications());
                }

                for (int pos = (joint.contains("TOP") ? 3 : 2); pos < joint.size(); pos++)
                {
                    getSelectedCountries().add(FieldValues.getCountryByAlpha3(joint.get(pos)));
                }

            }
            else if (parts[index].startsWith("COSMIC") || parts[index].startsWith("NATO") || parts[index].contains("ATOMAL"))
            {

                _isNATO = true;
                _classification = FieldValues.getMarkingValueByTitle(parts[index], getClassifications());

            }
            else if ((isPortionMarking && parts[index].startsWith("CTS")) || parts[index].startsWith("N"))
            {

                _isNATO = true;
                _classification = FieldValues.getMarkingValueByPortion(parts[index], getClassifications());

            }
            else
            {

                List<String> fgc = Arrays.asList(parts[index].split(" "));

                if (fgc.size() < (fgc.contains("TOP") ? 3 : 2)) return; // Invalid FGC Classification marking

                ISO3166Country country = FieldValues.getCountryByAlpha3(fgc.get(0));
                if (country != null)
                {
                    getSelectedCountries().add(country);
                }

                if (isPortionMarking)
                {
                    _classification = FieldValues.getMarkingValueByPortion(fgc.get(1), getClassifications());
                }
                else
                {
                    _classification = FieldValues.getMarkingValueByTitle((fgc.contains("TOP") ? "TOP SECRET" : fgc.get(1)),
                                                                         getClassifications());
                }

            }

            index += 1;

        }

        while (parts.length > index)
        {
            // CLASSIFICATION//CONTROLSYSTEM1/CONTROLSYSTEM2-COMPARTMENT1 SUBCOMPARTMENT1 SUBCOMPARTMENT2-COMPARTMENT2//
            // SPECIAL ACCESS REQUIRED-PROGRAM NICKNAME/CODEWORD//" & _
            // RD-SG 1//FGI DEU//FOUO//LIMDIS/ACCM-ALPHA/BRAVO

            String[] subparts = parts[index].split("/");
            boolean isACCM = false;

            for (String subpart : subparts)
            {
                // Disem
                if (subpart.startsWith("FOUO-LES"))
                {

                    _isFOUO = true;
                    _isLES = true;

                }
                else if (subpart.startsWith("FOUO"))
                {

                    _isFOUO = true;

                }
                else if (subpart.startsWith("LES"))
                {

                    _isLES = true;

                }
                else if (subpart.startsWith("RELIDO"))
                {

                    _isRELIDO = true;

                }
                else if (subpart.startsWith("PROPIN") || (isPortionMarking && subpart.startsWith("PR")))
                {

                    _isPROPIN = true;

                }
                else if (subpart.startsWith("FISA"))
                {

                    _isFISA = true;

                }
                else if (subpart.startsWith("IMCON") || (isPortionMarking && subpart.startsWith("IMC")))
                {

                    _isIMCON = true;

                }
                else if (subpart.startsWith("ORCON") || (isPortionMarking && subpart.startsWith("OC")))
                {

                    _isORCON = true;

                }
                else if (subpart.startsWith("DSEN"))
                {

                    _isDSEN = true;

                }
                else if (subpart.startsWith("NOFORN") || (isPortionMarking && subpart.startsWith("NF")))
                {

                    _isNOFORN = true;

                }
                else if (subpart.startsWith("DISPLAY ONLY"))
                {

                    // Pull the commas out
                    subpart = subpart.replace(",", "");
                    String[] disp = subpart.split(" ");

                    if (disp.length < 3) return; // Invalid DISPLAY ONLY Desemination marking

                    for (int pos = 2; pos < disp.length; pos++)
                    {
                        getDisplayOnlyCountries().add(FieldValues.getCountryByAlpha3(disp[pos]));
                    }

                }
                else if (subpart.startsWith("REL TO"))
                {

                    // Pull the commas out
                    subpart = subpart.replace(",", "");
                    String[] relto = subpart.split(" ");

                    if (relto.length < 3) return; // Invalid REL TO Desemination marking

                    for (int pos = 2; pos < relto.length; pos++)
                    {
                        getReleaseToCountries().add(FieldValues.getCountryByAlpha3(relto[pos]));
                    }

                    // Other Disem
                }
                else if (subpart.startsWith("SBU NOFORN") || (isPortionMarking && subpart.startsWith("SBU-NF")))
                {

                    _isSBUNF = true;

                }
                else if (subpart.startsWith("SBU"))
                {

                    _isSBU = true;

                }
                else if (subpart.startsWith("EXDIS") || (isPortionMarking && subpart.startsWith("XD")))
                {

                    _isEXDIS = true;

                }
                else if (subpart.startsWith("LIMITED DISTRIBUTION") || (isPortionMarking && subpart.startsWith("DS")))
                {

                    _isLIMDIS = true;

                }
                else if (subpart.startsWith("ACCM"))
                {

                    isACCM = true;

                    String[] accm = subpart.split("-");

                    if (accm.length != 2) return; // Invalid ACCM Other Desemination marking

                    getNicknames().add(accm[1]);

                }
                else
                {

                    if (isACCM)
                    { // Assume this is a ACCM Nickname

                        getNicknames().add(subpart);
                    }
                    else
                    {

                        return; // Unrecognized marking
                    }
                }
            } // subparts

            index += 1;

        }

    }

    /**
     * Returns the value of the isNATO property
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     */
    public boolean getIsNATO()
    {
        return _isNATO;
    }

    /**
     * Sets the value of the isNATO property
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     */
    public void setIsNATO(boolean value)
    {
        _isNATO = value;
    }

    /**
     * Returns the value of the isJOINT property
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     */
    public boolean getIsJOINT()
    {
        return getSelectedCountries().size() > 1;
    }

    /**
     * Returns the value of the isFOUO property
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     */
    public boolean getIsFOUO()
    {
        return _isFOUO;
    }

    /**
     * Sets the value of the isFOUO property
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     */
    public void setIsFOUO(boolean value)
    {
        _isFOUO = value;
    }

    /**
     * Returns the value of the isLES property
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     */
    public boolean getIsLES()
    {
        return _isLES;
    }

    /**
     * Sets the value of the isLES property
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     */
    public void setIsLES(boolean value)
    {
        _isLES = value;
    }

    /**
     * Returns the value of the isORCON property
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     */
    public boolean getIsORCON()
    {
        return _isORCON;
    }

    /**
     * Sets the value of the isORCON property
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     */
    public void setIsORCON(boolean value)
    {
        _isORCON = value;
    }

    /**
     * Returns the value of the isIMCON property
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     */
    public boolean getIsIMCON()
    {
        return _isIMCON;
    }

    /**
     * Sets the value of the isIMCON property
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     */
    public void setIsIMCON(boolean value)
    {
        _isIMCON = value;
    }

    /**
     * Returns the True if there are Display Only countries for this Marking
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     */
    public boolean getIsDisplay_Only()
    {
        return getDisplayOnlyCountries().size() > 0;
    }

    /**
     * Returns the value of the isDSEN property
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     */
    public boolean getIsDSEN()
    {
        return _isDSEN;
    }

    /**
     * Sets the value of the isDSEN property
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     */
    public void setIsDSEN(boolean value)
    {
        _isDSEN = value;
    }
    
    /**
     * Returns the value of the isFISA property
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     */
    public boolean getIsFISA()
    {
        return _isFISA;
    }

    /**
     * Sets the value of the isFISA property
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     */
    public void setIsFISA(boolean value)
    {
        _isFISA = value;
    }

    /**
     * Returns the value of the isPROPIN property
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     */
    public boolean getIsPROPIN()
    {
        return _isPROPIN;
    }

    /**
     * Sets the value of the isPROPIN property
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     */
    public void setIsPROPIN(boolean value)
    {
        _isPROPIN = value;
    }

    /**
     * Returns the value of the isRELIDO property
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     */
    public boolean getIsRELIDO()
    {
        return _isRELIDO;
    }

    /**
     * Sets the value of the isRELIDO property
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     */
    public void setIsRELIDO(boolean value)
    {
        _isRELIDO = value;
    }

    /**
     * Returns the True if there are Releaseable To countries for this Marking
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     */
    public boolean getIsReleaseTo()
    {
        return getReleaseToCountries().size() > 0;
    }

    /**
     * Returns the value of the isNOFORN property
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     */
    public boolean getIsNOFORN()
    {
        return _isNOFORN;
    }

    /**
     * Sets the value of the isNOFORN property
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     */
    public void setIsNOFORN(boolean value)
    {
        _isNOFORN = value;
    }

    /**
     * Returns a list of countries that are owners/producers of this classified information
     * 
     * @return
     *     possible object is
     *     {@link List<ISO3166Country> }
     */
    public List<ISO3166Country> getSelectedCountries()
    {
        if (_selectedCountries == null)
        {
            _selectedCountries = new ArrayList<ISO3166Country>();
        }

        return _selectedCountries;
    }

    /**
     * Returns a list of countries that this classified information is Releaseable To
     * 
     * @return
     *     possible object is
     *     {@link List<ISO3166Country> }
     */
    public List<ISO3166Country> getReleaseToCountries()
    {
        if (_releaseToCountries == null)
        {
            _releaseToCountries = new ArrayList<ISO3166Country>();
        }

        return _releaseToCountries;
    }

    /**
     * Returns a list of countries that this classified information is Displayable To
     * 
     * @return
     *     possible object is
     *     {@link List<ISO3166Country> }
     */
    public List<ISO3166Country> getDisplayOnlyCountries()
    {
        if (_displayOnlyCountries == null)
        {
            _displayOnlyCountries = new ArrayList<ISO3166Country>();
        }

        return _displayOnlyCountries;
    }

    /**
     * Returns the MarkingValue classification assigned to this information
     * 
     * @return
     *     possible object is
     *     {@link MarkingValue }
     */
    public MarkingValue getClassification()
    {
        if (_classification == null)
        {
            _classification = new MarkingValue();
        }

        return _classification;
    }

    /**
     * Sets the MarkingValue classification assigned to this information
     * 
     * @param value
     *     allowed object is
     *     {@link MarkingValue }
     */
    public void setClassification(MarkingValue value)
    {
        _classification = value;
    }

    /**
     * Returns the value of the isLIMDIS property
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     */
    public boolean getIsLIMDIS()
    {
        return _isLIMDIS;
    }

    /**
     * Sets the value of the isLIMDIS property
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     */
    public void setIsLIMDIS(boolean value)
    {
        _isLIMDIS = value;
    }

    /**
     * Returns the value of the isEXDIS property
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     */
    public boolean getIsEXDIS()
    {
        return _isEXDIS;
    }

    /**
     * Sets the value of the isEXDIS property
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     */
    public void setIsEXDIS(boolean value)
    {
        _isEXDIS = value;
    }

    /**
     * Returns the value of the isSBU property
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     */
    public boolean getIsSBU()
    {
        return _isSBU;
    }

    /**
     * Sets the value of the isSBU property
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     */
    public void setIsSBU(boolean value)
    {
        _isSBU = value;
    }

    /**
     * Returns the value of the isSBUNF property
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     */
    public boolean getIsSBUNF()
    {
        return _isSBUNF;
    }

    /**
     * Sets the value of the isSBUNF property
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     */
    public void setIsSBUNF(boolean value)
    {
        _isSBUNF = value;
    }

    /**
     * Returns the True if there are ACCM nicknames for this Marking
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     */
    public boolean getIsACCM()
    {
        return getNicknames().size() > 0;
    }

    /**
     * Returns a list of ACCM nicknames that are assigned to this classified information 
     * 
     * @return
     *     possible object is
     *     {@link List<String> }
     */
    public List<String> getNicknames()
    {
        if (_nicknames == null)
        {
            _nicknames = new ArrayList<String>();
        }
        ;

        return _nicknames;
    }

    /**
     * Returns True if dissemination controls have been applied to this Marking
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     */
    public boolean hasDeseminationControls()
    {
        return getIsFOUO() || getIsLES() || getIsORCON() || getIsIMCON() || getIsDSEN() || getIsDisplay_Only()
                || getIsFISA() || getIsNOFORN() || getIsPROPIN() || getIsPROPIN() || getIsReleaseTo() || getIsRELIDO();
    }

    /**
     * Returns True if non-IC markings have been applied to this Marking
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     */
    public boolean hasOtherDeseminationControls()
    {
        return getIsLIMDIS() || getIsEXDIS() || getIsSBU() || getIsSBUNF() || getIsACCM();
    }

    /**
     * Returns the classification to be applied to a section, part, paragraph, or similar portion of a 
     * classified document
     * 
     * @return
     *     possible object is
     *     {@link String }
     */
    public String toPortionString()
    {
        return "(" + toString(true) + ")";
    }

    /**
     * Returns the classification and associated markings of a classified document
     * 
     * @return
     *     possible object is
     *     {@link String }
     */
    @Override
    public String toString()
    {
        return toString(false);
    }

    /**
     * Returns the classification of a classified set of data, either full or portion based on the boolean parameter.
     * If True, the String returned applies to a section, part, paragraph, or similar portion 
     * If False, the String returned is the full classification and associated markings
     * 
     * @param toPortion
     *     allowed object is
     *     {@link Boolean }
     *     
     * @return
     *     possible object is
     *     {@link String }
     */
    public String toString(boolean toPortion)
    {

        String marking = "";

        // JOINT and NATO and FGI start with //
        if (getIsJOINT() || getIsNATO() || !getSelectedCountries().contains(ISO3166Country.getUSA()))
        {

            marking += "//";

            // If there is only one country and we know it is not USA then this if FGI eg. '//DEU SECRET'
            if (getSelectedCountries().size() == 1)
            {
                marking += getSelectedCountries().get(0).getAlpha3() + " ";
            }
        }

        // Now add the classification level
        marking += toPortion ? getClassification().getPortion() : getClassification().getTitle();

        // JOINT then adds the list of countries
        if (getIsJOINT())
        {

            for (ISO3166Country country : getSelectedCountries())
            {
                marking += " " + country.getAlpha3();
            }
        }

        if (hasDeseminationControls())
        {

            marking += "//";

            if (getIsFOUO())
            {

                if (getIsLES())
                {
                    marking += "FOUO-LES/";
                }
                else
                {
                    marking += "FOUO/";
                }
            }
            else
            {
                if (getIsLES())
                {
                    marking += "LES/";
                }
            }

            marking = addDeseminations(marking, "REL TO ", getIsReleaseTo(), getReleaseToCountries());

            marking = addDeseminations(marking, "DISPLAY ONLY ", getIsDisplay_Only(), getDisplayOnlyCountries());

            if (getIsRELIDO()) marking += "RELIDO/";
            if (getIsPROPIN()) marking += toPortion ? "PR/" : "PROPIN/";
            if (getIsFISA()) marking += "FISA/";
            if (getIsIMCON()) marking += toPortion ? "IMC/" : "IMCON/";
            if (getIsORCON()) marking += toPortion ? "OC/" : "ORCON/";
            if (getIsDSEN()) marking += "DSEN/";
            if (getIsNOFORN()) marking += toPortion ? "NF/" : "NOFORN/";

            marking = marking.substring(0, marking.length() - 1);
        }

        if (hasOtherDeseminationControls())
        {

            marking += "//";

            if (getIsLIMDIS()) marking += toPortion ? "DS/" : "LIMITED DISTRIBUTION/";
            if (getIsEXDIS()) marking += toPortion ? "XD/" : "EXDIS/";
            if (getIsSBU()) marking += "SBU/";
            if (getIsSBUNF()) marking += toPortion ? "SBU-NF/" : "SBU NOFORN/";

            if (getIsACCM())
            {

                marking += "ACCM-";

                Collections.sort(_nicknames);
                for (String nickname : getNicknames())
                {

                    marking += nickname + "/";
                }

            }

            // Remove trailing /
            marking = marking.substring(0, marking.length() - 1);
        }

        return marking;

    }

    private String addDeseminations(String marking, String label, boolean display, List<ISO3166Country> countries)
    {

        if (display && countries.size() > 0)
        {

            marking += label;

            // USA comes first in deseminations
            if (countries.contains(ISO3166Country.getUSA()))
            {

                marking += "USA";
                if (countries.size() > 1)
                {
                    marking += ", ";
                }
            }

            Collections.sort(countries);
            for (ISO3166Country country : countries)
            {

                if (!(country.compareTo(ISO3166Country.getUSA()) == 0))
                {

                    marking += country.getAlpha3() + ", ";
                }
            }

            // Remove training ', '
            marking = marking.trim();
            marking = marking.substring(0, marking.length() - 1);
            marking += "/";
        }

        return marking;

    }

}
