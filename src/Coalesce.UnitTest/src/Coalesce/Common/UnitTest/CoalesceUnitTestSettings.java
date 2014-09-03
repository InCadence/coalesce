package Coalesce.Common.UnitTest;

import Coalesce.Common.Runtime.CoalesceSettings;


public class CoalesceUnitTestSettings extends CoalesceSettings {

    public static boolean SetSubDirectoryLength(int value)
    {
        return CoalesceSettings.SetSetting(GetConfigurationFileName(), "Coalesce.FileStore.SubDirectoryLength", value);
    }

}
