package gift.product.builder;

import gift.option.entity.Option;
import gift.option.entity.OptionName;
import gift.product.entity.Product;
import java.util.HashSet;
import java.util.Set;

public class ProductBuilder {

    private String name = "기본 상품";
    private Double price = 4500.0;
    private String imageUrl = "http://default.img";
    private Boolean mdConfirmed = false;
    private Set<Option> options = new HashSet<>(Set.of(
        new Option(new OptionName("default"), 5),
        new Option(new OptionName("default2"), 10)
    ));

    private ProductBuilder() {

    }

    public static ProductBuilder aProduct() {
        return new ProductBuilder();
    }

    public ProductBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public ProductBuilder withPrice(Double price) {
        this.price = price;
        return this;
    }

    public ProductBuilder withImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public ProductBuilder withMdConfirmed(Boolean mdConfirmed) {
        this.mdConfirmed = mdConfirmed;
        return this;
    }

    public ProductBuilder withOptions(Set<Option> options) {
        this.options = options;
        return this;
    }

    public Product build() {

        Product product = new Product(name, price, imageUrl, mdConfirmed);

        product.addOptions(options);

        return product;
    }

    public String getName() {
        return name;
    }

    public Double getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Boolean getMdConfirmed() {
        return mdConfirmed;
    }

    public Set<Option> getOptions() {
        return options;
    }
}