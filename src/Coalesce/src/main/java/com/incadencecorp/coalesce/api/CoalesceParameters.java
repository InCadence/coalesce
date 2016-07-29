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

/**
 * Common Parameters
 * 
 * @author n78554
 *
 */
public final class CoalesceParameters {

    private CoalesceParameters()
    {
        // Do Nothing
    }

    private static final String INC = "com.incadencecorp.";

    /**
     * (URI) Specifies a directory the scanner can use. This can either be
     * absolute "file:/..." or relative from System.getProperty("user.dir").
     */
    public static final String PARAM_DIRECTORY = INC + "directory";

    /**
     * (Integer) When creating filenames from UUIDs this determines the
     * sub-directory which is based of the first X characters of the UUID.
     */
    public static final String PARAM_SUBDIR_LEN = INC + "subdirlen";

}
