package gift.option.dto;

import gift.option.entity.OptionName;

public record OptionUpdateCommand(
    Long optionId,
    OptionName name,
    Integer quantity
) {

}
