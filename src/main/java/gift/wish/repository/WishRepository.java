package gift.wish.repository;

import gift.wish.entity.Wish;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishRepository extends JpaRepository<Wish, Long> {

    Boolean existsByMember_MemberIdAndProduct_ProductId(Long memberId, Long productId);

    Page<Wish> findByMember_MemberId(Long memberId, Pageable pageable);
}
