package gift.kakao.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoUserProfile(
    @JsonProperty("id")
    Long id,

    @JsonProperty("connected_at")
    String connectedAt,

    @JsonProperty("kakao_account")
    KakaoAccount kakaoAccount
) {

    public record KakaoAccount(
        @JsonProperty("email")
        String email,

        @JsonProperty("profile")
        Profile profile
    ) {

        public record Profile(
            @JsonProperty("nickname")
            String nickname,

            @JsonProperty("profile_image_url")
            String profileImageUrl
        ) {

        }
    }
}
