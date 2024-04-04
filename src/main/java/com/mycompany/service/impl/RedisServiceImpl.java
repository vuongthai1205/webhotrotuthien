/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.service.RedisService;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 *
 * @author vuong
 */
@Service
public class RedisServiceImpl implements RedisService {
    @Autowired
    private RedisTemplate<String, String> redisTemplateString;

    @Override
    public void connectRedis() {
        RedisClient redisClient = RedisClient.create("redis://localhost:6379");

        try ( StatefulRedisConnection<String, String> connection = redisClient.connect()) {
            RedisAsyncCommands<String, String> asyncCommands = connection.async();

            asyncCommands.set("foo", "bar").get();
            System.out.println(asyncCommands.get("foo").get()); // prints bar

            Map<String, String> hash = new HashMap<>();
            hash.put("name", "John");
            hash.put("surname", "Smith");
            hash.put("company", "Redis");
            hash.put("age", "29");
            asyncCommands.hset("user-session:123", hash).get();

            System.out.println(asyncCommands.hgetall("user-session:123").get());

            // Kiểm tra xem key "foo" có tồn tại không
            System.out.println("Key 'foo' tồn tại trong Redis: " + asyncCommands.exists("foo").get());

            // Kiểm tra xem key "user-session:123" có tồn tại không
            System.out.println("Key 'user-session:123' tồn tại trong Redis: " + asyncCommands.exists("user-session:123").get());
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            redisClient.shutdown();
        }

    }

    @Override
    public void saveToRedis(String key, String value) {
        // Lưu chuỗi JSON vào Redis
        redisTemplateString.opsForValue().set(key, value);
    }

    @Override
    public String getFromRedis(String key) {
        return redisTemplateString.opsForValue().get(key);
    }

    @Override
    public void cleanCache() {
        redisTemplateString.getConnectionFactory().getConnection().flushAll();
    }

}
