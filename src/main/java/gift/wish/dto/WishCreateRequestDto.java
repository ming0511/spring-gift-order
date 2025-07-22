package gift.wish.dto;

import jakarta.validation.constraints.NotNull;

public record WishCreateRequestDto(
    @NotNull(message = "상품 ID는 필수 입력 항목입니다.")
    Long productId
) {

}
