package gift.kakao.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record KakaoUserProfile(
    @JsonIgnore
    Long id,

    String connectedAt,
    KakaoAccount kakaoAccount
) {

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record KakaoAccount(
        String email,
        Profile profile
    ) {

        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        public record Profile(
            String nickname,
            String profileImageUrl
        ) {

        }
    }
}
