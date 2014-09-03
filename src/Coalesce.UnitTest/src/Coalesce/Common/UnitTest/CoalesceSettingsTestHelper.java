package Coalesce.Common.UnitTest;

import unity.connector.local.LocalConfigurationsConnector;


public class CoalesceSettingsTestHelper {

    // Make static
    private CoalesceSettingsTestHelper() {
    }
    
    public static void setUpdBeforeClass() {
        

        //RestConfigConnector.initialize("localhost", 8080);

        //CoalesceUnitTestSettings.Initialize(new RestConfigurationsConnector("localhost", 8080));
        CoalesceUnitTestSettings.Initialize(new LocalConfigurationsConnector());

    }
}
