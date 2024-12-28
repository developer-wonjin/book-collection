package com.collection.book.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class AladinApiUtil {

    public static String getISBN(Map<String, String> hm, String BOOKLIST_URL)  {
        StringBuffer sb = new StringBuffer();
        for (String key : hm.keySet()) {
            String val = hm.get(key);
            sb.append(key).append("=").append(val).append("&");
        }

        log.info("목록API: {}", BOOKLIST_URL + sb);

        String responseStr = null;
        try {
            responseStr = HttpUtil.get(BOOKLIST_URL + sb, null);
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

    public static Map<String, Object> getDetail(Map<String, String> hm, String bookdetailUrl) {
        StringBuffer sb = new StringBuffer();
        for (String key : hm.keySet()) {
            String val = hm.get(key);
            sb.append(key).append("=").append(val).append("&");
        }

        log.info("상세정보API: {}", bookdetailUrl + sb);

        String responseStr = null;
        try {
            responseStr = HttpUtil.get(bookdetailUrl + sb, null);
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

    public static void main(String[] args) {

    }
}
