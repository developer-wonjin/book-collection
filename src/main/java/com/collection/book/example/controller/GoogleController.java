package com.collection.book.example.controller;

import com.collection.book.example.service.GoogleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/google")
@RequiredArgsConstructor
@Slf4j
public class GoogleController {

    private final GoogleService googleService;

    @Value("${spreadsheet.name}")
    private String SPREADSHEET_ID; // 1. 기존에 스프레스 시트id를 복사해둔곳을 여기에 저장해둔다.
    private static final String RANGE = "A1"; // 2. 작성할 행을 입력

    @PostMapping("/write")
    public ResponseEntity<String> writeToSheet(@RequestParam("word") String word) {
        try {
            log.info(word);
            // 3. 데이터를 스프레드시트에 쓰기 위해 전달되는 형식
            // 행과 열에 데이터를 매핑하기 위함
            List<List<Object>> values = List.of(Collections.singletonList(word));

            // 4. 서비스 로직 호출
            googleService.writeToSheet(SPREADSHEET_ID, RANGE, values);
            return ResponseEntity.ok("Data written successfully to the spreadsheet: " + word);
        } catch (Exception e) {
            log.info("e: {0}", e);
            return ResponseEntity.internalServerError().body("Failed to write data: " + e.getMessage());
        }
    }
}
