/*
 * This file is part of Quack and is Licensed under the MIT License.
 */
package net.covers1624.curl4j.httpapi;

import net.covers1624.curl4j.util.CurlHandle;
import net.covers1624.quack.util.Duration;

import java.lang.foreign.Arena;
import java.lang.ref.Cleaner;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Supplier;

/**
 * A simple pool of {@link CurlHandle} instances.
 * <p>
 * This is a time based pool. By default, after 5 minutes of inactivity the
 * entry will be purged from the pool. It is expected that the resource contained
 * within the pool does automatic native resource cleanup based on some reference {@link Cleaner}.
 * Such as {@link Arena#ofAuto} for native memory segment.
 * <p>
 * Created by covers1624 on 16/1/24.
 */
final class HandlePool<T> implements AutoCloseable {

    private final Supplier<T> factory;
    private final LinkedList<Entry> entries = new LinkedList<>();
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(r -> {
                Thread th = new Thread(r);
                th.setName("Handle Pool Cleaner");
                th.setDaemon(true);
                return th;
            }
    );

    /**
     * Create a new pool with default time management.
     *
     * @param factory The factory to create new instances.
     */
    public HandlePool(Supplier<T> factory) {
        this(factory, Duration.minutes(1), Duration.minutes(5));
    }

    public HandlePool(Supplier<T> factory, Duration pollTime, Duration liveTime) {
        this.factory = factory;
        executor.scheduleAtFixedRate(() -> clean(liveTime), pollTime.time, pollTime.time, pollTime.unit);
    }

    /**
     * Get an entry from the pool.
     *
     * @return The entry.
     */
    public Entry get() {
        synchronized (entries) {
            Entry entry = entries.poll();
            if (entry == null) {
                entry = new Entry(factory.get());
            }
            entry.lastUsed = System.currentTimeMillis();
            return entry;
        }
    }

    /**
     * Return an entry to the pool.
     *
     * @param entry The entry to return.
     */
    public void finished(Entry entry) {
        synchronized (entries) {
            // Add to front. Allows for usage pressure to discard old
            // handles automatically.
            entries.addFirst(entry);
        }
    }

    @Override
    public void close() {
        executor.shutdownNow();
        synchronized (entries) {
            entries.clear();
        }
    }

    private void clean(Duration liveTime) {
        long lt = liveTime.unit.toMillis(liveTime.time);
        synchronized (entries) {
            long currTime = System.currentTimeMillis();
            entries.removeIf(entry -> entry.lastUsed + lt > currTime);
        }
    }

    public final class Entry implements AutoCloseable {

        public T handle;
        private long lastUsed;

        private Entry(T handle) {
            this.handle = handle;
        }

        @Override
        public void close() {
            finished(this);
        }
    }
}
