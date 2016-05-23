package com.incadencecorp.coalesce.framework;

import java.util.Hashtable;

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
 * {@link com.incadencecorp.unity.common.SettingsBase} is the base class for all setting contexts
 * which allow an application
 * to store and retrieve setting values from a local or remote source. The
 * {@link com.incadencecorp.unity.common.SettingsBase} class encapsulates a connector and uses a
 * cache to temporary store
 * setting values.
 * 
 * @author InCadence
 */
public class SettingsBase {

    /*--------------------------------------------------------------------------
    	Private Member Variables
    --------------------------------------------------------------------------*/

    private Hashtable<String, String> _cache = null;
    private IConfigurationsConnector _connector = null;

    private Object _cacheLock = new Object();

    /*--------------------------------------------------------------------------
        Constructor
    --------------------------------------------------------------------------*/

    /**
     * Specify the connector to be used. Pass <code>null</code> to use memory.
     * 
     * @param connector
     */
    public SettingsBase(final IConfigurationsConnector connector) {
        _connector = connector;
    }

    /*--------------------------------------------------------------------------
    	Public Functions
    --------------------------------------------------------------------------*/

    /**
     * Remove all entries in the cache.
     */
    public void clearCache() {
        synchronized (_cacheLock) {
            getCache().clear();
        }
    }

    /*--------------------------------------------------------------------------
    	Protected Functions
    --------------------------------------------------------------------------*/

    /**
     * @param key
     * @param parameter
     * @param defaultValue
     *            if less then the minValue then the min is used instead.
     * @param minValue
     *            minimal value for this setting.
     * @param setIfNotFound
     * @return the configuration setting. If the value is less then the minValue specified then the
     *         min is returned instead.
     */
    public int getSettingWithMin(final String key, final String parameter, final int defaultValue,
            final int minValue, final boolean setIfNotFound) {

        int value;

        // Ensure Default Value Meets Requirement
        if (defaultValue < minValue) {
            value = getSetting(key, parameter, minValue, setIfNotFound);
        } else {
            value = getSetting(key, parameter, defaultValue, setIfNotFound);
        }

        // Ensure Setting Meets Requirement
        if (value < minValue) {
            value = minValue;
        }

        return value;

    }

    /**
     * @param key
     * @param parameter
     * @param defaultValue
     *            if less then the minValue then the min is used instead.
     * @param minValue
     *            minimal value for this setting.
     * @param setIfNotFound
     * @return the configuration setting. If the value is less then the minValue specified then the
     *         min is returned instead.
     */
    public double getSettingWithMin(final String key, final String parameter,
            final double defaultValue, final double minValue, final boolean setIfNotFound) {

        double value;

        // Ensure Default Value Meets Requirement
        if (defaultValue < minValue) {
            value = getSetting(key, parameter, minValue, setIfNotFound);
        } else {
            value = getSetting(key, parameter, defaultValue, setIfNotFound);
        }

        // Ensure Setting Meets Requirement
        if (value < minValue) {
            value = minValue;
        }

        return value;

    }

    /**
     * @param key
     * @param parameter
     * @param defaultValue
     *            if less then the minValue then the min is used instead.
     * @param minValue
     *            minimal value for this setting.
     * @param setIfNotFound
     * @return the configuration setting. If the value is less then the minValue specified then the
     *         min is returned instead.
     */
    public float getSettingWithMin(final String key, final String parameter,
            final float defaultValue, final float minValue, final boolean setIfNotFound) {

        float value;

        // Ensure Default Value Meets Requirement
        if (defaultValue < minValue) {
            value = getSetting(key, parameter, minValue, setIfNotFound);
        } else {
            value = getSetting(key, parameter, defaultValue, setIfNotFound);
        }

        // Ensure Setting Meets Requirement
        if (value < minValue) {
            value = minValue;
        }

        return value;

    }

