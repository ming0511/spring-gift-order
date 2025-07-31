package gift.kakao.controller;

import gift.kakao.dto.KakaoMessageRequestDto;
import gift.kakao.dto.KakaoUserProfile;
import gift.kakao.service.MessageService;
import gift.kakao.service.OAuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class KakaoOAuthController {

    private final OAuthService kakaoOAuthService;
    private final MessageService kakaoMessageService;

    public KakaoOAuthController(OAuthService kakaoOAuthService,
        MessageService kakaoMessageService) {
        this.kakaoOAuthService = kakaoOAuthService;
        this.kakaoMessageService = kakaoMessageService;
    }

    // authorize?scope=talk_message
    @GetMapping("/authorize")
    public RedirectView authorize(@RequestParam(required = false) String scope) {
        return new RedirectView(kakaoOAuthService.getAuthorizationUrl(scope));
    }

    @GetMapping("/redirect")
    public ResponseEntity<?> kakaoRedirect(@RequestParam("code") String code) {
        Boolean response = kakaoOAuthService.getToken(code);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        KakaoUserProfile profile = kakaoOAuthService.getUserProfile();

        return ResponseEntity.ok(profile);
    }

    @PostMapping("/message")
    public ResponseEntity<?> sendTextMessage(
        @Valid @RequestBody KakaoMessageRequestDto requestDto) {

        String message = requestDto.message();

        String templateJson = kakaoMessageService.createTextMessage(message);
        kakaoMessageService.sendTextMessage(templateJson);

        return ResponseEntity.ok().build();
    }
}
