package gift.option.repository;

import static org.assertj.core.api.Assertions.assertThat;

import gift.option.entity.Option;
import gift.option.entity.OptionName;
import gift.product.builder.ProductBuilder;
import gift.product.entity.Product;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class OptionRepositoryTest {

    @Autowired
    private OptionRepository optionRepository;

    @Test
    void addProductOption() {
        // given
        Product product = ProductBuilder.aProduct().withName("product").build();

        OptionName test = new OptionName("test");

        Option option = new Option(test, 10, product);

        product.addOption(option);
        // when
        Option savedOption = optionRepository.save(option);

        // then
        assertThat(savedOption.getOptionId()).isNotNull();
        assertThat(savedOption.getName()).isEqualTo(test);
        assertThat(savedOption.getQuantity()).isEqualTo(10);
        assertThat(savedOption.getProduct()).isEqualTo(product);

        assertThat(product.getOptions()).contains(savedOption);
    }

    @Test
    void getProductOptions() {
        // given
        Product product = ProductBuilder.aProduct().withName("product").build();

        OptionName test = new OptionName("test");

        Option option = new Option(test, 10, product);

        product.addOption(option);

        Option savedOption = optionRepository.save(option);

        // when
        Set<Option> options = product.getOptions();

        // then
        options.forEach(opt -> {
            System.out.println("옵션 이름: " + opt.getName());
            System.out.println("옵션 수량: " + opt.getQuantity());
        });
    }

    @Test
    void updateProductOption() {
        // given
        Product product = ProductBuilder.aProduct().withName("product").build();

        OptionName test = new OptionName("test");

        Option option = new Option(test, 10, product);

        product.addOption(option);

        Option savedOption = optionRepository.save(option);

        OptionName changedTest = new OptionName("changedTest");

        // when
        savedOption.rename(changedTest);

        Option updatedOption = optionRepository.save(savedOption);

        // then
        Set<Option> options = product.getOptions();

        options.forEach(opt -> {
            System.out.println("옵션 이름: " + opt.getName());
            System.out.println("옵션 수량: " + opt.getQuantity());
        });
    }

    @Test
    void deleteProductOption() {
        // given
        Product product = ProductBuilder.aProduct().withName("product").build();

        OptionName test = new OptionName("test");

        Option option = new Option(test, 10, product);

        product.addOption(option);

        Option savedOption = optionRepository.save(option);

        // when
        optionRepository.delete(savedOption);

        product.removeOption(option);

        // then
        Set<Option> options = product.getOptions();

        options.forEach(opt -> {
            System.out.println("옵션 이름: " + opt.getName());
            System.out.println("옵션 수량: " + opt.getQuantity());
        });
    }

    @Test
    void subtractOptionQuantity() {
        // given
        Product product = ProductBuilder.aProduct().withName("product").build();

        OptionName test = new OptionName("test");

        Option option = new Option(test, 10, product);

        product.addOption(option);

        Option savedOption = optionRepository.save(option);

        // when
        option.subtractQuantity(5);

        // then
        Set<Option> options = product.getOptions();

        options.forEach(opt -> {
            System.out.println("옵션 이름: " + opt.getName());
            System.out.println("옵션 수량: " + opt.getQuantity());
        });
    }
}