    /**
     * @param key
     * @param parameter
     * @param defaultValue
     *            if greater then maxValue then the max is used instead.
     * @param maxValue
     *            Maximum value for this setting.
     * @param setIfNotFound
     * @return the configuration setting. If the value is greater then the maxValue specified then
     *         the max is returned instead.
     */
    public int getSettingWithMax(final String key, final String parameter, final int defaultValue,
            final int maxValue, final boolean setIfNotFound) {
        int value;

        // Ensure Default Value Meets Requirement
        if (defaultValue > maxValue) {
            value = getSetting(key, parameter, maxValue, setIfNotFound);
        } else {
            value = getSetting(key, parameter, defaultValue, setIfNotFound);
        }

        // Ensure Setting Meets Requirement
        if (value > maxValue) {
            value = maxValue;
        }

        return value;

    }

    /**
     * @param key
     * @param parameter
     * @param defaultValue
     *            if greater then maxValue then the max is used instead.
     * @param maxValue
     *            Maximum value for this setting.
     * @param setIfNotFound
     * @return the configuration setting. If the value is greater then the maxValue specified then
     *         the max is returned instead.
     */
    public double getSettingWithMax(final String key, final String parameter,
            final double defaultValue, final double maxValue, final boolean setIfNotFound) {
        double value;

        // Ensure Default Value Meets Requirement
        if (defaultValue > maxValue) {
            value = getSetting(key, parameter, maxValue, setIfNotFound);
        } else {
            value = getSetting(key, parameter, defaultValue, setIfNotFound);
        }

        // Ensure Setting Meets Requirement
        if (value > maxValue) {
            value = maxValue;
        }

        return value;

    }

    /**
     * @param key
     * @param parameter
     * @param defaultValue
     *            if greater then maxValue then the max is used instead.
     * @param maxValue
     *            Maximum value for this setting.
     * @param setIfNotFound
     * @return the configuration setting. If the value is greater then the maxValue specified then
     *         the max is returned instead.
     */
    public float getSettingWithMax(final String key, final String parameter,
            final float defaultValue, final float maxValue, final boolean setIfNotFound) {
        float value;

        // Ensure Default Value Meets Requirement
        if (defaultValue > maxValue) {
            value = getSetting(key, parameter, maxValue, setIfNotFound);
        } else {
            value = getSetting(key, parameter, defaultValue, setIfNotFound);
        }

        // Ensure Setting Meets Requirement
        if (value > maxValue) {
            value = maxValue;
        }

        return value;

    }

    /**
     * Gets parameter value. If value < min then the min is returned; if value > max then max is
     * returned.
     * 
     * @param key
     * @param parameter
     * @param defaultValue
     *            if greater then the maxValue then max is used; if less then minValue then min is
     *            used.
     * @param minValue
     * @param maxValue
     * @param setIfNotFound
     * @return the configuration setting. If the value is less then the minValue then the
     *         min is returned; if value is greater then maxValue then max is returned.
     */
    public int getSettingWithMinMax(final String key, final String parameter,
            final int defaultValue, final int minValue, final int maxValue,
            final boolean setIfNotFound) {
        int value;

        // Ensure Default Value Meets Requirement
        if (defaultValue < minValue || defaultValue > maxValue) {
            value = getSetting(key, parameter, maxValue, setIfNotFound);
        } else {
            value = getSetting(key, parameter, defaultValue, setIfNotFound);
        }

        // Ensure Setting Meets Requirement
        if (value > maxValue) {
            value = maxValue;
        } else if (value < minValue) {
            value = minValue;
        }

        return value;

    }

    /**
     * Gets parameter value. If value < min then the min is returned; if value > max then max is
     * returned.
     * 
     * @param key
     * @param parameter
     * @param defaultValue
     *            if greater then the maxValue then max is used; if less then minValue then min is
     *            used.
     * @param minValue
     * @param maxValue
     * @param setIfNotFound
     * @return the configuration setting. If the value is less then the minValue then the
     *         min is returned; if value is greater then maxValue then max is returned.
     */
    public double getSettingWithMinMax(final String key, final String parameter,
            final double defaultValue, final double minValue, final double maxValue,
            final boolean setIfNotFound) {
        double value;

        // Ensure Default Value Meets Requirement
        if (defaultValue < minValue || defaultValue > maxValue) {
            value = getSetting(key, parameter, maxValue, setIfNotFound);
        } else {
            value = getSetting(key, parameter, defaultValue, setIfNotFound);
        }

        // Ensure Setting Meets Requirement
        if (value > maxValue) {
            value = maxValue;
        } else if (value < minValue) {
            value = minValue;
        }

        return value;

    }

