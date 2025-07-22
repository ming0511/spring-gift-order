package gift.product.dto;

import gift.option.dto.OptionCreateCommand;
import java.util.Set;

public record ProductCreateCommand(
    String name,
    Double price,
    String imageUrl,
    Boolean mdConfirmed,
    Set<OptionCreateCommand> options
) {

}