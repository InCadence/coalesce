package Coalesce.Framework.DataModel;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import Coalesce.Common.Helpers.FileHelper;

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

public class XsdFieldCommon {

    private XsdFieldCommon()
    {

    }

    public static String CallGetBaseFilenameWithFullDirectoryPathForKey(String key,
                                                                        boolean createIfDoesNotExist) throws NoSuchMethodException,
            SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        Class<?>[] args = new Class[2];
        args[0] = String.class;
        args[1] = boolean.class;

        Method method = FileHelper.class.getDeclaredMethod("GetBaseFilenameWithFullDirectoryPathForKey", args);
        method.setAccessible(true);

        Object results = method.invoke(null, key, createIfDoesNotExist);

        return (String) results;
    }
}