    /**
     * Gets parameter value. If value < min then the min is returned; if value > max then max is
     * returned.
     * 
     * @param key
     * @param parameter
     * @param defaultValue
     *            if greater then the maxValue then max is used; if less then minValue then min is
     *            used.
     * @param minValue
     * @param maxValue
     * @param setIfNotFound
     * @return the configuration setting. If the value is less then the minValue then the
     *         min is returned; if value is greater then maxValue then max is returned.
     */
    public float getSettingWithMinMax(final String key, final String parameter,
            final float defaultValue, final float minValue, final float maxValue,
            final boolean setIfNotFound) {
        float value;

        // Ensure Default Value Meets Requirement
        if (defaultValue < minValue || defaultValue > maxValue) {
            value = getSetting(key, parameter, maxValue, setIfNotFound);
        } else {
            value = getSetting(key, parameter, defaultValue, setIfNotFound);
        }

        // Ensure Setting Meets Requirement
        if (value > maxValue) {
            value = maxValue;
        } else if (value < minValue) {
            value = minValue;
        }

        return value;

    }

    /**
     * @param key
     * @param parameter
     * @param defaultValue
     * @param setIfNotFound
     * @return the configuration setting specified by key / parameter pair.
     */
    public int getSetting(final String key, final String parameter, final int defaultValue,
            final boolean setIfNotFound) {

        return Integer.parseInt(getSetting(key, parameter, Integer.toString(defaultValue),
                                           SettingType.ST_INTEGER, setIfNotFound));

    }

    /**
     * @param key
     * @param parameter
     * @param defaultValue
     * @param setIfNotFound
     * @return the configuration setting specified by key / parameter pair.
     */
    public long getSetting(final String key, final String parameter, final long defaultValue,
            final boolean setIfNotFound) {

        return Long.parseLong(getSetting(key, parameter, Long.toString(defaultValue),
                                           SettingType.ST_LONG, setIfNotFound));

    }
    
    /**
     * @param key
     * @param parameter
     * @param defaultValue
     * @param setIfNotFound
     * @return the configuration setting specified by key / parameter pair.
     */
    public double getSetting(final String key, final String parameter, final double defaultValue,
            final boolean setIfNotFound) {

        return Double.parseDouble(getSetting(key, parameter, Double.toString(defaultValue),
                                             SettingType.ST_DOUBLE, setIfNotFound));

    }

    /**
     * @param key
     * @param parameter
     * @param defaultValue
     * @param setIfNotFound
     * @return the configuration setting specified by key / parameter pair.
     */
    public float getSetting(final String key, final String parameter, final float defaultValue,
            final boolean setIfNotFound) {

        return Float.parseFloat(getSetting(key, parameter, Float.toString(defaultValue),
                                           SettingType.ST_FLOAT, setIfNotFound));

    }

    /**
     * @param key
     * @param parameter
     * @param defaultValue
     * @param setIfNotFound
     * @return the configuration setting specified by key / parameter pair.
     */
    public boolean getSetting(final String key, final String parameter, final boolean defaultValue,
            final boolean setIfNotFound) {

        return Boolean.parseBoolean(getSetting(key, parameter, Boolean.toString(defaultValue),
                                               SettingType.ST_BOOLEAN, setIfNotFound));

    }

    /**
     * @param key
     * @param parameter
     * @param defaultValue
     * @param setIfNotFound
     * @return the configuration setting specified by key / parameter pair.
     */
    public String getSetting(final String key, final String parameter, final String defaultValue,
            final boolean setIfNotFound) {

        return getSetting(key, parameter, defaultValue, SettingType.ST_STRING, setIfNotFound);

    }

