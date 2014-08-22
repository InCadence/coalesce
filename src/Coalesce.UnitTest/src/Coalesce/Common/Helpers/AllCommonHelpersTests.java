package Coalesce.Common.Helpers;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	DateTimeHelperTest.class,
	GUIDHelperTest.class,
	JodaDateTimeHelperTest.class,
	XmlHelperTest.class })
public class AllCommonHelpersTests {

}
