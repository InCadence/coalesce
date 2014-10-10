package com.incadencecorp.coalesce.common.classification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

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

public class FieldValues {

    private static void classListAdd(ArrayList<MarkingValue> classList,
                                     String parent,
                                     String title,
                                     String abbreviation,
                                     String portion)
    {

        MarkingValue value = new MarkingValue(parent, title, abbreviation, portion);

        // TODO: Banner needs to be implemented
        // Banner banner = new Banner
        // if (banner.CompareClassificationToSHWM(title) <> -1)
        // ClassList.Add(value);
        classList.add(value);
    }

    public static List<MarkingValue> getListOfClassifications(Marking marking)
    {
        return getListOfClassifications(marking, false);
    }

    public static List<MarkingValue> getListOfClassifications(Marking marking, boolean all)
    {

        ArrayList<MarkingValue> classList = new ArrayList<MarkingValue>();

        if (all) {
            classListAdd(classList, "", "COSMIC TOP SECRET", "", "CTS");
            classListAdd(classList, "", "COSMIC TOP SECRET ATOMAL", "", "CTS-A");
            classListAdd(classList, "", "COSMIC TOP SECRET BOHEMIA", "", "CTS-B");
            classListAdd(classList, "", "NATO SECRET", "", "NS");
            classListAdd(classList, "", "SECRET ATOMAL", "", "NS-A");
            classListAdd(classList, "", "NATO CONFIDENTIAL", "", "NC");
            classListAdd(classList, "", "CONFIDENTIAL ATOMAL", "", "NC-A");
            classListAdd(classList, "", "NATO RESTRICTED", "", "NR");
            classListAdd(classList, "", "NATO UNCLASSIFIED", "", "NU");
            classListAdd(classList, "", "JOINT TOP SECRET", "", "JOINT TS");
            classListAdd(classList, "", "JOINT SECRET", "", "JOINT S");
            classListAdd(classList, "", "JOINT CONFIDENTIAL", "", "JOINT C");
            classListAdd(classList, "", "JOINT RESTRICTED", "", "JOINT R");
            classListAdd(classList, "", "JOINT UNCLASSIFIED", "", "JOINT U");
            classListAdd(classList, "", "TOP SECRET", "", "TS");
            classListAdd(classList, "", "SECRET", "", "S");
            classListAdd(classList, "", "CONFIDENTIAL", "", "C");
            classListAdd(classList, "", "RESTRICTED", "", "R");
            classListAdd(classList, "", "UNCLASSIFIED", "", "U");

        } else {

            if (marking == null) {
                
                return new ArrayList<MarkingValue>();
                
            } else if (marking.getIsNATO()) {

                classListAdd(classList, "", "COSMIC TOP SECRET", "", "CTS");
                classListAdd(classList, "", "COSMIC TOP SECRET ATOMAL", "", "CTS-A");
                classListAdd(classList, "", "COSMIC TOP SECRET BOHEMIA", "", "CTS-B");
                classListAdd(classList, "", "NATO SECRET", "", "NS");
                classListAdd(classList, "", "SECRET ATOMAL", "", "NS-A");
                classListAdd(classList, "", "NATO CONFIDENTIAL", "", "NC");
                classListAdd(classList, "", "CONFIDENTIAL ATOMAL", "", "NC-A");
                classListAdd(classList, "", "NATO RESTRICTED", "", "NR");
                classListAdd(classList, "", "NATO UNCLASSIFIED", "", "NU");

            } else if (marking.getSelectedCountries().size() == 0) {

                return new ArrayList<MarkingValue>();

            } else if (marking.getSelectedCountries().size() < 2 && marking.getIsJOINT()) {

                return new ArrayList<MarkingValue>();

            } else if (marking.getIsJOINT()) {

                classListAdd(classList, "", "JOINT TOP SECRET", "", "JOINT TS");
                classListAdd(classList, "", "JOINT SECRET", "", "JOINT S");
                classListAdd(classList, "", "JOINT CONFIDENTIAL", "", "JOINT C");
                if (!marking.getSelectedCountries().contains(ISO3166Country.withAlpha3EqualTo("USA"))) {
                    classListAdd(classList, "", "JOINT RESTRICTED", "", "JOINT R");
                }
                classListAdd(classList, "", "JOINT UNCLASSIFIED", "", "JOINT U");

            } else {

                classListAdd(classList, "", "TOP SECRET", "", "TS");
                classListAdd(classList, "", "SECRET", "", "S");
                classListAdd(classList, "", "CONFIDENTIAL", "", "C");
                if (!marking.getSelectedCountries().contains(ISO3166Country.withAlpha3EqualTo("USA"))) {
                    classListAdd(classList, "", "RESTRICTED", "", "R");
                }
                classListAdd(classList, "", "UNCLASSIFIED", "", "U");
            }
        }

        return classList;

    }

