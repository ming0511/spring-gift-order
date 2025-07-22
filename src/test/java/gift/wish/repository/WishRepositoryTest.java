package gift.wish.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import gift.member.builder.MemberBuilder;
import gift.member.entity.Member;
import gift.member.repository.MemberRepository;
import gift.product.builder.ProductBuilder;
import gift.product.entity.Product;
import gift.product.repository.ProductRepository;
import gift.wish.entity.Wish;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@DataJpaTest
public class WishRepositoryTest {

    @Autowired
    private WishRepository wishRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void saveWish() {
        Member member = memberRepository.save(MemberBuilder.aMember().build());
        Product product = productRepository.save(ProductBuilder.aProduct().build());

        Wish expected = new Wish(member, product);
        Wish actual = wishRepository.save(expected);
        assertAll(
            () -> assertThat(actual.getWishId()).isNotNull(),
            () -> assertThat(actual.getMemberId()).isEqualTo(member.getMemberId()),
            () -> assertThat(actual.getProductId()).isEqualTo(product.getProductId())
        );
    }

    @Test
    void findAllWishes() {
        Member member = memberRepository.save(MemberBuilder.aMember().build());
        Product product = productRepository.save(ProductBuilder.aProduct().withName("3").build());
        Wish expected = new Wish(member, product);
        wishRepository.save(expected);

        Member member1 = memberRepository.save(MemberBuilder.aMember().withEmail("two").build());
        Product product1 = productRepository.save(ProductBuilder.aProduct().withName("2").build());
        Wish expected1 = new Wish(member1, product1);
        wishRepository.save(expected1);

        Member member2 = memberRepository.save(MemberBuilder.aMember().withEmail("three").build());
        Product product2 = productRepository.save(ProductBuilder.aProduct().withName("3").build());
        Wish expected2 = new Wish(member2, product2);
        wishRepository.save(expected2);

        List<Wish> wishList = wishRepository.findAll();

        assertThat(wishList).hasSize(3);
    }

    @Test
    void findWishById() {
        Member member = memberRepository.save(MemberBuilder.aMember().build());
        Product product = productRepository.save(ProductBuilder.aProduct().build());
        Wish expected = new Wish(member, product);
        wishRepository.save(expected);

        Wish actual = wishRepository.findById(expected.getWishId()).get();
        assertAll(
            () -> assertThat(actual.getWishId()).isNotNull(),
            () -> assertThat(actual.getMemberId()).isEqualTo(member.getMemberId()),
            () -> assertThat(actual.getProductId()).isEqualTo(product.getProductId())
        );
    }

    @Test
    void deleteWishById() {
        Member member = memberRepository.save(MemberBuilder.aMember().build());
        Product product = productRepository.save(ProductBuilder.aProduct().build());
        Wish expected = new Wish(member, product);
        wishRepository.save(expected);

        wishRepository.deleteById(expected.getWishId());

        assertThat(wishRepository.findById(expected.getWishId())).isEmpty();
    }

    @Test
    void existsByMemberIdAndProductId() {
        Member member = memberRepository.save(MemberBuilder.aMember().build());
        Product product = productRepository.save(ProductBuilder.aProduct().build());
        Wish expected = new Wish(member, product);
        wishRepository.save(expected);

        Boolean actual = wishRepository.existsByMember_MemberIdAndProduct_ProductId(
            member.getMemberId(),
            product.getProductId());

        assertThat(actual).isTrue();
    }

    @Test
    void findByMember_MemberId() {
        Member member = memberRepository.save(MemberBuilder.aMember().build());
        Product product = productRepository.save(ProductBuilder.aProduct().withName("3").build());
        Wish expected = new Wish(member, product);
        wishRepository.save(expected);

        Product product1 = productRepository.save(ProductBuilder.aProduct().withName("2").build());
        Wish expected1 = new Wish(member, product1);
        wishRepository.save(expected1);

        Member member2 = memberRepository.save(MemberBuilder.aMember().withEmail("three").build());
        Product product2 = productRepository.save(ProductBuilder.aProduct().withName("3").build());
        Wish expected2 = new Wish(member2, product2);
        wishRepository.save(expected2);

        Sort sort = Sort.by("createdAt").descending();

        Pageable pageable = PageRequest.of(0, 10, sort);

        Page<Wish> actual = wishRepository.findByMember_MemberId(member.getMemberId(), pageable);

        System.out.println("총 페이지 수: " + actual.getTotalPages());
        System.out.println("총 요소 수: " + actual.getTotalElements());
        System.out.println("현재 페이지 번호: " + actual.getNumber());
        System.out.println("현재 페이지 데이터:");
        for (Wish wish : actual.getContent()) {
            System.out.println("ID: " + wish.getWishId());
            System.out.println("memberId: " + wish.getMemberId());
            System.out.println("productId: " + wish.getProductId());
            System.out.println("productName: " + wish.getProduct().getName());
            System.out.println("생성일: " + wish.getCreatedAt());
            System.out.println("---------------");
        }
    }
}
