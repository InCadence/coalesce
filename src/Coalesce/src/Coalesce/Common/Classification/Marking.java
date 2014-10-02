package Coalesce.Common.Classification;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import Coalesce.Common.Helpers.StringHelper;

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

    public static String UNCLASSIFIED = "UNCLASSIFIED";
    public static String RESTRICTED = "RESTRICTED";
    public static String CONFIDENTIAL = "CONFIDENTIAL";
    public static String SECRET = "SECRET";
    public static String TOPSECRET = "TOP SECRET";
    
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

    private static List<MarkingValue> GetClassifications() {
        if (_classificationList == null) {
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

        if (title.contains("TOP SECRET")) {
            
            if (otherTitle.contains("TOP SECRET")) {
                return 0;
            }
            
        } else if (title.contains("SECRET")) {
            
            if (otherTitle.contains("TOP SECRET")) {
                return 1;
            } else if (otherTitle.contains("SECRET")) {
                return 0;
            }
            
        } else if (title.contains("CONFIDENTIAL")) {
            
            if (otherTitle.contains("SECRET")) { // handles ts and s
                return 1;
            } else if (otherTitle.contains("CONFIDENTIAL")) {
                return 0;
            }
            
        } else if (title.contains("RESTRICTED")) {
            
            if (otherTitle.contains("SECRET") || otherTitle.contains("CONFIDENTIAL")) {
                return 1;
            } else if (otherTitle.contains("RESTRICTED")) {
                return 0;
            }
            
        } else if (title.contains("UNCLASSIFIED")) {
            
            if (otherTitle.contains("SECRET") ||
                otherTitle.contains("CONFIDENTIAL") ||
                otherTitle.contains("RESTRICTED")) {
                return 1;
            } else if (otherTitle.contains("UNCLASSIFIED")) {
                return 0;
            }
        }
        
        return -1;
        
    }
    
    public Marking() {
        this("UNCLASSIFIED");
    }
    
    public Marking(String markingString) {
        
        int index = 0;
        boolean isPortionMarking = false;
        
        if (markingString == null) {
            markingString = "";
        }
        
        // CLASSIFICATION//CONTROLSYSTEM1/CONTROLSYSTEM2-COMPARTMENT1 SUBCOMPARTMENT1 SUBCOMPARTMENT2-COMPARTMENT2//
        // SPECIAL ACCESS REQUIRED-PROGRAM NICKNAME/CODEWORD//" & _
        // RD-SG 1//FGI DEU//FOUO//LIMDIS
        
        markingString = markingString.trim();
        
        if (markingString.startsWith("(") && markingString.endsWith(")")) {
            isPortionMarking = true;
            markingString = StringHelper.trimParentheses(markingString);
        }
        
        // If it is blank or invalid make it Unclass
        if (isPortionMarking) {
            if (markingString.length() < 1) {
                markingString = "U";
            }
        } else {
            if (markingString.length() < 6) { 
                markingString = "UNCLASSIFIED";
            }
        }
        
        // If it does not have // assume it is the USA class level only
        if (!markingString.contains("//")) {
            
            if (isPortionMarking) {
                _classification = FieldValues.getMarkingValueByPortion(markingString, GetClassifications());
            } else {
                _classification = FieldValues.getMarkingValueByTitle(markingString, GetClassifications());
            }
            
            getSelectedCountries().add(ISO3166Country.USA());
            return;
        }
        
        // Parse out the marking
        markingString = markingString.replace("//", "|");
        String[] parts = markingString.split("\\|");
        
        if (parts.length == 0) return; //Invalid Classification marking
        
        // Reset values first
        _isNATO = false;
        getSelectedCountries().clear();
        _classification = null;
        
        // CLASSIFICATION//
        if (!parts[index].equals("")) {
            
            if (isPortionMarking) {
                _classification = FieldValues.getMarkingValueByPortion(parts[0], GetClassifications());
            } else {
                _classification = FieldValues.getMarkingValueByTitle(parts[0], GetClassifications());
            }
            
            getSelectedCountries().add(ISO3166Country.USA());
            
            index += 1;
            
            // //CLASSIFICATION
        } else if (parts.length > 1) {
            
            index += 1;
            
            if (parts[index].startsWith("JOINT")) {
                
                List<String> joint = Arrays.asList(parts[index].split(" "));
                String marking = "";
                
                // The count must be at least 4 unless TS which would make it 5 eg. '//JOINT TOP SECRET USA DEU' or
                // '//JOINT TS USA DEU' for a portion
                if (joint.size() < (joint.contains("TOP") ? 5 : 4)) return; // Invalid Joint Classification marking
                
                marking = joint.contains("TOP") ? "JOINT TOP SECRET" : "JOINT " + joint.get(1);
                
                if (isPortionMarking) {
                    _classification = FieldValues.getMarkingValueByPortion(marking, GetClassifications());
                } else {
                    _classification = FieldValues.getMarkingValueByTitle(marking, GetClassifications());
                }
                
                for (int pos = (joint.contains("TOP") ? 3 : 2); pos < joint.size(); pos++) {
                    getSelectedCountries().add(FieldValues.getCountryByAlpha3(joint.get(pos)));
                }
                
            } else if (parts[index].startsWith("COSMIC") ||
                       parts[index].startsWith("NATO") ||
                       parts[index].contains("ATOMAL") ) {
                
                _isNATO = true;
                _classification = FieldValues.getMarkingValueByTitle(parts[index], GetClassifications());
                
            } else if ((isPortionMarking && parts[index].startsWith("CTS")) || parts[index].startsWith("N")) {
                
                _isNATO = true;
                _classification = FieldValues.getMarkingValueByPortion(parts[index], GetClassifications());
                
            } else {
                
                List<String> fgc = Arrays.asList(parts[index].split(" "));
                
                if (fgc.size() < (fgc.contains("TOP") ? 3 : 2)) return; // Invalid FGC Classification marking
                
                ISO3166Country country = FieldValues.getCountryByAlpha3(fgc.get(0));
                if (country != null) {
                    getSelectedCountries().add(country);
                }
                
                if (isPortionMarking) {
                    _classification = FieldValues.getMarkingValueByPortion(fgc.get(1), GetClassifications());
                } else {
                    _classification = FieldValues.getMarkingValueByTitle((fgc.contains("TOP") ? "TOP SECRET" : fgc.get(1)), GetClassifications());
                }
                
            }
            
            index += 1;
            
        }
        
        while (parts.length > index ) {
            // CLASSIFICATION//CONTROLSYSTEM1/CONTROLSYSTEM2-COMPARTMENT1 SUBCOMPARTMENT1 SUBCOMPARTMENT2-COMPARTMENT2//
            // SPECIAL ACCESS REQUIRED-PROGRAM NICKNAME/CODEWORD//" & _
            // RD-SG 1//FGI DEU//FOUO//LIMDIS/ACCM-ALPHA/BRAVO

            String[] subparts = parts[index].split("/");
            boolean isACCM = false;

            for (String subpart : subparts) {
                // Disem
                if (subpart.startsWith("FOUO-LES")) {
                    
                    _isFOUO = true;
                    _isLES = true;
                    
                } else if (subpart.startsWith("FOUO")) {
                
                    _isFOUO = true;
                    
                } else if (subpart.startsWith("LES")) {
                 
                    _isLES = true;
                    
                } else if (subpart.startsWith("RELIDO")) {
                
                    _isRELIDO = true;
                    
                } else if (subpart.startsWith("PROPIN") || (isPortionMarking && subpart.startsWith("PR"))) {
                
                    _isPROPIN = true;
                    
                } else if (subpart.startsWith("FISA")) {
                
                    _isFISA = true;
                    
                } else if (subpart.startsWith("IMCON") || (isPortionMarking && subpart.startsWith("IMC"))) {
                
                    _isIMCON = true;
                    
                } else if (subpart.startsWith("ORCON") || (isPortionMarking && subpart.startsWith("OC"))) {
                
                    _isORCON = true;
                    
                } else if (subpart.startsWith("DSEN")) {
                
                    _isDSEN = true;
                    
                } else if (subpart.startsWith("NOFORN") || (isPortionMarking && subpart.startsWith("NF"))) {
                
                    _isNOFORN = true;
                    
                } else if (subpart.startsWith("DISPLAY ONLY")) {
                
                    // Pull the commas out
                    subpart = subpart.replace(",", "");
                    String[] disp = subpart.split(" ");

                    if (disp.length < 3) return; // Invalid DISPLAY ONLY Desemination marking

                    for (int pos=2; pos < disp.length; pos++) {
                        getDisplayOnlyCountries().add(FieldValues.getCountryByAlpha3(disp[pos]));
                    }

                } else if (subpart.startsWith("REL TO")) {
                
                    // Pull the commas out
                    subpart = subpart.replace(",", "");
                    String[] relto = subpart.split(" ");

                    if (relto.length < 3) return; // Invalid REL TO Desemination marking

                    for (int pos=2; pos < relto.length; pos++) {
                        getReleaseToCountries().add(FieldValues.getCountryByAlpha3(relto[pos]));
                    }

                    // Other Disem
                } else if (subpart.startsWith("SBU NOFORN") || (isPortionMarking && subpart.startsWith("SBU-NF"))) {
                
                    _isSBUNF = true;
                    
                } else if (subpart.startsWith("SBU")) {
                    
                    _isSBU = true;
                    
                } else if (subpart.startsWith("EXDIS") || (isPortionMarking && subpart.startsWith("XD"))) {
                
                    _isEXDIS = true;
                    
                } else if (subpart.startsWith("LIMITED DISTRIBUTION") || (isPortionMarking && subpart.startsWith("DS"))) {
                
                    _isLIMDIS = true;
                    
                } else if (subpart.startsWith("ACCM")) {
                
                    isACCM = true;

                    String[] accm = subpart.split("-");

                    if (accm.length != 2) return;   // Invalid ACCM Other Desemination marking

                    getNicknames().add(accm[1]);
                    
                } else {
                
                    if (isACCM) {   // Assume this is a ACCM Nickname
                    
                        getNicknames().add(subpart);
                    } else {
                        
                        return; // Unrecognized marking
                    }
                }
            } // subparts

            index += 1;

        }
        
    }
    
    public boolean getIsNATO() {
        return _isNATO;
    }
    
    public void setIsNATO(boolean value) {
        _isNATO = value;
    }
    
    public boolean getIsJOINT() {
        return getSelectedCountries().size() > 1;
    }
    
    public boolean getIsFOUO() {
        return _isFOUO;
    }
    
    public void setIsFOUO(boolean value) {
        _isFOUO = value;
    }
    
    public boolean getIsLES() {
        return _isLES;
    }
    
    public void setIsLES(boolean value) {
        _isLES = value;
    }
    
    public boolean getIsORCON() {
        return _isORCON;
    }
    
    public void setIsORCON(boolean value) {
        _isORCON = value;
    }
    
    public boolean getIsIMCON() {
        return _isIMCON;
    }
    
    public void setIsIMCON(boolean value) {
        _isIMCON = value;
    }
    
    public boolean getIsDISPLAY_ONLY() {
        return getDisplayOnlyCountries().size() > 0;
    }
    
    public boolean getIsDSEN() {
        return _isDSEN;
    }
    
    public void setIsDSEN(boolean value) {
        _isDSEN = value;
    }
    
    public boolean getIsFISA() {
        return _isFISA;
    }
    
    public void setIsFISA(boolean value) {
        _isFISA = value;
    }
    
    public boolean getIsPROPIN() {
        return _isPROPIN;
    }
    
    public void setIsPROPIN(boolean value) {
        _isPROPIN = value;
    }
    
    public boolean getIsRELIDO() {
        return _isRELIDO;
    }
    
    public void setIsRELIDO(boolean value) {
        _isRELIDO = value;
    }
    
    public boolean getIsReleaseTo() {
        return getReleaseToCountries().size() > 0;
    }
    
    public boolean getIsNOFORN() {
        return _isNOFORN;
    }
    
    public void setIsNOFORN(boolean value) {
        _isNOFORN = value;
    }
    
    public List<ISO3166Country> getSelectedCountries() {
        if (_selectedCountries == null) {
            _selectedCountries = new ArrayList<ISO3166Country>();
        }
        
        return _selectedCountries;
    }
    
    public List<ISO3166Country> getReleaseToCountries() {
        if (_releaseToCountries == null) {
            _releaseToCountries = new ArrayList<ISO3166Country>();
        }
        
        return _releaseToCountries;
    }
    
    public List<ISO3166Country> getDisplayOnlyCountries() {
        if (_displayOnlyCountries == null) {
            _displayOnlyCountries = new ArrayList<ISO3166Country>();
        }
        
        return _displayOnlyCountries;
    }
    
    public MarkingValue getClassification() {
        if (_classification == null) {
            _classification = new MarkingValue();
        }
        
        return _classification;
    }
    
    public void setClassification(MarkingValue value) {
        _classification = value;
    }
    
    public boolean getIsLIMDIS() {
        return _isLIMDIS;
    }
    
    public void setIsLIMDIS(boolean value) {
        _isLIMDIS = value;
    }
    
    public boolean getIsEXDIS() {
        return _isEXDIS;
    }
    
    public void setIsEXDIS(boolean value) {
        _isEXDIS = value;
    }
    
    public boolean getIsSBU() {
        return _isSBU;
    }
    
    public void setIsSBU(boolean value) {
        _isSBU = value;
    }
    
    public boolean getIsSBUNF() {
        return _isSBUNF;
    }
    
    public void setIsSBUNF(boolean value) {
        _isSBUNF = value;
    }
    
    public boolean getIsACCM() {
        return getNicknames().size() > 0;
    }
    
    public List<String> getNicknames() {
        if (_nicknames == null) {
            _nicknames = new ArrayList<String>();
        };
        
        return _nicknames;
    }
    
    public boolean hasDeseminationControls() {
        return getIsFOUO() || getIsLES() || getIsORCON() || getIsIMCON() || getIsDSEN() || getIsDISPLAY_ONLY() ||
               getIsFISA() || getIsNOFORN() || getIsPROPIN() || getIsPROPIN() || getIsReleaseTo() || getIsRELIDO();
    }
   
    public boolean hasOtherDeseminationControls() {
        return getIsLIMDIS() || getIsEXDIS() || getIsSBU() || getIsSBUNF() || getIsACCM();
    }
    
    public String toPortionString() {
        return "(" + toString(true) + ")";
    }
    
    @Override
    public String toString() {
        return toString(false);
    }
    
    public String toString(boolean toPortion) {
        
        String marking = "";
        
        // JOINT and NATO and FGI start with //
        if (getIsJOINT() || getIsNATO() || !getSelectedCountries().contains(ISO3166Country.USA())) {
            
            marking += "//";
            
            // If there is only one country and we know it is not USA then this if FGI eg. '//DEU SECRET'
            if (getSelectedCountries().size() == 1) {
                marking += getSelectedCountries().get(0).getAlpha3() + " ";
            }
        }
        
        // Now add the classification level
        marking += toPortion ? getClassification().getPortion() : getClassification().getTitle();
        
        // JOINT then adds the list of countries
        if (getIsJOINT()) {
            
            for (ISO3166Country country : getSelectedCountries()) {
                marking += " " + country.getAlpha3();
            }
        }
        
        if (hasDeseminationControls()) {
            
            marking += "//";
            
            if (getIsFOUO()) {
                
                if (getIsLES()) {
                    marking += "FOUO-LES/";
                } else {
                    marking += "FOUO/";
                }
            } else {
                if (getIsLES()) {
                    marking += "LES/";
                }
            }
            
            marking = AddDeseminations(marking, "REL TO ", getIsReleaseTo(), getReleaseToCountries());
            
            marking = AddDeseminations(marking, "DISPLAY ONLY ", getIsDISPLAY_ONLY(), getDisplayOnlyCountries());
            
            if (getIsRELIDO()) marking += "RELIDO/";
            if (getIsPROPIN()) marking += toPortion ? "PR/" : "PROPIN/";
            if (getIsFISA()) marking += "FISA/";
            if (getIsIMCON()) marking += toPortion ? "IMC/" : "IMCON/";
            if (getIsORCON()) marking += toPortion ? "OC/" : "ORCON/";
            if (getIsDSEN()) marking += "DSEN/";
            if (getIsNOFORN()) marking += toPortion ? "NF/" : "NOFORN/";
            
            marking = marking.substring(0, marking.length() - 1);
        }
        
        if (hasOtherDeseminationControls()) {
            
            marking += "//";
            
            if (getIsLIMDIS()) marking += toPortion ? "DS/" : "LIMITED DISTRIBUTION/";
            if (getIsEXDIS()) marking += toPortion ? "XD/" : "EXDIS/";
            if (getIsSBU()) marking += "SBU/";
            if (getIsSBUNF()) marking += toPortion ? "SBU-NF/" : "SBU NOFORN/";
            
            if (getIsACCM()) {
                
                marking += "ACCM-";
                
                Collections.sort(_nicknames);
                for (String nickname : getNicknames()) {
                    
                    marking += nickname + "/";
                }
                
            }
            
            // Remove trailing /
            marking = marking.substring(0, marking.length() - 1);
        }
        
        return marking;
        
    }
    
    private String AddDeseminations(String marking, String label, boolean display, List<ISO3166Country> countries) {
       
        if (display && countries.size() > 0) {
            
            marking += label;
            
            // USA comes first in deseminations
            if (countries.contains(ISO3166Country.USA())) {
                
                marking += "USA";
                if (countries.size() > 1) {
                    marking += ", ";
                }
            }
                
            Collections.sort(countries);
            for (ISO3166Country country : countries) {
                
                if (!(country.compareTo(ISO3166Country.USA()) == 0)) {
                    
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
