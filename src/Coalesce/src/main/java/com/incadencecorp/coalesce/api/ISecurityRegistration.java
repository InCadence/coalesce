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

package com.incadencecorp.coalesce.api;

import java.sql.SQLException;

import com.incadencecorp.coalesce.common.classification.MarkingValue;
import com.incadencecorp.coalesce.framework.persistance.CoalesceDataConnectorBase;

/**
 * This interface defines the methods required to register security settings to a given data base.
 * 
 * @author n78554
 * @param <T>
 */
public interface ISecurityRegistration<T extends CoalesceDataConnectorBase> {

    /**
     * Creates classification objects within the database ranking based on the order they are passed
     * in. If you pass in Top Secret and Secret then Top Secret is higher. If you pass in Secret and
     * Top Secret then Secret is higher.
     * 
     * @param values
     * @throws SQLException
     */
    void registerClassificationLevels(MarkingValue... values) throws SQLException;

}