    /**
     * @param key
     * @param parameter
     * @param defaultValue
     * @param type
     * @param setIfNotFound
     * @return the configuration setting specified by key / parameter pair.
     */
    public String getSetting(final String key, final String parameter, final String defaultValue,
            final SettingType type, final boolean setIfNotFound) {
        String value = null;

        synchronized (_cacheLock) {
            // Normalize Key
            String cacheKey = normalizeCacheKey(key, parameter);

            // Read Value From Cache
            value = getCache().get(cacheKey);

            // Value Cached?
            if (value == null) {
                // No; User Defined Connector?
                if (_connector != null) {
                    // Yes; Read Configuration
                    value =
                            _connector
                                    .getSetting(key, parameter, defaultValue, type, setIfNotFound);

                    // Add to Cache
                    getCache().put(cacheKey, value);
                } else {
                    // No; Use Default
                    value = defaultValue;
                }

            }
        }

        return value;
    }

    /**
     * Sets the configuration setting.
     * 
     * @param key
     * @param parameter
     * @param value
     * @return <code>true</code> if successful.
     */
    public boolean setSetting(final String key, final String parameter, final int value) {

        return setSetting(key, parameter, Integer.toString(value), SettingType.ST_INTEGER);

    }
    
    /**
     * Sets the configuration setting.
     * 
     * @param key
     * @param parameter
     * @param value
     * @return <code>true</code> if successful.
     */
    public boolean setSetting(final String key, final String parameter, final long value) {

        return setSetting(key, parameter, Long.toString(value), SettingType.ST_LONG);

    }

    /**
     * Sets the configuration setting.
     * 
     * @param key
     * @param parameter
     * @param value
     * @return <code>true</code> if successful.
     */
    public boolean setSetting(final String key, final String parameter, final double value) {

        return setSetting(key, parameter, Double.toString(value), SettingType.ST_DOUBLE);

    }

    /**
     * Sets the configuration setting.
     * 
     * @param key
     * @param parameter
     * @param value
     * @return <code>true</code> if successful.
     */
    public boolean setSetting(final String key, final String parameter, final float value) {

        return setSetting(key, parameter, Float.toString(value), SettingType.ST_FLOAT);

    }

    /**
     * Sets the configuration setting.
     * 
     * @param key
     * @param parameter
     * @param value
     * @return <code>true</code> if successful.
     */
    public boolean setSetting(final String key, final String parameter, final boolean value) {

        return setSetting(key, parameter, Boolean.toString(value), SettingType.ST_BOOLEAN);

    }

    /**
     * Sets the configuration setting.
     * 
     * @param key
     * @param parameter
     * @param value
     * @return <code>true</code> if successful.
     */
    public boolean setSetting(final String key, final String parameter, final String value) {

        return setSetting(key, parameter, value, SettingType.ST_STRING);

    }

    /**
     * Sets the configuration setting.
     * 
     * @param key
     * @param parameter
     * @param value
     * @param type
     * @return <code>true</code> if successful.
     */
    public boolean setSetting(final String key, final String parameter, final String value,
            final SettingType type) {

        boolean updated = true;

        synchronized (_cacheLock) {
            // Normalize Key
            String cacheKey = normalizeCacheKey(key, parameter);

            // Setting Not Cached or Modified?
            if (!getCache().containsKey(cacheKey) || getCache().get(cacheKey) != value) {

                // Yes; Replace Cached Value
                getCache().put(cacheKey, value);

                // Update Configuration File
                if (_connector != null) {
                    updated = _connector.setSetting(key, parameter, value, type);
                }

            }
        }

        return updated;

    }

    private String normalizeCacheKey(final String key, final String parameter) {

        String cacheKey = key + "." + parameter;
        cacheKey = cacheKey.replace("/", ".").toUpperCase();
        return cacheKey;

    }

    private Hashtable<String, String> getCache() {

        // Cache Initialized?
        if (_cache == null) {

            // No; Initialize
            _cache = new Hashtable<String, String>();

        }

        return _cache;

    }

}
