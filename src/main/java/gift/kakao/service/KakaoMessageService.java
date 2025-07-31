package gift.kakao.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
public class KakaoMessageService implements MessageService {

    @Value("${kakao.kapi-host}")
    private String kapiHost;

    private final RestClient restClient;

    private final KakaoOAuthService kakaoOAuthService;

    public KakaoMessageService(RestClient restClient, KakaoOAuthService kakaoOAuthService) {
        this.restClient = restClient;
        this.kakaoOAuthService = kakaoOAuthService;
    }

    @Override
    public String createTextMessage(String userMessage) {
        String message =
            (userMessage != null && !userMessage.isBlank()) ? userMessage : "기본 메시지입니다.";

        return """
                {
                    "object_type": "text",
                    "text": "%s",
                    "link": {
                        "web_url": "http://localhost:8080",
                        "mobile_web_url": "http://localhost:8080"
                    }
                }
            """.formatted(message);
    }

    @Override
    public void sendTextMessage(String templateJson) {
        var url = kapiHost + "/v2/api/talk/memo/default/send";

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("template_object", templateJson);

        String token = kakaoOAuthService.provideAccessToken();

        try {
            restClient.post()
                .uri(url)
                .headers(headers -> headers.setBearerAuth(token))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .body(Void.class);

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                kakaoOAuthService.refreshAccessToken();

                restClient.post()
                    .uri(url)
                    .headers(
                        headers -> headers.setBearerAuth(kakaoOAuthService.provideAccessToken()))
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(body)
                    .retrieve()
                    .body(Void.class);
            }

            throw e;

        } catch (RestClientException e) {
            throw new RuntimeException("카카오 메시지 전송 중 오류 발생");
        }
    }
}
