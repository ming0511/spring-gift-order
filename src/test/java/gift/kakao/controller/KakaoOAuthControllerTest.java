package gift.kakao.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import gift.kakao.dto.KakaoMessageRequestDto;
import gift.kakao.service.KakaoMessageService;
import gift.kakao.service.OAuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;


@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class KakaoOAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OAuthService kakaoOAuthService;

    @MockBean
    private KakaoMessageService kakaoMessageService;

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

    @Test
    void sendTextMessage() throws Exception {
        KakaoMessageRequestDto requestDto = new KakaoMessageRequestDto("default");

        String mockTemplateJson = """
                {
                  "object_type": "text",
                  "text": "default",
                  "link": {
                    "web_url": "http://localhost:8080",
                    "mobile_web_url": "http://localhost:8080"
                  }
                }
            """;

        when(kakaoMessageService.createTextMessage("default")).thenReturn(mockTemplateJson);
        doNothing().when(kakaoMessageService).sendTextMessage(mockTemplateJson);

        // when & then
        mockMvc.perform(post("/message")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "message": "default"
                        }
                    """))
            .andExpect(status().isOk());

        // verify internal calls
        verify(kakaoMessageService).createTextMessage("default");
        verify(kakaoMessageService).sendTextMessage(mockTemplateJson);
    }

    @Test
    void sendTextMessage_BAD_REQEUST() throws Exception {
        String longMessage = "a".repeat(201);
        String jsonRequest = String.format("""
            {
              "message": "%s"
            }
            """, longMessage);

        // when & then
        mockMvc.perform(post("/message")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
            .andExpect(status().isBadRequest());
    }


}