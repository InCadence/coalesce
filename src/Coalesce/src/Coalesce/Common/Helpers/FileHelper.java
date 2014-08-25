package Coalesce.Common.Helpers;

import java.io.File;

import org.apache.commons.io.FilenameUtils;

import Coalesce.Common.Runtime.CoalesceSettings;

public class FileHelper {

    // Make static
    private FileHelper() {
        
    }
    
    public static String GetBaseFilenameWithFullDirectoryPathForKey(String key)
    {
        String baseFilename = GUIDHelper.RemoveBrackets(key);
        
        String fullDirectory;
        
        
          if (CoalesceSettings.GetSubDirectoryLength() > 0 &&
          	  CoalesceSettings.GetSubDirectoryLength() < baseFilename.length()) {
         
              fullDirectory = FilenameUtils.concat(CoalesceSettings.GetBinaryFileStoreBasePath(),
                                                   baseFilename.substring(0, CoalesceSettings.GetSubDirectoryLength()));
          } else {
              fullDirectory = CoalesceSettings.GetBinaryFileStoreBasePath();
          }
          
          File fileDir = new File(fullDirectory);
          if ( !fileDir.exists() ) {
              fileDir.mkdirs();
          }
          
          return FilenameUtils.concat(fullDirectory, baseFilename); 
          
    }
    
}
