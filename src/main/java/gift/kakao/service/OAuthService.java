package gift.kakao.service;

import java.util.Map;

public interface OAuthService {

    String getAuthorizationUrl(String scope);

    Boolean getToken(String code);

    Map<String, Object> getUserProfile();
}
