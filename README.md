# Respal
이력서 리뷰 및 공유 사이트
- 이력서(Resume) 친구(Pal)를 의미하며, 이력서 커뮤니티에서 서로 도움을 주고받는 친구들을 의미하는 프로젝트명입니다.
- [자세히 보기](https://sang-kwon-yeum.notion.site/ResPal-57ca1c5a16a842e2a9f58fee43b94894?pvs=4)

# 프로젝트 인프라
![%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8%20%EA%B5%AC%EC%84%B1](https://github.com/AiliartsuaL2/respal/assets/89395238/c159e7c6-41b4-426f-b35e-970b345dd258)
# 데이터 모델링
![데이터모델링](https://github.com/AiliartsuaL2/respal/assets/89395238/7b78e41c-98c8-4ffb-8306-8f2fe5b91495)

## 기능 요구사항
- 회원
  - 공통 기능
    - 회원가입
      - 이메일
        - 일반 회원과 소셜 회원은 같은 이메일을 사용 할 수 없다.
        - 소셜 회원 간 이메일은 같을 수 있다.
    - 로그인
      - AccessToken, Refresh Token을 반환한다.
    - 로그아웃
      - refreshToken을 DB에서 제거한다.
  - 소셜 로그인 회원
    - 회원가입
      - 일반 회원 중, 동일한 email 확인 하여 예외처리 
      - UUID로 랜덤한 비밀번호 생성하여 가입
    - 로그인
      - 기존 회원인 경우
        - 앱 요청인경우
          - 커스텀 스킴 url로 redirect
        - 웹 요청인경우
          - 토큰 반환
      - 신규 회원인 경우
        - 일반 회원 중 중복된 email이 있는 경우
          - 예외처리
        - 중복되지 않은 email인 경우
          - uid를 return하여 회원가입 폼으로 이동
  - 일반 로그인 회원
    - 회원가입
      - 이메일 중복 체크(일반, 소셜)하여 예외처리
      - 설정한 비밀번호로 회원가입
    - 로그인
      - 해당 email에 해당하는 회원 확인하여 예외처리
      - 암호화된 비밀번호와 입력 비밀번호 확인하여 예외처리
      - 임시 비밀번호 상태 확인하고, 임시 비밀번호 상태 변경
      - 토큰 반환
    - 비밀번호 재설정
      - UUID를 통해 임시 비밀번호를 설정 후, 등록된 메일로 메일을 전송
      - 재설정된 비밀번호로 로그인을 한 경우, status값 확인하여 비밀번호 변경 알림을 보여줌
        - 알림은 딱 한 번만, 사용자 입장에선 임시 비밀번호로 로그인하는게 번거로움이니 비밀번호 변경에 대한 자율성을 부여
- 이력서
  - 공통 기능
    - 게시물을 열람한 사용자는 댓글을 달 수 있다.
    - 댓글은 피그마 형태로 x,y축에 보여지며, SSE 방식을 통해 실시간으로 렌더링 된다.
    - 본인 이력서 조회시 조회수가 증가되지 않는다.
  - 공용 이력서
    - 로그인, 비로그인 회원이 모두 열람 할 수 있다.
  - 비밀 이력서
    - 본인 혹은 태그되어있는 사용자만 열람 할 수 있다.
      - 태그되어있지 않은 사용자 열람시 예외처리
    - 다른 회원을 태그를 할 수 있다.

