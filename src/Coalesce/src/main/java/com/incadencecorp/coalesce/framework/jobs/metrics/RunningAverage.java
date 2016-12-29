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

package com.incadencecorp.coalesce.framework.jobs.metrics;

/**
 * Stores the running average along with the maximum and minimal values.
 *
 * @author Derek C.
 */
public class RunningAverage {

    // ----------------------------------------------------------------------//
    // Private Members
    // ----------------------------------------------------------------------//

    private long totalValue;
    private long valueCount;

    private long maxValue;
    private long minValue;

    // ----------------------------------------------------------------------//
    // Public Functions
    // ----------------------------------------------------------------------//

    /**
     * Default constructor
     */
    public RunningAverage() {
        this(0, 0, 0, 0);
    }

    /**
     * Creates a RunningAverage object using the passed parameters.
     * 
     * @param total
     * @param count
     * @param max
     * @param min
     */
    public RunningAverage(long total, long count, long max, long min) {
        totalValue = total;
        valueCount = count;
        maxValue = max;
        minValue = min;
    }

    /**
     * Adds the value to the running average.
     *
     * @param value
     */
    public final void add(final long value) {

        // First Call?
        if (valueCount == 0) {
            // Yes; set Values
            totalValue = value;
            valueCount = 1;
            minValue = value;
            maxValue = value;

        } else {
            // No; Compute Average and Compare Max / Min
            totalValue += value;
            valueCount += 1;

            if (maxValue < value) {
                maxValue = value;
            }

            if (minValue > value) {
                minValue = value;
            }
        }
    }

    /**
     * Merges the running average.
     *
     * @param value
     */
    public final void add(final RunningAverage value) {

        // First Call?
        if (valueCount == 0) {
            // Yes; set Values
            totalValue = value.getAverage();
            valueCount = 1;
            minValue = value.getMin();
            maxValue = value.getMax();

        } else {
            // No; Compute Average and Compare Max / Min
            totalValue += value.getTotal();
            valueCount += value.getCount();

            if (maxValue < value.getMax()) {
                maxValue = value.getMax();
            }

            if (minValue > value.getMin()) {
                minValue = value.getMin();
            }
        }
    }

    // ----------------------------------------------------------------------//
    // Public Getters
    // ----------------------------------------------------------------------//

    /**
     * @return the average value.
     */
    public final long getAverage() {
        if (valueCount == 0) {
            return 0;
        } else {
            return totalValue / valueCount;
        }
    }

    /**
     * @return the maximum value.
     */
    public final long getMax() {
        return maxValue;
    }

    /**
     * @return the minimal value.
     */
    public final long getMin() {
        return minValue;
    }

    /**
     * @return the total value.
     */
    public final long getTotal() {
        return totalValue;
    }

    /**
     * @return the count.
     */
    public final long getCount() {
        return valueCount;
    }

}
