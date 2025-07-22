package gift.option.service;

import gift.exception.option.DuplicateOptionNameException;
import gift.exception.option.OptionNotFoundException;
import gift.exception.product.ProductMismatchException;
import gift.exception.product.ProductNotFoundException;
import gift.option.dto.OptionCreateCommand;
import gift.option.dto.OptionUpdateCommand;
import gift.option.entity.Option;
import gift.option.entity.OptionName;
import gift.option.repository.OptionRepository;
import gift.product.entity.Product;
import gift.product.repository.ProductRepository;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OptionServiceImpl implements OptionService {

    private final OptionRepository optionRepository;
    private final ProductRepository productRepository;

    public OptionServiceImpl(OptionRepository optionRepository,
        ProductRepository productRepository) {
        this.optionRepository = optionRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public Option addProductOption(Long productId, OptionCreateCommand dto) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException("해당 상품을 찾을 수 없습니다."));

        checkDuplicateOptionName(productId, dto.name());

        Option option = new Option(dto.name(), dto.quantity(), product);

        product.addOption(option);

        return optionRepository.save(option);
    }

    @Override
    public Set<Option> getProductOptions(Long productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException("해당 상품을 찾을 수 없습니다."));

        return product.getOptions();
    }

    @Override
    @Transactional
    public void updateProductOption(Long productId, OptionUpdateCommand dto) {

        Option option = optionRepository.findById(dto.optionId())
            .orElseThrow(() -> new OptionNotFoundException("해당 옵션을 찾을 수 없습니다."));

        Product product = option.getProduct();

        if (!product.getProductId().equals(productId)) {
            throw new ProductMismatchException("옵션에 저장된 상품과 전달받은 상품 ID가 일치하지 않습니다.");
        }

        if (!option.getName().equals(dto.name())) {
            checkDuplicateOptionName(productId, dto.name());
        }

        option.rename(dto.name());
        option.changeQuantity(dto.quantity());
    }

    @Override
    @Transactional
    public void deleteProductOption(Long productId, Long optionId) {

        Option option = optionRepository.findById(optionId)
            .orElseThrow(() -> new OptionNotFoundException("해당 옵션을 찾을 수 없습니다."));

        Product product = option.getProduct();

        if (!product.getProductId().equals(productId)) {
            throw new ProductMismatchException("옵션에 저장된 상품과 전달받은 상품 ID가 일치하지 않습니다.");
        }

        product.removeOption(option);
    }

    @Override
    public void subtractOptionQuantity(Long optionId, Integer amount) {
        Option option = optionRepository.findById(optionId)
            .orElseThrow(() -> new OptionNotFoundException("옵션을 찾을 수 없습니다."));

        option.subtractQuantity(amount);
    }

    public void checkDuplicateOptionName(Long productId, OptionName optionName) {
        Product product = productRepository.findWithOptionsById(productId)
            .orElseThrow(() -> new ProductNotFoundException("해당 상품을 찾을 수 없습니다."));

        boolean exists = product.getOptions().stream()
            .anyMatch(option -> option.getName().equals(optionName));

        if (exists) {
            throw new DuplicateOptionNameException("동일한 이름의 옵션이 이미 존재합니다: " + optionName);
        }
    }
}
