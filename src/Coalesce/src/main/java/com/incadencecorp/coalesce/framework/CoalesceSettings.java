package com.incadencecorp.coalesce.framework;

import java.util.Map;

import org.apache.commons.io.FilenameUtils;

import com.incadencecorp.coalesce.common.helpers.StringHelper;
import com.incadencecorp.unity.common.SettingsBase;

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
 * Contains all the settings used by Coalesce. Settings are not persisted between application restarts unless you specify a
 * connector by calling {@link SettingsBase#initialize(com.incadencecorp.unity.common.IConfigurationsConnector)}.
 */
public class CoalesceSettings extends SettingsBase {

    /*--------------------------------------------------------------------------
    	Private Member Variables
    --------------------------------------------------------------------------*/

    private static String _defaultApplicationName;
    private static String _defaultApplicationRoot;

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
        return CoalesceSettings.getSetting(getConfigurationFileName(), "Coalesce.FileStore.UseFileStore", true, true);
    }

    public static boolean setUseBinaryFileStore(boolean value)
    {
        return CoalesceSettings.setSetting(getConfigurationFileName(), "Coalesce.FileStore.UseFileStore", value);
    }

    public static boolean getUseIndexing()
    {
        return CoalesceSettings.getSetting(getConfigurationFileName(), "Coalesce.FileStore.UseIndexing", true, true);
    }

    public static boolean setUseIndexing(boolean value)
    {
        return CoalesceSettings.setSetting(getConfigurationFileName(), "Coalesce.FileStore.UseIndexing", value);
    }

    public static int getSubDirectoryLength()
    {
        return CoalesceSettings.getSettingWithMinMax(getConfigurationFileName(),
                                                     "Coalesce.FileStore.SubDirectoryLength",
                                                     2,
                                                     0,
                                                     5,
                                                     true);
    }

    public static String getBinaryFileStoreBasePath()
    {
        return CoalesceSettings.getSetting(getConfigurationFileName(),
                                           "Coalesce.FileStore.BasePath",
                                           FilenameUtils.concat(getDefaultApplicationRoot(), "..\\images\\uploads\\"),
                                           true);
    }

    public static boolean setBinaryFileStoreBasePath(String value)
    {
        return CoalesceSettings.setSetting(getConfigurationFileName(), "Coalesce.FileStore.BasePath", value);
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
        return CoalesceSettings.getSetting(getConfigurationFileName(), "Coalesce.Security.UseEncryption", false, true);
    }

    public static boolean setUseEncryption(boolean value)
    {
        return CoalesceSettings.setSetting(getConfigurationFileName(), "Coalesce.Security.UseEncryption", value);
    }

    public static String getPassPhrase()
    {
        return CoalesceSettings.getSetting(getConfigurationFileName(),
                                           "Coalesce.Security.PassPhrase",
                                           "9UFAF8FI98BDLQEZ",
                                           true);
    }

    public static boolean setPassPhrase(String value)
    {
        return CoalesceSettings.setSetting(getConfigurationFileName(), "Coalesce.Security.PassPhrase", value);
    }

    public static boolean getAuditSelectStatements()
    {
        return CoalesceSettings.getSetting(getConfigurationFileName(), "Coalesce.Security.AuditSelectStatements", true, true);
    }

    public static boolean setAuditSelectStatements(boolean value)
    {
        return CoalesceSettings.setSetting(getConfigurationFileName(), "Coalesce.Security.AuditSelectStatements", value);
    }

    public static String getImageFormat()
    {
        return CoalesceSettings.getSetting(getConfigurationFileName(), "Coalesce.FileStore.ImageFormat", "jpg", true);
    }

    public static boolean setImageFormat(String value)
    {
        return CoalesceSettings.setSetting(getConfigurationFileName(), "Coalesce.Filestore.ImageFormat", value);
    }

}
