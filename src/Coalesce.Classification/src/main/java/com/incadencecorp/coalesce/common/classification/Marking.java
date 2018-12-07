package com.incadencecorp.coalesce.common.classification;

import com.incadencecorp.coalesce.common.classification.helpers.StringHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

/**
 * Used to parse and format the classification and dissemination controls for a
 * given marking.
 *
 * @author wbrannock, Derek
 */
public class Marking implements Serializable, Comparable<Marking> {

    private static final long serialVersionUID = 956620060017206311L;

    /**
     * Unclassified
     */
    public static final String UNCLASSIFIED = "UNCLASSIFIED";
    /**
     * Restricted
     */
    public static final String RESTRICTED = "RESTRICTED";
    /**
     * Confidential
     */
    public static final String CONFIDENTIAL = "CONFIDENTIAL";
    /**
     * Secret
     */
    public static final String SECRET = "SECRET";
    /**
     * Top Secret
     */
    public static final String TOPSECRET = "TOP SECRET";

    private SCIControl controlSCI = new SCIControl();
    private SAPControl controlSAP = new SAPControl();
    private ACCMControl controlACCM = new ACCMControl();

    private boolean _isNATO;
    private List<ISO3166Country> _selectedCountries;
    private MarkingValue _classification;
    private boolean _isFOUO;
    private boolean _isLES;
    private boolean _isLESNF;
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
    private boolean _isNODIS;
    private boolean _isRSEN;
    private boolean _isSSI;

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
    public boolean equals(final Object other)
    {
        if (!(other instanceof Marking))
            return false;

        return toPortionString().equalsIgnoreCase(((Marking) other).toPortionString());
    }

    @Override
    public int compareTo(final Marking other)
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
     * Class constructor. Creates a Marking class with a default value of
     * Unclassified.
     */
    public Marking()
    {
        this("UNCLASSIFIED");
    }

