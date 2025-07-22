package gift.product.service;

import gift.product.dto.ProductCreateCommand;
import gift.product.dto.ProductGetResponseDto;
import gift.product.dto.ProductPageResponseDto;
import gift.product.dto.ProductUpdateCommand;
import gift.product.entity.Product;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    Product saveProduct(ProductCreateCommand dto);

    ProductPageResponseDto findAllProducts(Pageable pageable);

    ProductGetResponseDto findProductById(Long productId);

    void updateProduct(Long productId, ProductUpdateCommand dto);

    void deleteProduct(Long productId);
}