    // SCI must be processed only on an information system accredited for SCI
    // processing, per page 71 of DoDM 5200.01-V2, February 24, 2012
    public static List<MarkingValue> getListOfSciControlSystems()
    {

        List<MarkingValue> controlSystemList = new ArrayList<MarkingValue>();

        controlSystemList.add(new MarkingValue("", "COMINT", "SI", "SI"));
        controlSystemList.add(new MarkingValue("", "HCS", "HCS", "HCS")); // NOFORN
                                                                          // must
                                                                          // also
                                                                          // be
                                                                          // selected
        controlSystemList.add(new MarkingValue("", "KLONDIKE", "KDK", "KDK")); // NOFORN
                                                                               // must
                                                                               // also
                                                                               // be
                                                                               // selected
        controlSystemList.add(new MarkingValue("", "TALENT KEYHOLE", "TK", "TK"));

        return controlSystemList;

    }

    public static List<MarkingValue> getListOfCompartments()
    {

        List<MarkingValue> compartmentList = new ArrayList<MarkingValue>();

        compartmentList.add(new MarkingValue("", "COMINT", "SI", "SI"));
        compartmentList.add(new MarkingValue("", "HCS", "HCS", "HCS")); // NOFORN
                                                                        // must
                                                                        // also
                                                                        // be
                                                                        // selected
        compartmentList.add(new MarkingValue("", "KLONDIKE", "KDK", "KDK")); // NOFORN
                                                                             // must
                                                                             // also
                                                                             // be
                                                                             // selected
        compartmentList.add(new MarkingValue("", "TALENT KEYHOLE", "TK", "TK"));

        return compartmentList;
    }

    public static List<MarkingValue> getListOfSubCompartments()
    {

        List<MarkingValue> subCompartmentList = new ArrayList<MarkingValue>();

        subCompartmentList.add(new MarkingValue("", "COMINT", "SI", "SI"));
        subCompartmentList.add(new MarkingValue("", "HCS", "HCS", "HCS")); // NOFORN
                                                                           // must
                                                                           // also
                                                                           // be
                                                                           // selected
        subCompartmentList.add(new MarkingValue("", "KLONDIKE", "KDK", "KDK")); // NOFORN
                                                                                // must
                                                                                // also
                                                                                // be
                                                                                // selected
        subCompartmentList.add(new MarkingValue("", "TALENT KEYHOLE", "TK", "TK"));

        return subCompartmentList;
    }

