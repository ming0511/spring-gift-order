package gift.order.dto;

import java.time.LocalDateTime;

public record OrderCreateResponseDto(
    Long id,
    Long optionId,
    Integer quantity,
    LocalDateTime orderDateTime,
    String message
) {

}
