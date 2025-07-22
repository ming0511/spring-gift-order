package gift.wish.dto;

import java.time.LocalDateTime;

public record WishCreateResponseDto(
    Long wishId,
    Long memberId,
    Long productId,
    LocalDateTime createdDate
) {

}
