/*
 * (C) Copyright 2019 The PrematicLibraries Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 16.06.19 14:22
 *
 * The PrematicLibraries Project is under the Apache License, version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package net.prematic.libraries.caching;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * An array cached is based on a dynamic growing array.
 *
 * @param <O> The object to cache.
 */
public class ArrayCache<O> implements Cache<O>{

    private static final long TASK_SLEEP_TIME = 1000;
    private static final int DEFAULT_MAX_SIZE = 1000;
    private static final int DEFAULT_BUFFER = 128;

    private final Map<String,CacheQuery<O>> queries;
    private final ExecutorService executor;
    private CacheEntry[] entries;
    private Consumer<O> removeListener;
    private CacheTask task;
    private long refreshTime, expireTime;
    private int maxSize, size, buffer;

    public ArrayCache() {
        this(DEFAULT_MAX_SIZE);
    }

    public ArrayCache(int maxSize) {
        this(maxSize,DEFAULT_BUFFER);
    }

    public ArrayCache(int maxSize, int buffer) {
        this(getDefaultExecutor(),maxSize,buffer);
    }

    public ArrayCache(ExecutorService executor) {
        this(executor,DEFAULT_MAX_SIZE,DEFAULT_BUFFER);
    }

    public ArrayCache(ExecutorService executor, int maxSize) {
        this(executor,maxSize,DEFAULT_BUFFER);
    }

    public ArrayCache(ExecutorService executor, int maxSize, int buffer) {
        if(executor == null) throw new NullPointerException("Executor service is null.");
        this.executor = executor;
        this.buffer = buffer;
        this.maxSize = maxSize;

        this.queries = new LinkedHashMap<>();
        this.entries = new CacheEntry[buffer];
        this.removeListener = null;
        this.size = 0;
        this.refreshTime = 0;
        this.expireTime = 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        CacheEntry[] entries = this.entries;
        this.entries = new CacheEntry[buffer];
        for (int i = 0; i < entries.length; i++) entries[i] = null;//Reset for GC
    }

    @Override
    public Collection<O> getCachedObjects() {
        ArrayList<O> values = new ArrayList<>();
        for(CacheEntry entry : entries) values.add((O) entry.value);
        return values;
    }

    @Override
    public O get(String queryName, Object... identifiers) {
        CacheQuery<O> query = queries.get(queryName.toLowerCase());
        if(query == null) throw new IllegalArgumentException(queryName+" not found.");
        query.validate(identifiers);
        for(int i = 0; i < size; i++) {
            if(query.check((O) this.entries[i].value,identifiers)){
                CacheEntry entry = this.entries[i];
                move(i);
                entry.lastUsed = System.currentTimeMillis();
                this.entries[size-1] = entry;
                return (O) entry.value;
            }
        }
        O value = query.load(identifiers);
        insert(value);
        return value;
    }

    @Override
    public O get(Predicate<O> query) {
        return get(query,null);
    }

    @Override
    public O get(Predicate<O> query, Supplier<O> loader) {
        for(int i = 0; i < size; i++) {
            if(query.test((O) this.entries[i].value)){
                CacheEntry entry = this.entries[i];
                move(i);
                entry.lastUsed = System.currentTimeMillis();
                this.entries[size-1] = entry;
                return (O) entry.value;
            }
        }
        if(loader != null){
            O value = loader.get();
            insert(value);
            return value;
        }
        return null;
    }

    @Override
    public CompletableFuture<O> getAsync(String queryName, Object... identifiers) {
        return doAsync(() -> ArrayCache.this.get(queryName, identifiers));
    }

    @Override
    public CompletableFuture<O> getAsync(Predicate<O> query) {
        return getAsync(query,null);
    }

    @Override
    public CompletableFuture<O> getAsync(Predicate<O> query, Supplier<O> loader) {
        return doAsync(() -> ArrayCache.this.get(query,loader));
    }

    @Override
    public void insert(O value) {
        if(size >= maxSize){
            move(0);
            this.entries[size-1] = new CacheEntry(value);
        }else{
            if(size >= this.entries.length) grow();
            this.entries[size] = new CacheEntry(value);
            size++;
        }
    }

    @Override
    public void insertAsync(O object) {
        this.executor.execute(()-> insert(object));
    }

    @Override
    public O remove(String queryName, Object... identifiers) {
        CacheQuery<O> query = queries.get(queryName.toLowerCase());
        if(query == null) throw new IllegalArgumentException(queryName+" not found.");
        query.validate(identifiers);
        for(int i = 0; i < size; i++) {
            O value = (O)this.entries[i].value;
            if(query.check(value,identifiers)){
                move(i);
                this.entries[size--] = null;
                return value;
            }
        }
        return null;
    }

