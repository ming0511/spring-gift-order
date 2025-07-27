package gift.kakao.controller;

import gift.kakao.service.OAuthService;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class KakaoOAuthController {

    private final OAuthService kakaoOAuthService;

    public KakaoOAuthController(OAuthService kakaoOAuthService) {
        this.kakaoOAuthService = kakaoOAuthService;
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
        Map<String, Object> profile = kakaoOAuthService.getUserProfile();

        return ResponseEntity.ok(true);
    }
}
