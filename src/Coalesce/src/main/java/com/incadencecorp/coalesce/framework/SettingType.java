package com.incadencecorp.coalesce.framework;

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
* @author Jing Yang
* May 13, 2016
*/
/**
 * This class is copied to the package here from Unity project because of the dependencies  
 * 
 * Enumeration that stores the configuration object type.
 * 
 * @author InCadence
 */
public enum SettingType {

    /**
     * Type String.
     */
    ST_STRING(0),
    /**
     * Type Boolean.
     */
    ST_BOOLEAN(1),
    /**
     * Type Integer.
     */
    ST_INTEGER(2),
    /**
     * Type Long.
     */
    ST_LONG(6),    
    /**
     * Type EncryptedString.
     */
    ST_ENCRYPTED_STRING(3),
    /**
     * Type Double.
     */
    ST_DOUBLE(4),
    /**
     * Type Float.
     */
    ST_FLOAT(5),
    /**
     * Type Unknown.
     */
    ST_UNKNOWN(99);

    private int _value;

    private SettingType(final int value) {
        this._value = value;
    }

    /**
     * Sets the integer value of SettingType.
     * 
     * @param value
     *            integer value of SettingType.
     */
    public void setSettingType(final int value) {
        this._value = value;
    }

    /**
     * Returns the integer value of SettingType.
     * 
     * @return the integer value of SettingType.
     */
    public Integer getSettingType() {
        return this._value;
    }
}
