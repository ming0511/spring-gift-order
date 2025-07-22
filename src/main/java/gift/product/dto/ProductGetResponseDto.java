package gift.product.dto;

public record ProductGetResponseDto(Long productId,
                                    String name,
                                    Double price,
                                    String imageUrl,
                                    Boolean mdConfirmed) {

}