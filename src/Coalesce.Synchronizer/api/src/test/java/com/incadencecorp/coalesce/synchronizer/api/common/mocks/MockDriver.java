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

package com.incadencecorp.coalesce.synchronizer.api.common.mocks;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.synchronizer.api.common.AbstractDriver;

/**
 * Mock Driver Implementation
 * 
 * @author n78554
 */
public class MockDriver extends AbstractDriver {

    private ExecutorService service = Executors.newFixedThreadPool(1);

    @Override
    public void start()
    {
        // Do Nothing
    }

    @Override
    public void stop()
    {
        // Do Nothing
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException
    {
        return service.invokeAll(tasks);
    }
    
    @Override
    public <T> Future<T> submit(Callable<T> task) throws CoalescePersistorException
    {
        return service.submit(task);
    }
    
    @Override
    public final void execute(Runnable command)
    {
        service.execute(command);
    }

    @Override
    public final boolean isShutdown()
    {
        return service.isShutdown();
    }

    @Override
    public final boolean isTerminated()
    {
        return service.isTerminated();
    }

    @Override
    public final <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
            throws InterruptedException
    {
        return service.invokeAll(tasks, timeout, unit);
    }

    @Override
    public final <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException
    {
        return service.invokeAny(tasks);
    }

    @Override
    public final <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException
    {
        return service.invokeAny(tasks, timeout, unit);
    }

}
