package gift.kakao.service;

public interface KakaoOAuthService {

    String getAuthorizationUrl(String scope);

    String getToken(String code);
}
