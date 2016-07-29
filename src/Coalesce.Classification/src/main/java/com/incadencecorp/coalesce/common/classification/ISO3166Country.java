package com.incadencecorp.coalesce.common.classification;

import java.io.Serializable;

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
 * Represents a countries code.
 * 
 * @author wbrannock, Derek
 */
public class ISO3166Country implements Serializable, Comparable<ISO3166Country> {

    // This module is coded based on guidance found in DoDM 5200.01-V2, February
    // 24, 2012

    private static final long serialVersionUID = -1463057591228825948L;

    private String _alpha2;
    private String _alpha3;
    private String _name;

    /**
     * Class constructor. Creates an ISO3166Country class.
     */
    public ISO3166Country()
    {
    }

    /**
     * Class constructor. Creates an ISO3166Country class assigning the
     * parameters to its properties.
     * 
     * @param alpha2 The two character alpha string representation of the
     *            country allowed object is {@link String }
     * @param alpha3 The three character alpha string representation of the
     *            country allowed object is {@link String }
     * @param name The full name string of the country allowed object is
     *            {@link String }
     */
    public ISO3166Country(String alpha2, String alpha3, String name)
    {

        if (alpha2 == null)
            throw new IllegalArgumentException("alpha2");
        if (alpha3 == null)
            throw new IllegalArgumentException("alpha3");
        if (name == null)
            throw new IllegalArgumentException("name");

        _alpha2 = alpha2;
        _alpha3 = alpha3;
        _name = name;
    }

    /**
     * Returns an ISO3166Country shell that is empty except for the three
     * character alpha string passed in as a parameter.
     * 
     * @param alpha3 The three character alpha string representation of the
     *            country allowed object is {@link String }
     * @return possible object is {@link ISO3166Country }
     */
    public static ISO3166Country withAlpha3EqualTo(String alpha3)
    {
        ISO3166Country country = new ISO3166Country();
        country.setAlpha3(alpha3);
        return country;
    }

    /**
     * Returns an ISO3166Country representative of the United States of America.
     * 
     * @return possible object is {@link ISO3166Country }
     */
    public static ISO3166Country getUSA()
    {
        return new ISO3166Country("US", "USA", "UNITED STATES");
    }

    @Override
    public boolean equals(Object other)
    {
        if (!(other instanceof ISO3166Country))
            return false;

        ISO3166Country otherCountry = (ISO3166Country) other;

        return getAlpha3().equals(otherCountry.getAlpha3());

    }

    @Override
    public int compareTo(ISO3166Country other)
    {
        return getName().compareTo(other.getName());
    }

    /**
     * Returns the two character string property representing this
     * ISO3166Country.
     * 
     * @return possible object is {@link String }
     */
    public String getAlpha2()
    {
        return _alpha2;
    }

    /**
     * Sets the two character string property representing this ISO3166Country.
     * 
     * @param alpha2 allowed object is {@link String }
     */
    public void setAlpha2(String alpha2)
    {
        if (alpha2 == null)
            throw new IllegalArgumentException("alpha2");
        _alpha2 = alpha2;
    }

    /**
     * Returns the three character string property representing this
     * ISO3166Country.
     * 
     * @return possible object is {@link String }
     */
    public String getAlpha3()
    {
        return _alpha3;
    }

    /**
     * Sets the three character string property representing this
     * ISO3166Country.
     * 
     * @param alhpa3 allowed object is {@link String }
     */
    public void setAlpha3(String alhpa3)
    {
        if (alhpa3 == null)
            throw new IllegalArgumentException("alhpa3");
        _alpha3 = alhpa3;
    }

    /**
     * Returns the name string property for this ISO3166Country.
     * 
     * @return possible object is {@link String }
     */
    public String getName()
    {
        return _name;
    }

    /**
     * Sets the name string property for this ISO3166Country.
     * 
     * @param name allowed object is {@link String }
     */
    public void setName(String name)
    {
        if (name == null)
            throw new IllegalArgumentException("name");
        _name = name;
    }

}
