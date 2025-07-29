package gift.order.entity;

import gift.option.entity.Option;
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
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "option_id", foreignKey = @ForeignKey(name = "fk_order_option_id_ref_option_id"), nullable = false)
    private Option option;

    @Column(nullable = false)
    private Integer quantity;

    @Column(length = 200, nullable = false)
    private String message;

    @Column(nullable = false)
    private LocalDateTime orderDateTime;

    @PrePersist
    protected void onCreate() {
        orderDateTime = LocalDateTime.now();
    }

    protected Order() {

    }

    public Order(Option option, Integer quantity, String message) {
        this(option, quantity, message, null);
    }

    public Order(Option option, Integer quantity, String message, LocalDateTime orderDateTime) {
        this.option = option;
        this.quantity = quantity;
        this.message = message;
        this.orderDateTime = orderDateTime;
    }

    public Long getId() {
        return id;
    }

    public Option getOption() {
        return option;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getOrderDateTime() {
        return orderDateTime;
    }

    public Long getOptionId() {
        return option.getOptionId();
    }
}
