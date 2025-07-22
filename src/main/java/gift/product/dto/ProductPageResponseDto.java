package gift.product.dto;

import java.util.List;

public record ProductPageResponseDto(
    List<ProductGetResponseDto> content,
    Integer pageNumber,
    Integer pageSize,
    Long totalElements,
    Integer totalPages
) {

}