package gift.order.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderCreateRequestDto(
    @NotNull
    Long optionId,

    @NotNull
    @Positive
    Integer quantity,
    String message
) {

}
