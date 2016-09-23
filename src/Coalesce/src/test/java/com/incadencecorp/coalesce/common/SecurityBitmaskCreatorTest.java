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

package com.incadencecorp.coalesce.common;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.incadencecorp.coalesce.common.bitmask.SecurityBitmaskCreator;
import com.incadencecorp.coalesce.common.bitmask.SecurityBitmaskHelper;

/**
 * These unit test exercise bit mask creations.
 * 
 * @author n78554
 */
public class SecurityBitmaskCreatorTest {

    /**
     * Verifies the creation of bitmasks.
     */
    @Test
    public void testCreations()
    {

        String[] groups = new String[] {
                "A", "B"
        };

        SecurityBitmaskCreator creator = new SecurityBitmaskCreator(groups);

        boolean[] mask = creator.createBitmask(false, "B", "C");

        // C is an invalid group and ignore invalid is false; therefore ensure
        // no access is set.
        Assert.assertTrue(mask[0]);

        List<String> options = creator.getOptions(mask);

        Assert.assertFalse(options.contains("A"));
        Assert.assertTrue(options.contains("B"));

        options = creator.getOptions(SecurityBitmaskHelper.toString(mask));

        Assert.assertFalse(options.contains("A"));
        Assert.assertTrue(options.contains("B"));

    }

    /**
     * Verifies if an invalid group is specified but ignore invalid is specified
     * wont set the no access flag.
     * 
     * @throws Exception
     */
    @Test
    public void testIgnoreInvalid() throws Exception
    {

        String[] groups = new String[] {
                "A", "B"
        };

        SecurityBitmaskCreator creator = new SecurityBitmaskCreator(groups);

        boolean[] mask = creator.createBitmask(true, "B", "C");

        // C is an invalid group and ignore invalid is true; therefore ensure
        // no access is not set.
        Assert.assertFalse(mask[0]);

        List<String> options = creator.getOptions(mask);

        Assert.assertFalse(options.contains("A"));
        Assert.assertTrue(options.contains("B"));
    }

    /**
     * Verifies filling a bitmask.
     * 
     * @throws Exception
     */
    @Test
    public void testFillAccess() throws Exception
    {

        List<String> groups = new ArrayList<String>();
        groups.add("A");
        groups.add("B");

        SecurityBitmaskCreator creator = new SecurityBitmaskCreator(groups.iterator());

        boolean[] mask = creator.createFilledBitmask(true);

        for (boolean tick : mask)
        {
            Assert.assertTrue(tick);
        }

        mask = creator.createFilledBitmask(false);

        for (boolean tick : mask)
        {
            Assert.assertFalse(tick);
        }

    }

    /**
     * Verifies creation of bitmask from a Java enumeration.
     * 
     * @throws Exception
     */
    @Test
    public void testEnumerationCreation() throws Exception
    {

        SecurityBitmaskCreator creator = new SecurityBitmaskCreator(EGroups.class);

        boolean[] mask = creator.createBitmask(true, EGroups.B.toString());

        Assert.assertFalse(mask[0]);
        Assert.assertFalse(mask[1]);
        Assert.assertTrue(mask[2]);

    }

    /**
     * Verifies getting options from a creator.
     * 
     * @throws Exception
     */
    @Test
    public void testGetOptions() throws Exception
    {

        SecurityBitmaskCreator creator = new SecurityBitmaskCreator(EGroups.class);

        List<String> options = creator.getOptions();

        Assert.assertTrue(options.contains(EGroups.A.toString()));
        Assert.assertTrue(options.contains(EGroups.B.toString()));

    }

    /**
     * Verifies that creating a bitmask from an existing mask will pad or
     * truncate the mask based on the creators options.
     * 
     * @throws Exception
     */
    @Test
    public void testBitmaskPadding() throws Exception
    {

        SecurityBitmaskCreator creator = new SecurityBitmaskCreator("A", "B", "C");

        boolean[] mask = creator.createBitmask(new boolean[] {
                false, true
        });

        Assert.assertEquals(4, mask.length);
        Assert.assertFalse(mask[0]);
        Assert.assertTrue(mask[1]);
        Assert.assertFalse(mask[2]);
        Assert.assertFalse(mask[3]);

        mask = creator.createBitmask(new boolean[] {
                true, true, true, true, true, true
        });

        Assert.assertEquals(4, mask.length);
        Assert.assertTrue(mask[0]);
        Assert.assertTrue(mask[1]);
        Assert.assertTrue(mask[2]);
        Assert.assertTrue(mask[3]);

    }

    /**
     * Verifies that specifying no options will always create a bitmask without
     * the no access flag set. This allows for a means to disable to security
     * check.
     * 
     * @throws Exception
     */
    @Test
    public void testEmptyCreator() throws Exception
    {
        boolean[] mask;

        SecurityBitmaskCreator creator = new SecurityBitmaskCreator();

        mask = creator.createBitmask(false, EGroups.B.toString());

        Assert.assertEquals(1, mask.length);
        Assert.assertFalse(mask[0]);

        mask = creator.createBitmask(true, EGroups.B.toString());

        Assert.assertEquals(1, mask.length);
        Assert.assertFalse(mask[0]);

    }

    private enum EGroups
    {
        A, B;
    }

}
