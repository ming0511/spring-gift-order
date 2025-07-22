package gift.product.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import gift.option.entity.Option;
import gift.product.builder.ProductBuilder;
import gift.product.entity.Product;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    void saveProduct() {
        Product expected = ProductBuilder.aProduct().build();
        Product actual = productRepository.save(expected);

        assertAll(
            () -> assertThat(actual.getProductId()).isNotNull(),
            () -> assertThat(actual.getName()).isEqualTo(expected.getName()),
            () -> assertThat(actual.getPrice()).isEqualTo(expected.getPrice()),
            () -> assertThat(actual.getImageUrl()).isEqualTo(expected.getImageUrl()),
            () -> assertThat(actual.getMdConfirmed()).isEqualTo(expected.getMdConfirmed())
        );
    }

    @Test
    void findAllProducts() {
        productRepository.save(ProductBuilder.aProduct().withName("one").build());
        productRepository.save(ProductBuilder.aProduct().withName("two").build());
        productRepository.save(ProductBuilder.aProduct().withName("three").build());

        List<Product> productList = productRepository.findAll();

        assertThat(productList).hasSize(3);
    }

    @Test
    void findProductById() {
        Product expected = ProductBuilder.aProduct().build();
        Product savedProduct = productRepository.save(expected);

        Product actual = productRepository.findById(savedProduct.getProductId()).get();
        assertAll(
            () -> assertThat(actual.getProductId()).isNotNull(),
            () -> assertThat(actual.getName()).isEqualTo(expected.getName()),
            () -> assertThat(actual.getPrice()).isEqualTo(expected.getPrice()),
            () -> assertThat(actual.getImageUrl()).isEqualTo(expected.getImageUrl()),
            () -> assertThat(actual.getMdConfirmed()).isEqualTo(expected.getMdConfirmed())
        );
    }

    @Test
    void deleteProduct() {
        Product expected = ProductBuilder.aProduct().build();
        Product savedProduct = productRepository.save(expected);

        productRepository.delete(savedProduct);

        assertThat(productRepository.findById(savedProduct.getProductId())).isEmpty();
    }

    @Test
    void 상품_옵션_추가() {
        // given
        Product product = ProductBuilder.aProduct().withName("product").build();

        // when
        Product savedProduct = productRepository.save(product);

        // then
        assertThat(savedProduct.getOptions()).hasSize(2);

        for (Option option : savedProduct.getOptions()) {
            assertThat(option.getProduct()).isEqualTo(savedProduct);
        }
    }
}
