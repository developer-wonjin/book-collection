package com.collection.book.spreadsheet.service;

import com.collection.book.spreadsheet.domain.UpdateArea;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class GoogleService {

    private static final String APPLICATION_NAME = "Google Sheets Application"; // Google Sheets 애플리케이션의 이름을 나타냄
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance(); // 상수는 JSON 데이터를 처리하기 위한 JsonFactory 인스턴스를 제공
    private static final String CREDENTIALS_FILE_PATH = "/googlesheet/google.json"; // 인증에 사용되는 JSON 파링릐 경로를 지정
    private Sheets sheetsService;

    // 현재의 메소드는 Sheets 인스턴스를 얻는 데 사용
    private Sheets getSheetsService() throws IOException, GeneralSecurityException {
        if (sheetsService == null) {
            GoogleCredentials credentials = GoogleCredentials.fromStream(new ClassPathResource(CREDENTIALS_FILE_PATH).getInputStream())
                    // Google API를 호출할 떄 필요한 권한을 지정하는 부분 , 읽기/쓰기 권한을 나타냄
                    .createScoped(Collections.singletonList("https://www.googleapis.com/auth/spreadsheets"));
            sheetsService = new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, new HttpCredentialsAdapter(credentials))
                    .setApplicationName(APPLICATION_NAME)
                    .build();
        }
        return sheetsService;
    }


    public void writeToSheet(String spreadSheetId, String range, List<List<Object>> values) {
        try {
            Sheets service = getSheetsService();
            ValueRange body = new ValueRange().setValues(values);
            UpdateValuesResponse result = service.spreadsheets().values()
                    .update(spreadSheetId, range, body)
                    .setValueInputOption("USER_ENTERED")
                    .execute();
            log.info("Updated rows: {}", result.getUpdatedRows());
            Thread.sleep(1000);
        } catch (Exception e) {
            log.error("Failed to write data to the spreadsheet", e);
            throw new RuntimeException("Failed to write data to the spreadsheet: " + e.getMessage(), e);
        }
    }

    public List<List<Object>> readFromSheet(String spreadSheetId, String range) {
        try {
            Sheets service = getSheetsService(); // Sheets 서비스 객체 가져오기

//            // 스프레드시트 전체 데이터를 가져오기 (링크 포함)
//            Spreadsheet response = service.spreadsheets()
//                                        .get(spreadSheetId)
//                                        .setRanges(List.of(range))
//                                        .setFields("sheets(data(rowData(values(hyperlink,userEnteredValue))))") // 하이퍼링크 필드 요청
//                                        .execute();

            ValueRange response = service.spreadsheets()
                                        .values()
                                        .get(spreadSheetId, range)
                                        .execute();

            List<List<Object>> values = response.getValues();
            if (values == null || values.isEmpty()) {
                log.info("No data found in the range: {}", range);
            } else {
                log.info("Data retrieved from range {}: {}", range, values);
            }
            return values;
        } catch (Exception e) {
            log.error("Failed to read data from the spreadsheet", e);
            throw new RuntimeException("Failed to read data from the spreadsheet: " + e.getMessage(), e);
        }
    }

    public void writeBatchToSheet(String spreadsheetId, List<UpdateArea> updateAreaList) {
        try {
            Sheets service = getSheetsService(); // Sheets 서비스 객체 가져오기

            // 요청 생성
            List<Request> requests = new ArrayList<>();
            for (UpdateArea updateArea : updateAreaList) {
                requests.add(new Request()
                                .setUpdateCells(new UpdateCellsRequest()
                                                    .setRows(updateArea.getRowDataList())
                                                    .setRange(updateArea.areaToGridRange())
                                                    .setFields("userEnteredValue,hyperlink,userEnteredFormat"))
                );
            }

            BatchUpdateSpreadsheetRequest body = new BatchUpdateSpreadsheetRequest().setRequests(requests);

            // 요청 실행
            service.spreadsheets(). batchUpdate(spreadsheetId, body).execute();
            log.info("Batch write completed successfully.");
        } catch (Exception e) {
            log.error("Failed to write batch data to the spreadsheet", e);
            throw new RuntimeException("Batch write failed: " + e.getMessage(), e);
        }
    }
}