package gift.kakao.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record KakaoMessageRequestDto(
    @NotNull
    @Size(max = 200)
    String message
) {

}
