package com.incadencecorp.coalesce.common.classification;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.incadencecorp.coalesce.common.classification.FieldValues;
import com.incadencecorp.coalesce.common.classification.ISO3166Country;
import com.incadencecorp.coalesce.common.classification.Marking;
import com.incadencecorp.coalesce.common.classification.MarkingValue;

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

public class FieldValuesTest {

    private static List<MarkingValue> _allClassifications;
    private static List<MarkingValue> _natoClassifications;
    private static List<MarkingValue> _jointNotUSAClassifications;
    private static List<MarkingValue> _jointUSAClassifications;
    private static List<MarkingValue> _notJointNotUSAClassifications;
    private static List<MarkingValue> _notJointUSAClassifications;

    private static List<MarkingValue> _compartments;

    private static List<ISO3166Country> _countries;

    public static List<MarkingValue> getAllClassifications()
    {
        return _allClassifications;
    }

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        InitializeAllClassifications();
        InitializeNATOClassifications();
        InitializeJointNotUSAClassifications();
        InitializeJointUSAClassifications();
        InitializeNotJointNotUSAClassifications();
        InitializeNotJointUSAClassifications();

        InitializeCompartments();

        InitializeListOfCountries();

    }

    private static void InitializeAllClassifications()
    {
        List<MarkingValue> allClassifications = new ArrayList<MarkingValue>();

        allClassifications.add(new MarkingValue("", "COSMIC TOP SECRET", "", "CTS"));
        allClassifications.add(new MarkingValue("", "COSMIC TOP SECRET ATOMAL", "", "CTS-A"));
        allClassifications.add(new MarkingValue("", "COSMIC TOP SECRET BOHEMIA", "", "CTS-B"));
        allClassifications.add(new MarkingValue("", "NATO SECRET", "", "NS"));
        allClassifications.add(new MarkingValue("", "SECRET ATOMAL", "", "NS-A"));
        allClassifications.add(new MarkingValue("", "NATO CONFIDENTIAL", "", "NC"));
        allClassifications.add(new MarkingValue("", "CONFIDENTIAL ATOMAL", "", "NC-A"));
        allClassifications.add(new MarkingValue("", "NATO RESTRICTED", "", "NR"));
        allClassifications.add(new MarkingValue("", "NATO UNCLASSIFIED", "", "NU"));
        allClassifications.add(new MarkingValue("", "JOINT TOP SECRET", "", "JOINT TS"));
        allClassifications.add(new MarkingValue("", "JOINT SECRET", "", "JOINT S"));
        allClassifications.add(new MarkingValue("", "JOINT CONFIDENTIAL", "", "JOINT C"));
        allClassifications.add(new MarkingValue("", "JOINT RESTRICTED", "", "JOINT R"));
        allClassifications.add(new MarkingValue("", "JOINT UNCLASSIFIED", "", "JOINT U"));
        allClassifications.add(new MarkingValue("", "TOP SECRET", "", "TS"));
        allClassifications.add(new MarkingValue("", "SECRET", "", "S"));
        allClassifications.add(new MarkingValue("", "CONFIDENTIAL", "", "C"));
        allClassifications.add(new MarkingValue("", "RESTRICTED", "", "R"));
        allClassifications.add(new MarkingValue("", "UNCLASSIFIED", "", "U"));

        _allClassifications = Collections.unmodifiableList(allClassifications);

    }

    private static void InitializeNATOClassifications()
    {
        List<MarkingValue> natoClassifications = new ArrayList<MarkingValue>();

        natoClassifications.add(new MarkingValue("", "COSMIC TOP SECRET", "", "CTS"));
        natoClassifications.add(new MarkingValue("", "COSMIC TOP SECRET ATOMAL", "", "CTS-A"));
        natoClassifications.add(new MarkingValue("", "COSMIC TOP SECRET BOHEMIA", "", "CTS-B"));
        natoClassifications.add(new MarkingValue("", "NATO SECRET", "", "NS"));
        natoClassifications.add(new MarkingValue("", "SECRET ATOMAL", "", "NS-A"));
        natoClassifications.add(new MarkingValue("", "NATO CONFIDENTIAL", "", "NC"));
        natoClassifications.add(new MarkingValue("", "CONFIDENTIAL ATOMAL", "", "NC-A"));
        natoClassifications.add(new MarkingValue("", "NATO RESTRICTED", "", "NR"));
        natoClassifications.add(new MarkingValue("", "NATO UNCLASSIFIED", "", "NU"));

        _natoClassifications = Collections.unmodifiableList(natoClassifications);

    }

    private static void InitializeJointNotUSAClassifications()
    {
        List<MarkingValue> jointNotUSAClassification = new ArrayList<MarkingValue>();

        jointNotUSAClassification.add(new MarkingValue("", "JOINT TOP SECRET", "", "JOINT TS"));
        jointNotUSAClassification.add(new MarkingValue("", "JOINT SECRET", "", "JOINT S"));
        jointNotUSAClassification.add(new MarkingValue("", "JOINT CONFIDENTIAL", "", "JOINT C"));
        jointNotUSAClassification.add(new MarkingValue("", "JOINT RESTRICTED", "", "JOINT R"));
        jointNotUSAClassification.add(new MarkingValue("", "JOINT UNCLASSIFIED", "", "JOINT U"));

        _jointNotUSAClassifications = Collections.unmodifiableList(jointNotUSAClassification);
    }

    private static void InitializeJointUSAClassifications()
    {
        List<MarkingValue> jointUSAClassification = new ArrayList<MarkingValue>();

        jointUSAClassification.add(new MarkingValue("", "JOINT TOP SECRET", "", "JOINT TS"));
        jointUSAClassification.add(new MarkingValue("", "JOINT SECRET", "", "JOINT S"));
        jointUSAClassification.add(new MarkingValue("", "JOINT CONFIDENTIAL", "", "JOINT C"));
        jointUSAClassification.add(new MarkingValue("", "JOINT UNCLASSIFIED", "", "JOINT U"));

        _jointUSAClassifications = Collections.unmodifiableList(jointUSAClassification);
    }

    private static void InitializeNotJointNotUSAClassifications()
    {
        List<MarkingValue> notJointNotUSAClassification = new ArrayList<MarkingValue>();

        notJointNotUSAClassification.add(new MarkingValue("", "TOP SECRET", "", "TS"));
        notJointNotUSAClassification.add(new MarkingValue("", "SECRET", "", "S"));
        notJointNotUSAClassification.add(new MarkingValue("", "CONFIDENTIAL", "", "C"));
        notJointNotUSAClassification.add(new MarkingValue("", "RESTRICTED", "", "R"));
        notJointNotUSAClassification.add(new MarkingValue("", "UNCLASSIFIED", "", "U"));

        _notJointNotUSAClassifications = Collections.unmodifiableList(notJointNotUSAClassification);
    }

    private static void InitializeNotJointUSAClassifications()
    {
        List<MarkingValue> notJointUSAClassification = new ArrayList<MarkingValue>();

        notJointUSAClassification.add(new MarkingValue("", "TOP SECRET", "", "TS"));
        notJointUSAClassification.add(new MarkingValue("", "SECRET", "", "S"));
        notJointUSAClassification.add(new MarkingValue("", "CONFIDENTIAL", "", "C"));
        notJointUSAClassification.add(new MarkingValue("", "UNCLASSIFIED", "", "U"));

        _notJointUSAClassifications = Collections.unmodifiableList(notJointUSAClassification);
    }

    private static void InitializeCompartments()
    {
        List<MarkingValue> compartments = new ArrayList<MarkingValue>();

        compartments.add(new MarkingValue("", "COMINT", "SI", "SI"));
        compartments.add(new MarkingValue("", "HCS", "HCS", "HCS"));
        compartments.add(new MarkingValue("", "KLONDIKE", "KDK", "KDK"));
        compartments.add(new MarkingValue("", "TALENT KEYHOLE", "TK", "TK"));

        _compartments = Collections.unmodifiableList(compartments);
    }

    private static void InitializeListOfCountries()
    {

        List<ISO3166Country> countryList = new ArrayList<ISO3166Country>();

        countryList.add(new ISO3166Country("AF", "AFG", "AFGHANISTAN"));
        countryList.add(new ISO3166Country("AL", "ALB", "ALBANIA"));
        countryList.add(new ISO3166Country("DZ", "DZA", "ALGERIA"));
        countryList.add(new ISO3166Country("AS", "ASM", "AMERICAN SAMOA"));
        countryList.add(new ISO3166Country("AD", "AND", "ANDORRA"));
        countryList.add(new ISO3166Country("AO", "AGO", "ANGOLA"));
        countryList.add(new ISO3166Country("AI", "AIA", "ANGUILLA"));
        countryList.add(new ISO3166Country("AQ", "ATA", "ANTARCTICA"));
        countryList.add(new ISO3166Country("AG", "ATG", "ANTIGUA AND BARBUDA"));
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
        countryList.add(new ISO3166Country("BQ", "BES", "BONAIRE, SAINT EUSTATIUS AND SABA"));
        countryList.add(new ISO3166Country("BA", "BIH", "BOSNIA AND HERZEGOVINA"));
        countryList.add(new ISO3166Country("BW", "BWA", "BOTSWANA"));
        countryList.add(new ISO3166Country("BV", "BVT", "BOUVET ISLAND"));
        countryList.add(new ISO3166Country("BR", "BRA", "BRAZIL"));
        countryList.add(new ISO3166Country("IO", "IOT", "BRITISH INDIAN OCEAN TERRITORY"));
        countryList.add(new ISO3166Country("BN", "BRN", "BRUNEI DARUSSALAM"));
        countryList.add(new ISO3166Country("BG", "BGR", "BULGARIA"));
        countryList.add(new ISO3166Country("BF", "BFA", "BURKINA FASO"));
        countryList.add(new ISO3166Country("BI", "BDI", "BURUNDI"));
        countryList.add(new ISO3166Country("KH", "KHM", "CAMBODIA"));
        countryList.add(new ISO3166Country("CM", "CMR", "CAMEROON"));
        countryList.add(new ISO3166Country("CA", "CAN", "CANADA"));
        countryList.add(new ISO3166Country("CV", "CPV", "CAPE VERDE"));
        countryList.add(new ISO3166Country("KY", "CYM", "CAYMAN ISLANDS"));
        countryList.add(new ISO3166Country("CF", "CAF", "CENTRAL AFRICAN REPUBLIC"));
        countryList.add(new ISO3166Country("TD", "TCD", "CHAD"));
        countryList.add(new ISO3166Country("CL", "CHL", "CHILE"));
        countryList.add(new ISO3166Country("CN", "CHN", "CHINA"));
        countryList.add(new ISO3166Country("CX", "CXR", "CHRISTMAS ISLAND"));
        countryList.add(new ISO3166Country("CC", "CCK", "COCOS (KEELING) ISLANDS"));
        countryList.add(new ISO3166Country("CO", "COL", "COLOMBIA"));
        countryList.add(new ISO3166Country("KM", "COM", "COMOROS"));
        countryList.add(new ISO3166Country("CG", "COG", "CONGO"));
        countryList.add(new ISO3166Country("CD", "COD", "CONGO, DEMOCRATIC REPUBLIC OF THE"));
        countryList.add(new ISO3166Country("CK", "COK", "COOK ISLANDS"));
        countryList.add(new ISO3166Country("CR", "CRI", "COSTA RICA"));
        countryList.add(new ISO3166Country("HR", "HRV", "CROATIA"));
        countryList.add(new ISO3166Country("CU", "CUB", "CUBA"));
        countryList.add(new ISO3166Country("CW", "CUW", "CURAÇAO"));
        countryList.add(new ISO3166Country("CY", "CYP", "CYPRUS"));
        countryList.add(new ISO3166Country("CZ", "CZE", "CZECH REPUBLIC"));
        countryList.add(new ISO3166Country("CI", "CIV", "CÔTE D'IVOIRE"));
        countryList.add(new ISO3166Country("DK", "DNK", "DENMARK"));
        countryList.add(new ISO3166Country("DJ", "DJI", "DJIBOUTI"));
        countryList.add(new ISO3166Country("DM", "DMA", "DOMINICA"));
        countryList.add(new ISO3166Country("DO", "DOM", "DOMINICAN REPUBLIC"));
        countryList.add(new ISO3166Country("EC", "ECU", "ECUADOR"));
        countryList.add(new ISO3166Country("EG", "EGY", "EGYPT"));
        countryList.add(new ISO3166Country("SV", "SLV", "EL SALVADOR"));
        countryList.add(new ISO3166Country("GQ", "GNQ", "EQUATORIAL GUINEA"));
        countryList.add(new ISO3166Country("ER", "ERI", "ERITREA"));
        countryList.add(new ISO3166Country("EE", "EST", "ESTONIA"));
        countryList.add(new ISO3166Country("ET", "ETH", "ETHIOPIA"));
        countryList.add(new ISO3166Country("FK", "FLK", "FALKLAND ISLANDS (MALVINAS)"));
        countryList.add(new ISO3166Country("FO", "FRO", "FAROE ISLANDS"));
        countryList.add(new ISO3166Country("FJ", "FJI", "FIJI"));
        countryList.add(new ISO3166Country("FI", "FIN", "FINLAND"));
        countryList.add(new ISO3166Country("FR", "FRA", "FRANCE"));
        countryList.add(new ISO3166Country("GF", "GUF", "FRENCH GUIANA"));
        countryList.add(new ISO3166Country("PF", "PYF", "FRENCH POLYNESIA"));
        countryList.add(new ISO3166Country("TF", "ATF", "FRENCH SOUTHERN TERRITORIES"));
        countryList.add(new ISO3166Country("GA", "GAB", "GABON"));
        countryList.add(new ISO3166Country("GM", "GMB", "GAMBIA"));
        countryList.add(new ISO3166Country("GE", "GEO", "GEORGIA"));
        countryList.add(new ISO3166Country("DE", "DEU", "GERMANY"));
        countryList.add(new ISO3166Country("GH", "GHA", "GHANA"));
        countryList.add(new ISO3166Country("GI", "GIB", "GIBRALTAR"));
        countryList.add(new ISO3166Country("GR", "GRC", "GREECE"));
        countryList.add(new ISO3166Country("GL", "GRL", "GREENLAND"));
        countryList.add(new ISO3166Country("GD", "GRD", "GRENADA"));
        countryList.add(new ISO3166Country("GP", "GLP", "GUADELOUPE"));
        countryList.add(new ISO3166Country("GU", "GUM", "GUAM"));
        countryList.add(new ISO3166Country("GT", "GTM", "GUATEMALA"));
        countryList.add(new ISO3166Country("GG", "GGY", "GUERNSEY"));
        countryList.add(new ISO3166Country("GN", "GIN", "GUINEA"));
        countryList.add(new ISO3166Country("GW", "GNB", "GUINEA-BISSAU"));
        countryList.add(new ISO3166Country("GY", "GUY", "GUYANA"));
        countryList.add(new ISO3166Country("HT", "HTI", "HAITI"));
        countryList.add(new ISO3166Country("HM", "HMD", "HEARD ISLAND AND MCDONALD ISLANDS"));
        countryList.add(new ISO3166Country("VA", "VAT", "HOLY SEE (VATICAN CITY STATE)"));
        countryList.add(new ISO3166Country("HN", "HND", "HONDURAS"));
        countryList.add(new ISO3166Country("HK", "HKG", "HONG KONG"));
        countryList.add(new ISO3166Country("HU", "HUN", "HUNGARY"));
        countryList.add(new ISO3166Country("IS", "ISL", "ICELAND"));
        countryList.add(new ISO3166Country("IN", "IND", "INDIA"));
        countryList.add(new ISO3166Country("ID", "IDN", "INDONESIA"));
        countryList.add(new ISO3166Country("IR", "IRN", "IRAN, ISLAMIC REPUBLIC OF"));
        countryList.add(new ISO3166Country("IQ", "IRQ", "IRAQ"));
        countryList.add(new ISO3166Country("IE", "IRL", "IRELAND"));
        countryList.add(new ISO3166Country("IM", "IMN", "ISLE OF MAN"));
        countryList.add(new ISO3166Country("IL", "ISR", "ISRAEL"));
        countryList.add(new ISO3166Country("IT", "ITA", "ITALY"));
        countryList.add(new ISO3166Country("JM", "JAM", "JAMAICA"));
        countryList.add(new ISO3166Country("JP", "JPN", "JAPAN"));
        countryList.add(new ISO3166Country("JE", "JEY", "JERSEY"));
        countryList.add(new ISO3166Country("JO", "JOR", "JORDAN"));
        countryList.add(new ISO3166Country("KZ", "KAZ", "KAZAKHSTAN"));
        countryList.add(new ISO3166Country("KE", "KEN", "KENYA"));
        countryList.add(new ISO3166Country("KI", "KIR", "KIRIBATI"));
        countryList.add(new ISO3166Country("KP", "PRK", "KOREA, DEMOCRATIC PEOPLE'S REPUBLIC OF"));
        countryList.add(new ISO3166Country("KR", "KOR", "KOREA, REPUBLIC OF"));
        countryList.add(new ISO3166Country("KW", "KWT", "KUWAIT"));
        countryList.add(new ISO3166Country("KG", "KGZ", "KYRGYZSTAN"));
        countryList.add(new ISO3166Country("LA", "LAO", "LAO PEOPLE'S DEMOCRATIC REPUBLIC"));
        countryList.add(new ISO3166Country("LV", "LVA", "LATVIA"));
        countryList.add(new ISO3166Country("LB", "LBN", "LEBANON"));
        countryList.add(new ISO3166Country("LS", "LSO", "LESOTHO"));
        countryList.add(new ISO3166Country("LR", "LBR", "LIBERIA"));
        countryList.add(new ISO3166Country("LY", "LBY", "LIBYAN ARAB JAMAHIRIYA"));
        countryList.add(new ISO3166Country("LI", "LIE", "LIECHTENSTEIN"));
        countryList.add(new ISO3166Country("LT", "LTU", "LITHUANIA"));
        countryList.add(new ISO3166Country("LU", "LUX", "LUXEMBOURG"));
        countryList.add(new ISO3166Country("MO", "MAC", "MACAO"));
        countryList.add(new ISO3166Country("MK", "MKD", "MACEDONIA, THE FORMER YUGOSLAV REPUBLIC OF"));
        countryList.add(new ISO3166Country("MG", "MDG", "MADAGASCAR"));
        countryList.add(new ISO3166Country("MW", "MWI", "MALAWI"));
        countryList.add(new ISO3166Country("MY", "MYS", "MALAYSIA"));
        countryList.add(new ISO3166Country("MV", "MDV", "MALDIVES"));
        countryList.add(new ISO3166Country("ML", "MLI", "MALI"));
        countryList.add(new ISO3166Country("MT", "MLT", "MALTA"));
        countryList.add(new ISO3166Country("MH", "MHL", "MARSHALL ISLANDS"));
        countryList.add(new ISO3166Country("MQ", "MTQ", "MARTINIQUE"));
        countryList.add(new ISO3166Country("MR", "MRT", "MAURITANIA"));
        countryList.add(new ISO3166Country("MU", "MUS", "MAURITIUS"));
        countryList.add(new ISO3166Country("YT", "MYT", "MAYOTTE"));
        countryList.add(new ISO3166Country("MX", "MEX", "MEXICO"));
        countryList.add(new ISO3166Country("FM", "FSM", "MICRONESIA, FEDERATED STATES OF"));
        countryList.add(new ISO3166Country("MD", "MDA", "MOLDOVA, REPUBLIC OF"));
        countryList.add(new ISO3166Country("MC", "MCO", "MONACO"));
        countryList.add(new ISO3166Country("MN", "MNG", "MONGOLIA"));
        countryList.add(new ISO3166Country("ME", "MNE", "MONTENEGRO"));
        countryList.add(new ISO3166Country("MS", "MSR", "MONTSERRAT"));
        countryList.add(new ISO3166Country("MA", "MAR", "MOROCCO"));
        countryList.add(new ISO3166Country("MZ", "MOZ", "MOZAMBIQUE"));
        countryList.add(new ISO3166Country("MM", "MMR", "MYANMAR"));
        countryList.add(new ISO3166Country("NA", "NAM", "NAMIBIA"));
        countryList.add(new ISO3166Country("NR", "NRU", "NAURU"));
        countryList.add(new ISO3166Country("NP", "NPL", "NEPAL"));
        countryList.add(new ISO3166Country("NL", "NLD", "NETHERLANDS"));
        countryList.add(new ISO3166Country("NC", "NCL", "NEW CALEDONIA"));
        countryList.add(new ISO3166Country("NZ", "NZL", "NEW ZEALAND"));
        countryList.add(new ISO3166Country("NI", "NIC", "NICARAGUA"));
        countryList.add(new ISO3166Country("NE", "NER", "NIGER"));
        countryList.add(new ISO3166Country("NG", "NGA", "NIGERIA"));
        countryList.add(new ISO3166Country("NU", "NIU", "NIUE"));
        countryList.add(new ISO3166Country("NF", "NFK", "NORFOLK ISLAND"));
        countryList.add(new ISO3166Country("MP", "MNP", "NORTHERN MARIANA ISLANDS"));
        countryList.add(new ISO3166Country("NO", "NOR", "NORWAY"));
        countryList.add(new ISO3166Country("OM", "OMN", "OMAN"));
        countryList.add(new ISO3166Country("PK", "PAK", "PAKISTAN"));
        countryList.add(new ISO3166Country("PW", "PLW", "PALAU"));
        countryList.add(new ISO3166Country("PS", "PSE", "PALESTINIAN TERRITORY, OCCUPIED"));
        countryList.add(new ISO3166Country("PA", "PAN", "PANAMA"));
        countryList.add(new ISO3166Country("PG", "PNG", "PAPUA NEW GUINEA"));
        countryList.add(new ISO3166Country("PY", "PRY", "PARAGUAY"));
        countryList.add(new ISO3166Country("PE", "PER", "PERU"));
        countryList.add(new ISO3166Country("PH", "PHL", "PHILIPPINES"));
        countryList.add(new ISO3166Country("PN", "PCN", "PITCAIRN"));
        countryList.add(new ISO3166Country("PL", "POL", "POLAND"));
        countryList.add(new ISO3166Country("PT", "PRT", "PORTUGAL"));
        countryList.add(new ISO3166Country("PR", "PRI", "PUERTO RICO"));
        countryList.add(new ISO3166Country("QA", "QAT", "QATAR"));
        countryList.add(new ISO3166Country("RO", "ROU", "ROMANIA"));
        countryList.add(new ISO3166Country("RU", "RUS", "RUSSIAN FEDERATION"));
        countryList.add(new ISO3166Country("RW", "RWA", "RWANDA"));
        countryList.add(new ISO3166Country("RE", "REU", "RÉUNION"));
        countryList.add(new ISO3166Country("BL", "BLM", "SAINT BARTHÉLEMY"));
        countryList.add(new ISO3166Country("SH", "SHN", "SAINT HELENA, ASCENSION AND TRISTAN DA CUNHA"));
        countryList.add(new ISO3166Country("KN", "KNA", "SAINT KITTS AND NEVIS"));
        countryList.add(new ISO3166Country("LC", "LCA", "SAINT LUCIA"));
        countryList.add(new ISO3166Country("MF", "MAF", "SAINT MARTIN (FRENCH PART)"));
        countryList.add(new ISO3166Country("PM", "SPM", "SAINT PIERRE AND MIQUELON"));
        countryList.add(new ISO3166Country("VC", "VCT", "SAINT VINCENT AND THE GRENADINES"));
        countryList.add(new ISO3166Country("WS", "WSM", "SAMOA"));
        countryList.add(new ISO3166Country("SM", "SMR", "SAN MARINO"));
        countryList.add(new ISO3166Country("ST", "STP", "SAO TOME AND PRINCIPE"));
        countryList.add(new ISO3166Country("SA", "SAU", "SAUDI ARABIA"));
        countryList.add(new ISO3166Country("SN", "SEN", "SENEGAL"));
        countryList.add(new ISO3166Country("RS", "SRB", "SERBIA"));
        countryList.add(new ISO3166Country("SC", "SYC", "SEYCHELLES"));
        countryList.add(new ISO3166Country("SL", "SLE", "SIERRA LEONE"));
        countryList.add(new ISO3166Country("SG", "SGP", "SINGAPORE"));
        countryList.add(new ISO3166Country("SX", "SXM", "SINT MAARTEN (DUTCH PART)"));
        countryList.add(new ISO3166Country("SK", "SVK", "SLOVAKIA"));
        countryList.add(new ISO3166Country("SI", "SVN", "SLOVENIA"));
        countryList.add(new ISO3166Country("SB", "SLB", "SOLOMON ISLANDS"));
        countryList.add(new ISO3166Country("SO", "SOM", "SOMALIA"));
        countryList.add(new ISO3166Country("ZA", "ZAF", "SOUTH AFRICA"));
        countryList.add(new ISO3166Country("GS", "SGS", "SOUTH GEORGIA AND THE SOUTH SANDWICH ISLANDS"));
        countryList.add(new ISO3166Country("ES", "ESP", "SPAIN"));
        countryList.add(new ISO3166Country("LK", "LKA", "SRI LANKA"));
        countryList.add(new ISO3166Country("SD", "SDN", "SUDAN"));
        countryList.add(new ISO3166Country("SR", "SUR", "SURINAME"));
        countryList.add(new ISO3166Country("SJ", "SJM", "SVALBARD AND JAN MAYEN"));
        countryList.add(new ISO3166Country("SZ", "SWZ", "SWAZILAND"));
        countryList.add(new ISO3166Country("SE", "SWE", "SWEDEN"));
        countryList.add(new ISO3166Country("CH", "CHE", "SWITZERLAND"));
        countryList.add(new ISO3166Country("SY", "SYR", "SYRIAN ARAB REPUBLIC"));
        countryList.add(new ISO3166Country("TW", "TWN", "TAIWAN, PROVINCE OF CHINA"));
        countryList.add(new ISO3166Country("TJ", "TJK", "TAJIKISTAN"));
        countryList.add(new ISO3166Country("TZ", "TZA", "TANZANIA, UNITED REPUBLIC OF"));
        countryList.add(new ISO3166Country("TH", "THA", "THAILAND"));
        countryList.add(new ISO3166Country("TL", "TLS", "TIMOR-LESTE"));
        countryList.add(new ISO3166Country("TG", "TGO", "TOGO"));
        countryList.add(new ISO3166Country("TK", "TKL", "TOKELAU"));
        countryList.add(new ISO3166Country("TO", "TON", "TONGA"));
        countryList.add(new ISO3166Country("TT", "TTO", "TRINIDAD AND TOBAGO"));
        countryList.add(new ISO3166Country("TN", "TUN", "TUNISIA"));
        countryList.add(new ISO3166Country("TR", "TUR", "TURKEY"));
        countryList.add(new ISO3166Country("TM", "TKM", "TURKMENISTAN"));
        countryList.add(new ISO3166Country("TC", "TCA", "TURKS AND CAICOS ISLANDS"));
        countryList.add(new ISO3166Country("TV", "TUV", "TUVALU"));
        countryList.add(new ISO3166Country("UG", "UGA", "UGANDA"));
        countryList.add(new ISO3166Country("UA", "UKR", "UKRAINE"));
        countryList.add(new ISO3166Country("AE", "ARE", "UNITED ARAB EMIRATES"));
        countryList.add(new ISO3166Country("GB", "GBR", "UNITED KINGDOM"));
        countryList.add(new ISO3166Country("US", "USA", "UNITED STATES"));
        countryList.add(new ISO3166Country("UM", "UMI", "UNITED STATES MINOR OUTLYING ISLANDS"));
        countryList.add(new ISO3166Country("UY", "URY", "URUGUAY"));
        countryList.add(new ISO3166Country("UZ", "UZB", "UZBEKISTAN"));
        countryList.add(new ISO3166Country("VU", "VUT", "VANUATU"));
        countryList.add(new ISO3166Country("VE", "VEN", "VENEZUELA, BOLIVARIAN REPUBLIC OF"));
        countryList.add(new ISO3166Country("VN", "VNM", "VIET NAM"));
        countryList.add(new ISO3166Country("VG", "VGB", "VIRGIN ISLANDS (BRITISH)"));
        countryList.add(new ISO3166Country("VI", "VIR", "VIRGIN ISLANDS (U.S.)"));
        countryList.add(new ISO3166Country("WF", "WLF", "WALLIS AND FUTUNA"));
        countryList.add(new ISO3166Country("EH", "ESH", "WESTERN SAHARA *"));
        countryList.add(new ISO3166Country("YE", "YEM", "YEMEN"));
        countryList.add(new ISO3166Country("ZM", "ZMB", "ZAMBIA"));
        countryList.add(new ISO3166Country("ZW", "ZWE", "ZIMBABWE"));
        countryList.add(new ISO3166Country("AX", "ALA", "ÅLAND ISLANDS"));

        _countries = Collections.unmodifiableList(countryList);

    }

    /*
     * @AfterClass public static void tearDownAfterClass() throws Exception { }
     * 
     * @Before public void setUp() throws Exception { }
     * 
     * @After public void tearDown() throws Exception { }
     */

    @Test
    public void ClassListAddTest() throws IllegalAccessException,  InvocationTargetException,
            NoSuchMethodException
    {

        ArrayList<MarkingValue> markingValues = new ArrayList<MarkingValue>();

        CallClassListAdd(markingValues, "Parent", "Title", "Abbreviation", "Portion");

        assertEquals(1, markingValues.size());

        MarkingValue mv = markingValues.get(0);
        MarkingValueTest.assertMarkingValue("Parent", "Title", "Abbreviation", "Portion", mv);

    }

    @Test
    public void ClassListAddToExistingListTest() throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException
    {

        List<MarkingValue> markingValues = FieldValues.getListOfSciControlSystems();

        CallClassListAdd(markingValues, "Parent", "Title", "Abbreviation", "Portion");

        assertEquals(5, markingValues.size());

        for (int i = 0; i < _compartments.size(); i++)
        {

            MarkingValueTest.assertMarkingValue(_compartments.get(i), markingValues.get(i));
        }

        MarkingValueTest.assertMarkingValue("Parent", "Title", "Abbreviation", "Portion", markingValues.get(4));

    }

    // All IsNato Selected 0 !IsJoint !USA
    @Test
    public void GetListOfClassificationsAllIsNatoSelectedEmptyNotJointNotUSATest()
    {

        Marking mv = new Marking();
        mv.getSelectedCountries().clear();
        mv.setIsNATO(true);

        List<MarkingValue> classifications = FieldValues.getListOfClassifications(mv, true);

        assertEquals(0, mv.getSelectedCountries().size());
        assertClassifications(_allClassifications, classifications);

    }

    // All IsNato Selected 1 !IsJoint USA
    @Test
    public void GetListOfClassificationsAllIsNatoSelectedEmptyNotJointTest()
    {

        Marking mv = new Marking();
        mv.setIsNATO(true);

        List<MarkingValue> classifications = FieldValues.getListOfClassifications(mv, true);

        assertEquals(1, mv.getSelectedCountries().size());
        assertClassifications(_allClassifications, classifications);

    }

    // All IsNato Selected 1 !IsJoint !USA
    @Test
    public void GetListOfClassificationsAllIsNatoSelectedOneNotJointNotUSATest()
    {

        Marking mv = new Marking("//ATA UNCLASSIFIED");
        mv.setIsNATO(true);

        List<MarkingValue> classifications = FieldValues.getListOfClassifications(mv, true);

        assertEquals(1, mv.getSelectedCountries().size());
        assertClassifications(_allClassifications, classifications);

    }

    // All IsNato Selected 2 IsJoint USA
    @Test
    public void GetListOfClassificationsAllIsNatoSelectedTwoJointUSATest()
    {

        Marking mv = new Marking("//JOINT UNCLASSIFIED ALA USA");
        mv.setIsNATO(true);

        List<MarkingValue> classifications = FieldValues.getListOfClassifications(mv, true);

        assertEquals(2, mv.getSelectedCountries().size());
        assertClassifications(_allClassifications, classifications);

    }

    // All IsNato Selected 2 IsJoint !USA
    @Test
    public void GetListOfClassificationsAllIsNatoSelectedTwoJointNotUSATest()
    {

        Marking mv = new Marking("//JOINT UNCLASSIFIED ALA ATA");
        mv.setIsNATO(true);

        List<MarkingValue> classifications = FieldValues.getListOfClassifications(mv, true);

        assertEquals(2, mv.getSelectedCountries().size());
        assertClassifications(_allClassifications, classifications);

    }

    // All !IsNato Selected 0 !IsJoint !USA
    @Test
    public void GetListOfClassificationsAllNotIsNatoSelectedEmptyNotJointNotUSATest()
    {

        Marking mv = new Marking();
        mv.getSelectedCountries().clear();
        mv.setIsNATO(false);

        List<MarkingValue> classifications = FieldValues.getListOfClassifications(mv, true);

        assertEquals(0, mv.getSelectedCountries().size());
        assertClassifications(_allClassifications, classifications);

    }

    // All !IsNato Selected 1 !IsJoint USA
    @Test
    public void GetListOfClassificationsAllNotIsNatoSelectedEmptyNotJointTest()
    {

        Marking mv = new Marking();
        mv.setIsNATO(false);

        List<MarkingValue> classifications = FieldValues.getListOfClassifications(mv, true);

        assertEquals(1, mv.getSelectedCountries().size());
        assertClassifications(_allClassifications, classifications);

    }

    // All !IsNato Selected 1 !IsJoint !USA
    @Test
    public void GetListOfClassificationsAllNotIsNatoSelectedOneNotJointNotUSATest()
    {

        Marking mv = new Marking("//ATA UNCLASSIFIED");
        mv.setIsNATO(false);

        List<MarkingValue> classifications = FieldValues.getListOfClassifications(mv, true);

        assertEquals(1, mv.getSelectedCountries().size());
        assertClassifications(_allClassifications, classifications);

    }

    // All !IsNato Selected 2 IsJoint USA
    @Test
    public void GetListOfClassificationsAllNotIsNatoSelectedTwoJointUSATest()
    {

        Marking mv = new Marking("//JOINT UNCLASSIFIED ALA USA");
        mv.setIsNATO(false);

        List<MarkingValue> classifications = FieldValues.getListOfClassifications(mv, true);

        assertEquals(2, mv.getSelectedCountries().size());
        assertClassifications(_allClassifications, classifications);

    }

    // All !IsNato Selected 2 IsJoint !USA
    @Test
    public void GetListOfClassificationsAllNotIsNatoSelectedTwoJointNotUSATest()
    {

        Marking mv = new Marking("//JOINT UNCLASSIFIED ALA ATA");
        mv.setIsNATO(false);

        List<MarkingValue> classifications = FieldValues.getListOfClassifications(mv, true);

        assertEquals(2, mv.getSelectedCountries().size());
        assertClassifications(_allClassifications, classifications);

    }

    // !All IsNato Selected 0 !IsJoint !USA
    @Test
    public void GetListOfClassificationsNotAllIsNatoSelectedEmptyNotJointNotUSATest()
    {

        Marking mv = new Marking();
        mv.getSelectedCountries().clear();
        mv.setIsNATO(true);

        List<MarkingValue> classifications = FieldValues.getListOfClassifications(mv, false);

        assertEquals(0, mv.getSelectedCountries().size());
        assertClassifications(_natoClassifications, classifications);

    }

    // !All IsNato Selected 1 !IsJoint USA
    @Test
    public void GetListOfClassificationsNotAllIsNatoSelectedOneNotJointTest()
    {

        Marking mv = new Marking();
        mv.setIsNATO(true);

        List<MarkingValue> classifications = FieldValues.getListOfClassifications(mv, false);

        assertEquals(1, mv.getSelectedCountries().size());
        assertClassifications(_natoClassifications, classifications);

    }

    // !All IsNato Selected 1 !IsJoint !USA
    @Test
    public void GetListOfClassificationsNotAllIsNatoSelectedOneNotJointNotUSATest()
    {

        Marking mv = new Marking("//ATA UNCLASSIFIED");
        mv.setIsNATO(true);

        List<MarkingValue> classifications = FieldValues.getListOfClassifications(mv, false);

        assertEquals(1, mv.getSelectedCountries().size());
        assertClassifications(_natoClassifications, classifications);

    }

    // !All IsNato Selected 2 IsJoint USA
    @Test
    public void GetListOfClassificationsNotAllIsNatoSelectedTwoJointUSATest()
    {

        Marking mv = new Marking("//JOINT UNCLASSIFIED ALA USA");
        mv.setIsNATO(true);

        List<MarkingValue> classifications = FieldValues.getListOfClassifications(mv, false);

        assertEquals(2, mv.getSelectedCountries().size());
        assertClassifications(_natoClassifications, classifications);

    }

    // !All IsNato Selected 2 IsJoint !USA
    @Test
    public void GetListOfClassificationsNotAllIsNatoSelectedTwoJointNotUSATest()
    {

        Marking mv = new Marking("//JOINT UNCLASSIFIED ALA ATA");
        mv.setIsNATO(true);

        List<MarkingValue> classifications = FieldValues.getListOfClassifications(mv, false);

        assertEquals(2, mv.getSelectedCountries().size());
        assertClassifications(_natoClassifications, classifications);

    }

    // !All !IsNato Selected 0 !IsJoint !USA
    @Test
    public void GetListOfClassificationsNotAllNotIsNatoSelectedEmptyNotJointNotUSATest()
    {

        Marking mv = new Marking();
        mv.getSelectedCountries().clear();
        mv.setIsNATO(false);

        List<MarkingValue> classifications = FieldValues.getListOfClassifications(mv, false);

        assertEquals(0, mv.getSelectedCountries().size());
        assertTrue(classifications.isEmpty());

    }

    // !All !IsNato Selected 1 !IsJoint USA
    @Test
    public void GetListOfClassificationsNotAllNotIsNatoSelectedOneNotJointTest()
    {

        Marking mv = new Marking();
        mv.setIsNATO(false);

        List<MarkingValue> classifications = FieldValues.getListOfClassifications(mv, false);

        assertEquals(1, mv.getSelectedCountries().size());
        assertClassifications(_notJointUSAClassifications, classifications);

    }

    // !All !IsNato Selected 1 !IsJoint !USA
    @Test
    public void GetListOfClassificationsNotAllNotIsNatoSelectedOneNotJointNotUSATest()
    {

        Marking mv = new Marking("//ATA UNCLASSIFIED");
        mv.setIsNATO(false);

        List<MarkingValue> classifications = FieldValues.getListOfClassifications(mv, false);

        assertEquals(1, mv.getSelectedCountries().size());
        assertClassifications(_notJointNotUSAClassifications, classifications);

    }

    // !All !IsNato Selected 2 IsJoint USA
    @Test
    public void GetListOfClassificationsNotAllNotIsNatoSelectedTwoJointUSATest()
    {

        Marking mv = new Marking("//JOINT UNCLASSIFIED ALA USA");
        mv.setIsNATO(false);

        List<MarkingValue> classifications = FieldValues.getListOfClassifications(mv, false);

        assertEquals(2, mv.getSelectedCountries().size());
        assertClassifications(_jointUSAClassifications, classifications);

    }

    // !All !IsNato Selected 2 IsJoint !USA
    @Test
    public void GetListOfClassificationsNotAllNotIsNatoSelectedTwoJointNotUSATest()
    {

        Marking mv = new Marking("//JOINT UNCLASSIFIED ALA ATA");
        mv.setIsNATO(false);

        List<MarkingValue> classifications = FieldValues.getListOfClassifications(mv, false);

        assertEquals(2, mv.getSelectedCountries().size());
        assertClassifications(_jointNotUSAClassifications, classifications);

    }

    @Test
    public void GetListOfClassificationsNullMarkingAllTest()
    {

        List<MarkingValue> classifications = FieldValues.getListOfClassifications(null, true);

        assertClassifications(_allClassifications, classifications);

    }

    @Test
    public void GetListOfClassificationsNullMarkingNotAllTest()
    {

        List<MarkingValue> classifications = FieldValues.getListOfClassifications(null, false);

        assertTrue(classifications.isEmpty());

    }

    @Test
    public void GetListOfSCIcontrolSystemsTest()
    {

        List<MarkingValue> systems = FieldValues.getListOfSciControlSystems();

        assertClassifications(_compartments, systems);
    }

    @Test
    public void GetListOfCompartmentsTest()
    {

        List<MarkingValue> compartments = FieldValues.getListOfCompartments();

        assertClassifications(_compartments, compartments);
    }

    @Test
    public void GetListOfSubCompartmentsTest()
    {

        List<MarkingValue> subCompartments = FieldValues.getListOfSubCompartments();

        assertClassifications(_compartments, subCompartments);
    }

    @Test
    public void GetListOfCountriesTest()
    {

        List<ISO3166Country> countries = FieldValues.getListOfCountries();

        assertCountries(_countries, countries);
    }

    @Test
    public void GetCountryByNameTest()
    {

        ISO3166Country country = FieldValues.getCountryByName("SAN MARINO");

        ISO3166CountryTest.assertCountry("SM", "SMR", "SAN MARINO", country);
    }

    @Test
    public void GetCountryByNameNullTest()
    {

        ISO3166Country country = FieldValues.getCountryByName(null);

        assertNull(country);

    }

    @Test
    public void GetCountryByNameEmptyTest()
    {

        ISO3166Country country = FieldValues.getCountryByName("");

        assertNull(country);

    }

    @Test
    public void GetCountryByNameUnknownTest()
    {

        ISO3166Country country = FieldValues.getCountryByName("Unknown");

        assertNull(country);

    }

    @Test
    public void GetCountryByAlhpa3Test()
    {

        ISO3166Country country = FieldValues.getCountryByAlpha3("SMR");

        ISO3166CountryTest.assertCountry("SM", "SMR", "SAN MARINO", country);
    }

    @Test
    public void GetCountryByAlpha3NullTest()
    {

        ISO3166Country country = FieldValues.getCountryByAlpha3(null);

        assertNull(country);

    }

    @Test
    public void GetCountryByAlpha3EmptyTest()
    {

        ISO3166Country country = FieldValues.getCountryByAlpha3("");

        assertNull(country);

    }

    @Test
    public void GetCountryByAlpha3UnknownTest()
    {

        ISO3166Country country = FieldValues.getCountryByAlpha3("Unknown");

        assertNull(country);

    }

    @Test
    public void GetMarkingValueByTitleTest()
    {

        MarkingValue mv = FieldValues.getMarkingValueByTitle("COSMIC TOP SECRET", _allClassifications);

        MarkingValueTest.assertMarkingValue("", "COSMIC TOP SECRET", "", "CTS", mv);
    }

    @Test
    public void GetMarkingValueByTitleNullTest()
    {

        MarkingValue mv = FieldValues.getMarkingValueByTitle(null, _allClassifications);

        assertNull(mv);

    }

    @Test
    public void GetMarkingValueByTitleEmptyTest()
    {

        MarkingValue mv = FieldValues.getMarkingValueByTitle("", _allClassifications);

        assertNull(mv);

    }

    @Test
    public void GetMarkingValueByTitleUnknownTest()
    {

        MarkingValue mv = FieldValues.getMarkingValueByTitle("Unknown", _allClassifications);

        assertNull(mv);
    }

    @Test
    public void GetMarkingValueByPortionTest()
    {

        MarkingValue mv = FieldValues.getMarkingValueByPortion("CTS", _allClassifications);

        MarkingValueTest.assertMarkingValue("", "COSMIC TOP SECRET", "", "CTS", mv);
    }

    @Test
    public void GetMarkingValueByPortionNullTest()
    {

        MarkingValue mv = FieldValues.getMarkingValueByPortion(null, _allClassifications);

        assertNull(mv);

    }

    @Test
    public void GetMarkingValueByPortionEmptyTest()
    {

        MarkingValue mv = FieldValues.getMarkingValueByPortion("", _allClassifications);

        assertNull(mv);

    }

    @Test
    public void GetMarkingValueByPortionUnknownTest()
    {

        MarkingValue mv = FieldValues.getMarkingValueByPortion("Unknown", _allClassifications);

        assertNull(mv);
    }

    @Test
    public void GetListOfClassifications()
    {

        List<ISO3166Country> testCountries = FieldValues.getListOfCountries();

        assertEquals(_countries.size(), testCountries.size());

        for (int i = 0; i < _countries.size(); i++)
        {

            ISO3166Country expCountry = _countries.get(i);
            ISO3166Country testCountry = testCountries.get(i);

            assertEquals(expCountry.getAlpha2(), testCountry.getAlpha2());
            assertEquals(expCountry.getAlpha3(), testCountry.getAlpha3());
            assertEquals(expCountry.getName(), testCountry.getName());

        }

    }

    private void CallClassListAdd(List<MarkingValue> classList,
                                  String parent,
                                  String title,
                                  String abbreviation,
                                  String portion) throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException
    {

        Class<?>[] methodArgs = new Class[5];
        methodArgs[0] = new ArrayList<MarkingValue>().getClass();
        methodArgs[1] = String.class;
        methodArgs[2] = String.class;
        methodArgs[3] = String.class;
        methodArgs[4] = String.class;

        Method method = FieldValues.class.getDeclaredMethod("classListAdd", methodArgs);
        method.setAccessible(true);

        method.invoke(null, classList, parent, title, abbreviation, portion);
    }

    public static void assertClassifications(List<MarkingValue> expected, List<MarkingValue> actual)
    {

        assertEquals(expected.size(), actual.size());

        for (MarkingValue expectedValue : expected)
        {

            boolean found = false;
            for (MarkingValue actualValue : actual)
            {

                if (expectedValue.getParent().equals(actualValue.getParent())
                        && expectedValue.getTitle().equals(actualValue.getTitle())
                        && expectedValue.getAbbreviation().equals(actualValue.getAbbreviation())
                        && expectedValue.getPortion().equals(actualValue.getPortion()))
                {
                    found = true;
                    break;
                }
            }

            assertTrue("MarkingValue: " + expectedValue.getParent() + " " + expectedValue.getTitle() + " "
                               + expectedValue.getAbbreviation() + " " + expectedValue.getPortion(),
                       found);

        }
    }

    private void assertCountries(List<ISO3166Country> expected, List<ISO3166Country> actual)
    {

        assertEquals(expected.size(), actual.size());

        for (ISO3166Country expectedValue : expected)
        {

            boolean found = false;
            for (ISO3166Country actualValue : actual)
            {

                if (expectedValue.getAlpha2().equals(actualValue.getAlpha2())
                        && expectedValue.getAlpha3().equals(actualValue.getAlpha3())
                        && expectedValue.getName().equals(actualValue.getName()))
                {
                    found = true;
                    break;
                }
            }

            assertTrue("MarkingValue: " + expectedValue.getAlpha2() + " " + expectedValue.getAlpha3() + " "
                               + expectedValue.getName(),
                       found);

        }
    }
}
