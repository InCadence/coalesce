package com.incadencecorp.coalesce.framework;

import com.incadencecorp.coalesce.api.CoalesceParameters;
import com.incadencecorp.coalesce.common.helpers.StringHelper;
import com.incadencecorp.unity.common.IConfigurationsConnector;
import com.incadencecorp.unity.common.SettingsBase;
import com.incadencecorp.unity.common.connectors.FilePropertyConnector;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
 * {@link CoalesceSettings#setConnector(com.incadencecorp.unity.common.IConfigurationsConnector)}
 * .
 */
public class CoalesceSettings {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoalesceSettings.class);

    /*--------------------------------------------------------------------------
        Private Member Variables
    --------------------------------------------------------------------------*/

    private static String _defaultApplicationName;
    private static String _defaultApplicationRoot;

    private static SettingsBase settings = new SettingsBase(new FilePropertyConnector(CoalesceParameters.COALESCE_CONFIG_LOCATION));

    /*--------------------------------------------------------------------------
    Parameter Groups
    --------------------------------------------------------------------------*/

    private static final String COALESCE = "Coalesce.";
    private static final String HELPER = COALESCE + "Helper.";
    private static final String FILE_STORE = COALESCE + "FileStore.";
    private static final String SECURITY = COALESCE + "Security.";
    private static final String COORDINATES = COALESCE + "Coordinate.";

    /*--------------------------------------------------------------------------
    Helper Parameters
    --------------------------------------------------------------------------*/

    private static final String PARAM_TIME_PATTERNS = HELPER + "Time.Formats";

    /*--------------------------------------------------------------------------
    File Store Parameters
    --------------------------------------------------------------------------*/

    private static final String PARAM_FILE_STORE_USE_FILE_STORE = FILE_STORE + "UseFileStore";
    private static final String PARAM_FILE_STORE_USE_INDEXING = FILE_STORE + "UseIndexing";
    private static final String PARAM_FILE_STORE_SUB_DIRECTORY_LENGTH = FILE_STORE + "SubDirectoryLength";
    private static final String PARAM_FILE_STORE_BASE_PATH = FILE_STORE + "BasePath";
    private static final String PARAM_FILE_STORE_IMAGE_FORMAT = FILE_STORE + "ImageFormat";

    /*--------------------------------------------------------------------------
    Threading Parameters
    --------------------------------------------------------------------------*/

    private static final String PARAM_COALESCE_THREADING = COALESCE + "threads.";
    private static final String PARAM_CORE_COUNT = PARAM_COALESCE_THREADING + "numberOfCores";
    private static final String PARAM_MIN_THREADS = PARAM_COALESCE_THREADING + "minThreads";
    private static final String PARAM_MAX_THREADS = PARAM_COALESCE_THREADING + "maxThreads";
    /**
     * {@value #PARAM_THREAD_TIMOUT} specifies in seconds when terminating executor pools how long to wait on thread
     * completions before sending an interrupt. Default {@value #DEFAULT_THREAD_TIMOUT}.
     */
    public static final String PARAM_THREAD_TIMOUT = PARAM_COALESCE_THREADING + "timeout";

    /**
     * {@value #PARAM_KEEP_ALIVE_TIME} specifies in seconds how long a thread should remain around to be reused within a
     * executor pool before being cleaned up. Default {@value #DEFAULT_KEEP_ALIVE_TIME}.
     */
    public static final String PARAM_KEEP_ALIVE_TIME = PARAM_COALESCE_THREADING + "keepAliveTime";

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
    private static final String PARAM_COORD_NUM_DECIMALS = COORDINATES + "numDecimals";

    /*--------------------------------------------------------------------------
    Default Values
    --------------------------------------------------------------------------*/

    private static final boolean DEFAULT_USE_FILE_STORE = true;
    private static final boolean DEFAULT_USE_INDEXING = true;
    private static final boolean DEFAULT_USE_ENCRYPTION = false;
    private static final String DEFAULT_PASS_PHRASE = "9UFAF8FI98BDLQEZ";
    private static final String DEFAULT_IMAGE_FORMAT = "jpg";
    private static final double DEFAULT_Z_AXIS = 0;
    private static final int DEFAULT_NUM_DECIMALS = 6;
    private static final int DEFAULT_DIRECTORY_LENGTH = 2;

    private static final int DEFAULT_CORE_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int DEFAULT_MIN_THREADS = 10;
    private static final int DEFAULT_MAX_THREADS = 20;

    private static final int DEFAULT_KEEP_ALIVE_TIME = 60;
    private static final int DEFAULT_THREAD_TIMOUT = 60;

    

    /*--------------------------------------------------------------------------
    Public Constants
    --------------------------------------------------------------------------*/

    /**
     * String used to determine whether a data type is a list type.
     */
    public static final String VAR_IS_LIST_TYPE = "_LIST_TYPE";

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
    public static void setConnector(final IConfigurationsConnector connector)
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
            return "coalesce.properties";
        }
        else
        {
            return _defaultApplicationName + ".coalesce.properties";
        }
    }

    public static boolean getUseBinaryFileStore()
    {
        return settings.getSetting(getConfigurationFileName(),
                                   PARAM_FILE_STORE_USE_FILE_STORE,
                                   DEFAULT_USE_FILE_STORE,
                                   true);
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
        String defaultValue = FilenameUtils.concat(getDefaultApplicationRoot(), "files");

        if (defaultValue == null)
        {
            defaultValue = "files";
        }

        return settings.getSetting(getConfigurationFileName(), PARAM_FILE_STORE_BASE_PATH, defaultValue, true);
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

                        _defaultApplicationRoot = cls.getProtectionDomain().getCodeSource().getLocation().getPath();

                    }
                    catch (ClassNotFoundException cnfe)
                    {
                        LOGGER.warn("(FAILED) Determining Root Path");
                        _defaultApplicationRoot = "";
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

    public static void setSetting(String name, String value)
    {
        settings.setSetting(getConfigurationFileName(), name, value);
    }

    public static String getSetting(String name)
    {
        return settings.getSetting(getConfigurationFileName(), name, "", false);
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

    /**
     * Sets the number of decimal places for coordinates.
     *
     * @param value
     */
    public static void setNumDecimalsForCoordinates(int value)
    {
        settings.setSetting(getConfigurationFileName(), PARAM_COORD_NUM_DECIMALS, value);
    }

    /**
     * @return the number of decimal places for coordinates.
     */
    public static int getNumDecimalsForCoordinates()
    {
        return settings.getSetting(getConfigurationFileName(), PARAM_COORD_NUM_DECIMALS, DEFAULT_NUM_DECIMALS, true);
    }
    
    /*--------------------------------------------------------------------------
    Executive Service Settings
    --------------------------------------------------------------------------*/

    /**
     * @return the max threads per core * core
     */
    public static int getMaxThreads()
    {
        return getNumberOfCores() * getMaxThreadsPerCore();
    }

    /**
     * @return the min threads per core * core
     */
    public static int getMinThreads()
    {
        return getNumberOfCores() * getMinThreadsPerCore();
    }

    /**
     * @return the number of available cores
     */
    public static int getNumberOfCores()
    {
        return settings.getSetting(getConfigurationFileName(), PARAM_CORE_COUNT, DEFAULT_CORE_COUNT, true);
    }

    /**
     * Sets the number of available cores
     *
     * @param value
     */
    public static void setNumberOfCores(int value)
    {
        settings.setSetting(getConfigurationFileName(), PARAM_CORE_COUNT, value);
    }

    /**
     * @return the minimum number of threads per core
     */
    public static int getMinThreadsPerCore()
    {
        return settings.getSetting(getConfigurationFileName(), PARAM_MIN_THREADS, DEFAULT_MIN_THREADS, true);
    }

    /**
     * Sets the minimum number of threads per core
     *
     * @param value
     */
    public static void setMinThreadsPerCore(int value)
    {
        settings.setSetting(getConfigurationFileName(), PARAM_MIN_THREADS, value);
    }

    /**
     * @return the maximum number of threads per core
     */
    public static int getMaxThreadsPerCore()
    {
        return settings.getSetting(getConfigurationFileName(), PARAM_MAX_THREADS, DEFAULT_MAX_THREADS, true);
    }

    /**
     * Sets the maximum number of threads per core
     *
     * @param value
     */
    public static void setMaxThreadsPerCore(int value)
    {
        settings.setSetting(getConfigurationFileName(), PARAM_MAX_THREADS, value);
    }

    /**
     * @see #PARAM_KEEP_ALIVE_TIME
     */
    public static int getKeepAliveTime()
    {
        return settings.getSetting(getConfigurationFileName(), PARAM_KEEP_ALIVE_TIME, DEFAULT_KEEP_ALIVE_TIME, true);
    }

    /**
     * @see #PARAM_KEEP_ALIVE_TIME
     */
    public static void setKeepAliveTime(int value)
    {
        settings.setSetting(getConfigurationFileName(), PARAM_KEEP_ALIVE_TIME, value);
    }

    /**
     * @see #PARAM_THREAD_TIMOUT
     */
    public static int getThreadTimeout()
    {
        return settings.getSetting(getConfigurationFileName(), PARAM_THREAD_TIMOUT, DEFAULT_THREAD_TIMOUT, true);
    }

    /**
     * @see #PARAM_THREAD_TIMOUT
     */
    public static void setThreadTimeout(int value)
    {
        settings.setSetting(getConfigurationFileName(), PARAM_THREAD_TIMOUT, value);
    }

    /**
     * @return additional patterns for parsing DateTime values.
     */
    public static List<String> getTimePatterns()
    {
        return Arrays.asList(settings.getSetting(getConfigurationFileName(), PARAM_TIME_PATTERNS, "", true).split(","));
    }

    /**
     * Sets additional patterns for parsing DateTime values.
     *
     * @param value
     */
    public static void setTimePatterns(List<String> value)
    {
        settings.setSetting(getConfigurationFileName(), PARAM_TIME_PATTERNS, StringUtils.join(value, ","));
    }

    /*--------------------------------------------------------------------------
    Supported Axis
    --------------------------------------------------------------------------*/

    /**
     * Settings for coordinate axis.
     *
     * @author n78554
     */
    public enum EAxis {
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

        EAxis(boolean isRestricted, double min, double max)
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
