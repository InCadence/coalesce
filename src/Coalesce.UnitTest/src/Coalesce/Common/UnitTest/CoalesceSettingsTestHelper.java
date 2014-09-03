package Coalesce.Common.UnitTest;

import unity.connector.local.LocalConfigurationsConnector;


public class CoalesceSettingsTestHelper {

    // Make static
    private CoalesceSettingsTestHelper() {
    }
    
    public static void setUpdBeforeClass() {
        
        CoalesceUnitTestSettings.Initialize(new LocalConfigurationsConnector());

    }
}
