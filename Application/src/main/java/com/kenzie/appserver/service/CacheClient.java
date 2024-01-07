package com.kenzie.appserver.service;
import com.kenzie.appserver.repositories.model.EventRecord;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Optional;
import javax.inject.Inject;

public class CacheClient {
    private final JedisPool pool;


    public CacheClient() {
    pool = new JedisPool(new JedisPoolConfig(), "localhost", 6379, 20000);
    }




    private void checkForNullKey(String key){
        if(key == null){
            throw new IllegalArgumentException();
        }
    }

    /**
     * Method that sets a key-value pair in the cache.
     *
     * PARTICIPANTS: Implement this method.
     *
     * @param key     String used to identify an item in the cache
     * @param seconds The number of seconds during which the item is available
     * @param value   String representing the value set in the cache
     */
    public void setValue(String key, long seconds, String value){
        checkForNullKey(key);
        try(Jedis jedis = pool.getResource()){
            jedis.setex(key, seconds, value);

        }
    }

    /**
     * Method that retrieves a value from the cache.
     *
     * PARTICIPANTS: Replace return null with your implementation of this method.
     *
     * @param key String used to identify the item being retrieved
     * @return String representing the value stored in the cache or an empty Optional in the case of a cache miss.
     */
    public Optional<String> getValue(String key){
        checkForNullKey(key);
        try(Jedis jedis = pool.getResource()){
            String value = jedis.get(key);

            if(value == null){
                return Optional.empty();
            }
            else{
                return Optional.of(value);
            }
        }
    }

    /**
     * Method to invalidate an item in the cache. Checks whether the requested key exists before invalidating.
     *
     * PARTICIPANTS: Implement this method.
     *
     * @param key String representing the key to be deleted from the cache
     * @return true on invalidation, false if key does not exist in cache
     */
    public boolean invalidate(String key) {
        checkForNullKey(key);
        try(Jedis jedis = pool.getResource()) {
            if (jedis.get(key) != null) {
                jedis.del(key);

                return true;
            }
            return false;
        }
    }
}