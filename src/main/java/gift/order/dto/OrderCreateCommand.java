package gift.order.dto;

public record OrderCreateCommand(
    Long optionId,
    Integer quantity,
    String message
) {

}
