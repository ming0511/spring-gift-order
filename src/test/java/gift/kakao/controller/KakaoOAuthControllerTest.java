package gift.kakao.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import gift.kakao.service.OAuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.web.servlet.MockMvc;


@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
class KakaoOAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OAuthService kakaoOAuthService;

    @Test
    void authorize() throws Exception {
        var scope = "talk_message";
        var expectedUrl = "https://kauth.kakao.com/oauth/authorize?scope=talk_message";

        when(kakaoOAuthService.getAuthorizationUrl(scope)).thenReturn(expectedUrl);

        mockMvc.perform(get("/authorize").param("scope", scope))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl(expectedUrl));
    }

    @Test
    void kakaoRedirect() throws Exception {
        var code = "code";

        when(kakaoOAuthService.getToken(code)).thenReturn(true);

        mockMvc.perform(get("/redirect").param("code", code))
            .andExpect(status().isOk())
            .andExpect(content().string("true"));
    }

    @Test
    void kakaoRedirect_exception() throws Exception {
        var code = "code";

        when(kakaoOAuthService.getToken(code))
            .thenThrow(new RuntimeException("토큰 요청 실패"));

        mockMvc.perform(get("/redirect").param("code", code))
            .andExpect(status().is5xxServerError());
    }

    @Test
    void getProfile() throws Exception {
        mockMvc.perform(get("/profile"))
            .andExpect(status().isOk())
            .andExpect(content().string("true"));
    }

    @Test
    void getProfile_exception() throws Exception {
        when(kakaoOAuthService.getUserProfile())
            .thenThrow(new RuntimeException("사용자 정보 요청 실패"));

        mockMvc.perform(get("/profile"))
            .andExpect(status().is5xxServerError());
    }


}