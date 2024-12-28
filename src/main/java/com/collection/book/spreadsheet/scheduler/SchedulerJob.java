package com.collection.book.spreadsheet.scheduler;

import com.collection.book.spreadsheet.domain.UpdateArea;
import com.collection.book.spreadsheet.service.GoogleService;
import com.google.api.services.sheets.v4.model.CellData;
import com.google.api.services.sheets.v4.model.ExtendedValue;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.RowData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

import static com.collection.book.util.AladinApiUtil.getDetail;
import static com.collection.book.util.AladinApiUtil.getISBN;

@Slf4j
@DisallowConcurrentExecution
@Component
@RequiredArgsConstructor
public class SchedulerJob implements Job {

    @Value("${aladin.ttbkey}")
    private String TTBKEY;
    @Value("${spreadsheet.name}")
    private String SPREADSHEET_ID;
    @Value("${spreadsheet.sheetid}")
    private int SHEET_ID;

    @Value("${spreadsheet.booklisturl}")
    private String BOOKLIST_URL;
    @Value("${spreadsheet.bookdetailurl}")
    private String BOOKDETAIL_URL;
    @Value("${aladin.ttbkey}")
    private String ALADIN_TTBKEY;

    private final GoogleService googleService;

    private final int MAX_ROW = 1000;
    private final int BATCH_SIZE = 100;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
//        LocalTime now = LocalTime.now();
//        LocalTime start = LocalTime.of(8, 30);
//        LocalTime end = LocalTime.of(22, 0);
        // 특정시간대가 아니면 작업하지 말것
        //if (!now.isAfter(start) || !now.isBefore(end)) return;