    // ISO 3166 country codes
    public static List<ISO3166Country> getListOfCountries()
    {

        List<ISO3166Country> countryList = new ArrayList<ISO3166Country>();

        countryList.add(new ISO3166Country("AF", "AFG", "AFGHANISTAN"));
        countryList.add(new ISO3166Country("ZA", "ZAF", "SOUTH AFRICA"));
        countryList.add(new ISO3166Country("AL", "ALB", "ALBANIA"));
        countryList.add(new ISO3166Country("DZ", "DZA", "ALGERIA"));
        countryList.add(new ISO3166Country("DE", "DEU", "GERMANY"));
        countryList.add(new ISO3166Country("AD", "AND", "ANDORRA"));
        countryList.add(new ISO3166Country("AO", "AGO", "ANGOLA"));
        countryList.add(new ISO3166Country("AI", "AIA", "ANGUILLA"));
        countryList.add(new ISO3166Country("AQ", "ATA", "ANTARCTICA"));
        countryList.add(new ISO3166Country("AG", "ATG", "ANTIGUA AND BARBUDA"));
        countryList.add(new ISO3166Country("SA", "SAU", "SAUDI ARABIA"));
        countryList.add(new ISO3166Country("AR", "ARG", "ARGENTINA"));
        countryList.add(new ISO3166Country("AM", "ARM", "ARMENIA"));
        countryList.add(new ISO3166Country("AW", "ABW", "ARUBA"));
        countryList.add(new ISO3166Country("AU", "AUS", "AUSTRALIA"));
        countryList.add(new ISO3166Country("AT", "AUT", "AUSTRIA"));
        countryList.add(new ISO3166Country("AZ", "AZE", "AZERBAIJAN"));
        countryList.add(new ISO3166Country("BS", "BHS", "BAHAMAS"));
        countryList.add(new ISO3166Country("BH", "BHR", "BAHRAIN"));
        countryList.add(new ISO3166Country("BD", "BGD", "BANGLADESH"));
        countryList.add(new ISO3166Country("BB", "BRB", "BARBADOS"));
        countryList.add(new ISO3166Country("BY", "BLR", "BELARUS"));
        countryList.add(new ISO3166Country("BE", "BEL", "BELGIUM"));
        countryList.add(new ISO3166Country("BZ", "BLZ", "BELIZE"));
        countryList.add(new ISO3166Country("BJ", "BEN", "BENIN"));
        countryList.add(new ISO3166Country("BM", "BMU", "BERMUDA"));
        countryList.add(new ISO3166Country("BT", "BTN", "BHUTAN"));
        countryList.add(new ISO3166Country("BO", "BOL", "BOLIVIA, PLURINATIONAL STATE OF"));
        countryList.add(new ISO3166Country("BA", "BIH", "BOSNIA AND HERZEGOVINA"));
        countryList.add(new ISO3166Country("BW", "BWA", "BOTSWANA"));
        countryList.add(new ISO3166Country("BV", "BVT", "BOUVET ISLAND"));
        countryList.add(new ISO3166Country("BR", "BRA", "BRAZIL"));
        countryList.add(new ISO3166Country("BN", "BRN", "BRUNEI DARUSSALAM"));
        countryList.add(new ISO3166Country("BG", "BGR", "BULGARIA"));
        countryList.add(new ISO3166Country("BF", "BFA", "BURKINA FASO"));
        countryList.add(new ISO3166Country("BI", "BDI", "BURUNDI"));
        countryList.add(new ISO3166Country("KY", "CYM", "CAYMAN ISLANDS"));
        countryList.add(new ISO3166Country("KH", "KHM", "CAMBODIA"));
        countryList.add(new ISO3166Country("CM", "CMR", "CAMEROON"));
        countryList.add(new ISO3166Country("CA", "CAN", "CANADA"));
        countryList.add(new ISO3166Country("CV", "CPV", "CAPE VERDE"));
        countryList.add(new ISO3166Country("CF", "CAF", "CENTRAL AFRICAN REPUBLIC"));
        countryList.add(new ISO3166Country("CL", "CHL", "CHILE"));
        countryList.add(new ISO3166Country("CN", "CHN", "CHINA"));
        countryList.add(new ISO3166Country("CX", "CXR", "CHRISTMAS ISLAND"));
        countryList.add(new ISO3166Country("CY", "CYP", "CYPRUS"));
        countryList.add(new ISO3166Country("CC", "CCK", "COCOS (KEELING) ISLANDS"));
        countryList.add(new ISO3166Country("CO", "COL", "COLOMBIA"));
        countryList.add(new ISO3166Country("KM", "COM", "COMOROS"));
        countryList.add(new ISO3166Country("CG", "COG", "CONGO"));
        countryList.add(new ISO3166Country("CK", "COK", "COOK ISLANDS"));
        countryList.add(new ISO3166Country("KR", "KOR", "KOREA, REPUBLIC OF"));
        countryList.add(new ISO3166Country("KP", "PRK", "KOREA, DEMOCRATIC PEOPLE'S REPUBLIC OF"));
        countryList.add(new ISO3166Country("CR", "CRI", "COSTA RICA"));
        countryList.add(new ISO3166Country("CI", "CIV", "CÔTE D'IVOIRE"));
        countryList.add(new ISO3166Country("HR", "HRV", "CROATIA"));
        countryList.add(new ISO3166Country("CU", "CUB", "CUBA"));
        countryList.add(new ISO3166Country("DK", "DNK", "DENMARK"));
        countryList.add(new ISO3166Country("DJ", "DJI", "DJIBOUTI"));
        countryList.add(new ISO3166Country("DO", "DOM", "DOMINICAN REPUBLIC"));
        countryList.add(new ISO3166Country("DM", "DMA", "DOMINICA"));
        countryList.add(new ISO3166Country("EG", "EGY", "EGYPT"));
        countryList.add(new ISO3166Country("SV", "SLV", "EL SALVADOR"));
        countryList.add(new ISO3166Country("AE", "ARE", "UNITED ARAB EMIRATES"));
        countryList.add(new ISO3166Country("EC", "ECU", "ECUADOR"));
        countryList.add(new ISO3166Country("ER", "ERI", "ERITREA"));
        countryList.add(new ISO3166Country("ES", "ESP", "SPAIN"));
        countryList.add(new ISO3166Country("EE", "EST", "ESTONIA"));
        countryList.add(new ISO3166Country("US", "USA", "UNITED STATES"));
        countryList.add(new ISO3166Country("ET", "ETH", "ETHIOPIA"));
        countryList.add(new ISO3166Country("FK", "FLK", "FALKLAND ISLANDS (MALVINAS)"));
        countryList.add(new ISO3166Country("FO", "FRO", "FAROE ISLANDS"));
        countryList.add(new ISO3166Country("FJ", "FJI", "FIJI"));
        countryList.add(new ISO3166Country("FI", "FIN", "FINLAND"));
        countryList.add(new ISO3166Country("FR", "FRA", "FRANCE"));
        countryList.add(new ISO3166Country("GA", "GAB", "GABON"));
        countryList.add(new ISO3166Country("GM", "GMB", "GAMBIA"));
        countryList.add(new ISO3166Country("GE", "GEO", "GEORGIA"));
        countryList.add(new ISO3166Country("GS", "SGS", "SOUTH GEORGIA AND THE SOUTH SANDWICH ISLANDS"));
        countryList.add(new ISO3166Country("GH", "GHA", "GHANA"));
        countryList.add(new ISO3166Country("GI", "GIB", "GIBRALTAR"));
        countryList.add(new ISO3166Country("GR", "GRC", "GREECE"));
        countryList.add(new ISO3166Country("GD", "GRD", "GRENADA"));
        countryList.add(new ISO3166Country("GL", "GRL", "GREENLAND"));
        countryList.add(new ISO3166Country("GP", "GLP", "GUADELOUPE"));
        countryList.add(new ISO3166Country("GU", "GUM", "GUAM"));
        countryList.add(new ISO3166Country("GT", "GTM", "GUATEMALA"));
        countryList.add(new ISO3166Country("GN", "GIN", "GUINEA"));
        countryList.add(new ISO3166Country("GW", "GNB", "GUINEA-BISSAU"));
        countryList.add(new ISO3166Country("GQ", "GNQ", "EQUATORIAL GUINEA"));
        countryList.add(new ISO3166Country("GY", "GUY", "GUYANA"));
        countryList.add(new ISO3166Country("GF", "GUF", "FRENCH GUIANA"));
        countryList.add(new ISO3166Country("HT", "HTI", "HAITI"));
        countryList.add(new ISO3166Country("HM", "HMD", "HEARD ISLAND AND MCDONALD ISLANDS"));
        countryList.add(new ISO3166Country("HN", "HND", "HONDURAS"));
        countryList.add(new ISO3166Country("HK", "HKG", "HONG KONG"));
        countryList.add(new ISO3166Country("HU", "HUN", "HUNGARY"));
        countryList.add(new ISO3166Country("UM", "UMI", "UNITED STATES MINOR OUTLYING ISLANDS"));
        countryList.add(new ISO3166Country("VG", "VGB", "VIRGIN ISLANDS (BRITISH)"));
        countryList.add(new ISO3166Country("VI", "VIR", "VIRGIN ISLANDS (U.S.)"));
        countryList.add(new ISO3166Country("IN", "IND", "INDIA"));
        countryList.add(new ISO3166Country("ID", "IDN", "INDONESIA"));
        countryList.add(new ISO3166Country("IR", "IRN", "IRAN, ISLAMIC REPUBLIC OF"));
        countryList.add(new ISO3166Country("IQ", "IRQ", "IRAQ"));
        countryList.add(new ISO3166Country("IE", "IRL", "IRELAND"));
        countryList.add(new ISO3166Country("IS", "ISL", "ICELAND"));
        countryList.add(new ISO3166Country("IL", "ISR", "ISRAEL"));
        countryList.add(new ISO3166Country("IT", "ITA", "ITALY"));
        countryList.add(new ISO3166Country("JM", "JAM", "JAMAICA"));
        countryList.add(new ISO3166Country("JP", "JPN", "JAPAN"));
        countryList.add(new ISO3166Country("JO", "JOR", "JORDAN"));
        countryList.add(new ISO3166Country("KZ", "KAZ", "KAZAKHSTAN"));
        countryList.add(new ISO3166Country("KE", "KEN", "KENYA"));
        countryList.add(new ISO3166Country("KG", "KGZ", "KYRGYZSTAN"));
        countryList.add(new ISO3166Country("KI", "KIR", "KIRIBATI"));
        countryList.add(new ISO3166Country("KW", "KWT", "KUWAIT"));
        countryList.add(new ISO3166Country("LA", "LAO", "LAO PEOPLE'S DEMOCRATIC REPUBLIC"));
        countryList.add(new ISO3166Country("LS", "LSO", "LESOTHO"));
        countryList.add(new ISO3166Country("LV", "LVA", "LATVIA"));
        countryList.add(new ISO3166Country("LB", "LBN", "LEBANON"));
        countryList.add(new ISO3166Country("LR", "LBR", "LIBERIA"));
        countryList.add(new ISO3166Country("LY", "LBY", "LIBYAN ARAB JAMAHIRIYA"));
        countryList.add(new ISO3166Country("LI", "LIE", "LIECHTENSTEIN"));
        countryList.add(new ISO3166Country("LT", "LTU", "LITHUANIA"));
        countryList.add(new ISO3166Country("LU", "LUX", "LUXEMBOURG"));
        countryList.add(new ISO3166Country("MO", "MAC", "MACAO"));
        countryList.add(new ISO3166Country("MK", "MKD", "MACEDONIA, THE FORMER YUGOSLAV REPUBLIC OF"));
        countryList.add(new ISO3166Country("MG", "MDG", "MADAGASCAR"));
        countryList.add(new ISO3166Country("MY", "MYS", "MALAYSIA"));
        countryList.add(new ISO3166Country("MW", "MWI", "MALAWI"));
        countryList.add(new ISO3166Country("MV", "MDV", "MALDIVES"));
        countryList.add(new ISO3166Country("ML", "MLI", "MALI"));
        countryList.add(new ISO3166Country("MT", "MLT", "MALTA"));
        countryList.add(new ISO3166Country("MP", "MNP", "NORTHERN MARIANA ISLANDS"));
        countryList.add(new ISO3166Country("MA", "MAR", "MOROCCO"));
        countryList.add(new ISO3166Country("MH", "MHL", "MARSHALL ISLANDS"));
        countryList.add(new ISO3166Country("MQ", "MTQ", "MARTINIQUE"));
        countryList.add(new ISO3166Country("MU", "MUS", "MAURITIUS"));
        countryList.add(new ISO3166Country("MR", "MRT", "MAURITANIA"));
        countryList.add(new ISO3166Country("YT", "MYT", "MAYOTTE"));
        countryList.add(new ISO3166Country("MX", "MEX", "MEXICO"));
        countryList.add(new ISO3166Country("FM", "FSM", "MICRONESIA, FEDERATED STATES OF"));
        countryList.add(new ISO3166Country("MD", "MDA", "MOLDOVA, REPUBLIC OF"));
        countryList.add(new ISO3166Country("MC", "MCO", "MONACO"));
        countryList.add(new ISO3166Country("MN", "MNG", "MONGOLIA"));
        countryList.add(new ISO3166Country("MS", "MSR", "MONTSERRAT"));
        countryList.add(new ISO3166Country("MZ", "MOZ", "MOZAMBIQUE"));
        countryList.add(new ISO3166Country("MM", "MMR", "MYANMAR"));
        countryList.add(new ISO3166Country("NA", "NAM", "NAMIBIA"));
        countryList.add(new ISO3166Country("NR", "NRU", "NAURU"));
        countryList.add(new ISO3166Country("NP", "NPL", "NEPAL"));
        countryList.add(new ISO3166Country("NI", "NIC", "NICARAGUA"));
        countryList.add(new ISO3166Country("NE", "NER", "NIGER"));
        countryList.add(new ISO3166Country("NG", "NGA", "NIGERIA"));
        countryList.add(new ISO3166Country("NU", "NIU", "NIUE"));
        countryList.add(new ISO3166Country("NF", "NFK", "NORFOLK ISLAND"));
        countryList.add(new ISO3166Country("NO", "NOR", "NORWAY"));
        countryList.add(new ISO3166Country("NC", "NCL", "NEW CALEDONIA"));
        countryList.add(new ISO3166Country("NZ", "NZL", "NEW ZEALAND"));
        countryList.add(new ISO3166Country("IO", "IOT", "BRITISH INDIAN OCEAN TERRITORY"));
        countryList.add(new ISO3166Country("OM", "OMN", "OMAN"));
        countryList.add(new ISO3166Country("UG", "UGA", "UGANDA"));
        countryList.add(new ISO3166Country("UZ", "UZB", "UZBEKISTAN"));
        countryList.add(new ISO3166Country("PK", "PAK", "PAKISTAN"));
        countryList.add(new ISO3166Country("PW", "PLW", "PALAU"));
        countryList.add(new ISO3166Country("PA", "PAN", "PANAMA"));
        countryList.add(new ISO3166Country("PG", "PNG", "PAPUA NEW GUINEA"));
        countryList.add(new ISO3166Country("PY", "PRY", "PARAGUAY"));
        countryList.add(new ISO3166Country("NL", "NLD", "NETHERLANDS"));
        countryList.add(new ISO3166Country("PE", "PER", "PERU"));
        countryList.add(new ISO3166Country("PH", "PHL", "PHILIPPINES"));
        countryList.add(new ISO3166Country("PN", "PCN", "PITCAIRN"));
        countryList.add(new ISO3166Country("PL", "POL", "POLAND"));
        countryList.add(new ISO3166Country("PF", "PYF", "FRENCH POLYNESIA"));
        countryList.add(new ISO3166Country("PR", "PRI", "PUERTO RICO"));
        countryList.add(new ISO3166Country("PT", "PRT", "PORTUGAL"));
        countryList.add(new ISO3166Country("QA", "QAT", "QATAR"));
        countryList.add(new ISO3166Country("RE", "REU", "RÉUNION"));
        countryList.add(new ISO3166Country("RO", "ROU", "ROMANIA"));
        countryList.add(new ISO3166Country("GB", "GBR", "UNITED KINGDOM"));
        countryList.add(new ISO3166Country("RU", "RUS", "RUSSIAN FEDERATION"));
        countryList.add(new ISO3166Country("RW", "RWA", "RWANDA"));
        countryList.add(new ISO3166Country("EH", "ESH", "WESTERN SAHARA *"));
        countryList.add(new ISO3166Country("SH", "SHN", "SAINT HELENA, ASCENSION AND TRISTAN DA CUNHA"));
        countryList.add(new ISO3166Country("KN", "KNA", "SAINT KITTS AND NEVIS"));
        countryList.add(new ISO3166Country("LC", "LCA", "SAINT LUCIA"));
        countryList.add(new ISO3166Country("SM", "SMR", "SAN MARINO"));
        countryList.add(new ISO3166Country("PM", "SPM", "SAINT PIERRE AND MIQUELON"));
        countryList.add(new ISO3166Country("VA", "VAT", "HOLY SEE (VATICAN CITY STATE)"));
        countryList.add(new ISO3166Country("VC", "VCT", "SAINT VINCENT AND THE GRENADINES"));
        countryList.add(new ISO3166Country("SB", "SLB", "SOLOMON ISLANDS"));
        countryList.add(new ISO3166Country("WS", "WSM", "SAMOA"));
        countryList.add(new ISO3166Country("AS", "ASM", "AMERICAN SAMOA"));
        countryList.add(new ISO3166Country("ST", "STP", "SAO TOME AND PRINCIPE"));
        countryList.add(new ISO3166Country("SN", "SEN", "SENEGAL"));
        countryList.add(new ISO3166Country("SC", "SYC", "SEYCHELLES"));
        countryList.add(new ISO3166Country("SL", "SLE", "SIERRA LEONE"));
        countryList.add(new ISO3166Country("SG", "SGP", "SINGAPORE"));
        countryList.add(new ISO3166Country("SK", "SVK", "SLOVAKIA"));
        countryList.add(new ISO3166Country("SI", "SVN", "SLOVENIA"));
        countryList.add(new ISO3166Country("SO", "SOM", "SOMALIA"));
        countryList.add(new ISO3166Country("SD", "SDN", "SUDAN"));
        countryList.add(new ISO3166Country("LK", "LKA", "SRI LANKA"));
        countryList.add(new ISO3166Country("SE", "SWE", "SWEDEN"));
        countryList.add(new ISO3166Country("CH", "CHE", "SWITZERLAND"));
        countryList.add(new ISO3166Country("SR", "SUR", "SURINAME"));
        countryList.add(new ISO3166Country("SJ", "SJM", "SVALBARD AND JAN MAYEN"));
        countryList.add(new ISO3166Country("SZ", "SWZ", "SWAZILAND"));
        countryList.add(new ISO3166Country("SY", "SYR", "SYRIAN ARAB REPUBLIC"));
        countryList.add(new ISO3166Country("TJ", "TJK", "TAJIKISTAN"));
        countryList.add(new ISO3166Country("TW", "TWN", "TAIWAN, PROVINCE OF CHINA"));
        countryList.add(new ISO3166Country("TZ", "TZA", "TANZANIA, UNITED REPUBLIC OF"));
        countryList.add(new ISO3166Country("TD", "TCD", "CHAD"));
        countryList.add(new ISO3166Country("CZ", "CZE", "CZECH REPUBLIC"));
        countryList.add(new ISO3166Country("TF", "ATF", "FRENCH SOUTHERN TERRITORIES"));
        countryList.add(new ISO3166Country("TH", "THA", "THAILAND"));
        countryList.add(new ISO3166Country("TL", "TLS", "TIMOR-LESTE"));
        countryList.add(new ISO3166Country("TG", "TGO", "TOGO"));
        countryList.add(new ISO3166Country("TK", "TKL", "TOKELAU"));
        countryList.add(new ISO3166Country("TO", "TON", "TONGA"));
        countryList.add(new ISO3166Country("TT", "TTO", "TRINIDAD AND TOBAGO"));
        countryList.add(new ISO3166Country("TN", "TUN", "TUNISIA"));
        countryList.add(new ISO3166Country("TM", "TKM", "TURKMENISTAN"));
        countryList.add(new ISO3166Country("TC", "TCA", "TURKS AND CAICOS ISLANDS"));
        countryList.add(new ISO3166Country("TR", "TUR", "TURKEY"));
        countryList.add(new ISO3166Country("TV", "TUV", "TUVALU"));
        countryList.add(new ISO3166Country("UA", "UKR", "UKRAINE"));
        countryList.add(new ISO3166Country("UY", "URY", "URUGUAY"));
        countryList.add(new ISO3166Country("VU", "VUT", "VANUATU"));
        countryList.add(new ISO3166Country("VE", "VEN", "VENEZUELA, BOLIVARIAN REPUBLIC OF"));
        countryList.add(new ISO3166Country("VN", "VNM", "VIET NAM"));
        countryList.add(new ISO3166Country("WF", "WLF", "WALLIS AND FUTUNA"));
        countryList.add(new ISO3166Country("YE", "YEM", "YEMEN"));
        countryList.add(new ISO3166Country("CD", "COD", "CONGO, DEMOCRATIC REPUBLIC OF THE"));
        countryList.add(new ISO3166Country("ZM", "ZMB", "ZAMBIA"));
        countryList.add(new ISO3166Country("ZW", "ZWE", "ZIMBABWE"));
        countryList.add(new ISO3166Country("PS", "PSE", "PALESTINIAN TERRITORY, OCCUPIED"));
        countryList.add(new ISO3166Country("AX", "ALA", "ÅLAND ISLANDS"));
        countryList.add(new ISO3166Country("JE", "JEY", "JERSEY"));
        countryList.add(new ISO3166Country("IM", "IMN", "ISLE OF MAN"));
        countryList.add(new ISO3166Country("GG", "GGY", "GUERNSEY"));
        countryList.add(new ISO3166Country("ME", "MNE", "MONTENEGRO"));
        countryList.add(new ISO3166Country("RS", "SRB", "SERBIA"));
        countryList.add(new ISO3166Country("BL", "BLM", "SAINT BARTHÉLEMY"));
        countryList.add(new ISO3166Country("MF", "MAF", "SAINT MARTIN (FRENCH PART)"));
        countryList.add(new ISO3166Country("BQ", "BES", "BONAIRE, SAINT EUSTATIUS AND SABA"));
        countryList.add(new ISO3166Country("CW", "CUW", "CURAÇAO"));
        countryList.add(new ISO3166Country("SX", "SXM", "SINT MAARTEN (DUTCH PART)"));

        Collections.sort(countryList);

        return countryList;

    }

