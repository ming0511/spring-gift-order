package gift.product.dto;

import gift.option.dto.OptionCreateResponseDto;
import java.util.List;

public record ProductCreateResponseDto(
    Long productId,
    String name,
    Double price,
    String imageUrl,
    Boolean mdConfirmed,
    List<OptionCreateResponseDto> options
) {

}