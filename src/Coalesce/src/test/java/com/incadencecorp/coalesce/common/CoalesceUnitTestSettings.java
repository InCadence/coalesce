package com.incadencecorp.coalesce.common;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import com.incadencecorp.coalesce.common.runtime.CoalesceSettings;

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

public class CoalesceUnitTestSettings extends CoalesceSettings {

    public static void initialize() throws IOException
    {
        File projectRoot = new File("");

        String projectPath = projectRoot.getAbsolutePath();

        CoalesceUnitTestSettings.setDefaultApplicationRoot(FilenameUtils.concat(projectPath, "UnitTestBin\\bin"));

        File root = new File(CoalesceUnitTestSettings.getDefaultApplicationRoot());

        if (root.exists())
        {
            FileUtils.cleanDirectory(root.getParentFile());
        }

        root.mkdirs();

        File uploads = new File(CoalesceUnitTestSettings.getBinaryFileStoreBasePath());
        
        uploads.mkdirs();
        
    }

    public static void tearDownAfterClass()
    {
        try
        {
            Path dirPath = Paths.get(FilenameUtils.concat(CoalesceUnitTestSettings.getDefaultApplicationRoot(), ".."));

            FileUtils.cleanDirectory(new File(dirPath.toUri()));
            
            Files.delete(dirPath);
        }
        catch (IOException e)
        {
        }
    }

    public static boolean setSubDirectoryLength(int value)
    {
        return CoalesceSettings.setSetting(getConfigurationFileName(), "Coalesce.FileStore.SubDirectoryLength", value);
    }

}
