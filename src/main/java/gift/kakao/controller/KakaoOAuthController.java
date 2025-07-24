package gift.kakao.controller;

import gift.kakao.service.KakaoOAuthService;
import org.springframework.web.bind.annotation.GetMapping;
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
}
