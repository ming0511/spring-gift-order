package gift.kakao.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext
class KakaoOAuthServiceTest {

    @Autowired
    private OAuthService kakaoOAuthService;

    @Test
    void getAuthorizationUrl() {
        String authUrl = kakaoOAuthService.getAuthorizationUrl("talk_message");
        System.out.println(authUrl);
    }
}