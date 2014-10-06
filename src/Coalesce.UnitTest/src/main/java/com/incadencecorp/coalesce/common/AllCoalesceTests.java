package com.incadencecorp.coalesce.common;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.incadencecorp.coalesce.common.classification.AllCommonClassificationTests;
import com.incadencecorp.coalesce.common.helpers.AllCommonHelpersTests;
import com.incadencecorp.coalesce.common.runtime.AllCommonRunTimeTests;
import com.incadencecorp.coalesce.framework.datamodel.AllFrameworkDataModelTests;
import com.incadencecorp.coalesce.framework.persistance.AllFrameworkPersisterTests;

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

@RunWith(Suite.class)
@SuiteClasses({ AllCommonClassificationTests.class, AllCommonHelpersTests.class, AllCommonRunTimeTests.class,
               AllFrameworkDataModelTests.class, AllFrameworkPersisterTests.class })
public class AllCoalesceTests {

}