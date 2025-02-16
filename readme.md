## 서비스 개요
1. 구글SpreadSheet에 정보를 입력.
   - 책 제목, 검색기준
2. 책 정보를 서비스가 조회하여 구글SpreadSheet에 기록한다
   - 도서명, 저자, 목차, 출판일

## 기술
1. Spring Boot (MVC, Quartz)
2. 온라인서점 알라딘 도서API
3. 구글SpreadSheet API

<a href="https://docs.google.com/spreadsheets/d/1sGkBKUzV3Of8K787DeHcUaGrbDe_9U67o7FIlnkLdSk/edit?gid=0#gid=0" target="_blank">책정보 조회서비스로 이동</a>

![Animation](https://github.com/user-attachments/assets/0030a15c-2196-4fa6-96d9-02033c85f84a)

## 이후 개발할 기능
- 목차에서 다루는 기술만 정리하여 기술목록을 표현하는 배열만들기
- 채용공고가 주어졌을 때
  - 구글 스프레드시트에 등록된 도서 중 권장할 만한 도서목록을 우선순위 대로 정렬해서 보여준다.

## 서버실행
```bash
nohup java -jar book-collection-0.0.1-SNAPSHOT.jar > /dev/null 2>&1 &

netstat -tulnp | grep 8080
lsof -i:8080
kill -9 {pid}
```
