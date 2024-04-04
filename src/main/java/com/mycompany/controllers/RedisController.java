/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.service.RedisService;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author vuong
 */
@RestController
@RequestMapping("/api")
public class RedisController {
    @Autowired
    private RedisService redisService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
     @Autowired
    private RedisTemplate<String, String> redisTemplateString;
    @GetMapping(path="/test-redis")
    public ResponseEntity<String> testRedis(){
        redisService.connectRedis();
        return ResponseEntity.status(HttpStatus.OK).body("OKE");
    }
    
    @GetMapping("/setMap")
    public String setMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("key1", "value1");
        map.put("key2", 123);
        map.put("key3", true);

        redisTemplate.opsForValue().set("myMap", map);
        return "Map set successfully!";
    }

    @GetMapping("/setValue")
    public String setValue() {
        // Tạo một đối tượng JSON
        Map<String, Object> jsonData = new HashMap<>();
        jsonData.put("name", "John");
        jsonData.put("age", 30);
        jsonData.put("city", "New York");

        // Lưu đối tượng JSON vào Redis
        redisTemplate.opsForValue().set("myKey", jsonData);
        return "Value set successfully!";
    }
    
    @GetMapping("/setValueString")
    public String setValueString() {
        // Tạo một đối tượng JSON
        Map<String, Object> jsonData = new HashMap<>();
        jsonData.put("name", "John");
        jsonData.put("age", 30);
        jsonData.put("city", "New York");

        // Chuyển đổi đối tượng JSON thành chuỗi JSON
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(jsonData);

            // Lưu chuỗi JSON vào Redis
            redisTemplateString.opsForValue().set("myKey", jsonString);
            return "Value set successfully!";
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "Error setting value!";
        }
    }

    @GetMapping("/getValue")
    public String getValue() {
        
        // Lấy giá trị từ Redis
        return redisTemplateString.opsForValue().get("myKey");
    }
}
