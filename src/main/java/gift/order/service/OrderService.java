package gift.order.service;

import gift.exception.option.OptionNotFoundException;
import gift.exception.order.OutOfStockException;
import gift.kakao.service.KakaoMessageService;
import gift.option.entity.Option;
import gift.option.repository.OptionRepository;
import gift.option.service.OptionService;
import gift.order.dto.OrderCreateCommand;
import gift.order.entity.Order;
import gift.order.repository.OrderRepository;
import gift.product.entity.Product;
import gift.wish.repository.WishRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OptionRepository optionRepository;

    private final OptionService optionService;
    private final KakaoMessageService kakaoMessageService;
    private final WishRepository wishRepository;

    public OrderService(OrderRepository orderRepository, OptionRepository optionRepository,
        OptionService optionService, KakaoMessageService kakaoMessageService,
        WishRepository wishRepository) {
        this.orderRepository = orderRepository;
        this.optionRepository = optionRepository;
        this.optionService = optionService;
        this.kakaoMessageService = kakaoMessageService;
        this.wishRepository = wishRepository;
    }

    @Transactional
    public Order createOrder(Long memberId, OrderCreateCommand dto) {
        Option option = optionRepository.findById(dto.optionId())
            .orElseThrow(() -> new OptionNotFoundException("해당 옵션을 찾을 수 없습니다."));

        if (option.getQuantity() < dto.quantity()) {
            throw new OutOfStockException("재고가 부족합니다.");
        }

        Order order = new Order(option, dto.quantity(), dto.message());

        optionService.subtractOptionQuantity(option.getOptionId(), dto.quantity());

        Product product = option.getProduct();
        wishRepository.deleteByMember_MemberIdAndProduct_ProductId(memberId,
            product.getProductId());

        Order savedOrder = orderRepository.save(order);

        String templateJson = kakaoMessageService.createTextMessage(dto.message());
        kakaoMessageService.sendTextMessage(templateJson);

        return savedOrder;
    }

}
