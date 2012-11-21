/**
 *  Copyright (c) 2011 Terracotta, Inc.
 *  Copyright (c) 2011 Oracle and/or its affiliates.
 *
 *  All rights reserved. Use is subject to license terms.
 */

package javax.cache;

import java.util.concurrent.TimeUnit;

import javax.cache.event.CacheEntryListener;
import javax.cache.transaction.IsolationLevel;
import javax.cache.transaction.Mode;

/**
 * The basic representation of a configuration for a {@link Cache}.
 * <p/>
 * The properties provided by instances of this interface are used by 
 * {@link CacheManager}s to configure {@link Cache}s.
 * <p/>
 * Implementations of this interface must always override {@link #hashCode()} and
 * {@link #equals(Object)} as {@link CacheConfiguration}s are often compared
 * at runtime.
 * 
 * @param <K> the type of keys maintained the cache
 * @param <V> the type of cached values
 * 
 * @author Greg Luck
 * @author Yannis Cosmadopoulos
 * @author Brian Oliver
 * 
 * @since 1.0
 */
public interface CacheConfiguration<K, V> {

    /**
     * Determines if a {@link Cache} should operate in "read-through" mode.
     * <p/>
     * When in "read-through" mode, cache misses that occur due to cache entries
     * not existing as a result of performing a "get" call via one of {@link Cache#get(Object)}, 
     * {@link Cache#getAll(java.util.Set)}, {@link Cache#getAndRemove(Object)} and/or
     * {@link Cache#getAndReplace(Object, Object)} will appropriately cause 
     * the configured {@link CacheLoader} to be invoked.
     * <p/>
     * The default value is <code>false</code>.
     * 
     * @return <code>true</code> when a {@link Cache} is in "read-through" mode. 
     * 
     * @see #getCacheLoader()
     */
    boolean isReadThrough();
    
    /**
     * Determines if a {@link Cache} should operate in "write-through" mode.
     * <p/>
     * When in "write-through" mode, cache updates that occur as a result of performing 
     * "put" operations call via one of {@link Cache#put(Object, Object)}, {@link Cache#getAndPut(Object, Object)}
     * {@link Cache#getAndRemove(Object)}, {@link Cache#getAndReplace(Object, Object)}, 
     * {@link Cache#invokeEntryProcessor(Object, javax.cache.Cache.EntryProcessor)}
     * will appropriately cause the configured {@link CacheWriter} to be invoked.
     * <p/>
     * The default value is <code>false</code>.
     * 
     * @return <code>true</code> when a {@link Cache} is in "write-through" mode.
     * 
     * @see #getCacheWriter()
     */
    boolean isWriteThrough();
    
    /**
     * Whether storeByValue (true) or storeByReference (false).
     * When true, both keys and values are stored by value.
     * <p/>
     * When false, both keys and values are stored by reference.
     * Caches stored by reference are capable of mutation by any threads holding
     * the reference. The effects are:
     * <ul>
     * <li>if the key is mutated, then the key may not be retrievable or removable</li>
     * <li>if the value is mutated, then all threads in the JVM can potentially observe those mutations,
     * subject to the normal Java Memory Model rules.</li>
     * </ul>
     * Storage by reference only applies to the local heap. If an entry is moved off heap it will
     * need to be transformed into a representation. Any mutations that occur after transformation
     * may not be reflected in the cache.
     * <p/>
     * When a cache is storeByValue, any mutation to the key or value does not affect the key of value
     * stored in the cache.
     * <p/>
     * The default value is <code>true</code>.
     * 
     * @return true if the cache is store by value
     */
    boolean isStoreByValue();

    /**
     * Checks whether statistics collection is enabled in this cache.
     * <p/>
     * The default value is <code>false</code>.
     *
     * @return true if statistics collection is enabled
     */
    boolean isStatisticsEnabled();

    /**
     * Sets whether statistics gathering is enabled on this cache. This may be changed at runtime.
     *
     * @param enableStatistics true to enable statistics, false to disable.
     */
    void setStatisticsEnabled(boolean enableStatistics);

    /**
     * Checks whether transaction are enabled for this cache.
     * <p/>
     * Note that in a transactional cache, entries being mutated within a transaction cannot be expired by the cache.
     * <p/>
     * The default value is <code>false</code>.
     * 
     * @return true if transaction are enabled
     */
    boolean isTransactionEnabled();

    /**
     * Gets the transaction isolation level.
     * <p/>
     * The default value is {@link IsolationLevel#NONE}.
     * 
     * @return the isolation level.
     */
    IsolationLevel getTransactionIsolationLevel();

