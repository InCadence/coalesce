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

package com.incadencecorp.coalesce.common.classification.tests;

import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.incadencecorp.coalesce.common.classification.ACCMControl;
import com.incadencecorp.coalesce.common.classification.ACCMNickname;
import com.incadencecorp.coalesce.common.classification.Marking;
import com.incadencecorp.coalesce.common.classification.MarkingUtil;
import com.incadencecorp.coalesce.common.classification.SAPControl;
import com.incadencecorp.coalesce.common.classification.SCIControl;

public class MarkingUtilTest {

    @Test
    public void testToSet() throws Exception
    {
        Marking mk = new Marking("UNCLASSIFIED//SI-BP-B C-BC");
        Set<String> values = MarkingUtil.toSet(mk.getSCIElements());

        Assert.assertTrue(values.contains("SI"));
        Assert.assertTrue(values.contains("SI-BP"));
        Assert.assertTrue(values.contains("SI-B"));
        Assert.assertTrue(values.contains("SI-B-C"));
        Assert.assertTrue(values.contains("SI-BC"));
        Assert.assertEquals(mk.getSCIString(), new SCIControl(MarkingUtil.fromSCISet(values)).toString());

        mk = new Marking("UNCLASSIFIED//SAR-BP-B C-BC");
        values = MarkingUtil.toSet(mk.getSAPPrograms());

        Assert.assertTrue(values.contains("BP"));
        Assert.assertTrue(values.contains("BP-B"));
        Assert.assertTrue(values.contains("BP-B-C"));
        Assert.assertTrue(values.contains("BP-BC"));
        Assert.assertEquals(mk.getSAPString(), new SAPControl(MarkingUtil.fromSAPSet(values)).toString());

        mk = new Marking("UNCLASSIFIED//ACCM-BP/B/C");
        values = MarkingUtil.toSet(mk.getACCMNicknames());

        Assert.assertTrue(values.contains("BP"));
        Assert.assertTrue(values.contains("B"));
        Assert.assertTrue(values.contains("C"));
        Assert.assertEquals(mk.getACCMString(), new ACCMControl(MarkingUtil.fromACCMSet(values)).toString());

    }
}
