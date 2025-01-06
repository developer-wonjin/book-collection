# 서비스 목표
- 구글SpreadSheet에 아래 정보를 입력한다.
  - 책 제목 or ISBN넘버
- 책 목차를 통해 다루고 있는 기술 정리하여 엑셀칸에 적어준다. 
-  책 마다 커버하는 기술들이 모두 다르다.
- 책들 간의 커버하는 기술의 공통점, 차이점을 볼 수 있게 만든다.

<a href="https://docs.google.com/spreadsheets/d/1sGkBKUzV3Of8K787DeHcUaGrbDe_9U67o7FIlnkLdSk/edit?gid=0#gid=0" target="_blank">책정보 조회서비스로 이동</a>

![Animation](https://github.com/user-attachments/assets/0030a15c-2196-4fa6-96d9-02033c85f84a)

# 추후 개발할 것들
- 채용공고가 주어졌을 때
  - 구글 스프레드시트에 등록된 도서 중 권장할 만한 도서목록을 우선순위 대로 정렬해서 보여준다.

# 서버실행 명령어
```bash
nohup java -jar book-collection-0.0.1-SNAPSHOT.jar > /dev/null 2>&1 &

netstat -tulnp | grep 8080
lsof -i:8080
kill -9 {pid}
```