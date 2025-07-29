package gift.kakao.service;

import gift.kakao.dto.KakaoUserProfile;

public interface OAuthService {

    String getAuthorizationUrl(String scope);

    Boolean getToken(String code);

    KakaoUserProfile getUserProfile();
}
