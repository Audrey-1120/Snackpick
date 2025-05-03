# Snackpick - 편의점 식품 리뷰 사이트
  <img src="https://github.com/user-attachments/assets/c98a91ba-b954-40aa-948b-3a1f529336b6" width="100" height="100"/>  
  
# ☑️버전 정보
✅ ver 1.0 (최초 배포)
- 로그인/회원가입
- 제품 검색
- 리뷰 CRUD

✅ ver 1.1 (배포 완료)
- 제품 검색 로직 개선
- 코드 리팩토링

✅ ver 1.2 (배포 완료)
- 댓글 CRUD

✅ ver 1.3 (배포 완료)
- 마이페이지

🚧 ver 1.4 (추후 배포 예정)
- 모바일 UI/UX

# ☑️ 사이트
- **배포주소** : http://www.snackpick.site
- **테스트 계정**
  - 아이디 : monami1908
  - 비밀번호 : Asdfg12345@
- **확인 방법**
    - 리뷰가 존재하는 상품을 확인해주세요!
    - 예시 상품 : 이클립스 페퍼민트향
  
# ☑️ 개요
- **프로젝트 명 : Snackpick**
- **개발내용**
    - 편의점 식품 리뷰 사이트
- **기간** : 2025-01-25 ~
- **인원** : 1명
- **개발환경**

| 구분         | 내용                                                                 |
|--------------|----------------------------------------------------------------------|
| 백엔드       | Java 17                                                              |
| 프론트엔드   | HTML5, CSS3, JavaScript                                              |
| 프레임워크   | Spring Boot 3.3.7, Spring Data JPA, Spring Security, Bootstrap       |
| 라이브러리   | jQuery, Axios, Thymeleaf                                             |
| API          | Kakao Map API                                                        |
| 데이터베이스 | MariaDB                                                              |
| 개발 환경    | IntelliJ IDEA Ultimate, Windows 11, WSL2                             |
| 협업/툴      | Git, GitHub, DBeaver                                                 |
| 배포         | Docker, AWS (EC2)                                                          |

<br>

# ☑️ 구현 기능  
<br>

