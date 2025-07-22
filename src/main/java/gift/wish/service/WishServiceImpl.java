package gift.wish.service;

import gift.exception.member.MemberNotFoundException;
import gift.exception.product.ProductNotFoundException;
import gift.exception.wish.InvalidPageException;
import gift.exception.wish.WishNotFoundException;
import gift.exception.wish.WishlistAccessDeniedException;
import gift.member.entity.Member;
import gift.member.repository.MemberRepository;
import gift.product.entity.Product;
import gift.product.repository.ProductRepository;
import gift.wish.dto.WishCreateCommand;
import gift.wish.dto.WishCreateResponseDto;
import gift.wish.dto.WishGetResponseDto;
import gift.wish.dto.WishPageResponseDto;
import gift.wish.entity.Wish;
import gift.wish.repository.WishRepository;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class WishServiceImpl implements WishService {

    private static final Set<String> ALLOWED_FIELDS = Set.of("createdAt", "wishId");

    private final WishRepository wishRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    public WishServiceImpl(WishRepository wishRepository,
        MemberRepository memberRepository, ProductRepository productRepository) {
        this.wishRepository = wishRepository;
        this.memberRepository = memberRepository;
        this.productRepository = productRepository;
    }

    @Override
    public WishCreateResponseDto addWish(Long memberId, WishCreateCommand dto) {
        // TODO: 이미 추가한 상품인지 확인하기(WishRepository.existsByMemberAndProduct) 실패 시 예외 처리(이미 존재하는 위시) -> 이후 수량 관련해서 추가.
        Long productId = dto.productId();

        Boolean exists = wishRepository.existsByMember_MemberIdAndProduct_ProductId(memberId,
            productId);
        if (exists) {
            throw new IllegalStateException("이미 위시리스트에 추가하셨습니다.");
        }

        Member member = memberRepository.findById(memberId).orElseThrow(
            () -> new MemberNotFoundException("회원이 존재하지 않습니다. memberId =" + memberId)
        );
        Product product = productRepository.findById(productId).orElseThrow(
            () -> new ProductNotFoundException("상품이 존재하지 않습니다. productId =" + productId)
        );

        Wish wish = new Wish(member, product);
        Wish savedWish = wishRepository.save(wish);

        return new WishCreateResponseDto(savedWish.getWishId(), savedWish.getMemberId(),
            savedWish.getProductId(),
            savedWish.getCreatedAt());
    }

    @Override
    public WishPageResponseDto getWishes(Long memberId, Pageable pageable) {

        for (Sort.Order order : pageable.getSort()) {
            if (!ALLOWED_FIELDS.contains(order.getProperty())) {
                throw new InvalidPageException("허용되지 않은 정렬 필드입니다: " + order.getProperty());
            }
        }

        Page<Wish> wishPage = wishRepository.findByMember_MemberId(memberId, pageable);

        List<WishGetResponseDto> content = wishPage.getContent().stream()
            .map(wish -> new WishGetResponseDto(
                wish.getWishId(),
                wish.getProductId(),
                (wish.getProduct() != null) ? wish.getProduct().getName() : "알 수 없음",
                wish.getCreatedAt()))
            .collect(Collectors.toList());

        return new WishPageResponseDto(
            content,
            wishPage.getNumber(),
            wishPage.getSize(),
            wishPage.getTotalElements(),
            wishPage.getTotalPages());
    }

    @Override
    public void deleteWish(Long memberId, Long wishId) {
        Wish wish = wishRepository.findById(wishId).orElseThrow(
            () -> new WishNotFoundException("위시 상품이 존재하지 않습니다. wishId = " + wishId)
        );

        if (!memberId.equals(wish.getMemberId())) {
            throw new WishlistAccessDeniedException("다른 사용자의 위시리스트에 접근할 수 없습니다.");
        }

        wishRepository.deleteById(wishId);
    }

}
