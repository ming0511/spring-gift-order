package gift.product.dto;

public record ProductUpdateCommand(
    String name,
    Double price,
    String imageUrl,
    Boolean mdConfirmed) {

}