    /**
     * Gets the transaction mode.
     * <p/>
     * The default value is {@link Mode#NONE}.
     * 
     * @return the the mode of the cache.
     */
    Mode getTransactionMode();

    /**
     * Obtains an {@link Iterable} over the {@link CacheEntryListener}s
     * to be configured on the {@link Cache}.
     * 
     * @return the {@link CacheEntryListener}s
     */
    Iterable<CacheEntryListener<? super K, ? super V>> getCacheEntryListeners();
    
    /**
     * Gets the registered {@link CacheLoader}, if any.
     * <p/>
     * A CacheLoader should be configured for "Read Through" caches
     * to load values when a cache miss occurs using either the
     * {@link Cache#get(Object)} and/or {@link Cache#getAll(java.util.Set} methods.
     * <p/>
     * The default value is <code>null</code>.
     * 
     * @return the {@link CacheLoader} or null if none has been set.
     */
    CacheLoader<K, ? extends V> getCacheLoader();

    /**
     * Gets the registered {@link CacheWriter}, if any.
     * <p/>
     * The default value is <code>null</code>.
     * 
     * @return the {@link CacheWriter} or null if none has been set.
     */
    CacheWriter<? super K, ? super V> getCacheWriter();

    /**
     * Gets the {@link ExpiryType} use for the configured {@link Cache}.
     * <p/>
     * The default value is {@link ExpiryType#MODIFIED}.
     * 
     * TODO: This will change when we introduce ExpiryPolicys
     * 
     * @return the {@link ExpiryType}
     */
    ExpiryType getExpiryType();
    
    /**
     * Gets the default time to live {@link Duration} for the {@link #getExpiryType()}
     * of the configured {@link Cache}.
     * <p/>
     * The default value is {@link Duration#ETERNAL}.
     * 
     * TODO: This will change when we introduce ExpiryPolicys
     *
     * @return a {@link Duration}
     */
    Duration getExpiryDuration();

    /**
     * A time duration.
     */
    public static class Duration {
        /**
         * ETERNAL
         */
        public static final Duration ETERNAL = new Duration(TimeUnit.SECONDS, 0);

        /**
         * The unit of time to specify time in. The minimum time unit is milliseconds.
         */
        private final TimeUnit timeUnit;

       /*
        * How long, in the specified units, the cache entries should live.
        * The lifetime is measured from the cache entry was last put (i.e. creation or modification for an update) or
        * the time accessed depending on the {@link ExpiryType}
        * 0 means eternal.
        *
        */
        private final long durationAmount;

        /**
         * Constructs a duration.
         *
         * @param timeUnit   the unit of time to specify time in. The minimum time unit is milliseconds.
         * @param durationAmount how long, in the specified units, the cache entries should live. 0 means eternal.
         * @throws NullPointerException          if timeUnit is null
         * @throws IllegalArgumentException      if durationAmount is less than 0 or a TimeUnit less than milliseconds is specified
         */
        public Duration(TimeUnit timeUnit, long durationAmount) {
            if (timeUnit == null) {
                throw new NullPointerException();
            }
            switch (timeUnit) {
                case NANOSECONDS:
                case MICROSECONDS:
                    throw new IllegalArgumentException("Must specify a TimeUnit of milliseconds or higher.");
                default:
                    this.timeUnit = timeUnit;
                    break;
            }
            if (durationAmount < 0) {
                throw new IllegalArgumentException("Cannot specify a negative durationAmount.");
            }
            this.durationAmount = durationAmount;
        }

        /**
         * @return the TimeUnit used to specify this duration
         */
        public TimeUnit getTimeUnit() {
            return timeUnit;
        }

        /**
         * @return the number of TimeUnits which quantify this duration
         */
        public long getDurationAmount() {
            return durationAmount;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Duration duration = (Duration) o;

            long time1 =  timeUnit.toMillis(durationAmount);
            long time2 = duration.timeUnit.toMillis(duration.durationAmount);
            return time1 == time2;
        }

        @Override
        public int hashCode() {
            return ((Long)timeUnit.toMillis(durationAmount)).hashCode();
        }
    }

    /**
     * Type of Expiry
     */
    public enum ExpiryType {

        /**
         * The time since a {@link Cache.Entry} was <em>created</em> or <em>last modified</em>. An example of a cache operation
         * which does this is {@link Cache#put}.
         */
        MODIFIED,

        /**
         * The Time since a {@link Cache.Entry} was last <em>accessed</em> by a cache operation. An access explicitly includes  <em>modified</em>
         * operations. An example is {@link Cache#get(Object)}
         */
        ACCESSED
    }
}
