package Coalesce.Common.Helpers;

import java.io.File;

import org.apache.commons.io.FilenameUtils;

import Coalesce.Common.Runtime.CoalesceSettings;

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

public class FileHelper {

    // Make static
    private FileHelper()
    {

    }

    public static String GetBaseFilenameWithFullDirectoryPathForKey(String key)
    {
        return GetBaseFilenameWithFullDirectoryPathForKey(key, true);
    }

    private static String GetBaseFilenameWithFullDirectoryPathForKey(String key, boolean createIfDoesNotExist)
    {
        return GetBaseFilenameWithFullDirectoryPathForKey(CoalesceSettings.GetBinaryFileStoreBasePath(),
                                                          CoalesceSettings.GetSubDirectoryLength(),
                                                          key,
                                                          createIfDoesNotExist);
    }

    private static String GetBaseFilenameWithFullDirectoryPathForKey(String binaryFileStoreBasePath,
                                                                     int subDirectoryLength,
                                                                     String key,
                                                                     boolean createIfDoesNotExist)
    {

        if (key == null || StringHelper.IsNullOrEmpty(key.trim())) return null;

        String baseFilename = GUIDHelper.RemoveBrackets(key);

        if (baseFilename == null) return null;

        String fullDirectory;

        if (subDirectoryLength > 0 && subDirectoryLength < baseFilename.length())
        {

            fullDirectory = FilenameUtils.concat(binaryFileStoreBasePath, baseFilename.substring(0, subDirectoryLength));
        }
        else
        {
            fullDirectory = binaryFileStoreBasePath;
        }

        if (createIfDoesNotExist)
        {
            File fileDir = new File(fullDirectory);
            if (!fileDir.exists())
            {
                fileDir.mkdirs();
            }
        }

        return FilenameUtils.concat(fullDirectory, baseFilename);

    }

}
