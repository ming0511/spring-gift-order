package gift.product.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import gift.exception.product.UnapprovedProductException;
import gift.option.entity.Option;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private Boolean mdConfirmed;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference
    private Set<Option> options;

    protected Product() {

    }

    public Product(String name, Double price, String imageUrl, Boolean mdConfirmed) {
        this(null, name, price, imageUrl, mdConfirmed, null);
    }

    public Product(Long productId, String name, Double price, String imageUrl,
        Boolean mdConfirmed) {
        this(productId, name, price, imageUrl, mdConfirmed, null);
    }

    public Product(Long productId, String name, Double price, String imageUrl,
        Boolean mdConfirmed, Set<Option> options) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.mdConfirmed = mdConfirmed;
        this.options = (options != null) ? options : new HashSet<>();
    }

    public Long getProductId() {
        return productId;
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

    public void rename(String name) {
        this.name = name;
    }

    public void updatePrice(Double price) {
        this.price = price;
    }

    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void updateMdConfirmed(Boolean mdConfirmed) {
        this.mdConfirmed = mdConfirmed;
    }

    public void validate() {
        boolean containsKakao = name != null && name.contains("카카오");
        boolean confirmed = mdConfirmed != null && mdConfirmed;

        if (containsKakao && !confirmed) {
            throw new UnapprovedProductException(
                "협의되지 않은 '카카오'가 포함된 상품명은 사용할 수 없습니다.");
        }
    }

    public void addOptions(Set<Option> options) {
        for (Option option : options) {
            addOption(option);
        }
    }

    public void addOption(Option option) {
        options.add(option);
        option.changeProduct(this);
    }

    public void removeOption(Option option) {
        options.remove(option);
        option.changeProduct(null);
    }
}