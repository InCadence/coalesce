/*
 *  Copyright 2017 - InCadence Strategic Solutions Inc., All Rights Reserved
 *
 *  Notwithstanding any contractor copyright notice, the Government has Unlimited
 *  Rights in this work as defined by DFARS 252.227-7013 and 252.227-7014.  Use
 *  of this work other than as specifically authorized by these DFARS Clauses may
 *  violate Government rights in this work.
 *
 *  DFARS Clause reference: 252.227-7013 (a)(16) and 252.227-7014 (a)(16)
 *  Unlimited Rights. The Government has the right to use, modify, reproduce,
 *  perform, display, release or disclose this computer software and to have or
 *  authorize others to do so.
 *
 *  Distribution Statement D. Distribution authorized to the Department of
 *  Defense and U.S. DoD contractors only in support of U.S. DoD efforts.
 *
 */

package com.incadencecorp.coalesce.framework.persistance.sql.impl.tests;

import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.CoalesceSettings;
import com.incadencecorp.coalesce.framework.persistance.AbstractCoalescePersistorTest;
import com.incadencecorp.coalesce.framework.persistance.sql.impl.SQLPersisterImpl;
import com.incadencecorp.coalesce.framework.persistance.sql.impl.SQLPersisterImplSettings;
import com.incadencecorp.unity.common.connectors.FilePropertyConnector;
import org.junit.BeforeClass;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * @author GGaito
 */
public class SQLPersisterImplIT extends AbstractCoalescePersistorTest<SQLPersisterImpl> {

    public static Map<String, String> params = new HashMap<>();
    @BeforeClass
    public static void initialize() throws CoalescePersistorException
    {
        FilePropertyConnector connector = new FilePropertyConnector(Paths.get("src", "test", "resources"));
        connector.setReadOnly(true);

        SQLPersisterImplSettings.setConnector(connector);
        CoalesceSettings.setConnector(connector);

    }

    @Override
    protected SQLPersisterImpl createPersister()
    {
        return new SQLPersisterImpl();
    }




}