    /**
     * Uses predicate validation to find a specific ISO3166Country within a
     *            ISO3166Country list based on caller provided ISO3166Country name.
     * 
     * @param countryName Country name to use for selecting.
     * @return The country that matches the country name.
     */
    public static ISO3166Country getCountryByName(String countryName)
    {

        if (StringHelper.isNullOrEmpty(countryName)) return null;

        List<ISO3166Country> countryList = FieldValues.getListOfCountries();
        @SuppressWarnings("unchecked")
        Collection<ISO3166Country> filtered = CollectionUtils.select(countryList, new CountryNamePredicate(countryName));
        if (filtered.isEmpty()) return null;

        ISO3166Country item = filtered.toArray(new ISO3166Country[filtered.size()])[0];

        return item;

    }

    /**
     * Uses predicate validation to find a specific ISO3166Country within a
     * ISO3166Country list based on caller provided ISO3166Country Alpha3.
     * 
     * @param countryAlpha3 Country Alpha3 to use for selecting.
     * @return The country that matches the country Alpha3.
     */
    public static ISO3166Country getCountryByAlpha3(String countryAlpha3)
    {
        if (StringHelper.isNullOrEmpty(countryAlpha3)) return null;

        List<ISO3166Country> countryList = FieldValues.getListOfCountries();

        @SuppressWarnings("unchecked")
        Collection<ISO3166Country> filtered = CollectionUtils.select(countryList,
                                                                     new CountryAlpha3Predicate(countryAlpha3));
        if (filtered.isEmpty()) return null;

        ISO3166Country item = filtered.toArray(new ISO3166Country[filtered.size()])[0];

        return item;
    }

