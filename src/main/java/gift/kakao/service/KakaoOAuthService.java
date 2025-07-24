package gift.kakao.service;

import java.util.Map;

public interface KakaoOAuthService {

    String getAuthorizationUrl(String scope);

    String getToken(String code);

    Map<String, Object> getUserProfile(String accessToken);
}
