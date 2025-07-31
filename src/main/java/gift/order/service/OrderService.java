package gift.order.service;

import gift.kakao.service.MessageService;
import gift.option.entity.Option;
import gift.option.service.OptionService;
import gift.order.dto.OrderCreateCommand;
import gift.order.entity.Order;
import gift.order.repository.OrderRepository;
import gift.product.entity.Product;
import gift.wish.service.WishService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    // Service
    private final WishService wishService;
    private final OptionService optionService;
    private final MessageService kakaoMessageService;

    // Repository
    private final OrderRepository orderRepository;

    public OrderService(WishService wishService, OptionService optionService,
        MessageService kakaoMessageService, OrderRepository orderRepository) {
        this.wishService = wishService;
        this.optionService = optionService;
        this.kakaoMessageService = kakaoMessageService;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public Order createOrder(Long memberId, OrderCreateCommand dto) {
        Option option = optionService.getOption(dto.optionId());

        Order order = new Order(option, dto.quantity(), dto.message());

        optionService.subtractOptionQuantity(option.getOptionId(), dto.quantity());

        Product product = option.getProduct();

        wishService.deleteWishByMemberIdAndProductId(memberId, product.getProductId());

        Order savedOrder = orderRepository.save(order);

        String templateJson = kakaoMessageService.createTextMessage(dto.message());
        kakaoMessageService.sendTextMessage(templateJson);

        return savedOrder;
    }

}