    /**
     * Uses predicate validation to find a specific MarkingValue within a
     * MarkingValue list based on caller provided MarkingValue title.
     * 
     * @param markingValueTitle Title to use for selecting.
     * @return The MarkingValue for the title.
     */
    public static MarkingValue getMarkingValueByTitle(String markingValueTitle, List<MarkingValue> markings)
    {

        @SuppressWarnings("unchecked")
        Collection<MarkingValue> filtered = CollectionUtils.select(markings,
                                                                   new MarkingValueTitlePredicate(markingValueTitle));
        if (filtered.isEmpty()) return null;

        MarkingValue item = filtered.toArray(new MarkingValue[filtered.size()])[0];

        return item;
    }

    /**
     * Uses predicate validation to find a specific MarkingValue within a
     * MarkingValue list based on caller provided MarkingValue portion.
     * 
     * @param markingValuePortion Portion to use for selecting.
     * @return The MarkingValue for the portion.
     */
    public static MarkingValue getMarkingValueByPortion(String markingValuePortion, List<MarkingValue> markings)
    {

        @SuppressWarnings("unchecked")
        Collection<MarkingValue> filtered = CollectionUtils.select(markings,
                                                                   new MarkingValuePortionPredicate(markingValuePortion));
        if (filtered.isEmpty()) return null;

        MarkingValue item = filtered.toArray(new MarkingValue[filtered.size()])[0];

        return item;
    }

