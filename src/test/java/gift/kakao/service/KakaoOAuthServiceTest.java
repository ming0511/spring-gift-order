package gift.kakao.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext
class KakaoOAuthServiceTest {

    @Autowired
    private OAuthService kakaoOAuthService;

    @Autowired
    private KakaoMessageService kakaoMessageService;

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession().setAttribute("accessToken", "mock-access-token");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test
    void getAuthorizationUrl() {
        var scope = "talk_message";
        var authUrl = kakaoOAuthService.getAuthorizationUrl(scope);

        assertTrue(authUrl.contains("client_id=" + clientId));
        assertTrue(authUrl.contains("redirect_uri=" + redirectUri));
        assertTrue(authUrl.contains("response_type=code"));
        assertTrue(authUrl.contains("scope=" + scope));

        System.out.println(authUrl);
    }

    @Test
    void createTextMessage() {
        String messageJson = kakaoMessageService.createTextMessage("Hello World!");
        System.out.println(messageJson);
    }
}