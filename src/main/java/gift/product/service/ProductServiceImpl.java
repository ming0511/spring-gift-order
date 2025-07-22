package gift.product.service;

import gift.exception.product.ProductNotFoundException;
import gift.option.entity.Option;
import gift.product.dto.ProductCreateCommand;
import gift.product.dto.ProductGetResponseDto;
import gift.product.dto.ProductPageResponseDto;
import gift.product.dto.ProductUpdateCommand;
import gift.product.entity.Product;
import gift.product.repository.ProductRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product saveProduct(ProductCreateCommand dto) {

        Boolean mdConfirmed = dto.name().contains("카카오") ? dto.mdConfirmed() : false;

        Product product = new Product(dto.name(), dto.price(), dto.imageUrl(), mdConfirmed);

        product.validate();

        Set<Option> options = dto.options().stream()
            .map(option -> new Option(option.name(), option.quantity(), product))
            .collect(Collectors.toSet());

        product.addOptions(options);

        return productRepository.save(product);
    }

    @Override
    public ProductPageResponseDto findAllProducts(Pageable pageable) {

        Page<Product> Products = productRepository.findAll(pageable);

        List<ProductGetResponseDto> content = Products.getContent().stream()
            .map(product -> new ProductGetResponseDto(
                product.getProductId(),
                product.getName(),
                product.getPrice(),
                product.getImageUrl(),
                product.getMdConfirmed()))
            .collect(Collectors.toList());

        return new ProductPageResponseDto(
            content,
            Products.getNumber(),
            Products.getSize(),
            Products.getTotalElements(),
            Products.getTotalPages());
    }

    @Override
    public ProductGetResponseDto findProductById(Long productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException("존재하지 않는 상품입니다."));

        return new ProductGetResponseDto(product.getProductId(), product.getName(),
            product.getPrice(), product.getImageUrl(), product.getMdConfirmed());
    }

    @Override
    @Transactional
    public void updateProduct(Long productId, ProductUpdateCommand dto) {
        Boolean mdConfirmed = dto.name().contains("카카오") ? dto.mdConfirmed() : false;

        Product product = new Product(productId, dto.name(), dto.price(), dto.imageUrl(),
            mdConfirmed);

        product.validate();

        update(productId, product);
    }

    @Override
    public void deleteProduct(Long productId) {
        productRepository.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException("존재하지 않는 상품입니다."));

        productRepository.deleteById(productId);
    }

    public void update(Long id, Product product) {
        Product foundProduct = productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException("존재하지 않는 상품입니다."));

        foundProduct.rename(product.getName());
        foundProduct.updatePrice(product.getPrice());
        foundProduct.updateImageUrl(product.getImageUrl());
        foundProduct.updateMdConfirmed(product.getMdConfirmed());
    }
}
