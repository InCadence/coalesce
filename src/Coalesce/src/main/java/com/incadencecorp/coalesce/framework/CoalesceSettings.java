package com.incadencecorp.coalesce.framework;

import java.util.Map;

import org.apache.commons.io.FilenameUtils;

import com.incadencecorp.coalesce.common.helpers.StringHelper;
//import com.incadencecorp.unity.common.IConfigurationsConnector;
//import com.incadencecorp.unity.common.SettingsBase;

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
 * Contains all the settings used by Coalesce. Settings are not persisted
 * between application restarts unless you specify a connector by calling
 * {@link CoalesceSettings#initialize(com.incadencecorp.unity.common.IConfigurationsConnector)}
 * .
 */
public class CoalesceSettings {

    /*--------------------------------------------------------------------------
    	Private Member Variables
    --------------------------------------------------------------------------*/

    private static String _defaultApplicationName;
    private static String _defaultApplicationRoot;

    protected static SettingsBase settings = new SettingsBase(null);

    /*--------------------------------------------------------------------------
    Parameter Groups
    --------------------------------------------------------------------------*/

    private static final String COALESCE = "Coalesce.";
    private static final String FILE_STORE = COALESCE + "FileStore.";
    private static final String SECURITY = COALESCE + "Security.";
    private static final String COORDINATES = COALESCE + "Coordinate.";

    /*--------------------------------------------------------------------------
    File Store Parameters
    --------------------------------------------------------------------------*/

    private static final String PARAM_FILE_STORE_USE_FILE_STORE = FILE_STORE + "UseFileStore";
    private static final String PARAM_FILE_STORE_USE_INDEXING = FILE_STORE + "UseIndexing";
    private static final String PARAM_FILE_STORE_SUB_DIRECTORY_LENGTH = FILE_STORE + "SubDirectoryLength";
    private static final String PARAM_FILE_STORE_BASE_PATH = FILE_STORE + "BasePath";
    private static final String PARAM_FILE_STORE_IMAGE_FORMAT = FILE_STORE + "ImageFormat";

    /*--------------------------------------------------------------------------
    Security Parameters
    --------------------------------------------------------------------------*/

    private static final String PARAM_SECURITY_USE_ENCRYPTION = SECURITY + "UseEncryption";
    private static final String PARAM_SECURITY_PASS_PHRASE = SECURITY + "PassPhrase";
    private static final String PARAM_SECURITY_AUDIT_SELECT_STATEMENTS = SECURITY + "AuditSelectStatements";

    /*--------------------------------------------------------------------------
    Coordinate Parameters
    --------------------------------------------------------------------------*/

    private static final String PARAM_COORD_DEFAULT_Z_AXIS = COORDINATES + "DefaultZ";
    private static final String PARAM_COORD_IS_RESTRICTED = COORDINATES + "Restricted.";
    private static final String PARAM_COORD_MAX = COORDINATES + "Max.";
    private static final String PARAM_COORD_MIN = COORDINATES + "Min.";

    /*--------------------------------------------------------------------------
    Default Values
    --------------------------------------------------------------------------*/

    private static final boolean DEFAULT_USE_FILE_STORE = true;
    private static final boolean DEFAULT_USE_INDEXING = true;
    private static final boolean DEFAULT_USE_ENCRYPTION = false;
    private static final String DEFAULT_PASS_PHRASE = "9UFAF8FI98BDLQEZ";
    private static final String DEFAULT_IMAGE_FORMAT = "jpg";
    private static final double DEFAULT_Z_AXIS = 0;
    private static final int DEFAULT_DIRECTORY_LENGTH = 2;

    /*--------------------------------------------------------------------------
    Initialization
    --------------------------------------------------------------------------*/

    protected CoalesceSettings()
    {
        // Do Nothing
    }

    /**
     * Configures the settings to use a particular connector.
     * 
     * @param connector
     */
    public static void initialize(final IConfigurationsConnector connector)
    {
        settings = new SettingsBase(connector);
    }

    /*--------------------------------------------------------------------------
    	Public Configuration Functions
    --------------------------------------------------------------------------*/

    public static String getConfigurationFileName()
    {

        if (_defaultApplicationName == null)
        {
            return "Coalesce.config";
        }
        else
        {
            return _defaultApplicationName + ".Coalesce.config";
        }
    }

    public static boolean getUseBinaryFileStore()
    {
        return settings.getSetting(getConfigurationFileName(), PARAM_FILE_STORE_USE_FILE_STORE, DEFAULT_USE_FILE_STORE, true);
    }

    public static boolean setUseBinaryFileStore(boolean value)
    {
        return settings.setSetting(getConfigurationFileName(), PARAM_FILE_STORE_USE_FILE_STORE, value);
    }

    public static boolean getUseIndexing()
    {
        return settings.getSetting(getConfigurationFileName(), PARAM_FILE_STORE_USE_INDEXING, DEFAULT_USE_INDEXING, true);
    }

    public static boolean setUseIndexing(boolean value)
    {
        return settings.setSetting(getConfigurationFileName(), PARAM_FILE_STORE_USE_INDEXING, value);
    }

    public static int getSubDirectoryLength()
    {
        return settings.getSettingWithMinMax(getConfigurationFileName(),
                                             PARAM_FILE_STORE_SUB_DIRECTORY_LENGTH,
                                             DEFAULT_DIRECTORY_LENGTH,
                                             0,
                                             5,
                                             true);
    }

    public static String getBinaryFileStoreBasePath()
    {
        return settings.getSetting(getConfigurationFileName(),
                                   PARAM_FILE_STORE_BASE_PATH,
                                   FilenameUtils.concat(getDefaultApplicationRoot(), "..\\images\\uploads\\"),
                                   true);
    }

    public static boolean setBinaryFileStoreBasePath(String value)
    {
        return settings.setSetting(getConfigurationFileName(), PARAM_FILE_STORE_BASE_PATH, value);
    }

    public static void setDefaultApplicationName(String value)
    {
        _defaultApplicationName = value;
    }

    public static String getDefaultApplicationRoot()
    {

        if (StringHelper.isNullOrEmpty(_defaultApplicationRoot))
        {

            Map<Thread, StackTraceElement[]> stackMap = Thread.getAllStackTraces();

            for (Map.Entry<Thread, StackTraceElement[]> threadStack : stackMap.entrySet())
            {

                if (threadStack.getKey().getId() == 1)
                {
                    StackTraceElement[] stack = threadStack.getValue();
                    StackTraceElement main = stack[stack.length - 1];
                    String mainClassName = main.getClassName();

                    try
                    {
                        Class<?> cls = Class.forName(mainClassName);

                        String classPath = cls.getProtectionDomain().getCodeSource().getLocation().getPath();

                        _defaultApplicationRoot = classPath;

                    }
                    catch (ClassNotFoundException cnfe)
                    {
                        _defaultApplicationRoot = null;
                    }

                    break;
                }
            }

        }

        return _defaultApplicationRoot;

    }

    public static void setDefaultApplicationRoot(String value)
    {
        _defaultApplicationRoot = value;
    }

    public static boolean getUseEncryption()
    {
        return settings.getSetting(getConfigurationFileName(), PARAM_SECURITY_USE_ENCRYPTION, DEFAULT_USE_ENCRYPTION, true);
    }

    public static boolean setUseEncryption(boolean value)
    {
        return settings.setSetting(getConfigurationFileName(), PARAM_SECURITY_USE_ENCRYPTION, value);
    }

    public static String getPassPhrase()
    {
        return settings.getSetting(getConfigurationFileName(), PARAM_SECURITY_PASS_PHRASE, DEFAULT_PASS_PHRASE, true);
    }

    public static boolean setPassPhrase(String value)
    {
        return settings.setSetting(getConfigurationFileName(), PARAM_SECURITY_PASS_PHRASE, value);
    }

    public static boolean getAuditSelectStatements()
    {
        return settings.getSetting(getConfigurationFileName(), PARAM_SECURITY_AUDIT_SELECT_STATEMENTS, true, true);
    }

    public static boolean setAuditSelectStatements(boolean value)
    {
        return settings.setSetting(getConfigurationFileName(), PARAM_SECURITY_AUDIT_SELECT_STATEMENTS, value);
    }

    public static String getImageFormat()
    {
        return settings.getSetting(getConfigurationFileName(), PARAM_FILE_STORE_IMAGE_FORMAT, DEFAULT_IMAGE_FORMAT, true);
    }

    public static boolean setImageFormat(String value)
    {
        return settings.setSetting(getConfigurationFileName(), PARAM_FILE_STORE_IMAGE_FORMAT, value);
    }

    /*--------------------------------------------------------------------------
    Coordinate Configuration Functions
    --------------------------------------------------------------------------*/

    /**
     * Sets the default value of the z-axis if one was not specified.
     * 
     * @param value
     */
    public static void setDefaultZValue(double value)
    {
        settings.setSetting(getConfigurationFileName(), PARAM_COORD_DEFAULT_Z_AXIS, value);
    }

    /**
     * @return the default value of the z-axis if one was not specified.
     */
    public static double getDefaultZValue()
    {
        return settings.getSetting(getConfigurationFileName(), PARAM_COORD_DEFAULT_Z_AXIS, DEFAULT_Z_AXIS, true);
    }
    
    /*--------------------------------------------------------------------------
    Supported Axis
    --------------------------------------------------------------------------*/

    /**
     * Settings for coordinate axis.
     * 
     * @author n78554
     *
     */
    public enum EAxis
    {
        /**
         * The default is restricted to the range (-180 to 180).
         */
        X(true, -180, 180),
        /**
         * The default is restricted to the range (-90 to 90).
         */
        Y(true, -90, 90),
        /**
         * The default is not restricted.
         */
        Z(false, 0, 0);

        private boolean _restricted;
        private double _min;
        private double _max;

        private EAxis(boolean isRestricted, double min, double max)
        {
            _restricted = isRestricted;
            _min = min;
            _max = max;
        }

        /**
         * @return whether the axis should be restricted.
         */
        public boolean isRestricted()
        {
            return settings.getSetting(getConfigurationFileName(), getRestrictedParam(), _restricted, true);
        }

        /**
         * Sets whether the axis should be restricted.
         * 
         * @param value
         */
        public void setRestricted(boolean value)
        {
            settings.setSetting(getConfigurationFileName(), getRestrictedParam(), value);
        }

        /**
         * @return the max value allowed for this axis.
         */
        public double getMax()
        {
            return settings.getSetting(getConfigurationFileName(), getMaxParam(), _max, true);
        }

        /**
         * Sets the max value allowed for this axis.
         * 
         * @param value
         */
        public void setMax(double value)
        {
            settings.setSetting(getConfigurationFileName(), getMaxParam(), value);
        }

        /**
         * @return the min value allowed for this axis.
         */
        public double getMin()
        {
            return settings.getSetting(getConfigurationFileName(), getMinParam(), _min, true);
        }

        /**
         * Sets the min value allowed for this axis.
         * 
         * @param value
         */
        public void setMin(double value)
        {
            settings.setSetting(getConfigurationFileName(), getMinParam(), value);
        }

        /**
         * @param value
         * @return whether the value is within the restricted range.
         */
        public boolean isValid(double value)
        {
            return !isRestricted() || (value <= getMax() && value >= getMin());
        }

        private String getRestrictedParam()
        {
            return PARAM_COORD_IS_RESTRICTED + toString();
        }

        private String getMaxParam()
        {
            return PARAM_COORD_MAX + toString();
        }

        private String getMinParam()
        {
            return PARAM_COORD_MIN + toString();
        }

    }

}