    /**
     * Class constructor. Creates a Marking class with a classification set to
     * the contents of the String parameter.
     *
     * @param markingString allowed object is {@link String }
     */
    public Marking(String markingString)
    {
        int index = 0;
        boolean isPortionMarking = false;

        if (markingString == null)
        {
            markingString = "";
        }

        markingString = markingString.trim().toUpperCase();

        // CLASSIFICATION//CONTROLSYSTEM1/CONTROLSYSTEM2-COMPARTMENT1
        // SUBCOMPARTMENT1 SUBCOMPARTMENT2-COMPARTMENT2//
        // SPECIAL ACCESS REQUIRED-PROGRAM NICKNAME/CODEWORD//" & _
        // RD-SG 1//FGI DEU//FOUO//LIMDIS

        // Is a portion marking?
        if (markingString.startsWith("(") && markingString.endsWith(")"))
        {
            // Yes; Trim Parentheses
            isPortionMarking = true;
            markingString = StringHelper.trimParentheses(markingString);
        }

        // If it is blank or invalid make it unclassified
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

        if (parts.length == 0)
        {
            throw new IllegalArgumentException("Invalid Classification Marking");
        }

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

                // The count must be at least 4 unless TS which would make it 5
                // eg. '//JOINT TOP SECRET USA DEU' or
                // '//JOINT TS USA DEU' for a portion
                if (joint.size() < (joint.contains("TOP") ? 5 : 4))
                {
                    throw new IllegalArgumentException("Invalid Joint Classification Marking");
                }

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

                if (fgc.size() < (fgc.contains("TOP") ? 3 : 2))
                {
                    throw new IllegalArgumentException("Invalid FGC Classification Marking");
                }

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

            // CLASSIFICATION//CONTROLSYSTEM1/CONTROLSYSTEM2-COMPARTMENT1
            // SUBCOMPARTMENT1 SUBCOMPARTMENT2-COMPARTMENT2//
            // SPECIAL ACCESS REQUIRED-PROGRAM NICKNAME/CODEWORD//" & _
            // RD-SG 1//FGI DEU//FOUO//LIMDIS/ACCM-ALPHA/BRAVO

            if (parts[index].startsWith("ACCM-"))
            {
                controlACCM = new ACCMControl(parts[index]);
            }
            else if (parts[index].startsWith("SAR-") || (isPortionMarking && parts[index].startsWith(
                    "SPECIAL ACCESS REQUIRED-")))
            {
                controlSAP = new SAPControl(parts[index]);
            }
            else if (parts[index].startsWith("HCS") || parts[index].startsWith("KDK") || parts[index].startsWith("RSV")
                    || parts[index].startsWith("SI") || parts[index].startsWith("G") || parts[index].startsWith("TK")
                    || parts[index].contains("-") && !parts[index].contains("-NF"))
            {
                controlSCI = new SCIControl(parts[index]);
            }
            else
            {

                String[] subparts = parts[index].split("/");

                for (int ii = 0; ii < subparts.length; ii++)
                {
                    String subpart = subparts[ii];

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
                    else if (subpart.startsWith("LES NOFORN") || (isPortionMarking && subpart.startsWith("LES-NF")))
                    {
                        _isLESNF = true;
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
                    else if (subpart.startsWith("RSEN") || (isPortionMarking && subpart.startsWith("RS")))
                    {
                        _isRSEN = true;
                    }
                    else if (subpart.startsWith("DISPLAY ONLY"))
                    {

                        // Pull the commas out
                        subpart = subpart.replace(",", "");
                        String[] disp = subpart.split(" ");

                        if (disp.length < 3)
                        {
                            throw new IllegalArgumentException("Invalid DISPLAY ONLY Marking");
                        }

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

                        if (relto.length < 3)
                        {
                            throw new IllegalArgumentException("Invalid REL TO Marking");
                        }

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
                    else if (subpart.startsWith("NODIS") || (isPortionMarking && subpart.startsWith("ND")))
                    {
                        _isNODIS = true;
                    }
                    else if (subpart.startsWith("SSI"))
                    {
                        _isSSI = true;
                    }
                    else
                    {
                        // First Control?
                        if (ii == 0 && !controlSCI.hasControls())
                        {
                            // Yes; Treat it as SCI
                            controlSCI = new SCIControl(parts[index]);
                            break;
                        }
                        else
                        {
                            throw new IllegalArgumentException("Invalid Classification Marking: " + subpart);
                        }

                    }
                } // subparts

            }
            index += 1;
        }

    }

    /**
     * @return whether this is a NATO classification.
     */
    public boolean isNATO()
    {
        return _isNATO;
    }

    /**
     * Sets whether this is marked as a NATO classification.
     *
     * @param value
     */
    public void setIsNATO(final boolean value)
    {
        _isNATO = value;
    }

    /**
     * @return whether this is marked as a JOINT classification.
     */
    public boolean isJOINT()
    {
        return getSelectedCountries().size() > 1;
    }

    /**
     * @return whether this is marked as for official use only.
     */
    public boolean isFOUO()
    {
        return _isFOUO;
    }

    /**
     * Sets whether this is marked as for official use only.
     *
     * @param value
     */
    public void setIsFOUO(final boolean value)
    {
        _isFOUO = value;
    }

    /**
     * @return whether this is marked as law enforcement sensitive.
     */
    public boolean isLES()
    {
        return _isLES;
    }

    /**
     * Sets whether this is marked as law enforcement sensitive.
     *
     * @param value
     */
    public void setIsLES(final boolean value)
    {
        _isLES = value;
    }

    /**
     * @return whether this is marked as originator controlled.
     */
    public boolean isORCON()
    {
        return _isORCON;
    }

    /**
     * Sets whether this is marked as originator controlled.
     *
     * @param value
     */
    public void setIsORCON(final boolean value)
    {
        _isORCON = value;
    }

    /**
     * @return whether this is marked as controlled imagery.
     */
    public boolean isIMCON()
    {
        return _isIMCON;
    }

    /**
     * Sets whether this is marked as controlled imagery.
     *
     * @param value
     */
    public void setIsIMCON(final boolean value)
    {
        _isIMCON = value;
    }

    /**
     * @return <code>true</code> if there are Display Only countries for this
     * Marking.
     */
    public boolean isDisplayOnly()
    {
        return getDisplayOnlyCountries().size() > 0;
    }

    /**
     * @return whether this is marked as DEA sensitive.
     */
    public boolean isDSEN()
    {
        return _isDSEN;
    }

    /**
     * Sets whether this is marked as DEA sensitive.
     *
     * @param value
     */
    public void setIsDSEN(final boolean value)
    {
        _isDSEN = value;
    }

    /**
     * @return whether this is marked as foreign intelligence surveillance act.
     */
    public boolean isFISA()
    {
        return _isFISA;
    }

    /**
     * Sets whether this is marked as foreign intelligence surveillance act.
     *
     * @param value
     */
    public void setIsFISA(final boolean value)
    {
        _isFISA = value;
    }

    /**
     * @return whether this is marked as caution-proprietary information
     * involved.
     */
    public boolean isPROPIN()
    {
        return _isPROPIN;
    }

    /**
     * Sets whether this is marked as caution-proprietary information involved.
     *
     * @param value
     */
    public void setIsPROPIN(final boolean value)
    {
        _isPROPIN = value;
    }

    /**
     * @return whether this is marked as releasable by information disclosure
     * official.
     */
    public boolean isRELIDO()
    {
        return _isRELIDO;
    }

    /**
     * Sets whether this is marked as releasable by information disclosure
     * official.
     *
     * @param value
     */
    public void setIsRELIDO(final boolean value)
    {
        _isRELIDO = value;
    }

    /**
     * @return <code>true</code> if there are Releaseable To countries for this
     * Marking.
     */
    public boolean isReleaseTo()
    {
        return getReleaseToCountries().size() > 0;
    }

    /**
     * @return whether this is marked as not releasable to foreign nationals.
     */
    public boolean isNOFORN()
    {
        return _isNOFORN;
    }

    /**
     * Sets whether this is marked as not releasable to foreign nationals.
     *
     * @param value
     */
    public void setIsNOFORN(final boolean value)
    {
        _isNOFORN = value;
    }

    /**
     * @return a list of countries that are owners/producers of this classified
     * information.
     */
    public List<ISO3166Country> getSelectedCountries()
    {
        if (_selectedCountries == null)
        {
            _selectedCountries = new ArrayList<>();
        }

        return _selectedCountries;
    }

    /**
     * @return a list of countries that this classified information is
     * releasable To.
     */
    public List<ISO3166Country> getReleaseToCountries()
    {
        if (_releaseToCountries == null)
        {
            _releaseToCountries = new ArrayList<>();
        }

        return _releaseToCountries;
    }

    /**
     * @return a list of countries that this classified information is
     * displayable To.
     */
    public List<ISO3166Country> getDisplayOnlyCountries()
    {
        if (_displayOnlyCountries == null)
        {
            _displayOnlyCountries = new ArrayList<>();
        }

        return _displayOnlyCountries;
    }

    /**
     * @return the MarkingValue classification assigned to this information.
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
     * Sets the MarkingValue classification assigned to this information.
     *
     * @param value
     */
    public void setClassification(final MarkingValue value)
    {
        _classification = value.clone();
    }

    /**
     * @return whether this is marked as limited distribution.
     */
    public boolean isLIMDIS()
    {
        return _isLIMDIS;
    }

    /**
     * Sets whether this is marked as limited distribution.
     *
     * @param value
     */
    public void setIsLIMDIS(final boolean value)
    {
        _isLIMDIS = value;
    }

    /**
     * @return whether this is marked as exclusive distribution.
     */
    public boolean isEXDIS()
    {
        return _isEXDIS;
    }

    /**
     * Sets whether this is marked as exclusive distribution.
     *
     * @param value
     */
    public void setIsEXDIS(final boolean value)
    {
        _isEXDIS = value;
    }

    /**
     * @return the whether its marked sensitive but unclassified.
     */
    public boolean isSBU()
    {
        return _isSBU;
    }

    /**
     * Sets whether its marked sensitive but unclassified.
     *
     * @param value
     */
    public void setIsSBU(final boolean value)
    {
        _isSBU = value;
    }

    /**
     * @return whether its marked sensitive but unclassified and no foreign.
     */
    public boolean isSBUNF()
    {
        return _isSBUNF;
    }

    /**
     * Sets whether its marked sensitive but unclassified and no foreign.
     *
     * @param value
     */
    public void setIsSBUNF(final boolean value)
    {
        _isSBUNF = value;
    }

    /**
     * @return whether its marked law enforcement sensitive no foreign.
     */
    public boolean isLESNF()
    {
        return _isLESNF;
    }

    /**
     * Sets whether its marked law enforcement sensitive no foreign.
     *
     * @param _isLESNF
     */
    public void setIsLESNF(final boolean _isLESNF)
    {
        this._isLESNF = _isLESNF;
    }

    /**
     * @return whether its marked no distribution.
     */
    public boolean isNODIS()
    {
        return _isNODIS;
    }

    /**
     * Sets whether its marked no distribution.
     *
     * @param _isNODIS
     */
    public void setIsNODIS(final boolean _isNODIS)
    {
        this._isNODIS = _isNODIS;
    }

    /**
     * @return whether its marked risk sensitive.
     */
    public boolean isRSEN()
    {
        return _isRSEN;
    }

    /**
     * Sets whether its marked risk sensitive.
     *
     * @param _isRSEN
     */
    public void setIsRSEN(final boolean _isRSEN)
    {
        this._isRSEN = _isRSEN;
    }

    /**
     * @return whether its marked sensitive security information.
     */
    public boolean isSSI()
    {
        return _isSSI;
    }

    /**
     * Sets whether its marked sensitive security information.
     *
     * @param _isSSI
     */
    public void setIsSSI(final boolean _isSSI)
    {
        this._isSSI = _isSSI;
    }

    /**
     * @return the ACCM control portion of the CAPCO formatted marking.
     */
    public String getACCMString()
    {
        String result = null;

        if (controlACCM.hasControls())
        {
            result = controlACCM.toString();
        }

        return result;
    }

    /**
     * @return a list of alternative or compensatory control measures (ACCM)
     * which are defined as a list of personnel to who the specific
     * classified information has been or may be provided together with
     * the use of an unclassified project name.
     */
    public ACCMNickname[] getACCMNicknames()
    {
        return controlACCM.getNicknames();
    }

    /**
     * Adds a ACCM nickname to this marking.
     *
     * @param value
     */
    public void addACCMNickname(final ACCMNickname value)
    {
        controlACCM.addNickname(value);
    }

    /**
     * Sets the ACCM portion of this marking.
     *
     * @param value
     */
    public void setACCMNickname(final ACCMControl value)
    {
        controlACCM = value;
    }

    /**
     * @return the SAP control portion of the CAPCO formatted marking.
     */
    public String getSAPString()
    {
        String result = null;

        if (controlSAP.hasControls())
        {
            result = controlSAP.toString();
        }

        return result;
    }

    /**
     * @return the SAP programs included within this marking.
     */
    public SAPProgram[] getSAPPrograms()
    {
        return controlSAP.getPrograms();
    }

    /**
     * Adds a SAP program to this marking.
     *
     * @param value
     */
    public void addSAPProgram(final SAPProgram value)
    {
        controlSAP.addProgram(value);
    }

    /**
     * Sets the SAP portion of this marking
     *
     * @param value
     */
    public void setSAPProgram(final SAPControl value)
    {
        controlSAP = value;
    }

    /**
     * @return the SCI control portion of the CAPCO formatted marking.
     */
    public String getSCIString()
    {
        String result = null;

        if (controlSCI.hasControls())
        {
            result = controlSCI.toString();
        }

        return result;
    }

    /**
     * @return the SCI projects included within the marking.
     */
    public SCIElement[] getSCIElements()
    {
        return controlSCI.getElements();
    }

    /**
     * Adds a SCI element to this marking.
     *
     * @param value
     */
    public void addSCIElement(final SCIElement value)
    {
        controlSCI.addElement(value);
    }

    /**
     * Sets the SCI portion of the marking
     *
     * @param value
     */
    public void setSCIProgram(final SCIControl value)
    {
        controlSCI = value;
    }

    /**
     * @return <code>true</code> if dissemination controls have been applied to
     * this Marking.
     */
    public boolean hasDisseminationControls()
    {
        return isRSEN() || isFOUO() || isORCON() || isIMCON() || isNOFORN() || isDSEN() || isDisplayOnly() || isFISA()
                || isNOFORN() || isPROPIN() || isReleaseTo() || isRELIDO();
    }

    /**
     * @return <code>true</code> if non-IC markings have been applied to this
     * Marking.
     */
    public boolean hasOtherDisseminationControls()
    {
        return isLIMDIS() || isEXDIS() || isNODIS() || isSBU() || isSBUNF() || isLES() || isLESNF() || isSSI();
    }

    /**
     * @return the classification to be applied to a section, part, paragraph,
     * or similar portion of a classified document.
     */
    public String toPortionString()
    {
        return "(" + toString(true) + ")";
    }

    /**
     * @return the classification and associated markings of a classified
     * document.
     */
    @Override
    public String toString()
    {
        return toString(false);
    }

    /**
     * @param toPortion allowed object is {@link Boolean }
     * @return the classification of a classified set of data, either full or
     * portion based on the boolean parameter. If True, the String
     * returned applies to a section, part, paragraph, or similar
     * portion. If False, the String returned is the full classification
     * and associated markings.
     */
    public String toString(final boolean toPortion)
    {

        StringBuilder marking = new StringBuilder();

        // JOINT and NATO and FGI start with //
        if (isJOINT() || isNATO() || !getSelectedCountries().contains(ISO3166Country.getUSA()))
        {

            marking.append("//");

            // If there is only one country and we know it is not USA then this
            // if FGI eg. '//DEU SECRET'
            if (getSelectedCountries().size() == 1)
            {
                marking.append(getSelectedCountries().get(0).getAlpha3()).append(" ");
            }
        }

        // Now add the classification level
        marking.append(toPortion ? getClassification().getPortion() : getClassification().getTitle());

        // JOINT then adds the list of countries
        if (isJOINT())
        {
            for (ISO3166Country country : getSelectedCountries())
            {
                marking.append(" ").append(country.getAlpha3());
            }
        }

        if (controlSCI.hasControls())
        {
            marking.append("//").append(controlSCI.toString());
        }
        if (controlSAP.hasControls())
        {
            marking.append("//").append(controlSAP.toString());
        }
        if (controlACCM.hasControls())
        {
            marking.append("//").append(controlACCM.toString());
        }

        if (hasDisseminationControls())
        {

            marking.append("//");

            if (isFOUO())
                marking.append("FOUO/");

            addDisseminations(marking, "REL TO ", isReleaseTo(), getReleaseToCountries());

            addDisseminations(marking,"DISPLAY ONLY ", isDisplayOnly(), getDisplayOnlyCountries());

            if (isRSEN())
                marking.append(toPortion ? "RS/" : "RSEN/");
            if (isRELIDO())
                marking.append("RELIDO/");
            if (isPROPIN())
                marking.append(toPortion ? "PR/" : "PROPIN/");
            if (isFISA())
                marking.append("FISA/");
            if (isIMCON())
                marking.append(toPortion ? "IMC/" : "IMCON/");
            if (isORCON())
                marking.append(toPortion ? "OC/" : "ORCON/");
            if (isDSEN())
                marking.append("DSEN/");
            if (isNOFORN())
                marking.append(toPortion ? "NF/" : "NOFORN/");

            marking = new StringBuilder(marking.substring(0, marking.length() - 1));
        }

        if (hasOtherDisseminationControls())
        {

            marking.append("//");

            if (isLES())
                marking.append("LES/");
            if (isLESNF())
                marking.append(toPortion ? "LES-NF/" : "LES NOFORN/");
            if (isLIMDIS())
                marking.append(toPortion ? "DS/" : "LIMITED DISTRIBUTION/");
            if (isEXDIS())
                marking.append(toPortion ? "XD/" : "EXDIS/");
            if (isNODIS())
                marking.append(toPortion ? "ND/" : "NODIS/");
            if (isSBU())
                marking.append("SBU/");
            if (isSBUNF())
                marking.append(toPortion ? "SBU-NF/" : "SBU NOFORN/");
            if (isSSI())
                marking.append("SSI/");

            // Remove trailing /
            marking = new StringBuilder(marking.substring(0, marking.length() - 1));
        }

        return marking.toString();

    }

    private void addDisseminations(StringBuilder marking, String label, boolean display, List<ISO3166Country> countries)
    {
        if (display && countries.size() > 0)
        {
            marking.append(label);

            int original = marking.length();

            // USA comes first in deseminations
            if (countries.contains(ISO3166Country.getUSA()))
            {
                marking.append("USA");
            }

            Collections.sort(countries);
            for (ISO3166Country country : countries)
            {
                if (!(country.compareTo(ISO3166Country.getUSA()) == 0))
                {
                    if (marking.length() > original)
                    {
                        marking.append(", ");
                    }

                    marking.append(country.getAlpha3());
                }
            }

            marking.append("/");
        }
    }

}
