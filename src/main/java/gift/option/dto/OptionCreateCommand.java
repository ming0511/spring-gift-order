package gift.option.dto;

import gift.option.entity.OptionName;

public record OptionCreateCommand(
    OptionName name,
    Integer quantity
) {

}
