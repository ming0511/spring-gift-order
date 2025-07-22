package gift.option.service;

import gift.option.dto.OptionCreateCommand;
import gift.option.dto.OptionUpdateCommand;
import gift.option.entity.Option;
import java.util.Set;

public interface OptionService {

    Option addProductOption(Long productId, OptionCreateCommand dto);

    Set<Option> getProductOptions(Long productId);

    void updateProductOption(Long productId, OptionUpdateCommand dto);

    void deleteProductOption(Long productId, Long optionId);

    void subtractOptionQuantity(Long optionId, Integer amount);
}
