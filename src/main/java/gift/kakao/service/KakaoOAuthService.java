package gift.kakao.service;

import gift.kakao.dto.KakaoUserProfile;
import jakarta.servlet.http.HttpSession;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class KakaoOAuthService implements OAuthService, KakaoMessageService {

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.kauth-host}")
    private String kauthHost;

    @Value("${kakao.kapi-host}")
    private String kapiHost;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    private final RestClient restClient;

    public KakaoOAuthService(RestClient.Builder restClientBuilder) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout((int) Duration.ofSeconds(5).toMillis());
        requestFactory.setReadTimeout((int) Duration.ofSeconds(3).toMillis());

        this.restClient = restClientBuilder
            .baseUrl("https://kauth.kakao.com")
            .requestFactory(requestFactory)
            .build();
    }

    private HttpSession getSession() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return attributes.getRequest().getSession();
    }

    private void saveAccessToken(String accessToken) {
        getSession().setAttribute("access_token", accessToken);
    }

    private String getAccessToken() {
        return (String) getSession().getAttribute("access_token");
    }

    @Override
    public String getAuthorizationUrl(String scope) {
        return UriComponentsBuilder
            .fromHttpUrl(kauthHost + "/oauth/authorize")
            .queryParam("client_id", clientId)
            .queryParam("redirect_uri", redirectUri)
            .queryParam("response_type", "code")
            .queryParamIfPresent("scope", scope != null ? Optional.of(scope) : Optional.empty())
            .build()
            .toUriString();
    }

    @Override
    public Boolean getToken(String code) {
        var url = kauthHost + "/oauth/token";

        var body = new LinkedMultiValueMap<String, String>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);

        try {
            Map<String, Object> response = restClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .body(Map.class);

            saveAccessToken((String) response.get("access_token"));
            return true;

        } catch (RestClientException e) {
            throw new RuntimeException("토큰 요청 실패: " + e.getMessage(), e);
        }
    }

    @Override
    public KakaoUserProfile getUserProfile() {
        var url = kapiHost + "/v2/user/me";

        try {
            KakaoUserProfile response = restClient.get()
                .uri(url)
                .headers(headers -> {
                    headers.setBearerAuth(getAccessToken());
                    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                })
                .retrieve()
                .toEntity(KakaoUserProfile.class)
                .getBody();

            return response;

        } catch (RestClientException e) {
            throw new RuntimeException("사용자 정보 요청 실패: " + e.getMessage(), e);
        }
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

        try {
            restClient.post()
                .uri(url)
                .headers(headers -> headers.setBearerAuth(getAccessToken()))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .body(Void.class);

        } catch (RestClientException e) {
            throw new RuntimeException("카카오 메시지 전송 중 오류 발생");
        }
    }

}
