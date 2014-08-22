package Coalesce.Common.UnitTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import Coalesce.Common.Helpers.AllCommonHelpersTests;
import Coalesce.Framework.DataModel.AllFrameworkDataModelTests;

@RunWith(Suite.class)
@SuiteClasses({ AllCommonHelpersTests.class, AllFrameworkDataModelTests.class})
public class AllCoalesceTests {

}
