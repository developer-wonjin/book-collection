package com.collection.book.spreadsheet.controller;

import com.collection.book.spreadsheet.domain.ReadRangeRequestDto;
import com.collection.book.spreadsheet.service.GoogleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/google")
@RequiredArgsConstructor
@Slf4j
public class GoogleController {

    @Value("${spreadsheet.name}")
    private String SPREADSHEET_ID;

    private final GoogleService googleService;

    @PostMapping("/write")
    public ResponseEntity<String> writeToSheet(@RequestBody String word) {
        List<List<Object>> values = List.of(Collections.singletonList(word));
        googleService.writeToSheet(SPREADSHEET_ID, "A1", values);
        return ResponseEntity.ok("Data written successfully to the spreadsheet: " + word);
    }

    @PostMapping("/read")
    public ResponseEntity<List<List<Object>>> readFromSheet(@RequestBody ReadRangeRequestDto readRange) {
        String rangeStr = readRange.toString();
        log.info("rangeStr: {}", rangeStr);
        List<List<Object>> lists = googleService.readFromSheet(SPREADSHEET_ID, rangeStr);
        return ResponseEntity.ok(lists);
    }
}