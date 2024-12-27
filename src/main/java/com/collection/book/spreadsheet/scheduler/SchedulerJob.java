package com.collection.book.spreadsheet.scheduler;

import com.collection.book.spreadsheet.service.GoogleService;
import com.collection.book.util.HttpUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SchedulerJob implements Job {


    @Value("${spreadsheet.name}")
    private String SPREADSHEET_ID;
    @Value("${spreadsheet.booklisturl}")
    private String BOOKLIST_URL;
    @Value("${spreadsheet.bookdetailurl}")
    private String BOOKDETAIL_URL;
    @Value("${aladin.ttbkey}")
    private String ALADIN_TTBKEY;

    private final GoogleService googleService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
//        LocalTime now = LocalTime.now();
//        LocalTime start = LocalTime.of(8, 30);
//        LocalTime end = LocalTime.of(22, 0);
        // 특정시간대가 아니면 작업하지 말것
        //if (!now.isAfter(start) || !now.isBefore(end)) return;

        // 특정 영역을 읽어온다.
        String colList[] = {"키워드", "도서명", "저자", "목차", "출판일"};
        int colSize = colList.length;
        char startCol = 'A';
        char endCol = (char) ('A' + colSize);
        String range = String.format("bookList!%s%d:%s%d", startCol, 2, endCol, 100);

        List<List<Object>> lists = googleService.readFromSheet(SPREADSHEET_ID, range);

        for (List<Object> list : lists) {
            if (list.isEmpty() || list.size() == colSize) continue;

            String isbn = null;
            try {
                isbn = getISBN((String) list.get(0));
            } catch (IOException e) {
                log.error(e.getMessage());
                throw new RuntimeException(e);
            }
            if (isbn == null) continue;

            Map<String, Object> detail = null;
            try {
                detail = getDetail(isbn);
            } catch (IOException e) {
                log.error(e.getMessage());
                throw new RuntimeException(e);
            }

            List<Object> items = (List<Object>) detail.get("item");
            if (items.isEmpty()) continue;

            Map<String, Object> item = (Map<String, Object>) items.get(0);

            String title = (String) item.get("title");
            String author = (String) item.get("author");
            String pubDate = (String) item.get("pubDate");
            String link = (String) item.get("link");

            Map<String, Object> bookInfo = (Map<String, Object>) item.get("bookinfo");
            Document document = Jsoup.parse((String) bookInfo.get("toc"));

            list.add(String.format("=HYPERLINK(\"" + link + "\", \"" + title + "\")"));
            list.add(author);
            list.add(document.text());
            list.add(pubDate);
        }

        // 다시 쓴다. 이때 진행여부를 기록남겨둔다.
        googleService.writeToSheet(SPREADSHEET_ID, range, lists);
    }

    private String getISBN(String bookName) throws IOException {
        Map<String, String> hm = new HashMap<String, String>();
        hm.put("ttbkey", ALADIN_TTBKEY);
        hm.put("QueryType", "Title");
        hm.put("SearchTarget", "Book");
        hm.put("Query", bookName);
        hm.put("output", "js");
        hm.put("start", "1");
        hm.put("MaxResults", "20");
        hm.put("Version", "20131101");

        StringBuffer sb = new StringBuffer();
        for (String key : hm.keySet()) {
            String val = hm.get(key);
            sb.append(key).append("=").append(val).append("&");
        }

        log.info("목록API: {}", BOOKLIST_URL + sb.toString());

        String responseStr = HttpUtil.get(BOOKLIST_URL + sb.toString(), null);
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> response = objectMapper.readValue(responseStr, Map.class);
        List<Object> item = (List<Object>) response.get("item");
        if (item == null || item.isEmpty()) return null;

        Map<String, Object> book = (Map<String, Object>) item.get(0);
        return (String) book.get("isbn13");
    }

    private Map<String, Object> getDetail(String isbn) throws IOException {
        Map<String, String> hm = new HashMap<String, String>();

        hm.put("ttbkey", "ttbehdnjswls31002004");
        hm.put("ItemIdType", "ISBN13");
        hm.put("ItemId", isbn);
        hm.put("Output", "js");
        hm.put("OptResult", "ratingInfo,bestSellerRank,previewImgList,authors,fulldescription,Toc,Story,categoryIdList,mdrecommend");

        StringBuffer sb = new StringBuffer();
        for (String key : hm.keySet()) {
            String val = hm.get(key);
            sb.append(key).append("=").append(val).append("&");
        }

        log.info("상세정보API: {}", BOOKLIST_URL + sb);

        String responseStr = HttpUtil.get(BOOKDETAIL_URL + sb, null);
        String jsonStr = responseStr.substring(0, responseStr.length() - 1);
        jsonStr = jsonStr.replace("\\'", "'");
        jsonStr = jsonStr.replace("\\n", " ");
        jsonStr = jsonStr.replace("\t", "");

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(jsonStr, Map.class);
    }
}