    private abstract static class FilterPredicate implements Predicate {

        private String _filter;

        public FilterPredicate(String filter) {
            _filter = filter;
        }

        public boolean evaluate(Object object)
        {
            if (_filter == null) return (object == null);
            
            return _filter.equals(getValue(object));
        }

        protected abstract String getValue(Object object);

    }

    private static class CountryNamePredicate extends FilterPredicate {

        public CountryNamePredicate(String filter) {
            super(filter);
        }

        protected String getValue(Object object)
        {
            if (!(object instanceof ISO3166Country)) return null;
            return ((ISO3166Country) object).getName();
        }
    }

    private static class CountryAlpha3Predicate extends FilterPredicate {

        public CountryAlpha3Predicate(String filter) {
            super(filter);
        }

        protected String getValue(Object object)
        {
            if (!(object instanceof ISO3166Country)) return null;
            return ((ISO3166Country) object).getAlpha3();
        }
    }

    private static class MarkingValueTitlePredicate extends FilterPredicate {

        public MarkingValueTitlePredicate(String filter) {
            super(filter);
        }

        protected String getValue(Object object)
        {
            if (!(object instanceof MarkingValue)) return null;
            return ((MarkingValue) object).getTitle();
        }
    }

    private static class MarkingValuePortionPredicate extends FilterPredicate {

        public MarkingValuePortionPredicate(String filter) {
            super(filter);
        }

        protected String getValue(Object object)
        {
            if (!(object instanceof MarkingValue)) return null;
            return ((MarkingValue) object).getPortion();
        }
    }

}