package gift.wish.service;

import gift.wish.dto.WishCreateCommand;
import gift.wish.dto.WishCreateResponseDto;
import gift.wish.dto.WishPageResponseDto;
import org.springframework.data.domain.Pageable;

public interface WishService {

    WishCreateResponseDto addWish(Long memberId, WishCreateCommand dto);

    WishPageResponseDto getWishes(Long memberId, Pageable pageable);

    void deleteWish(Long memberId, Long wishId);

}