        int time = 10;
        for (int t = 1; t <= time; t++) {
            String range = String.format("bookList!%s%d:%s%d", 'A', 1, 'A', 1);
            List<List<Object>> values = List.of(Collections.singletonList(String.format("업데이트\n카운트다운: %d", time - t)));
            googleService.writeToSheet(SPREADSHEET_ID, range, values);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        // 특정 영역을 읽어온다.
        String colList[] = {"키워드", "도서명"};

        char startCol = 'A';
        int colSize = colList.length;

        for (int startRow = 3; startRow <= MAX_ROW; startRow += BATCH_SIZE) {
            int end = Math.min(startRow + BATCH_SIZE - 1, MAX_ROW);

            String range = String.format("bookList!%s%d:%s%d", startCol, startRow, (char) (startCol + colSize), end);
            List<List<Object>> lists = googleService.readFromSheet(SPREADSHEET_ID, range);
            if (lists == null || lists.isEmpty()) break;

            log.info("lists.size: {}", lists.size());

            List<UpdateArea> updateAreaList = new ArrayList<>();
            for (int i = 0; i < lists.size(); i++) {
                List<Object> list = lists.get(i);
                log.info("list: {}", list);

                int absoluteRow = startRow + i;
                if (list.isEmpty()){
                    log.info("빈 레코드");
                    continue;
                }
                if (list.size() > 1){
                    log.info("이미 채워진 레코드");
                    continue;
                }


                Map<String, String> hm = new HashMap<String, String>();
                hm.put("ttbkey", ALADIN_TTBKEY);
                hm.put("QueryType", "Title");
                hm.put("SearchTarget", "Book");
                hm.put("Query", (String) list.get(0));
                hm.put("output", "js");
                hm.put("start", "1");
                hm.put("MaxResults", "20");
                hm.put("Version", "20131101");
                String isbn = getISBN(hm, BOOKLIST_URL);
                if (isbn == null || isbn.isBlank()) {
                    log.info("isbn이 비어있음");
                    List<CellData> cellList = new ArrayList<>();
                    //도서명
                    CellData cell = new CellData()
                            .setUserEnteredValue(new ExtendedValue().setStringValue("조회불가"));
                    cellList.add(cell);
                    RowData rowData = new RowData().setValues(cellList);
                    UpdateArea updateArea = UpdateArea.builder()
                            .sheetId(SHEET_ID)
                            .startRowIndex(absoluteRow - 1)
                            .rowSize(1)
                            .startColumnIndex(1) // 키워드 건너띄고 책제목 컬럼부터
                            .colSize(4)
                            .rowDataList(List.of(rowData))
                            .build();
                    log.info("updateArea: {}", updateArea);
                    updateAreaList.add(updateArea);
                    googleService.writeBatchToSheet(SPREADSHEET_ID, updateAreaList);
                    continue;
                }

                Map<String, Object> detail = null;
                hm = new HashMap<String, String>();
                hm.put("ttbkey", ALADIN_TTBKEY);
                hm.put("ItemIdType", "ISBN13");
                hm.put("ItemId", isbn);
                hm.put("Output", "js");
                hm.put("OptResult", "ratingInfo,bestSellerRank,previewImgList,authors,fulldescription,Toc,Story,categoryIdList,mdrecommend");
                detail = getDetail(hm, BOOKDETAIL_URL);

                if (detail == null) {
                    log.info("detail이 비어있음");

                    List<CellData> cellList = new ArrayList<>();
                    //도서명
                    CellData cell = new CellData()
                            .setUserEnteredValue(new ExtendedValue().setStringValue("조회불가"));
                    cellList.add(cell);
                    RowData rowData = new RowData().setValues(cellList);
                    UpdateArea updateArea = UpdateArea.builder()
                            .sheetId(SHEET_ID)
                            .startRowIndex(absoluteRow - 1)
                            .rowSize(1)
                            .startColumnIndex(1) // 키워드 건너띄고 책제목 컬럼부터
                            .colSize(4)
                            .rowDataList(List.of(rowData))
                            .build();
                    log.info("updateArea: {}", updateArea);
                    updateAreaList.add(updateArea);
                    googleService.writeBatchToSheet(SPREADSHEET_ID, updateAreaList);
                    continue;
                }


                List<Object> items = (List<Object>) detail.get("item");
                if (items.isEmpty()) {
                    log.info("items이 비어있음");

                    List<CellData> cellList = new ArrayList<>();
                    //도서명
                    CellData cell = new CellData()
                            .setUserEnteredValue(new ExtendedValue().setStringValue("조회불가"));
                    cellList.add(cell);
                    RowData rowData = new RowData().setValues(cellList);
                    UpdateArea updateArea = UpdateArea.builder()
                            .sheetId(SHEET_ID)
                            .startRowIndex(absoluteRow - 1)
                            .rowSize(1)
                            .startColumnIndex(1) // 키워드 건너띄고 책제목 컬럼부터
                            .colSize(4)
                            .rowDataList(List.of(rowData))
                            .build();
                    log.info("updateArea: {}", updateArea);
                    updateAreaList.add(updateArea);
                    googleService.writeBatchToSheet(SPREADSHEET_ID, updateAreaList);
                    continue;
                }

                Map<String, Object> item = (Map<String, Object>) items.get(0);
                String title = (String) item.get("title");
                String author = (String) item.get("author");
                String pubDate = (String) item.get("pubDate");
                String link = (String) item.get("link");
                Map<String, Object> bookInfo = (Map<String, Object>) item.get("bookinfo");
                String toc = Jsoup.parse((String) bookInfo.get("toc")).text();
                if (toc.length() > 50000) {
                    toc = toc.substring(0, 50000 - 10) + "...";
                }

                List<CellData> cellList = new ArrayList<>();
                //도서명
                CellData cell1 = new CellData()
                        .setUserEnteredValue(new ExtendedValue()
                                .setFormulaValue("=HYPERLINK(\"" + link + "\", \"" + title + "\")")
                        )
//                        .setUserEnteredFormat(new CellFormat()
//                                .setTextFormat(new TextFormat().setBold(true))
//                                .setBackgroundColor(new Color().setRed(0.8f).setGreen(0.9f).setBlue(1f)) // 연한 파란색 배경
//                        )
                        ;
                //저자
                CellData cell2 = new CellData()
                        .setUserEnteredValue(new ExtendedValue().setStringValue(author));
                //목차
                CellData cell3 = new CellData()
                        .setUserEnteredValue(new ExtendedValue().setStringValue(toc));
                //출판일
                CellData cell4 = new CellData()
                        .setUserEnteredValue(new ExtendedValue().setStringValue(pubDate));

                cellList.add(cell1);
                cellList.add(cell2);
                cellList.add(cell3);
                cellList.add(cell4);

                RowData rowData = new RowData().setValues(cellList);

                UpdateArea updateArea = UpdateArea.builder()
                        .sheetId(SHEET_ID)
                        .startRowIndex(absoluteRow - 1)
                        .rowSize(1)
                        .startColumnIndex(1) // 키워드 건너띄고 책제목 컬럼부터
                        .colSize(4)
                        .rowDataList(List.of(rowData))
                        .build();
                log.info("updateArea: {}", updateArea);
                updateAreaList.add(updateArea);
                googleService.writeBatchToSheet(SPREADSHEET_ID, updateAreaList);
            }
        }
    }
}
