package com.incadencecorp.coalesce.services.common.api;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.incadencecorp.coalesce.framework.jobs.AbstractCoalesceJob;

/**
 * This is a subset from {@link ExecutorService} to allow submitting jobs but
 * does not expose methods to terminate the service.
 * 
 * @author Derek Clemenzi
 */
public interface ICoalesceExecutorService {

    void execute(Runnable command);

    boolean isShutdown();

    boolean isTerminated();

    <T, Y> Future<Y> submit(AbstractCoalesceJob<T, Y> task);

    <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException;

    <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
            throws InterruptedException;

    <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException;

    <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException;

}
