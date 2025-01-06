package com.collection.book.example.service;

import com.collection.book.util.HttpUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AladinService {

    @Cacheable(cacheNames = "getIsbn", key = "'book:isbn:' + #booklistUrl", cacheManager = "bookCacheManager")
    public String getIsbn(String booklistUrl) {
        String responseStr = null;
        try {
            log.info("isbn api호출");
            responseStr = HttpUtil.get(booklistUrl, null);
        } catch (IOException e) {
            log.error("e: {0}", e);
            throw new RuntimeException(e);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> response = null;
        try {
            response = objectMapper.readValue(responseStr, Map.class);
        } catch (JsonProcessingException e) {
            log.error("e: {0}", e);
            throw new RuntimeException(e);
        }
        List<Object> item = (List<Object>) response.get("item");
        if (item == null || item.isEmpty()) return null;

        Map<String, Object> book = (Map<String, Object>) item.get(0);
        return (String) book.get("isbn13");
    }

    @Cacheable(cacheNames = "getDetail", key = "'book:detail:' + #booklistUrl", cacheManager = "bookCacheManager")
    public Map<String, Object> getDetail(String bookdetailUrl) {
        String responseStr = null;
        try {
            log.info("detail api호출");
            responseStr = HttpUtil.get(bookdetailUrl, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String jsonStr = responseStr.substring(0, responseStr.length() - 1);

        log.info("원본: {}", jsonStr);
        jsonStr = jsonStr.replace("\\", "");
        jsonStr = jsonStr.replace("\\n", "\\\\n");
        jsonStr = jsonStr.replace("\t", "");
        log.info("가공: {}", jsonStr);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(jsonStr, Map.class);
        } catch (JsonProcessingException e) {
            log.error("e: {0}", e);
            throw new RuntimeException(e);
        }
    }
}