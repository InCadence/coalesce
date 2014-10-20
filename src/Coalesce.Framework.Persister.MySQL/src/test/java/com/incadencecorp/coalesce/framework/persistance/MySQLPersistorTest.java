package com.incadencecorp.coalesce.framework.persistance;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.xml.sax.SAXException;

import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.persistance.mysql.MySQLDataConnector;
import com.incadencecorp.coalesce.framework.persistance.mysql.MySQLPersistor;

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

public class MySQLPersistorTest extends CoalescePersistorBaseTest {

    @BeforeClass
    public static void setupBeforeClass() throws SAXException, IOException, CoalesceException
    {
        MySQLPersistorTest tester = new MySQLPersistorTest();

        CoalescePersistorBaseTest.setupBeforeClassBase(tester);

    }

    @AfterClass
    public static void tearDownAfterClass()
    {
        MySQLPersistorTest tester = new MySQLPersistorTest();

        CoalescePersistorBaseTest.tearDownAfterClassBase(tester);

    }

    @Override
    protected ServerConn getConnection()
    {
        ServerConn serCon = new ServerConn();
        serCon = new ServerConn();
        serCon.setUser("root");
        serCon.setPassword("Passw0rd");
        serCon.setServerName("127.0.0.1");
        serCon.setPortNumber(3306);
        serCon.setDatabase("coalescedatabase");

        return serCon;

    }

    @Override
    protected ICoalescePersistor getPersistor(ServerConn conn)
    {
        MySQLPersistor mySQLPersistor = new MySQLPersistor();
        mySQLPersistor.initialize(conn);

        return mySQLPersistor;

    }

    @Override
    protected ServerConn getInvalidConnection()
    {
        ServerConn serConFail = new ServerConn();
        serConFail.setServerName("192.168.1.1");
        serConFail.setPortNumber(3306);
        serConFail.setDatabase("coalescedatabase");

        serConFail.setUser("root");
        serConFail.setPassword("Passw0rd");

        return serConFail;

    }

    @Override
    protected CoalesceDataConnectorBase getDataConnector(ServerConn conn) throws CoalescePersistorException
    {
        return new MySQLDataConnector(conn);
    }

}
