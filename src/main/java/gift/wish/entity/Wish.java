package gift.wish.entity;

import gift.member.entity.Member;
import gift.product.entity.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "wish")
public class Wish {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long wishId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(name = "fk_wish_member_id_ref_member_id"), nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", foreignKey = @ForeignKey(name = "fk_wish_product_id_ref_product_id"), nullable = false)
    private Product product;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    protected Wish() {

    }

    public Wish(Member member, Product product) {
        this(null, member, product, null);
    }

    public Wish(Long wishId, Member member, Product product, LocalDateTime createdAt) {
        this.wishId = wishId;
        this.member = member;
        this.product = product;
        this.createdAt = createdAt;
    }

    public Long getWishId() {
        return wishId;
    }

    public Member getMember() {
        return member;
    }

    public Product getProduct() {
        return product;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Long getMemberId() {
        return member.getMemberId();
    }

    public Long getProductId() {
        return product.getProductId();
    }
}
