/*-----------------------------------------------------------------------------'
 Copyright 2016 - InCadence Strategic Solutions Inc., All Rights Reserved

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

package com.incadencecorp.coalesce.framework.persistance;

import org.junit.Test;

import com.incadencecorp.coalesce.common.classification.MarkingValue;
import com.incadencecorp.coalesce.framework.persistance.neo4j.Neo4JRegistration;

/**
 * Test the neo4j implementation of the security registration.
 * 
 * @author n78554
 *
 */
public class Neo4jSecurityRegistrationIT {

    /**
     * Registers a test enumeration.
     * 
     * @throws Exception
     */
    @Test
    public void testRegisteringEnumeration() throws Exception
    {

        ServerConn svConnNeo4j = new ServerConn();
        svConnNeo4j.setServerName("dbsp3");
        svConnNeo4j.setPortNumber(7474);

        MarkingValue[] values = new MarkingValue[EClassification.values().length];

        int ii = 0;

        for (EClassification value : EClassification.values())
        {

            values[ii++] = new MarkingValue("", value.toString(), value.toString(), "");

        }

        Neo4JRegistration registration = new Neo4JRegistration();
        registration.setConnectionSettings(svConnNeo4j);
        registration.setMarkings(values);
        registration.register();

    }

    private enum EClassification
    {
        U, R, C, S, TS;
    }

}
