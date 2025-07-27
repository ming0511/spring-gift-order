package gift.kakao.service;

import jakarta.servlet.http.HttpSession;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class KakaoOAuthService implements OAuthService {

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
        this.restClient = restClientBuilder
            .baseUrl("https://kauth.kakao.com")
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
    public Map<String, Object> getUserProfile() {
        var url = kapiHost + "/v2/user/me";

        try {
            Map<String, Object> response = restClient.get()
                .uri(url)
                .headers(headers -> {
                    headers.setBearerAuth(getAccessToken());
                    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                })
                .retrieve()
                .toEntity(Map.class)
                .getBody();

            return response;

        } catch (RestClientException e) {
            throw new RuntimeException("사용자 정보 요청 실패: " + e.getMessage(), e);
        }
    }
}
