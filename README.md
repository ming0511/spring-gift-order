# spring-gift-order

## 주문하기

### 🚀 0단계 - 기본 코드 준비

- [x] 상품 고도화 코드 옮기기

### 🚀 1단계 - 카카오 로그인

카카오 로그인을 통해 인가 코드를 받고, 인가 코드를 사용해 토큰을 받은 후 향후 카카오 API 사용을 준비한다.

- [x] 카카오계정 로그인을 통해 인증 코드를 받는다.
- [x] 토큰 받기를 읽고 액세스 토큰을 추출한다.
- [x] 앱 키, 인가 코드가 절대 유출되지 않도록 한다.
    - [x] 특히 시크릿 키는 GitHub나 클라이언트 코드 등 외부에서 볼 수 있는 곳에 추가하지 않는다.
- (선택) 인가 코드를 받는 방법이 불편한 경우 카카오 로그인 화면을 구현한다.

#### 🛠 구현할 기능 목록

[ 카카오 로그인 API 참고 ](https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#request-token)

**서버 요청**

- [x] 인증 코드 받기

1. 서비스 서버가 카카오 인증 서버로 인가 코드 받기를 요청한다.(동의)
2. 카카오 인증서버는 서비스 서버의 리다이렉트 URI로 인가 코드를 전달한다.

    - [x] **Request**: GET `https://kauth.kakao.com/oauth/authorize`

      | 이름	| 타입	| 설명	| 필수 |
            |---|---|---|---|
      | client_id	| String	| 앱 REST API 키 |	O |
      | redirect_uri	| String	| 인가 코드를 전달받을 서비스 서버의 URI | O |
      | response_type	| String	| code로 고정	| O |
      | scope	| String	| 사용자에게 동의 요청할 동의항목 ID 목록	| X |

        - `https://kauth.kakao.com/oauth/authorize`
            - `?scope=talk_message`
            - `&response_type=code`
            - `&redirect_uri=http://localhost:8080`
            - `&client_id=${REST_API_KEY}`

- [x] **Response**
    ```http
    HTTP/1.1 302
    Content-Type: 0
    Location: ${REDIRECT_URI}?code=${AUTHORIZE_CODE}
    ```
    - 로그인 취소
        ```http
        HTTP/1.1 302
        Content-Length: 0
        Location: ${REDIRECT_URI}?error=access_denied&error_description=User%20denied%20access
        ```

- [x] 토큰 받기

1. 서비스 서버가 리다이렉트 URL로 전달받은 인가 코드로 토큰 받기를 요청한다.
2. 카카오 인증 서버가 토큰을 발급해 서비스 서버에 전달한다.

    - [x] **Request**: POST `https://kauth.kakao.com/oauth/token`
        ```http
        Content-Type: application/x-www-form-urlencoded;charset=utf-8
        ``` 
      | 이름 | 타입 | 설명 | 필수 |
            |---|---|---|---|
      | grant_type | String | authorization_code로 고정 | O |
      | client_id | String | 앱 REST API 키 | O |
      | redirect_uri | String | 인가 코드가 리다이렉트된 URI | O |
      | code | String | 인가 코드 받기 요청으로 얻은 인가 코드 | O |
      | client_secret | String | 토큰 발급 시, 보안을 강화하기 위해 추가 확인하는 코드 | X |

        ```
        curl -v -X POST "https://kauth.kakao.com/oauth/token" \
            -H "Content-Type: application/x-www-form-urlencoded;charset=utf-8" \
            -d "grant_type=authorization_code" \
            -d "client_id=${REST_API_KEY}" \
            --data-urlencode "redirect_uri=${REDIRECT_URI}" \
            -d "code=${AUTHORIZE_CODE}"
        ```

    - [x] **Response**
        ```http
        HTTP/1.1 200
        Content-Type: application/json;charset=UTF-8
        ```
        ```json
        {
            "token_type":"bearer",
            "access_token":"${ACCESS_TOKEN}",
            "expires_in":43199,
            "refresh_token":"${REFRESH_TOKEN}",
            "refresh_token_expires_in":5184000,
            "scope":"account_email profile"
        }
        ```
      | 이름	| 타입	| 설명	| 필수 |
            |---|---|---|---|
      | token_type	| String	| 토큰 타입, bearer로 고정 |	O |
      | access_token	| String |	사용자 액세스 토큰 값	| O |
      | expires_in	| Integer	| 액세스 토큰과 ID 토큰의 만료 시간(초)	| O |
      | refresh_token	| String	| 사용자 리프레시 토큰 값	| O |
      | refresh_token_expires_in	| Integer	| 리프레시 토큰 만료 시간(초)	| O |
      | scope	| String |	인증된 사용자의 정보 조회 권한 범위 | X |

- [ ] 사용자 로그인 처리

1. 서비스 서버가 발급받은 액세스 토큰으로 사용자 정보 가져오기를 요청해 사용자의 회원번호 및 정보를 조회하여 서비스 회원인지 확인한다.
2. 서비스 회원 정보 확인 결과에 따라 서비스 로그인 또는 회원 가입한다.
3. 이 외 서비스에서 필요한 로그인 절차를 수행한 후, 카카오 로그인한 사용자의 서비스 로그인 처리를 완료한다.

- [x] 사용자 정보 가져오기

    - [x] **Request**: GET/POST `https://kapi.kakao.com/v2/user/me`
      액세스 토큰 방식
        - 헤더

          | 이름	| 설명	| 필수 |
                    |---|---|---|
          | Authorization	| Authorization: Bearer ${ACCESS_TOKEN} | O |
          | Content-Type	| Content-Type: application/x-www-form-urlencoded;charset=utf-8 | O |

        - 쿼리 파라미터

          | 이름	| 타입	| 설명	| 필수 |
                    |---|---|---|---|
          | secure_resource |	Boolean	| 이미지 URL 값 HTTPS 여부, true 설정 시 HTTPS 사용, 기본 값 false	| X |
          | property_keys	| PropertyKeys[]	| Property 키 목록, JSON Array를 ["kakao_account.email"]과 같은 형식으로 사용 | X |

    - [x] **Response**: 성공, 모든 사용자 정보 포함
        - 일부 사용자 정보의 동의항목은 설정 권한 필요, 동의항목 참고

      | 이름	| 타입	| 설명	| 필수 |
            |---|---|---|---|
      |   id	| Long	| 회원번호	| O |