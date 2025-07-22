package gift.product.repository;

import gift.product.entity.Product;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p JOIN FETCH p.options WHERE p.productId = :productId")
    Optional<Product> findWithOptionsById(Long productId);
}
