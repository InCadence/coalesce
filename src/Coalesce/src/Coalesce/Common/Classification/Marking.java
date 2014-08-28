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
            _classificationList = FieldValues.GetListOfClassifications(null, true);
        }
        
        return _classificationList;
    }
    
    @Override
    public int compareTo(Marking other)
    {

        String title = GetClassification().GetTitle();
        String otherTitle = other.GetClassification().GetTitle();

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
            markingString = StringHelper.TrimParentheses(markingString);
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
                _classification = FieldValues.GetMarkingValueByPortion(markingString, GetClassifications());
            } else {
                _classification = FieldValues.GetMarkingValueByTitle(markingString, GetClassifications());
            }
            
            GetSelectedCountries().add(ISO3166Country.USA());
            return;
        }
        
        // Parse out the marking
        markingString = markingString.replace("//", "|");
        String[] parts = markingString.split("\\|");
        
        if (parts.length == 0) return; //Invalid Classification marking
        
        // Reset values first
        _isNATO = false;
        GetSelectedCountries().clear();
        _classification = null;
        
        // CLASSIFICATION//
        if (!parts[index].equals("")) {
            
            if (isPortionMarking) {
                _classification = FieldValues.GetMarkingValueByPortion(parts[0], GetClassifications());
            } else {
                _classification = FieldValues.GetMarkingValueByTitle(parts[0], GetClassifications());
            }
            
            GetSelectedCountries().add(ISO3166Country.USA());
            
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
                    _classification = FieldValues.GetMarkingValueByPortion(marking, GetClassifications());
                } else {
                    _classification = FieldValues.GetMarkingValueByTitle(marking, GetClassifications());
                }
                
                for (int pos = (joint.contains("TOP") ? 3 : 2); pos < joint.size(); pos++) {
                    GetSelectedCountries().add(FieldValues.GetCountryByAlpha3(joint.get(pos)));
                }
                
            } else if (parts[index].startsWith("COSMIC") ||
                       parts[index].startsWith("NATO") ||
                       parts[index].contains("ATOMAL") ) {
                
                _isNATO = true;
                _classification = FieldValues.GetMarkingValueByTitle(parts[index], GetClassifications());
                
            } else if ((isPortionMarking && parts[index].startsWith("CTS")) || parts[index].startsWith("N")) {
                
                _isNATO = true;
                _classification = FieldValues.GetMarkingValueByPortion(parts[index], GetClassifications());
                
            } else {
                
                List<String> fgc = Arrays.asList(parts[index].split(" "));
                
                if (fgc.size() < (fgc.contains("TOP") ? 3 : 2)) return; // Invalid FGC Classification marking
                
                ISO3166Country country = FieldValues.GetCountryByAlpha3(fgc.get(0));
                if (country != null) {
                    GetSelectedCountries().add(country);
                }
                
                if (isPortionMarking) {
                    _classification = FieldValues.GetMarkingValueByPortion(fgc.get(1), GetClassifications());
                } else {
                    _classification = FieldValues.GetMarkingValueByTitle((fgc.contains("TOP") ? "TOP SECRET" : fgc.get(1)), GetClassifications());
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
                        GetDisplayOnlyCountries().add(FieldValues.GetCountryByAlpha3(disp[pos]));
                    }

                } else if (subpart.startsWith("REL TO")) {
                
                    // Pull the commas out
                    subpart = subpart.replace(",", "");
                    String[] relto = subpart.split(" ");

                    if (relto.length < 3) return; // Invalid REL TO Desemination marking

                    for (int pos=2; pos < relto.length; pos++) {
                        GetReleaseToCountries().add(FieldValues.GetCountryByAlpha3(relto[pos]));
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

                    GetNicknames().add(accm[1]);
                    
                } else {
                
                    if (isACCM) {   // Assume this is a ACCM Nickname
                    
                        GetNicknames().add(subpart);
                    } else {
                        
                        return; // Unrecognized marking
                    }
                }
            } // subparts

            index += 1;

        }
        
    }
    
    public boolean GetIsNATO() {
        return _isNATO;
    }
    
    public void SetIsNATO(boolean value) {
        _isNATO = value;
    }
    
    public boolean GetIsJOINT() {
        return GetSelectedCountries().size() > 1;
    }
    
    public boolean GetIsFOUO() {
        return _isFOUO;
    }
    
    public void SetIsFOUO(boolean value) {
        _isFOUO = value;
    }
    
    public boolean GetIsLES() {
        return _isLES;
    }
    
    public void SetIsLES(boolean value) {
        _isLES = value;
    }
    
    public boolean GetIsORCON() {
        return _isORCON;
    }
    
    public void SetIsORCON(boolean value) {
        _isORCON = value;
    }
    
    public boolean GetIsIMCON() {
        return _isIMCON;
    }
    
    public void SetIsIMCON(boolean value) {
        _isIMCON = value;
    }
    
    public boolean GetIsDISPLAY_ONLY() {
        return GetDisplayOnlyCountries().size() > 0;
    }
    
    public boolean GetIsDSEN() {
        return _isDSEN;
    }
    
    public void SetIsDSEN(boolean value) {
        _isDSEN = value;
    }
    
    public boolean GetIsFISA() {
        return _isFISA;
    }
    
    public void SetIsFISA(boolean value) {
        _isFISA = value;
    }
    
    public boolean GetIsPROPIN() {
        return _isPROPIN;
    }
    
    public void SetIsPROPIN(boolean value) {
        _isPROPIN = value;
    }
    
    public boolean GetIsRELIDO() {
        return _isRELIDO;
    }
    
    public void SetIsRELIDO(boolean value) {
        _isRELIDO = value;
    }
    
    public boolean GetIsReleaseTo() {
        return GetReleaseToCountries().size() > 0;
    }
    
    public boolean GetIsNOFORN() {
        return _isNOFORN;
    }
    
    public void SetIsNOFORN(boolean value) {
        _isNOFORN = value;
    }
    
    public List<ISO3166Country> GetSelectedCountries() {
        if (_selectedCountries == null) {
            _selectedCountries = new ArrayList<ISO3166Country>();
        }
        
        return _selectedCountries;
    }
    
    public List<ISO3166Country> GetReleaseToCountries() {
        if (_releaseToCountries == null) {
            _releaseToCountries = new ArrayList<ISO3166Country>();
        }
        
        return _releaseToCountries;
    }
    
    public List<ISO3166Country> GetDisplayOnlyCountries() {
        if (_displayOnlyCountries == null) {
            _displayOnlyCountries = new ArrayList<ISO3166Country>();
        }
        
        return _displayOnlyCountries;
    }
    
    public MarkingValue GetClassification() {
        if (_classification == null) {
            _classification = new MarkingValue();
        }
        
        return _classification;
    }
    
    public void SetClassification(MarkingValue value) {
        _classification = value;
    }
    
    public boolean GetIsLIMDIS() {
        return _isLIMDIS;
    }
    
    public void SetIsLIMDIS(boolean value) {
        _isLIMDIS = value;
    }
    
    public boolean GetIsEXDIS() {
        return _isEXDIS;
    }
    
    public void SetIsEXDIS(boolean value) {
        _isEXDIS = value;
    }
    
    public boolean GetIsSBU() {
        return _isSBU;
    }
    
    public void SetIsSBU(boolean value) {
        _isSBU = value;
    }
    
    public boolean GetIsSBUNF() {
        return _isSBUNF;
    }
    
    public void SetIsSBUNF(boolean value) {
        _isSBUNF = value;
    }
    
    public boolean GetIsACCM() {
        return GetNicknames().size() > 0;
    }
    
    public List<String> GetNicknames() {
        if (_nicknames == null) {
            _nicknames = new ArrayList<String>();
        };
        
        return _nicknames;
    }
    
    public boolean HasDeseminationControls() {
        return GetIsFOUO() || GetIsLES() || GetIsORCON() || GetIsIMCON() || GetIsDSEN() || GetIsDISPLAY_ONLY() ||
               GetIsFISA() || GetIsNOFORN() || GetIsPROPIN() || GetIsPROPIN() || GetIsReleaseTo() || GetIsRELIDO();
    }
   
    public boolean HasOtherDeseminationControls() {
        return GetIsLIMDIS() || GetIsEXDIS() || GetIsSBU() || GetIsSBUNF() || GetIsACCM();
    }
    
    public String ToPortionString() {
        return "(" + toString(true) + ")";
    }
    
    @Override
    public String toString() {
        return toString(false);
    }
    
    public String toString(boolean toPortion) {
        
        String marking = "";
        
        // JOINT and NATO and FGI start with //
        if (GetIsJOINT() || GetIsNATO() || !GetSelectedCountries().contains(ISO3166Country.USA())) {
            
            marking += "//";
            
            // If there is only one country and we know it is not USA then this if FGI eg. '//DEU SECRET'
            if (GetSelectedCountries().size() == 1) {
                marking += GetSelectedCountries().get(0).GetAlpha3() + " ";
            }
        }
        
        // Now add the classification level
        marking += toPortion ? GetClassification().GetPortion() : GetClassification().GetTitle();
        
        // JOINT then adds the list of countries
        if (GetIsJOINT()) {
            
            for (ISO3166Country country : GetSelectedCountries()) {
                marking += " " + country.GetAlpha3();
            }
        }
        
        if (HasDeseminationControls()) {
            
            marking += "//";
            
            if (GetIsFOUO()) {
                
                if (GetIsLES()) {
                    marking += "FOUO-LES/";
                } else {
                    marking += "FOUO/";
                }
            } else {
                if (GetIsLES()) {
                    marking += "LES/";
                }
            }
            
            marking = AddDeseminations(marking, "REL TO ", GetIsReleaseTo(), GetReleaseToCountries());
            
            marking = AddDeseminations(marking, "DISPLAY ONLY ", GetIsDISPLAY_ONLY(), GetDisplayOnlyCountries());
            
            if (GetIsRELIDO()) marking += "RELIDO/";
            if (GetIsPROPIN()) marking += toPortion ? "PR/" : "PROPIN/";
            if (GetIsFISA()) marking += "FISA/";
            if (GetIsIMCON()) marking += toPortion ? "IMC/" : "IMCON/";
            if (GetIsORCON()) marking += toPortion ? "OC/" : "ORCON/";
            if (GetIsDSEN()) marking += "DSEN/";
            if (GetIsNOFORN()) marking += toPortion ? "NF/" : "NOFORN/";
            
            marking = marking.substring(0, marking.length() - 1);
        }
        
        if (HasOtherDeseminationControls()) {
            
            marking += "//";
            
            if (GetIsLIMDIS()) marking += toPortion ? "DS/" : "LIMITED DISTRIBUTION/";
            if (GetIsEXDIS()) marking += toPortion ? "XD/" : "EXDIS/";
            if (GetIsSBU()) marking += "SBU/";
            if (GetIsSBUNF()) marking += toPortion ? "SBU-NF/" : "SBU NOFORN/";
            
            if (GetIsACCM()) {
                
                marking += "ACCM-";
                
                Collections.sort(_nicknames);
                for (String nickname : GetNicknames()) {
                    
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
                    
                    marking += country.GetAlpha3() + ", ";
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
