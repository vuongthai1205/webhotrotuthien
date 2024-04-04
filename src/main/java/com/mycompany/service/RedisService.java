/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.service;

/**
 *
 * @author vuong
 */
public interface RedisService {
    void connectRedis();
    void saveToRedis(String key, String value);
    String getFromRedis(String key);
    void cleanCache();
}
