package gift.kakao.controller;

import gift.kakao.service.KakaoOAuthService;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class KakaoOAuthController {

    private final KakaoOAuthService kakaoOAuthService;

    public KakaoOAuthController(KakaoOAuthService kakaoOAuthService) {
        this.kakaoOAuthService = kakaoOAuthService;
    }

    // authorize?scope=talk_message
    @GetMapping("/authorize")
    public RedirectView authorize(@RequestParam(required = false) String scope) {
        return new RedirectView(kakaoOAuthService.getAuthorizationUrl(scope));
    }

    @GetMapping("/redirect")
    public ResponseEntity<?> kakaoRedirect(@RequestParam("code") String code) {
        String accessToken = kakaoOAuthService.getToken(code);
        return ResponseEntity.ok(Map.of("access_token", accessToken));
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String authHeader) {
        String accessToken = authHeader.replace("Bearer ", "");
        if (accessToken == null) {
            return ResponseEntity.status(401).body("Unauthorized: No access token in session");
        }

        Map<String, Object> profile = kakaoOAuthService.getUserProfile(accessToken);

        return ResponseEntity.ok(profile);
    }
}