    @Override
    public O remove(Predicate<O> query) {
        for(int i = 0; i < size; i++) {
            O value = (O)this.entries[i].value;
            if(query.test(value)){
                move(i);
                this.entries[size--] = null;
                return value;
            }
        }
        return null;
    }

    @Override
    public boolean remove(Object value) {
        for(int i = 0; i < size; i++) {
            if(this.entries[i].value.equals(value)){
                move(i);
                this.entries[size--] = null;
                return true;
            }
        }
        return false;
    }

    @Override
    public CompletableFuture<O> removeAsync(String queryName, Object... identifiers) {
        return doAsync(() -> remove(queryName, identifiers));
    }

    @Override
    public CompletableFuture<O> removeAsync(Predicate<O> query) {
        return doAsync(() -> remove(query));
    }

    @Override
    public CompletableFuture<Boolean> removeAsync(O cachedObject) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        this.executor.execute(()->{
            try{
                future.complete(remove(cachedObject));
            }catch (Exception exception){
                future.completeExceptionally(exception);
            }
        });
        return future;
    }

    @Override
    public Cache<O> setMaxSize(int maxSize) {
        this.maxSize = maxSize;
        return this;
    }

    @Override
    public Cache<O> setRefresh(long expireTime, TimeUnit unit) {
        this.refreshTime = unit.toMillis(expireTime);
        createTask();
        return this;
    }

    @Override
    public Cache<O> setExpire(long expireTime, TimeUnit unit) {
        this.expireTime = unit.toMillis(expireTime);
        createTask();
        return this;
    }

    @Override
    public Cache<O> setRemoveListener(Consumer<O> removeListener) {
        this.removeListener = removeListener;
        return this;
    }

    @Override
    public Cache<O> registerQuery(String name, CacheQuery<O> query) {
        this.queries.put(name.toLowerCase(),query);
        return this;
    }

    @Override
    public Cache<O> unregisterQuery(String name) {
        this.queries.remove(name);
        return this;
    }

    @Override
    public void shutdown() {
        this.task.stop();
        clear();
    }

    /**
     * Set the array buffer.
     *
     * @param buffer The buffer
     */
    public void setBuffer(int buffer){
        if(buffer > maxSize) throw new IllegalArgumentException("Buffer is higher then the maximum size");
        this.buffer = buffer;
    }

    private void createTask(){
        if(task == null){
            task = new CacheTask();
            this.executor.execute(task);
        }
    }

    private void grow(){
        int newLength = this.entries.length+buffer;
        if(newLength > maxSize) this.entries = Arrays.copyOf(this.entries,maxSize);
        else this.entries = Arrays.copyOf(this.entries,newLength);
    }

    private void shrink(){
        int different = this.entries.length-size;
        if(different > buffer) entries = Arrays.copyOf(this.entries,size+buffer);
    }

    private void move(int index){
        int move = this.size - index - 1;
        System.arraycopy(entries,index+1,entries,index, move);
    }

    private CompletableFuture<O> doAsync(Supplier<O> runner){
        CompletableFuture<O> future = new CompletableFuture<>();
        this.executor.execute(()->{
            try{
                future.complete(runner.get());
            }catch (Exception exception){
                future.completeExceptionally(exception);
            }
        });
        return future;
    }

    static ExecutorService getDefaultExecutor(){
        try{
            return (ExecutorService) Class.forName("net.prematic.libraries.utility.GeneralUtil").getMethod("getDefaultExecutorService").invoke(null);
        }catch (Exception ignored){}
        return Executors.newCachedThreadPool();
    }

    private static class CacheEntry {

        private long entered, lastUsed;
        private Object value;

        public CacheEntry(Object value) {
            this.lastUsed = this.entered = System.currentTimeMillis();
            this.value = value;
        }
    }

    private class CacheTask implements Runnable{

        private boolean running;

        public CacheTask() {
            this.running = false;
        }

        public boolean isRunning() {
            return running;
        }

        public void stop(){
            this.running = false;
        }

        @Override
        public void run() {
            this.running = true;
            while(!Thread.interrupted() && this.running){
                try{
                    Thread.sleep(TASK_SLEEP_TIME);
                    for(int i = 0; i < size; i++) {
                        CacheEntry entry = entries[i];
                        if((expireTime > 0 && entry.lastUsed+expireTime <= System.currentTimeMillis())
                                || (refreshTime > 0 && entry.entered+refreshTime <= System.currentTimeMillis())){
                            move(i);
                            size--;
                            entries[size] = null;
                            i--;
                            if(removeListener != null) removeListener.accept((O) entry.value);
                        }
                    }
                    shrink();
                }catch (Exception exception){
                    exception.printStackTrace();
                }
            }
        }
    }
}
