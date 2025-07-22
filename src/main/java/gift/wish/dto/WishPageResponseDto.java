package gift.wish.dto;

import java.util.List;

public record WishPageResponseDto(
    List<WishGetResponseDto> content,
    Integer pageNumber,
    Integer pageSize,
    Long totalElements,
    Integer totalPages
) {

}