## 회원가입 및 로그인
### 1) 로그인
![Image](https://github.com/user-attachments/assets/0cfcadb6-5d27-404f-a377-f4d1c53d3503)
<br>
<br>
<br>
<br>
<br>
<br>


### 2) 회원가입
![Image](https://github.com/user-attachments/assets/3efbdfcb-6412-48d5-a050-24e753434509)
<br>
<br>
<br>
<br>
<br>
<br>


## 리뷰 및 제품 검색
### 3) 제품 검색
![Image](https://github.com/user-attachments/assets/abc135d2-d25e-49cb-99f4-2fb16a4172ea)
- 원하는 제품을 제품명으로 검색할 수 있습니다.
<br>
<br>
<br>
<br>
<br>
<br>

### 4) 리뷰 목록 조회
![Image](https://github.com/user-attachments/assets/7fdb36c8-59f8-49a9-bf3a-b757fe0b10ee)
- 왼쪽에는 리뷰에 해당하는 제품의 상세 정보(카테고리, 리뷰 별점 평균)
- 리뷰를 최신순, 맛 별점 높은 순, 가격 별점 높은 순으로 정렬해서 조회할 수 있습니다.
<br>
<br>
<br>
<br>
<br>
<br>

### 5) 리뷰 상세 조회
![Image](https://github.com/user-attachments/assets/05794add-f924-4239-8362-fe172c613e6a)
- 리뷰를 클릭하면 리뷰의 상세 데이터 및 댓글을 조회할 수 있습니다.
<br>
<br>
<br>
<br>
<br>
<br>

### 6) 리뷰 작성
![Image](https://github.com/user-attachments/assets/77fa6df9-45ce-4916-904d-52930fdb72ef)
- 원하는 제품의 맛 별점, 가격 별점, 구매 위치를 지정할 수 있습니다.
- 첨부하는 이미지 중 대표 이미지를 설정할 수 있습니다.
<br>
<br>
<br>
<br>
<br>
<br>

### 7) 새 제품 추가
![Image](https://github.com/user-attachments/assets/399ff8da-4e07-4616-a463-dd6fbc264a81)
- 리뷰 작성 시 찾는 제품이 없을 경우 직접 제품을 추가할 수 있습니다.
- 제품을 직접 추가할 시 카테고리를 직접 선택할 수 있습니다.
<br>
<br>
<br>
<br>
<br>
<br>

### 8) 리뷰 수정
![Image](https://github.com/user-attachments/assets/5477ae58-5e3f-471c-838e-2c01ef1a9993)
- 리뷰를 수정할 수 있으며, 대표 이미지 여부를 수정할 경우 리뷰 목록에서 보이는 이미지가 변경됩니다.
<br>
<br>
<br>
<br>
<br>
<br>

### 9) 리뷰 삭제
![Image](https://github.com/user-attachments/assets/bac31022-417e-416c-a775-de0cfbf43a68)
- 원하는 리뷰를 삭제할 수 있습니다.
<br>
<br>
<br>
<br>
<br>
<br>

## 댓글
### 10) 댓글 및 대댓글 작성
![Image](https://github.com/user-attachments/assets/91955bc0-1249-40d5-ac73-cce7b2fddd40)
- 댓글 및 대댓글을 작성할 수 있습니다.
- 대댓글의 경우 댓글달기 버튼을 클릭하여 원하는 댓글을 선택한 후 작성할 수 있습니다.
- 댓글을 작성하면 맨 밑으로 스크롤이 이동합니다.
<br>
<br>
<br>
<br>
<br>
<br>

### 11) 댓글 수정 및 삭제
![Image](https://github.com/user-attachments/assets/f3445d4a-d011-4d2b-9b7f-e624966444db)
- 댓글을 수정하고 삭제할 수 있습니다.
<br>
<br>
<br>
<br>
<br>
<br>

## 마이페이지
### 12) 정보 조회
![Image](https://github.com/user-attachments/assets/2660ceb6-4e04-4852-9433-96be8c0509eb)
- 기본적인 회원 정보를 조회할 수 있습니다.
- 오른쪽에는 작성한 리뷰 및 댓글을 확인할 수 있습니다.
<br>
<br>
<br>
<br>
<br>
<br>

### 14) 회원별 작성한 리뷰, 댓글 조회 및 수정
![Image](https://github.com/user-attachments/assets/32265644-717e-41ec-a6e7-b43791998132)
- 회원별로 작성한 리뷰 및 댓글을 목록 및 상세 조회할 수 있습니다.
- 작성한 리뷰와 댓글을 수정하거나 삭제할 수 있습니다.
<br>
<br>
<br>
<br>
<br>
<br>

### 13) 회원 정보 수정
![Image](https://github.com/user-attachments/assets/6450a378-c384-46fc-9954-9b622b5294dd)
- 정보 수정 페이지로 접속하기 전에 비밀번호를 입력받아 사용자 검증을 진행합니다.
- 프로필 이미지, 이름, 닉네임을 수정할 수 있습니다.
<br>
<br>
<br>
<br>
<br>
<br>

### 14) 비밀번호 재설정
![Image](https://github.com/user-attachments/assets/f2d4f035-41bb-424e-9409-98137f3a1ae7)
- 비밀번호를 재설정할 수 있습니다.
- 아래와 같은 경우에는 경고 문구가 표시됩니다.
  - 현재 비밀번호가 틀린 경우
  - 새 비밀번호가 유효성 검사를 통과하지 못할 경우
  - 새 비밀번호와 새 비밀번호 확인란의 값이 일치하지 않는 경우
  - 현재 비밀번호와 새 비밀번호가 같은 경우
<br>
<br>
<br>
<br>
<br>

### 15) 마이페이지 - 회원탈퇴
![Image](https://github.com/user-attachments/assets/b07d6f85-b42c-4cf8-9f20-f764f6f95ad7)
- 정해진 문구를 입력하면 회원탈퇴가 진행됩니다.

