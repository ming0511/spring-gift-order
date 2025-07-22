package gift.wish.dto;

import java.time.LocalDateTime;

public record WishGetResponseDto(
    Long wishId,
    Long productId,
    String productName,
    LocalDateTime createdDate
) {

}
